package jacksondeng.revoluttest.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.RevolutApplication
import javax.inject.Singleton

@Module
class RevolutAppModule {
    @Singleton
    @Provides
    internal fun provideContext(application: RevolutApplication): Context {
        return application
    }
}