package com.desen.trackme.ui;

import androidx.lifecycle.ViewModel;
import com.desen.trackme.domain.repository.AuthRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R/\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u00068F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006\u0011"}, d2 = {"Lcom/desen/trackme/ui/RootViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lcom/desen/trackme/domain/repository/AuthRepository;", "(Lcom/desen/trackme/domain/repository/AuthRepository;)V", "<set-?>", "", "loggedIn", "getLoggedIn", "()Ljava/lang/Boolean;", "setLoggedIn", "(Ljava/lang/Boolean;)V", "loggedIn$delegate", "Landroidx/compose/runtime/MutableState;", "onLoggedIn", "", "onLoggedOut", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class RootViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.domain.repository.AuthRepository authRepository = null;
    
    /**
     * null = still checking the persisted session.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState loggedIn$delegate = null;
    
    @javax.inject.Inject()
    public RootViewModel(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.domain.repository.AuthRepository authRepository) {
        super();
    }
    
    /**
     * null = still checking the persisted session.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getLoggedIn() {
        return null;
    }
    
    /**
     * null = still checking the persisted session.
     */
    private final void setLoggedIn(java.lang.Boolean p0) {
    }
    
    public final void onLoggedIn() {
    }
    
    public final void onLoggedOut() {
    }
}