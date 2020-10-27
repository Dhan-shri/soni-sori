package org.merakilearn

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.merakilearn.core.di.coreModules
import org.merakilearn.di.appModules
import org.navgurukul.chat.core.ChatInitializer
import org.navgurukul.chat.core.di.chatModules
import org.navgurukul.commonui.di.commonUIModules
import org.navgurukul.learn.di.learnModules
import org.navgurukul.playground.di.playgroundModules
import timber.log.Timber

class MerakiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@MerakiApp)
            androidLogger()
            modules(appModules + chatModules + learnModules + playgroundModules + commonUIModules + coreModules)
        }

        ChatInitializer.initialise(this)
        subscribeToDefaultTopic()
    }

    private fun subscribeToDefaultTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID)
            .addOnCompleteListener { task ->
                Timber.d("subscribeToDefaultTopic: ")
            }
    }
}