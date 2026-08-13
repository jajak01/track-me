package com.desen.trackme.core.di

import com.desen.trackme.data.repository.AuthRepositoryImpl
import com.desen.trackme.data.repository.LocationRepositoryImpl
import com.desen.trackme.domain.repository.AuthRepository
import com.desen.trackme.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
