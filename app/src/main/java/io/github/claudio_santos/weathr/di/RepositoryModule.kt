package io.github.claudio_santos.weathr.di

import io.github.claudio_santos.weathr.data.repository.WeatherRepository
import io.github.claudio_santos.weathr.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
}
