#!/bin/bash
BASE="http://localhost:8080/api/v1"
PASS=0
FAIL=0

check() { # label, expected_http, method, path, auth_token, body
  local label="$1" exp="$2" method="$3" path="$4" token="$5" body="$6"
  local code http_code
  if [ -z "$token" ]; then
    http_code=$(curl -s -o /tmp/resp.json -w "%{http_code}" -X "$method" "$BASE$path" -H 'Content-Type: application/json' ${body:+-d "$body"})
  else
    http_code=$(curl -s -o /tmp/resp.json -w "%{http_code}" -X "$method" "$BASE$path" -H 'Content-Type: application/json' -H "Authorization: Bearer $token" ${body:+-d "$body"})
  fi
  local body_text=$(cat /tmp/resp.json)
  if echo "$http_code" | grep -qE "^$exp"; then
    echo "✅ $label (HTTP $http_code)"
    PASS=$((PASS+1))
  else
    echo "❌ $label — expected ${exp}*, got HTTP $http_code: $body_text"
    FAIL=$((FAIL+1))
  fi
}

echo "========== AUTH (public) =========="
check "Register new user"        "201|409" POST /auth/register "" '{"email":"test3@test.com","password":"test123456","display_name":"Tester"}'
check "Login"                    "200" POST /auth/login "" '{"email":"reza01@gmail.com","password":"reza1234"}'

TOKEN1=$(cat /tmp/resp.json | python -c "import sys,json; d=json.load(sys.stdin); print(d['data']['access_token'])" 2>/dev/null)
TOKEN2=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' -d '{"email":"reza02@gmail.com","password":"reza1234"}' | python -c "import sys,json; print(json.load(sys.stdin)['data']['access_token'])")

check "Login fail (wrong pw)"   "401" POST /auth/login "" '{"email":"reza01@gmail.com","password":"wrong"}'
check "Refresh token"           "200" POST /auth/refresh "" '{"refresh_token":"invalid"}'

echo ""
echo "========== USER (protected) =========="
check "Get profile"             "200" GET /user/profile "$TOKEN1"
check "Update profile"          "200" PATCH /user/profile "$TOKEN1" '{"status_message":"Hello world!"}'

echo ""
echo "========== FRIEND (protected) =========="
check "Get friends"             "200" GET /friend/list "$TOKEN1"
check "Get pending requests"    "200" GET /friend/requests "$TOKEN1"
check "Get blocked users"       "200" GET /friend/blocked "$TOKEN1"
check "Send request fail(self)" "400" POST /friend/request "$TOKEN1" '{"receiver_email":"reza01@gmail.com"}'

echo ""
echo "========== NOTIFICATIONS =========="
check "Get notifications"       "200" GET /notification/list "$TOKEN2"
check "Mark all read"           "200" POST /notification/read-all "$TOKEN2"

echo ""
echo "========== LOCATION (protected) =========="
check "Update location"         "200" POST /location/update "$TOKEN2" '{"latitude":-6.2,"longitude":106.8,"battery_percentage":85,"activity_type":"walking","is_charging":false,"is_mock":false}'
check "Get friend location"     "200" GET /location/current/47f5517a-1615-4f6a-941a-dcfa42c9b199 "$TOKEN1"
check "Get location history"    "200" GET "/location/history/47f5517a-1615-4f6a-941a-dcfa42c9b199?start=2026-08-07T00:00:00Z&end=2026-08-09T00:00:00Z" "$TOKEN1"
check "Location no auth"        "401" GET /location/current/47f5517a-1615-4f6a-941a-dcfa42c9b199 ""

echo ""
echo "========== SHARING =========="
check "Revoke sharing"          "200" DELETE /sharing/revoke/47f5517a-1615-4f6a-941a-dcfa42c9b199 "$TOKEN1"
# After revoke, can't see location
check "Location after revoke"   "403" GET /location/current/47f5517a-1615-4f6a-941a-dcfa42c9b199 "$TOKEN1"

echo ""
echo "========== SUMMARY =========="
echo "Passed: $PASS | Failed: $FAIL"
