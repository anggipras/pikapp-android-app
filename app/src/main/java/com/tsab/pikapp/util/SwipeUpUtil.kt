package com.tsab.pikapp.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import id.rizmaulana.floatingslideupsheet.R
import id.rizmaulana.floatingslideupsheet.helper.orZero
import id.rizmaulana.floatingslideupsheet.helper.setColorAlpha
import id.rizmaulana.floatingslideupsheet.helper.toDp
import id.rizmaulana.floatingslideupsheet.helper.toPx
import id.rizmaulana.floatingslideupsheet.view.FloatingSlideUpBuilder
import id.rizmaulana.floatingslideupsheet.view.FloatingSlideUpLayout

/**
 * rizmaulana 21/07/20.
 */

class SwipeUpUtil(private val context: Context, private val viewGroup: ViewGroup) {
    private var floatingMenuView: View? = null
    private var panelView: View? = null
    private var menuRadius = 0f
    private var menuBackgroundColor = R.color.white
    public var status: Boolean = false

    private val rootView: ViewGroup by lazy {
        ViewGroup.inflate(context, R.layout.layout_floating_slideup, null) as CoordinatorLayout
    }

    private val panelExpandable by lazy {
        rootView.findViewById<FrameLayout>(R.id.slide_layout_expand)
    }

    private val nestedContent by lazy {
        rootView.findViewById<FrameLayout>(R.id.slide_layout_expand)
                .findViewById<NestedScrollView>(R.id.slide_nested_content)
    }

    private val backgroundRoundedDrawable by lazy {
        GradientDrawable()
    }

    private val parentLayout by lazy {
        panelExpandable.parent.parent as FloatingSlideUpLayout
    }

    private val defaultPaddingBottom by lazy {
        parentLayout.paddingBottom
    }

    private val defaultPaddingLeft by lazy {
        panelExpandable.paddingLeft
    }

    private val defaultPaddingRight by lazy {
        panelExpandable.paddingRight
    }

    private val framePanel by lazy {
        (rootView.findViewById<FrameLayout>(R.id.slide_layout_expand)
                .getChildAt(0) as NestedScrollView).findViewById<FrameLayout>(R.id.slide_frame_panel)
    }


    private val bottomSheetBehaviour by lazy {
        BottomSheetBehavior.from(panelExpandable).apply {
            setBottomSheetCallback(bottomSheetBehaviorCallback)

            halfExpandedRatio = 0.999999f
            floatingMenuView?.post {
                peekHeight = floatingMenuView?.height?.plus(12.toDp(context)).orZero()
            }
            isFitToContents = false
        }
    }

    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            /**.0 = Hide
             * 1.0 = Expanded
             * range = 1.0 to 2.0
             * pointPercent between 0 - 100
             * */

            val pointPercent = slideOffset.times(100)
            val alpha = pointPercent.div(2).toInt()
            listenSlideChangeColor(alpha)
            listenSlidePadding(pointPercent.toInt())
            listenSlideAlphaContent(pointPercent.toInt())
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }


    fun setFloatingMenuRadiusInDp(radius: Int): SwipeUpUtil {
        menuRadius = radius.toPx(context).toFloat()
        return this
    }

    fun setFloatingMenuColor(@ColorInt color: Int): SwipeUpUtil {
        menuBackgroundColor = color
        return this
    }

    fun setFloatingMenu(view: View): SwipeUpUtil {
        floatingMenuView = view
        return this
    }

    fun setPanel(view: View): SwipeUpUtil {
        panelView = view
        return this
    }

    fun build(): SwipeUpUtil {
        viewGroup.post {
            arrangeView()
            setMenuBackground()
            setBottomSheetBehaviour()
        }
        return this
    }

    private fun arrangeView() {
        val panelLayoutParams = panelView?.layoutParams
        val floatingMenuLayoutParams = floatingMenuView?.layoutParams

        viewGroup.removeView(floatingMenuView)
        viewGroup.removeView(panelView)

        val frameFloatingMenu =
                panelExpandable.findViewById<FrameLayout>(R.id.slide_frame_floating_menu)

        frameFloatingMenu.addView(floatingMenuView)
        framePanel.addView(panelView)


        viewGroup.addView(rootView)

        panelView?.layoutParams = panelLayoutParams
        floatingMenuView?.layoutParams = floatingMenuLayoutParams

        panelExpandable.setPadding(
                viewGroup.paddingLeft,
                panelExpandable.paddingTop,
                viewGroup.paddingRight,
                panelExpandable.paddingBottom
        )

        viewGroup.setPadding(
                0,
                viewGroup.paddingTop,
                0,
                viewGroup.paddingBottom
        )

        rootView.layoutParams = rootView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun setMenuBackground() {
        floatingMenuView?.setBackgroundDrawable(backgroundRoundedDrawable)
        backgroundRoundedDrawable.setColor(context.resources.getColor(menuBackgroundColor))
        backgroundRoundedDrawable.cornerRadius = menuRadius.toFloat()
        backgroundRoundedDrawable.setStroke(4, Color.parseColor("#10000000"))
    }

    private fun setBottomSheetBehaviour() {
        collapseBottomSheet()
    }

    private fun listenSlideChangeColor(alpha: Int) {
        if (alpha > 0) {
            parentLayout.setBackgroundColor(Color.parseColor(setColorAlpha(alpha)))
        }
    }

    private fun listenSlidePadding(point: Int) {
        floatingMenuView?.post {

            /**Value of point
             * 0 Hide - 100 Expanded*/

            val dynamicLeftPadding =
                    defaultPaddingLeft.minus(((defaultPaddingLeft.toFloat().div(100)).times(point)))
                            .toInt()
            val dynamicRightPadding =
                    defaultPaddingRight.minus(((defaultPaddingRight.toFloat().div(100)).times(point)))
                            .toInt()
            val dynamicBottomPadding =
                    defaultPaddingBottom.minus(((defaultPaddingBottom.toFloat().div(100)).times(point)))
                            .toInt()


            panelExpandable.setPadding(dynamicLeftPadding, 0, dynamicRightPadding, 0)
            parentLayout.setPadding(
                    parentLayout.paddingLeft,
                    parentLayout.paddingTop,
                    parentLayout.paddingRight,
                    dynamicBottomPadding
            )
        }
    }

    private fun listenSlideAlphaContent(point: Int) {
        val opacity = point.toFloat().div(100)
        framePanel.alpha = opacity

        val cardOpacity = 1f.minus(opacity)
        val twentyPercentOpacity = cardOpacity.minus(0.8f).times(5f)

        floatingMenuView?.alpha = twentyPercentOpacity


        backgroundRoundedDrawable.cornerRadius =
                menuRadius.minus((menuRadius.div(5).times(point)))
        if (point > 4) {
            nestedContent.visibility = View.VISIBLE
            floatingMenuView?.visibility = View.INVISIBLE
            status = true
        } else {
            nestedContent.visibility = View.INVISIBLE
            floatingMenuView?.visibility = View.VISIBLE
            status == false
        }

    }

    fun collapseBottomSheet() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        status = false
    }

    fun collapseBottomSheetStatus(): Boolean {
        return status
    }

    fun expandBottomSheet() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        status = true
    }


}