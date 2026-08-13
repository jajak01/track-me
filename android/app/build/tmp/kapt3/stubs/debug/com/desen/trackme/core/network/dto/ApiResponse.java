package com.desen.trackme.core.network.dto;

import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;

/**
 * Wraps the backend `dto.APIResponse{success,message,data,errors}` envelope.
 */
@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 4*\u0004\b\u0000\u0010\u00012\u00020\u0002:\u000234BM\b\u0011\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0001\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0001\u0010\t\u001a\u0004\u0018\u00018\u0000\u0012\u0010\b\u0001\u0010\n\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0002\u0010\u000eB5\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00018\u0000\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001e\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\bH\u00c6\u0003J\u0010\u0010 \u001a\u0004\u0018\u00018\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\b0\u000bH\u00c6\u0003JD\u0010\"\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00018\u00002\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000bH\u00c6\u0001\u00a2\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020\u00062\b\u0010%\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010&\u001a\u00020\u0004H\u00d6\u0001J\t\u0010\'\u001a\u00020\bH\u00d6\u0001J@\u0010(\u001a\u00020)\"\u0004\b\u0001\u0010*2\f\u0010+\u001a\b\u0012\u0004\u0012\u0002H*0\u00002\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/2\f\u00100\u001a\b\u0012\u0004\u0012\u0002H*01H\u00c1\u0001\u00a2\u0006\u0002\b2R \u0010\t\u001a\u0004\u0018\u00018\u00008\u0006X\u0087\u0004\u00a2\u0006\u0010\n\u0002\u0010\u0014\u0012\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\"\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0015\u0010\u0011\u001a\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0018\u0010\u0011\u001a\u0004\b\u0019\u0010\u001aR\u001c\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u001b\u0010\u0011\u001a\u0004\b\u001c\u0010\u001d\u00a8\u00065"}, d2 = {"Lcom/desen/trackme/core/network/dto/ApiResponse;", "T", "", "seen1", "", "success", "", "message", "", "data", "errors", "", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(IZLjava/lang/String;Ljava/lang/Object;Ljava/util/List;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "(ZLjava/lang/String;Ljava/lang/Object;Ljava/util/List;)V", "getData$annotations", "()V", "getData", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getErrors$annotations", "getErrors", "()Ljava/util/List;", "getMessage$annotations", "getMessage", "()Ljava/lang/String;", "getSuccess$annotations", "getSuccess", "()Z", "component1", "component2", "component3", "component4", "copy", "(ZLjava/lang/String;Ljava/lang/Object;Ljava/util/List;)Lcom/desen/trackme/core/network/dto/ApiResponse;", "equals", "other", "hashCode", "toString", "write$Self", "", "T0", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "typeSerial0", "Lkotlinx/serialization/KSerializer;", "write$Self$app_debug", "$serializer", "Companion", "app_debug"})
public final class ApiResponse<T extends java.lang.Object> {
    private final boolean success = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.Nullable()
    private final T data = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> errors = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.desen.trackme.core.network.dto.ApiResponse.Companion Companion = null;
    
    public ApiResponse(boolean success, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    T data, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> errors) {
        super();
    }
    
    public final boolean getSuccess() {
        return false;
    }
    
    @kotlinx.serialization.SerialName(value = "success")
    @java.lang.Deprecated()
    public static void getSuccess$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMessage() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "message")
    @java.lang.Deprecated()
    public static void getMessage$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final T getData() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "data")
    @java.lang.Deprecated()
    public static void getData$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getErrors() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "errors")
    @java.lang.Deprecated()
    public static void getErrors$annotations() {
    }
    
    public ApiResponse() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final T component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.network.dto.ApiResponse<T> copy(boolean success, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    T data, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> errors) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    public static final <T0 extends java.lang.Object>void write$Self$app_debug(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.ApiResponse<T0> self, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.descriptors.SerialDescriptor serialDesc, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.KSerializer<T0> typeSerial0) {
    }
    
    /**
     * Wraps the backend `dto.APIResponse{success,message,data,errors}` envelope.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000*\u0004\b\u0001\u0010\u00012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00030\u0002B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0004B\u0015\b\u0017\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\u000e\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00060\u000fH\u00d6\u0001\u00a2\u0006\u0002\u0010\u0010J\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00028\u00000\u00032\u0006\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\u001f\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003H\u00d6\u0001J\u0018\u0010\u0019\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00060\u000fH\u00d6\u0001\u00a2\u0006\u0002\u0010\u0010R\u0014\u0010\b\u001a\u00020\t8VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00010\u00068BX\u00c2\u0005\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001a"}, d2 = {"com/desen/trackme/core/network/dto/ApiResponse.$serializer", "T", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/desen/trackme/core/network/dto/ApiResponse;", "()V", "typeSerial0", "Lkotlinx/serialization/KSerializer;", "(Lkotlinx/serialization/KSerializer;)V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "getTypeSerial0", "()Lkotlinx/serialization/KSerializer;", "childSerializers", "", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "typeParametersSerializers", "app_debug"})
    @java.lang.Deprecated()
    public static final class $serializer<T extends java.lang.Object> implements kotlinx.serialization.internal.GeneratedSerializer<com.desen.trackme.core.network.dto.ApiResponse<T>> {
        
        private $serializer() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] childSerializers() {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.desen.trackme.core.network.dto.ApiResponse<T> deserialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Decoder decoder) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
            return null;
        }
        
        private final kotlinx.serialization.KSerializer<T> getTypeSerial0() {
            return null;
        }
        
        @java.lang.Override()
        public void serialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
        com.desen.trackme.core.network.dto.ApiResponse<T> value) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
            return null;
        }
    }
    
    /**
     * Wraps the backend `dto.APIResponse{success,message,data,errors}` envelope.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J)\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00060\u00050\u0004\"\u0004\b\u0001\u0010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0004H\u00c6\u0001\u00a8\u0006\b"}, d2 = {"Lcom/desen/trackme/core/network/dto/ApiResponse$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/desen/trackme/core/network/dto/ApiResponse;", "T0", "typeSerial0", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final <T0 extends java.lang.Object>kotlinx.serialization.KSerializer<com.desen.trackme.core.network.dto.ApiResponse<T0>> serializer(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.KSerializer<T0> typeSerial0) {
            return null;
        }
    }
}