package com.desen.trackme.feature.auth;

import android.util.Patterns;
import androidx.lifecycle.ViewModel;
import com.desen.trackme.domain.repository.AuthRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0015\u001a\u00020\u000fJ\u0012\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0017H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R+\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006\u0019"}, d2 = {"Lcom/desen/trackme/feature/auth/AuthViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lcom/desen/trackme/domain/repository/AuthRepository;", "(Lcom/desen/trackme/domain/repository/AuthRepository;)V", "<set-?>", "Lcom/desen/trackme/feature/auth/AuthUiState;", "uiState", "getUiState", "()Lcom/desen/trackme/feature/auth/AuthUiState;", "setUiState", "(Lcom/desen/trackme/feature/auth/AuthUiState;)V", "uiState$delegate", "Landroidx/compose/runtime/MutableState;", "login", "", "onDisplayNameChange", "value", "", "onEmailChange", "onPasswordChange", "register", "validate", "", "requireName", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AuthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.domain.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState uiState$delegate = null;
    
    @javax.inject.Inject()
    public AuthViewModel(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.domain.repository.AuthRepository authRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.feature.auth.AuthUiState getUiState() {
        return null;
    }
    
    private final void setUiState(com.desen.trackme.feature.auth.AuthUiState p0) {
    }
    
    public final void onEmailChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onPasswordChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onDisplayNameChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void login() {
    }
    
    public final void register() {
    }
    
    private final boolean validate(boolean requireName) {
        return false;
    }
}