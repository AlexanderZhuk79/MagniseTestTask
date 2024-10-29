package com.magnise.datastore.di

import com.magnise.datastore.DataStoreImpl
import com.magnise.datastore.DataStorePreferenceAPI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Singleton
    @Binds
    abstract fun DataStorePreferenceAPI(myDataStore: DataStoreImpl): DataStorePreferenceAPI

}