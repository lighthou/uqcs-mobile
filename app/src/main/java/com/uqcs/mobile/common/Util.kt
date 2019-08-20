package com.uqcs.mobile.common

import java.text.SimpleDateFormat
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.loading_overlay.view.*
import android.graphics.Bitmap
import android.util.Base64
import com.uqcs.mobile.R
import java.io.ByteArrayOutputStream
import java.util.*


class Util {
    companion object {
        fun monthNumberToName(monthNumber : Int): String {
            val calendar : Calendar = Calendar.getInstance()
            val monthDate = SimpleDateFormat("MMMM", Locale.UK)
            calendar.set(Calendar.MONTH, monthNumber)
            return monthDate.format(calendar.time)
        }

        /**
         * @param view         View to animate
         * @param toVisibility Visibility at the end of animation
         * @param toAlpha      Alpha at the end of animation
         * @param duration     Animation duration in ms
         */
        fun animateView(context: Context, view: View, toVisibility: Int, toAlpha: Float, duration: Int) {
            val show = toVisibility == View.VISIBLE
            if (show) {
                view.alpha = 0f
            }
            view.visibility = View.VISIBLE
            view.loading_image.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.rotate_indefinitely
            ))
            view.animate()
                .setDuration(duration.toLong())
                .alpha(if (show) toAlpha else 0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = toVisibility
                    }
                })
        }

        fun closeKeyboardIfPresent(context : Context, focus : View?) {
            focus?.let {
                try {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                } catch (e: Exception) {
                    // Failed to hide keyboard. Doesn't matter.
                }
            }

        }
    }

}
