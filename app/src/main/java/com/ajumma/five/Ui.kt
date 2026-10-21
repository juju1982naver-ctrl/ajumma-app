package com.ajumma.five

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.graphics.Paint
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.View
import android.widget.TextView

/** 화면 색과 글씨 크기. 크게, 보기 편하게. */
object Colors {
    const val BG = 0xFFFFF8F2.toInt()
    const val CARD = 0xFFFFFFFF.toInt()
    const val MAIN = 0xFFE8664A.toInt()
    const val MAIN_LIGHT = 0xFFFFE3DB.toInt()
    const val TEXT = 0xFF222222.toInt()
    const val SUB = 0xFF5F5F5F.toInt()
    const val LINE = 0xFFEADFD8.toInt()
}

fun Context.dp(v: Int): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()

fun rounded(color: Int, radius: Int, stroke: Int = 0, strokeColor: Int = 0) = GradientDrawable().apply {
    setColor(color)
    cornerRadius = radius.toFloat()
    if (stroke > 0) setStroke(stroke, strokeColor)
}

fun Context.text(
    value: String,
    sizeSp: Float = 18f,
    color: Int = Colors.TEXT,
    bold: Boolean = false,
): TextView = TextView(this).apply {
    text = value
    setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
    setTextColor(color)
    if (bold) typeface = Typeface.DEFAULT_BOLD
    setLineSpacing(0f, 1.15f)
}

/** 눌러서 고르는 둥근 버튼(카테고리 탭, 지역 선택). */
fun Context.chip(label: String, selected: Boolean, sizeSp: Float, onClick: () -> Unit): TextView =
    text(label, sizeSp, if (selected) 0xFFFFFFFF.toInt() else Colors.TEXT, bold = selected).apply {
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(10), dp(12), dp(10))
        minHeight = dp(44)
        background = if (selected) rounded(Colors.MAIN, dp(24))
        else rounded(Colors.CARD, dp(24), dp(1), Colors.LINE)
        setOnClickListener { onClick() }
    }

/** 큰 주황 버튼. */
fun Context.bigButton(label: String, filled: Boolean, onClick: () -> Unit): TextView =
    text(label, 20f, if (filled) 0xFFFFFFFF.toInt() else Colors.MAIN, bold = true).apply {
        gravity = Gravity.CENTER
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = if (filled) rounded(Colors.MAIN, dp(14))
        else rounded(Colors.CARD, dp(14), dp(2), Colors.MAIN)
        setOnClickListener { onClick() }
    }

fun matchWrap(top: Int = 0) = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
).apply { topMargin = top }

/** 넓은 화면(태블릿, 펼친 폴드, 가로)에서 내용이 너무 퍼지지 않게 가운데로 모아 주는 틀. */
class MaxWidthFrame(context: Context, private val maxDp: Int = 640) : FrameLayout(context) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val maxPx = context.dp(maxDp)
        val capped = if (MeasureSpec.getSize(widthSpec) > maxPx)
            MeasureSpec.makeMeasureSpec(maxPx, MeasureSpec.EXACTLY) else widthSpec
        super.onMeasure(capped, heightSpec)
    }
}

fun centerLp(width: Int = LinearLayout.LayoutParams.MATCH_PARENT, height: Int = LinearLayout.LayoutParams.WRAP_CONTENT, weight: Float = 0f) =
    LinearLayout.LayoutParams(width, height, weight).apply { gravity = Gravity.CENTER_HORIZONTAL }

/**
 * 버튼들을 한 줄에 다 들어가면 한 줄로, 안 들어가면(작은 폰, 큰 글씨) 여러 줄로 나눠 놓습니다.
 * 글씨가 두 줄로 꺾이거나 잘리지 않게 하려는 것.
 */
fun Context.fillChips(
    box: LinearLayout, labels: List<String>, sizeSp: Float, gapDp: Int, availDp: Int,
    makeChip: (String) -> TextView,
) {
    box.removeAllViews()
    box.orientation = LinearLayout.VERTICAL
    val paint = Paint().apply {
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeSp, resources.displayMetrics)
        isFakeBoldText = true
    }
    val widest = labels.maxOf { paint.measureText(it) } * 1.1f + dp(2 * 12)
    val avail = dp(availDp).toFloat()
    val gap = dp(gapDp).toFloat()
    var perRow = labels.size
    while (perRow > 1 && perRow * widest + (perRow - 1) * gap > avail) perRow--
    val rows = (labels.size + perRow - 1) / perRow
    val even = (labels.size + rows - 1) / rows
    labels.chunked(even).forEachIndexed { r, group ->
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        group.forEachIndexed { i, label ->
            row.addView(makeChip(label), LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (i > 0) leftMargin = dp(gapDp)
            })
        }
        // 마지막 줄이 짧으면 칸 폭을 맞추려고 빈 칸을 채움
        repeat(even - group.size) {
            row.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f).apply { leftMargin = dp(gapDp) })
        }
        box.addView(row, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply { if (r > 0) topMargin = dp(gapDp) })
    }
}
