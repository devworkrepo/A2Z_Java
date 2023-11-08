package com.a2zsuvidhaa.`in`.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.database.DBHelper
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.VolleyClient
import com.a2zsuvidhaa.`in`.util.apis.ApiInterceptor
import com.a2zsuvidhaa.`in`.util.apis.NetworkConnection
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providerOldAppPreference(@ApplicationContext context : Context): AppPreference =
         AppPreference.getInstance(context)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context : Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideSecurity() : Security = Security(APIs.ENCRYPTED_KEY)

    @Provides
    @Singleton
    fun provideNetworkConnection(@ApplicationContext context : Context) : NetworkConnection {
        return NetworkConnection(context)
    }

    @Provides
    @Singleton
    fun provideApiInterceptor(appPreference: AppPreference, networkConnection: NetworkConnection) : ApiInterceptor {
        return ApiInterceptor(appPreference,networkConnection)
    }


    @Provides
    @Singleton
    fun provideLocalDB(@ApplicationContext context: Context) : DBHelper {
        return DBHelper(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideVolleyClient(@ApplicationContext context : Context,appPreference: AppPreference) : VolleyClient {
        return VolleyClient(context,appPreference)
    }

    @Provides
    @Singleton
    fun provideContextInjectUtil(@ApplicationContext context: Context): ContextInjectUtil{
        return ContextInjectUtil(context);
    }
}