package com.uqcs.mobile.features.signup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import com.uqcs.mobile.common.CameraPermissionHelper
import com.uqcs.mobile.common.Util
import kotlinx.android.synthetic.main.fragment_signup.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class SignupFragment : Fragment(), AuthenticatedFragment {

    private val STUDENT_SERIAL_NUMBER_LENGTH = 14
    private lateinit var faceDetector : FaceDetector
    private lateinit var textRecognizer : TextRecognizer
    private lateinit var viewModel : SignupViewModel
    private var handler = Handler(Looper.getMainLooper())

    private var runnable : Runnable = object : Runnable {
        override fun run() {
            camera.captureImage { _, bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {image ->
                    val textFrame = Frame.Builder()
                        .setBitmap(image)
                        .build()

                    val textBlocks : SparseArray<TextBlock> = textRecognizer.detect(textFrame);

                    for (i in 0 until textBlocks.size()) {
                        val value = textBlocks.get(textBlocks.keyAt(i)).value.toString()
                        if (value.length == STUDENT_SERIAL_NUMBER_LENGTH && value.toLongOrNull() != null) {
                            Toast.makeText(context, value, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            handler.postDelayed(this, 500)
        }

    }

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

        textRecognizer = TextRecognizer.Builder(context)
            .build()
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

}
