package com.zaz.peakringer

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.zaz.support.base.BaseActivity
import com.zaz.support.utils.PRToast
import com.zaz.support.utils.gotoActivity


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val winControl = WindowCompat.getInsetsController(window,window.decorView)
        winControl.hide(WindowInsetsCompat.Type.systemBars())
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_splash)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm))
            .setMessage(getString(R.string.privacy_policy_content))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.accept)){ dialog,whitch->
                gotoActivity(MainActivity::class.java)
            }
            .setNeutralButton(getString(R.string.privacy_policy)){ dialog,whitch->
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(Constant.PRIVACY_PROTOCOL))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    PRToast.show(applicationContext,getString(R.string.install_browser_first))
                }
            }
            .setNegativeButton(getString(R.string.refuse)){ dialog,whitch->
                finish()
            }
            .show()
    }
}