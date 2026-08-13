package com.desen.trackme.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\b\u0010\u0004\u001a\u00020\u0001H\u0003\u00a8\u0006\u0005"}, d2 = {"AppRoot", "", "rootViewModel", "Lcom/desen/trackme/ui/RootViewModel;", "LoadingScreen", "app_debug"})
public final class AppRootKt {
    
    /**
     * Top-level navigation. If a persisted session exists the app jumps straight to
     * the status screen (silent re-authentication = "login forever"); otherwise it
     * shows the one-time auth screen.
     */
    @androidx.compose.runtime.Composable()
    public static final void AppRoot(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.ui.RootViewModel rootViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LoadingScreen() {
    }
}