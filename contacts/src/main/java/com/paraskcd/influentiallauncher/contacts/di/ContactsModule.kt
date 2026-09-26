package com.paraskcd.influentiallauncher.contacts.di

import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactPinStore
import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactsSource
import com.paraskcd.influentiallauncher.contacts.infrastructure.ContactsContractSource
import com.paraskcd.influentiallauncher.contacts.infrastructure.DataStoreContactPinStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {
    @Binds
    abstract fun bindContactsSource(impl: ContactsContractSource): ContactsSource

    @Binds
    abstract fun bindContactPinStore(impl: DataStoreContactPinStore): ContactPinStore
}
