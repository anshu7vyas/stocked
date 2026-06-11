package io.github.anshu7vyas.stocked

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import android.os.Handler
import android.os.Looper

/** Replaced by the SplashScreen API in Phase 3. */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(SPLASH_TIME_OUT_MS) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private companion object {
        const val SPLASH_TIME_OUT_MS = 2_000L
    }
}
