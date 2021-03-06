package com.lightone.lighthouse.kotlin.util

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.lightone.lighthouse.kotlin.src.home.adapter.SectorHolderPage
import com.lightone.lighthouse.kotlin.src.scrap.adapter.ScrapHolderPage
import com.lightone.lighthouse.kotlin.src.suggest_detail.adapter.SuggestSectorHolderPage
import kotlinx.android.synthetic.main.sectors_item.view.*
import kotlin.math.max
import kotlin.math.min

class AddScrapSuggestTouchCallback() : ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        getDefaultUIUtil().clearView(getView(viewHolder))
        previousPosition = viewHolder.absoluteAdapterPosition
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.absoluteAdapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        // ?????? View??? ?????????????????? ?????? ???????????? -clamp ?????? swipe??? isClamped true??? ?????? ????????? false??? ??????
        setTag(viewHolder, !isClamped && currentDx <= clamp)
        return 2f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean,
    ) : Float {
        // LEFT ???????????? swipe ??????
        val min: Float = 0f
        // View??? ?????? ????????? ??????????????? swipe ?????????
        val max: Float = view.width.toFloat()/2

        val x = if (isClamped) {
            // View??? ??????????????? ??? swipe?????? ?????? ??????
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX + clamp
        }

        return min(max(min, x), max)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped??? view??? tag??? ??????
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped??? view??? tag??? ??????
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder) : View {
        return (viewHolder as SuggestSectorHolderPage).itemView.sector_item
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    fun removePreviousClamp(recyclerView: RecyclerView) {
//        if (currentPosition == previousPosition)
//            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    fun removeNowClamp(recyclerView: RecyclerView) {
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it)
            getView(viewHolder!!).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }
}