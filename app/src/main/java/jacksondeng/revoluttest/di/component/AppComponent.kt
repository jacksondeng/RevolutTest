package jacksondeng.revoluttest.di.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import jacksondeng.revoluttest.RevolutApplication
import jacksondeng.revoluttest.di.builder.ActivityBuilder


@Component(modules = [AndroidSupportInjectionModule::class, ActivityBuilder::class])
interface AppComponent : AndroidInjector<RevolutApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<RevolutApplication>()
}