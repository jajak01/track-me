package websocket

import (
	"context"
	"encoding/json"
	"net/http"
	"sync"
	"time"

	"github.com/google/uuid"
	"github.com/gorilla/websocket"
	"github.com/rs/zerolog/log"
)

// Message defines the structure of WebSocket communication
type Message struct {
	Event string      `json:"event"`
	Data  interface{} `json:"data,omitempty"`
}

// Client represents a connected WebSocket client
type Client struct {
	Hub    *Hub
	Conn   *websocket.Conn
	UserID uuid.UUID
	Send   chan []byte
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true // Allow all origins for simplicity (CORS is handled at router/proxy level)
	},
}

// Hub maintains the set of active clients and handles routing messages
type Hub struct {
	clients    map[uuid.UUID]map[*Client]bool
	register   chan *Client
	unregister chan *Client
	broadcast  chan *UserMessage
	mu         sync.RWMutex
}

type UserMessage struct {
	UserID  uuid.UUID
	Payload []byte
}

func NewHub() *Hub {
	return &Hub{
		clients:    make(map[uuid.UUID]map[*Client]bool),
		register:   make(chan *Client),
		unregister: make(chan *Client),
		broadcast:  make(chan *UserMessage, 256),
	}
}

func (h *Hub) Run() {
	for {
		select {
		case client := <-h.register:
			h.mu.Lock()
			if _, ok := h.clients[client.UserID]; !ok {
				h.clients[client.UserID] = make(map[*Client]bool)
			}
			h.clients[client.UserID][client] = true
			h.mu.Unlock()
			log.Debug().Str("user_id", client.UserID.String()).Msg("client registered to websocket hub")

		case client := <-h.unregister:
			h.mu.Lock()
			if userClients, ok := h.clients[client.UserID]; ok {
				if _, ok := userClients[client]; ok {
					delete(userClients, client)
					close(client.Send)
					if len(userClients) == 0 {
						delete(h.clients, client.UserID)
					}
				}
			}
			h.mu.Unlock()
			log.Debug().Str("user_id", client.UserID.String()).Msg("client unregistered from websocket hub")

		case userMsg := <-h.broadcast:
			h.mu.RLock()
			userClients, ok := h.clients[userMsg.UserID]
			if ok {
				for client := range userClients {
					select {
					case client.Send <- userMsg.Payload:
					default:
						// If send channel is full, unregister client
						h.mu.RUnlock()
						h.mu.Lock()
						delete(userClients, client)
						close(client.Send)
						if len(userClients) == 0 {
							delete(h.clients, client.UserID)
						}
						h.mu.Unlock()
						h.mu.RLock()
					}
				}
			}
			h.mu.RUnlock()
		}
	}
}

func (h *Hub) SendToUser(userID uuid.UUID, event string, data interface{}) {
	msg := Message{
		Event: event,
		Data:  data,
	}
	payload, err := json.Marshal(msg)
	if err != nil {
		log.Error().Err(err).Msg("failed to marshal websocket message")
		return
	}

	h.broadcast <- &UserMessage{
		UserID:  userID,
		Payload: payload,
	}
}

// ReadPump pumps messages from the websocket connection to the hub
func (c *Client) ReadPump() {
	defer func() {
		c.Conn.Close()
		c.Hub.unregister <- c
	}()

	c.Conn.SetReadLimit(512 * 1024) // 512KB max size
	_ = c.Conn.SetReadDeadline(time.Now().Add(60 * time.Second))
	c.Conn.SetPongHandler(func(string) error {
		_ = c.Conn.SetReadDeadline(time.Now().Add(60 * time.Second))
		return nil
	})

	for {
		_, message, err := c.Conn.ReadMessage()
		if err != nil {
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				log.Error().Err(err).Msg("websocket read error")
			}
			break
		}

		var wsMsg Message
		if err := json.Unmarshal(message, &wsMsg); err != nil {
			log.Debug().Err(err).Msg("failed to parse client websocket message")
			continue
		}

		// Handle heartbeat
		if wsMsg.Event == "heartbeat" {
			c.Send <- []byte(`{"event":"heartbeat","data":"pong"}`)
		}

		// Handle client-initiated events here if needed in future
	}
}

// WritePump pumps messages from the hub to the websocket connection
func (c *Client) WritePump() {
	ticker := time.NewTicker(54 * time.Second)
	defer func() {
		ticker.Stop()
		c.Conn.Close()
	}()

	for {
		select {
		case message, ok := <-c.Send:
			_ = c.Conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
			if !ok {
				_ = c.Conn.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}

			w, err := c.Conn.NextWriter(websocket.TextMessage)
			if err != nil {
				return
			}
			_, _ = w.Write(message)

			// Add queued chat messages to the current websocket message.
			n := len(c.Send)
			for i := 0; i < n; i++ {
				_, _ = w.Write([]byte{'\n'})
				_, _ = w.Write(<-c.Send)
			}

			if err := w.Close(); err != nil {
				return
			}

		case <-ticker.C:
			_ = c.Conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
			if err := c.Conn.WriteMessage(websocket.PingMessage, nil); err != nil {
				return
			}
		}
	}
}

// ServeWs handles websocket requests from the peer.
func ServeWs(hub *Hub, w http.ResponseWriter, r *http.Request, userID uuid.UUID) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Error().Err(err).Msg("failed to upgrade connection to websocket")
		return
	}

	client := &Client{
		Hub:    hub,
		Conn:   conn,
		UserID: userID,
		Send:   make(chan []byte, 256),
	}

	client.Hub.register <- client

	// Start pump goroutines
	go client.WritePump()
	go client.ReadPump()
}

// IsUserConnected checks if a user has any active connections
func (h *Hub) IsUserConnected(userID uuid.UUID) bool {
	h.mu.RLock()
	defer h.mu.RUnlock()
	userClients, ok := h.clients[userID]
	return ok && len(userClients) > 0
}
