package jacksondeng.revoluttest

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import jacksondeng.revoluttest.di.component.DaggerAppComponent


class RevolutApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
    }

}