package com.ajumma.five

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
            setPadding(0, dp(8), dp(16), dp(8))
            setOnClickListener { finish() }
        })

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
        info.addView(row("요금", e.fee.ifBlank { "원문에서 확인해 주세요" }))
        box.addView(info, matchWrap(dp(16)))

        if (e.description.isNotBlank()) {
            box.addView(text(e.description, 18f), matchWrap(dp(18)))
        }

        if (e.url.isNotBlank()) {
            box.addView(bigButton("원문 보기", filled = true) { open(Uri.parse(e.url)) }, matchWrap(dp(24)))
        }
        if (e.place.isNotBlank()) {
            box.addView(bigButton("지도에서 보기", filled = false) { openMap(e.place) }, matchWrap(dp(10)))
        }

        setContentView(ScrollView(this).apply {
            setBackgroundColor(Colors.BG)
            addView(box)
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

    private fun open(uri: Uri): Boolean = try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

    /** 네이버 지도 앱 → 다른 지도 앱 → 네이버 지도 웹 순서로 열어 봅니다. */
    private fun openMap(place: String) {
        val q = Uri.encode(place)
        if (open(Uri.parse("nmap://search?query=$q&appname=$packageName"))) return
        if (open(Uri.parse("geo:0,0?q=$q"))) return
        if (open(Uri.parse("https://map.naver.com/p/search/$q"))) return
        Toast.makeText(this, "지도를 열 수 없어요", Toast.LENGTH_SHORT).show()
    }
}
