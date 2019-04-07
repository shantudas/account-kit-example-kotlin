package com.snipex.shantu.accountkitexamplektx

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.facebook.accountkit.AccountKitError
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.ui.SkinManager
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LogInActivity : AppCompatActivity() {

    companion object {
        private const val APP_REQUEST_CODE = 1001
        private const val TAG = "LogInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        printKeyHash()

        btnLogIn.setOnClickListener {
            handlePhoneLogin()
        }
    }

    private fun handlePhoneLogin() {
        val intent = Intent(this@LogInActivity, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
            LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN
        ) // or .ResponseType.TOKEN and .ResponseType.CODE
        // ... perform additional configuration ...

        // to change the color of account kit skin
        val uiManager = SkinManager(
            SkinManager.Skin.CLASSIC,
            ContextCompat.getColor(this@LogInActivity, android.R.color.holo_blue_dark)
        )
        configurationBuilder.setUIManager(uiManager)
        configurationBuilder.setDefaultCountryCode("BD")
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build())
        startActivityForResult(intent, APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            val loginResult = data!!.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            val toastMessage: String

            if (loginResult.error != null) {
                toastMessage = loginResult.error!!.errorType.message
                showErrorActivity(loginResult.error!!)
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled"
            } else {
                if (loginResult.accessToken != null) {
                    toastMessage = "Success:" + loginResult.accessToken!!.accountId
                } else {
                    toastMessage = String.format("Success:%s...", loginResult.authorizationCode!!.substring(0, 10))
                }
                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                goToMyLoggedInActivity()
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToMyLoggedInActivity() {
        Log.d(TAG, "goToMyLoggedInActivity: called")
    }

    private fun showErrorActivity(error: AccountKitError) {
        Log.d(TAG, "showErrorActivity: $error$")
    }

    /**
     * get key hash for facebook account kit settings
     * This key hash will be saved in account kit's android platform's debug key hash
     *
     * @param @null
     */
    private fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo("com.snipex.shantu.accountkitexamplektx", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }
}
