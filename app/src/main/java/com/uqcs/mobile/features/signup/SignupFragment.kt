package com.uqcs.mobile.features.signup

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import com.uqcs.mobile.common.CameraPermissionHelper
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment(), AuthenticatedFragment {

    private lateinit var viewModel : SignupViewModel

    companion object {
        fun newInstance(): SignupFragment {
            return SignupFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
    }

    override fun onStop() {
        super.onStop()
        camera.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (!CameraPermissionHelper.hasCameraPermission(this.activity as Activity)) {
            CameraPermissionHelper.requestCameraPermission(this.activity as Activity)
            return
        }
        camera.onResume()
    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun registerServerCredentials() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

}
