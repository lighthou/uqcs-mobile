package com.uqcs.mobile.features.signup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.camerakit.CameraKitView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.uqcs.mobile.R;

public class SignupFragmentJava extends Fragment {
    private static final int INITIAL_DELAY = 2000;
    private static final int TIME_BETWEEN_PHOTOS = 500;
    private static final double SIZE_OF_FACE_RELATIVE_TO_SCREEN = 0.50;
    private static final double MIN_SIZE_OF_FACE_RELATIVE_TO_SCREEN = 0.35;
    private static final String AUTHOUT_IMAGE_CHECK = "https://deco3801.wisebaldone.com/api/kiosk/login";

    private CameraKitView camera;
    private FaceDetector faceDetector;
    private Bitmap currentImage;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            takeAndSetCurrentImage();
            Face face = faceProcessing();
            if (face != null) {
                Toast.makeText(getContext(), "Face Detected", Toast.LENGTH_LONG).show();
            }
            handler.postDelayed(this, TIME_BETWEEN_PHOTOS);
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        camera.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.onPause();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceDetector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
                .setMode(FaceDetector.FAST_MODE)
                .build();


        handler.postDelayed(runnable, TIME_BETWEEN_PHOTOS);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camera = getActivity().findViewById(R.id.camera);
        camera.setAdjustViewBounds(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    public static SignupFragmentJava newInstance() {
        SignupFragmentJava myFragment = new SignupFragmentJava();
        Bundle args = new Bundle();
        myFragment.setArguments(args);
        return myFragment;
    }

    private void takeAndSetCurrentImage() {
        camera.captureImage((cameraKitView, bytes) -> currentImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    private Face faceProcessing() {
        if (currentImage == null) return null;

        Frame outputFrame = new Frame.Builder().setBitmap(currentImage).build();
        SparseArray<Face> sparseArray = faceDetector.detect(outputFrame);

        return sparseArray.size() == 0 ? null : sparseArray.valueAt(0);
    }
}
