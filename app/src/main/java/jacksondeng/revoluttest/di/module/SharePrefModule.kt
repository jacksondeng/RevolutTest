package jacksondeng.revoluttest.di.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.RevolutApplication
import jacksondeng.revoluttest.util.PREF_FILE_NAME
import javax.inject.Singleton

@Module
class SharePrefModule {
    @Provides
    fun provideSharedPreference(application: RevolutApplication): SharedPreferences {
        return application.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }
}
