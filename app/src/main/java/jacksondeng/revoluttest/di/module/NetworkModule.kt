package jacksondeng.revoluttest.di.module

import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.di.scope.ActivityScope
import jacksondeng.revoluttest.util.BASE_URL
import jacksondeng.revoluttest.util.TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @ActivityScope
    fun providesRatesApi(retrofit: Retrofit): RatesApi {
        return retrofit.create(RatesApi::class.java)
    }
}