package com.desen.trackme.feature.auth;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.foundation.text.KeyboardOptions;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u0006\u0006"}, d2 = {"AuthScreen", "", "onLoggedIn", "Lkotlin/Function0;", "viewModel", "Lcom/desen/trackme/feature/auth/AuthViewModel;", "app_debug"})
public final class AuthScreenKt {
    
    /**
     * One-time login/register screen. After a successful login the app keeps the
     * user authenticated forever (persistent refresh token) and never shows this
     * again until logout.
     */
    @androidx.compose.runtime.Composable()
    public static final void AuthScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLoggedIn, @org.jetbrains.annotations.NotNull()
    com.desen.trackme.feature.auth.AuthViewModel viewModel) {
    }
}