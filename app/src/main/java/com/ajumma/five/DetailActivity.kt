package com.ajumma.five

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import org.json.JSONObject

class DetailActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Colors.MAIN

        val e = Event.fromJson(JSONObject(intent.getStringExtra("event") ?: run { finish(); return }))

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(32))
        }

        box.addView(text("←  목록으로", 18f, Colors.MAIN, bold = true).apply {
            gravity = Gravity.CENTER_VERTICAL
            minHeight = dp(48)
            setPadding(0, dp(8), dp(16), dp(8))
            setOnClickListener { finish() }
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        box.addView(text("${e.category} · ${e.region}", 15f, Colors.MAIN, bold = true).apply {
            setPadding(dp(12), dp(4), dp(12), dp(4))
            background = rounded(Colors.MAIN_LIGHT, dp(12))
        }, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply { topMargin = dp(12) })

        box.addView(text(e.title, 25f, bold = true), matchWrap(dp(10)))

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(8), dp(18), dp(16))
            background = rounded(Colors.CARD, dp(16), dp(1), Colors.LINE)
        }
        info.addView(row("날짜", e.dateText()))
        info.addView(row("시간", e.time.ifBlank { "원문에서 확인해 주세요" }))
        info.addView(row("장소", e.place.ifBlank { "원문에서 확인해 주세요" }))
        info.addView(row("주소", e.address.ifBlank { "정확한 주소는 원문에서 확인해 주세요" }))
        info.addView(row("요금", e.fee.ifBlank { "원문에서 확인해 주세요" }))
        box.addView(info, matchWrap(dp(16)))

        if (e.address.isNotBlank()) {
            box.addView(bigButton("주소 복사하기", filled = false) { copyAddress(e.address) }, matchWrap(dp(10)))
        }

        if (e.description.isNotBlank()) {
            box.addView(text(e.description, 18f), matchWrap(dp(18)))
        }

        if (e.url.isNotBlank()) {
            box.addView(bigButton("원문 보기", filled = true) { open(Uri.parse(e.url)) }, matchWrap(dp(24)))
        }

        setContentView(ScrollView(this).apply {
            setBackgroundColor(Colors.BG)
            addView(MaxWidthFrame(this@DetailActivity).apply { addView(box) }, FrameLayout.LayoutParams(-1, -2, Gravity.CENTER_HORIZONTAL))
        })
    }

    private fun row(label: String, value: String) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(0, dp(8), 0, 0)
        addView(text(label, 17f, Colors.SUB, bold = true), LinearLayout.LayoutParams(
            dp(56), LinearLayout.LayoutParams.WRAP_CONTENT,
        ))
        addView(text(value, 18f), LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f,
        ))
    }

    private fun open(uri: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "원문을 열 수 없어요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyAddress(address: String) {
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText("주소", address))
        Toast.makeText(this, "주소를 복사했어요. 지도 앱에 붙여넣어 보세요.", Toast.LENGTH_LONG).show()
    }
}
