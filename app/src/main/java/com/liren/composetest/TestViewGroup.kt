package com.liren.composetest

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class TestViewGroup(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    constructor(context: Context) : this(context, null)

    init {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                println("onPageScrolled $position $positionOffset")
            }

            override fun onPageSelected(position: Int) {
                println("onPageSelected $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                println("onPageScrollStateChanged $state")
            }

        })
        adapter = TestAdapter()
        overScrollMode = OVER_SCROLL_ALWAYS
        setBackgroundColor(Color.Black.toArgb())
        currentItem = 3
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        println("onTouchEvent ${MotionEvent.actionToString(ev.actionMasked)}")
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        println("onInterceptTouchEvent ${MotionEvent.actionToString(ev.actionMasked)}")
        return super.onInterceptTouchEvent(ev)
    }


    class TestAdapter : PagerAdapter() {
        private val colors = arrayOf(Color.Blue, Color.Gray, Color.Yellow, Color.Cyan)
        override fun getCount(): Int {
            return colors.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            return View(container.context).apply {
//                background = ColorDrawable(colors[position].toArgb())
//
//                container.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            }
            return SubsamplingScaleImageView(container.context).apply {
                setImage(ImageSource.resource(R.drawable.mengdian))
                container.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        println("onLayout")
        super.onLayout(changed, l, t, r, b)
    }
}