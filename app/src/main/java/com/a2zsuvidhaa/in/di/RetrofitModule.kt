package com.a2zsuvidhaa.`in`.di

import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.data.service.*
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.apis.ApiInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideBaseUrl() = AppConstants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(apiInterceptor: ApiInterceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okkHttpClient = OkHttpClient.Builder()
        val timeout = 300L;

        okkHttpClient.addInterceptor(apiInterceptor)
        okkHttpClient.readTimeout(timeout, TimeUnit.SECONDS);
        okkHttpClient.connectTimeout(timeout, TimeUnit.SECONDS);
        okkHttpClient.callTimeout(timeout, TimeUnit.SECONDS);
        okkHttpClient.writeTimeout(timeout, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) okkHttpClient.addInterceptor(logger)

        return okkHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideApiClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .client(okHttpClient)
            .build()
    }

    /* @Provides
     @Singleton
     fun provideAuthService(retrofit: Retrofit): AuthService =
         retrofit.create(AuthService::class.java)
     */

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)

    @Provides
    @Singleton
    fun providerRegistrationService(retrofit: Retrofit): RegistrationService =
        retrofit.create(RegistrationService::class.java)


    @Provides
    @Singleton
    fun providerKycService(retrofit: Retrofit): KycService = retrofit.create(KycService::class.java)

    @Provides
    @Singleton
    fun providerLoginService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideFundRequestService(retrofit: Retrofit): FundRequestService =
        retrofit.create(FundRequestService::class.java)


    @Provides
    @Singleton
    fun provideAepsService(retrofit: Retrofit): AepsService =
        retrofit.create(AepsService::class.java)

    @Provides
    @Singleton
    fun provideAgreementService(retrofit: Retrofit): AgreementService =
        retrofit.create(AgreementService::class.java)


    @Provides
    @Singleton
    fun providerDmtService(retrofit: Retrofit): DmtWalletService =
        retrofit.create(DmtWalletService::class.java)

    @Provides
    @Singleton
    fun provideDmtThreeService(retrofit: Retrofit): DmtThreeService =
        retrofit.create(DmtThreeService::class.java)

    @Provides
    @Singleton
    fun provideUpiService(retrofit: Retrofit): UpiService = retrofit.create(UpiService::class.java)


    @Provides
    @Singleton
    fun providerReportService(retrofit: Retrofit): ReportService =
        retrofit.create(ReportService::class.java)

    @Provides
    @Singleton
    fun providerMatmService(retrofit: Retrofit): MatmService =
        retrofit.create(MatmService::class.java)

}