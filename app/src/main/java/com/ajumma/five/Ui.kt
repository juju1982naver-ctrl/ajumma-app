package com.ajumma.five

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
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
        setPadding(dp(14), dp(10), dp(14), dp(10))
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
