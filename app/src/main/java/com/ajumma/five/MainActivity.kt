package com.ajumma.five

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.time.LocalDate

class MainActivity : Activity() {

    private var data: EventList? = null
    private var category = CATEGORIES[0]
    /** 고른 지역. null이면 전체. */
    private var region: String? = null

    private lateinit var updatedText: TextView
    private lateinit var statusText: TextView
    private lateinit var tabRow: LinearLayout
    private lateinit var regionRow: LinearLayout
    private lateinit var regionHint: TextView
    private lateinit var listBox: LinearLayout
    private lateinit var scroll: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Colors.MAIN

        val prefs = getPreferences(MODE_PRIVATE)
        category = prefs.getString("category", category)!!.takeIf { it in CATEGORIES } ?: CATEGORIES[0]
        region = prefs.getString("region", null)?.takeIf { it in REGIONS }

        setContentView(buildScreen())
        showData(EventStore.loadLocal(this))
        refresh()
    }

    private fun buildScreen(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Colors.BG)
        }

        // 맨 위: 앱 이름 + 새로고침
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Colors.MAIN)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        header.addView(mascot(), LinearLayout.LayoutParams(dp(58), dp(58)).apply { rightMargin = dp(8) })
        val titleBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleBox.addView(text("수빈서아MOM 나들이", 22f, 0xFFFFFFFF.toInt(), bold = true))
        updatedText = text("", 15f, 0xFFFFEDE7.toInt())
        titleBox.addView(updatedText)
        header.addView(titleBox, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        header.addView(text("새로고침", 17f, Colors.MAIN, bold = true).apply {
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = rounded(0xFFFFFFFF.toInt(), dp(20))
            setOnClickListener { refresh() }
        })
        root.addView(header)

        // 카테고리 탭 3개
        tabRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(12), dp(14), dp(12), dp(4))
        }
        root.addView(tabRow)

        // 지역 고르기: 누르면 그 지역만, 한 번 더 누르면 전체
        regionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(12), dp(8), dp(12), dp(2))
        }
        root.addView(regionRow)
        regionHint = text("", 14f, Colors.SUB).apply { setPadding(dp(20), dp(2), dp(20), dp(4)) }
        root.addView(regionHint)

        statusText = text("", 15f, Colors.SUB).apply {
            setPadding(dp(20), 0, dp(20), dp(4))
            visibility = View.GONE
        }
        root.addView(statusText)

        listBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(4), dp(14), dp(24))
        }
        scroll = ScrollView(this).apply { addView(listBox) }
        root.addView(scroll, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        return root
    }

    private fun refresh() {
        statusText.visibility = View.VISIBLE
        statusText.text = "새 소식 확인 중…"
        Thread {
            val fresh = EventStore.fetch(this)
            runOnUiThread {
                if (isFinishing) return@runOnUiThread
                if (fresh != null) {
                    statusText.visibility = View.GONE
                    showData(fresh)
                } else {
                    statusText.text = "인터넷이 안 돼서 마지막으로 받은 목록을 보여드려요."
                }
            }
        }.start()
    }

    private fun showData(list: EventList) {
        data = list
        updatedText.text = runCatching {
            val d = LocalDate.parse(list.updated)
            "${d.monthValue}월 ${d.dayOfMonth}일 소식"
        }.getOrDefault("")
        render()
    }

    private fun select(newCategory: String = category, newRegion: String? = region) {
        category = newCategory
        region = newRegion
        getPreferences(MODE_PRIVATE).edit()
            .putString("category", category).putString("region", region).apply()
        render()
        scroll.scrollTo(0, 0)
    }

    private fun render() {
        tabRow.removeAllViews()
        CATEGORIES.forEachIndexed { i, c ->
            tabRow.addView(
                chip(c, c == category, 18f) { select(newCategory = c) },
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    if (i > 0) leftMargin = dp(6)
                },
            )
        }

        regionRow.removeAllViews()
        REGIONS.forEachIndexed { i, r ->
            regionRow.addView(
                chip(r, r == region, 17f) { select(newRegion = if (r == region) null else r) }.apply {
                    setPadding(dp(2), dp(10), dp(2), dp(10))
                },
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    if (i > 0) leftMargin = dp(5)
                },
            )
        }
        regionHint.text = if (region == null) "전체 지역을 보고 있어요. 지역을 누르면 그 지역만 보여요."
        else "$region 행사만 보고 있어요. 한 번 더 누르면 전체가 보여요."

        listBox.removeAllViews()
        val today = LocalDate.now()
        val shown = data?.events.orEmpty()
            .filter { it.category == category }
            .filter { region == null || it.region == region }
            .filter { !it.end.isBefore(today) }
            .sortedWith(compareBy({ it.start }, { it.title }))

        if (shown.isEmpty()) {
            listBox.addView(mascot(), LinearLayout.LayoutParams(dp(120), dp(120)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(40)
            })
            listBox.addView(
                text("이 지역에는 아직 등록된 행사가 없어요.\n다른 지역이나 탭을 눌러 보세요.", 18f, Colors.SUB).apply {
                    gravity = Gravity.CENTER
                    setPadding(dp(16), dp(12), dp(16), dp(48))
                },
                matchWrap(),
            )
            return
        }
        shown.forEach { listBox.addView(card(it), matchWrap(dp(10))) }
    }

    private fun card(e: Event): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(16), dp(18), dp(16))
        background = rounded(Colors.CARD, dp(16), dp(1), Colors.LINE)

        addView(text(e.region, 14f, Colors.MAIN, bold = true).apply {
            setPadding(dp(10), dp(3), dp(10), dp(3))
            background = rounded(Colors.MAIN_LIGHT, dp(10))
        }, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
        ))
        addView(text(e.title, 21f, bold = true), matchWrap(dp(8)))
        addView(text("📅  ${e.dateText()}", 17f, Colors.SUB), matchWrap(dp(8)))
        if (e.place.isNotBlank()) addView(text("📍  ${e.place}", 17f, Colors.SUB), matchWrap(dp(4)))

        setOnClickListener {
            startActivity(Intent(this@MainActivity, DetailActivity::class.java).putExtra("event", e.toJson()))
        }
    }

    /** 수빈·서아 나비 캐릭터 */
    private fun mascot() = ImageView(this).apply {
        setImageResource(R.drawable.mascot)
        scaleType = ImageView.ScaleType.FIT_CENTER
        contentDescription = "풍선 든 여자아이"
    }
}
