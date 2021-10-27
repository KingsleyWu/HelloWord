package com.kingsley.shimeji

import org.solovyev.android.checkout.Billing

import android.app.Application
import com.kingsley.shimeji.data.Helper
import com.kingsley.shimeji.purchases.Encryption
import org.solovyev.android.checkout.Billing.DefaultConfiguration
import javax.annotation.Nonnull


class MyApplication : Application() {

    @get:Nonnull
    @Nonnull
    val billing = Billing(this, object : DefaultConfiguration() {
        @Nonnull
        override fun getPublicKey(): String {
            return Encryption.decrypt(
                "JygscHoEcHcqRgEQDVlaKQhOWGMrMCB0ci9+eilwUiAoe3oscl4jYiswIHNEJ1htBkgAOBZwAgwEQVl7WDItZ3ktcxIDZDxTMEoEIUsOP1QzOQJzZlgeS0dRJCw0BXwrVk9RVyINFmYKFgZzBEAeNVFYfAxDexpqUwopQHwpY3FQSC1KKGEFD2RzKVQEL05UVFZBCBFoDAMgc0ZaRGE4bwZQCFZkAGZ2WGcPESoDRB1JbAVuOCpRBVkXY3MfcDMjHQN7WlcPH1EwCU5/Z11JUzx2PFAOB3UpQm04WCkZVnNHXUNzEU0nUT1wXRlybQZqBxARZQMfeEABVz0DXHsCHUN8UXY7JU5CRzd3TQp2BikJamQhcFEKcR0wVnRjDGlYDFsaGVN8XzQFeD0VBVYqUHRYSXBQZwtKEUgEKQJ2EmccBAJLYC0DDkdCIBcqUURbBV4lGD8YHEBKPmQKGxYgNShqcVlFYRtqHw02RAoraENcQyEYJ0FDLFRVIVdbO1ZUABl4fSlwKyM=",
                "jae23n19h!"
            )
        }
    })

    companion object {
        private lateinit var sInstance: MyApplication
        fun get(): MyApplication {
            return sInstance
        }
    }

    init {
        sInstance = this
    }

    override fun onCreate() {
        super.onCreate()
        Helper.saveActiveTeamMembers(this, arrayListOf<Int>()
            .apply {
            add(1)
            add(2)
        })
    }
}
