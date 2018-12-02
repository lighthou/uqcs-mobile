package com.uqcs.mobile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import android.util.Base64.NO_WRAP
import com.android.volley.toolbox.Volley
import com.uqcs.mobile.Helpers.Util
import kotlinx.android.synthetic.main.loading_overlay.view.*


class SignInActivity : AppCompatActivity() {

    private val SIGN_IN_URL = "http://www.ryankurz.me/sign_in"
    private var requestQueue: RequestQueue? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        requestQueue = Volley.newRequestQueue(this)
        progress_overlay.loading_text.text = "Authenticating..."
    }

    fun login(v: View) {
        Util.animateView(this, progress_overlay, View.VISIBLE, 0.8f, 200)
        requestQueue?.add(createSignInRequest())
    }

    private fun createSignInRequest() : JsonArrayRequest {

        val usernameText = username.text.toString()
        val passwordText = password.text.toString()

        return object :  JsonArrayRequest(
            Method.GET, SIGN_IN_URL, null,
            Response.Listener {
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.putExtra("USERNAME", usernameText)
                homeIntent.putExtra("PASSWORD", passwordText)
                startActivity(homeIntent)

            },
            Response.ErrorListener {
                Log.i("VolleyIssues", it.toString())
                Util.animateView(this, progress_overlay, View.GONE, 0.8f, 200)
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = mutableMapOf<String, String>()
                val userAndPassword = "$usernameText:$passwordText"
                val basicAuth = "Basic " + Base64.encodeToString(userAndPassword.toByteArray(), NO_WRAP)
                params["Authorization"] = basicAuth
                Log.i("auth", basicAuth)
                return params
            }
        }
    }
}