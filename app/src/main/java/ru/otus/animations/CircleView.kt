package ru.otus.animations
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.sqrt


class CircleView(context: Context) : View(context) {
    private val paint = Paint()
    var centerX: Float = 0f
    var centerX2: Float = 0f
    var centerY2: Float = 0f
    var endX: Float = 0f
    var radius: Float = 0f
    var radius2: Float = 0f
    var alpha2: Int = 255
    val animationDuration: Long = 2000
    val animationCount = 2

    private var animatorSet: AnimatorSet? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 4f
        endX = w - centerX
        centerX2 = w * 3 / 4f
        centerY2 = h / 2f
        radius = w.coerceAtMost(h) / 4.5f
        radius2 = w.coerceAtMost(h) / 4.5f

        createAnimatorSet()
    }

    private fun createAnimatorSet() {

        val centerXAnimator1 = ObjectAnimator.ofFloat(this, "centerX", centerX, endX).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
        }

        val centerX2Animator1 = ObjectAnimator.ofFloat(this, "centerX2", centerX2, centerX).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
        }

        val radiusAnimator = ValueAnimator.ofFloat(radius, radius * 1.1f, radius).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                radius = animation.animatedValue as Float
                invalidate()
            }
        }

        val radius2Animator = ValueAnimator.ofFloat(radius2, radius2 * 0.7f, radius2).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                radius2 = animation.animatedValue as Float
                invalidate()
            }
        }

        val centerY2Animator = ValueAnimator.ofFloat(centerY2, centerY2 - radius2 * 0.3f, centerY2).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                centerY2 = animation.animatedValue as Float
                invalidate()
            }
        }

        val alpha2Animator = ValueAnimator.ofInt(255, 100, 50,0, 0, 30,255).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                alpha2 = animation.animatedValue as Int
                invalidate()
            }
        }

        val centerXAnimator2 = ObjectAnimator.ofFloat(this, "centerX", endX, centerX).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
            addUpdateListener {
                invalidate()
            }
        }

        val centerX2Animator2 = ObjectAnimator.ofFloat(this, "centerX2", centerX, centerX2).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
            addUpdateListener {
                invalidate()
            }
        }


        val firstSet = AnimatorSet().apply {
            playTogether(centerXAnimator1, radiusAnimator, centerX2Animator1, radius2Animator, centerY2Animator, alpha2Animator)
        }

        val secondSet = AnimatorSet().apply {
            playTogether(centerXAnimator2, centerX2Animator2)
        }

        animatorSet = AnimatorSet().apply {
            playSequentially(firstSet, secondSet)
        }
        animatorSet?.addListener(object : AnimatorListenerAdapter() {
            var count = 0
            override fun onAnimationEnd(animation: Animator) {
                if (count < animationCount - 1) {
                    animation.start()
                    count++
                } else
                    count = 0
            }
        })
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLUE
        canvas.drawCircle(centerX, height / 2f, radius, paint)
        paint.color = Color.MAGENTA
        paint.alpha = alpha2
        canvas.drawCircle(centerX2, centerY2, radius2, paint)
        paint.alpha = 255
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y
            if (isInsideCircle(touchX, touchY)) {
                animatorSet?.start()
            }
        }
        return true
    }

    private fun isInsideCircle(x: Float, y: Float): Boolean {
        val distance1 = sqrt((x - centerX) * (x - centerX) + (y - height / 2f) * (y - height / 2f))
        val distance2 = sqrt((x - centerX2) * (x - centerX2) + (y - centerY2) * (y - centerY2))
        return distance1 <= radius || distance2 <= radius2
    }
}

