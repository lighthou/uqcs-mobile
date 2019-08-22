package com.uqcs.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.uqcs.mobile.common.ServiceGenerator
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.common.Webserver
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*
import okhttp3.ResponseBody
import retrofit2.Call


class SignInActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN_RC = 9999
    private lateinit var webserver: Webserver
    private lateinit var usernameText : String
    private lateinit var passwordText : String
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        progress_overlay.loading_text.text = getString(R.string.authenticating)

        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account?.email == "uqcomputing@gmail.com") {
            signIn()
        }

        google_sign_in_button.setSize(SignInButton.SIZE_WIDE)
        google_sign_in_button.setOnClickListener { v ->
            when (v.id) {
                R.id.google_sign_in_button -> {
                    Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)
                    val signInIntent = mGoogleSignInClient.signInIntent
                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC)
                }
            }
        }

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
//                    signIn()
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

    private fun signIn() {
        Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
        startActivity(MainActivity.getIntent(this@SignInActivity, "", ""))
    }

    private fun registerCredentials() {
        usernameText = username.text.toString()
        passwordText = password.text.toString()
        webserver = ServiceGenerator.createService(Webserver::class.java, usernameText, passwordText)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_RC) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            if (account?.email == "uqcomputing@gmail.com") {
                //finish sign in.
                signIn()
            } else {
                mGoogleSignInClient.signOut()
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Invalid Login", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("account", "hi")

        }

    }

}
