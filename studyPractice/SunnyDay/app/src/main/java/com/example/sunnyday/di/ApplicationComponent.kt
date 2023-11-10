package com.example.sunnyday.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules =
[NetworkModule::class,
    RoomModule::class,
    DataModule::class,
])

interface AppComponent {}
