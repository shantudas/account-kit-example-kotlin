package com.snipex.shantu.accountkitexamplektx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.facebook.accountkit.Account
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.AccountKitError
import com.facebook.accountkit.AccountKitCallback
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getLoggedInAccount()

        btnLogOut.setOnClickListener {
            AccountKit.logOut()
            val intent = Intent(this@HomeActivity, LogInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getLoggedInAccount() {
        AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
            override fun onSuccess(account: Account) {
                // Get phone number
                val phoneNumber = account.getPhoneNumber()
                if (phoneNumber != null) {
                    val phoneNumberString = "You are logged in with $phoneNumber"
                    labelLoggedInAccount.text = phoneNumberString
                }
            }

            override fun onError(error: AccountKitError) {
                Log.d(TAG, "goToMyLoggedInActivity: called")
            }
        })
    }
}
