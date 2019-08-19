package com.uqcs.mobile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.uqcs.mobile.common.ServiceGenerator
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.common.Webserver
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback


class SignInActivity : AppCompatActivity() {

    private lateinit var webserver: Webserver
    private lateinit var usernameText : String
    private lateinit var passwordText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        progress_overlay.loading_text.text = getString(R.string.authenticating)

        btn_login.setOnClickListener {
            Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)
            registerCredentials()
            Util.closeKeyboardIfPresent(this@SignInActivity, currentFocus)
            checkSignIn()
        }
    }

    private fun checkSignIn() {
        val loginRequest : Call<ResponseBody> = webserver.signIn()

        startActivity(MainActivity.getIntent(this@SignInActivity, usernameText, passwordText))
//        loginRequest.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
//                Util.animateView(this@SignInActivity , progress_overlay, View.GONE, 0.8f, 200)
//                //showLoading.value = false
//                if (response.code() == 200) {
//                    startActivity(MainActivity.getIntent(this@SignInActivity, usernameText, passwordText))
//                } else {
//                    Toast.makeText(this@SignInActivity, "Invalid Credentials", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Util.animateView(this@SignInActivity, progress_overlay, View.GONE, 0.8f, 200)
//                Toast.makeText(this@SignInActivity, "Something went wrong, Try again", Toast.LENGTH_LONG).show()
//                //showLoading.value = false
//            }
//
//        })
    }

    private fun registerCredentials() {
        usernameText = username.text.toString()
        passwordText = password.text.toString()
        webserver = ServiceGenerator.createService(Webserver::class.java, usernameText, passwordText)
    }

}
