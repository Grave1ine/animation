package ru.otus.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.sqrt

class RippleView(context: Context) : View(context) {
    private val paint = Paint()
    private var rippleCircles = ArrayList<RippleCircle>()
    private val animationDuration: Long = 5000
    private val percentageList = mutableListOf<Float>()


    init {
        for (i in 4 downTo -50 step 1) {
            percentageList.add(i.toFloat() / 10)
        }
        for (percentage in percentageList) {
            rippleCircles.add(RippleCircle(radius = 0f, alpha = (255 * (1 - percentage * 2)).toInt(), radiusEnd = 0f))
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for ((index, circle) in rippleCircles.withIndex()) {
            circle.radius = h * percentageList[index]
            circle.radiusEnd = h / 2f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.CYAN
        for (circle in rippleCircles) {
            paint.alpha = circle.alpha
            canvas.drawCircle(width / 2f, height / 2f, circle.radius, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y
            if (isInsideCircle(touchX, touchY)) {
                val animatorSet = AnimatorSet()
                val animatorList = ArrayList<Animator>()
                for (i in percentageList.indices) {
                    animatorList.add(createAnimator(rippleCircles[i]))
                }
                animatorSet.playTogether(animatorList)
                animatorSet.start()
            }
        }
        return true
    }


    private fun isInsideCircle(x: Float, y: Float): Boolean {
        val distance = sqrt((x - width / 2f) * (x - width / 2f) + (y - height / 2f) * (y - height / 2f))
        return distance <= width * 0.4f
    }

    private fun createAnimator(circle: RippleCircle): Animator {
        return ValueAnimator.ofFloat(circle.radius, circle.radius+circle.radiusEnd).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                circle.radius = animation.animatedValue as Float
                if (circle.radius >= circle.radiusEnd)
                    circle.alpha = 0
                else
                    circle.alpha = (255 - (circle.radius / circle.radiusEnd) * 255).toInt()
                invalidate()
            }
        }
    }
}

class RippleCircle(var radius: Float, var alpha: Int, var radiusEnd: Float)
