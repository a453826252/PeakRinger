package com.zaz.support.dercoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.zaz.support.utils.dp

class VerticalDecoration(color:Int,val spaceDp:Float): ItemDecoration() {
    private val mPaint = Paint().apply {
        setColor(color)
    }
    private var top = 0f
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        top = spaceDp.dp
        outRect.top = top.toInt()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(view)
            //第一个ItemView不需要绘制
            if (index == 0) {
                continue
            }

            val dividerTop: Float = view.top - top
            val dividerLeft = parent.paddingLeft.toFloat()
            val dividerBottom = view.top.toFloat()
            val dividerRight = (parent.width - parent.paddingRight).toFloat()

            c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, mPaint)
        }
    }
}