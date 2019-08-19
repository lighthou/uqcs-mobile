package com.uqcs.mobile.features.signup

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import com.uqcs.mobile.common.CameraPermissionHelper
import kotlinx.android.synthetic.main.fragment_signup.*
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector


class SignupFragment : Fragment(), AuthenticatedFragment {

    var runnable : Runnable = object : Runnable {
        override fun run() {
            camera.captureImage { _, bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                    val outputFrame : Frame = Frame.Builder().setBitmap(it).build()
                    val sparseArray : SparseArray<Face> = faceDetector.detect(outputFrame)

                    if (sparseArray.size() != 0) {
                        val face : Face = sparseArray.valueAt(0)
                        Toast.makeText(context, "Face detected!", Toast.LENGTH_LONG).show()
                    }
                }
            }

            handler.postDelayed(this, 500)
        }

    }
    private lateinit var faceDetector : FaceDetector
    private lateinit var viewModel : SignupViewModel
    private var handler = Handler(Looper.getMainLooper())

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
        handler.postDelayed(runnable, 500)
    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        faceDetector = FaceDetector.Builder(context)
            .setTrackingEnabled(true)
            .setProminentFaceOnly(true)
            .setMode(FaceDetector.FAST_MODE)
            .build()
    }

    override fun registerServerCredentials() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

}
