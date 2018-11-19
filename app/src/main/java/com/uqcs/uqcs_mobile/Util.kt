package com.uqcs.uqcs_mobile

import java.text.SimpleDateFormat
import java.util.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v4.view.ViewCompat.animate
import android.support.v4.view.ViewCompat.setAlpha
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.loading_overlay.view.*


class Util {
    companion object {
        fun monthNumberToName(monthNumber : Int): String {
            var calendar : Calendar = Calendar.getInstance()
            var month_date : SimpleDateFormat = SimpleDateFormat("MMMM")
            calendar.set(Calendar.MONTH, monthNumber)
            return month_date.format(calendar.time)
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
            view.loading_image.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely))
            view.animate()
                .setDuration(duration.toLong())
                .alpha(if (show) toAlpha else 0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = toVisibility
                    }
                })
        }
    }

}
