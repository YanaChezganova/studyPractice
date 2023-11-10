package com.example.sunnyday.di

import com.example.sunnyday.data.DataRepository
import com.example.sunnyday.data.NetworkRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class DataModule{
    fun provideDataRepository(): DataRepository {
        return DataRepository()
    }
    fun provideNetworkRepository(): NetworkRepository{
        return NetworkRepository()
    }

}