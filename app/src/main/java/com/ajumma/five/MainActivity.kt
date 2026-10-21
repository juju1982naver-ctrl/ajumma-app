package com.ajumma.five

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
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

        // 맨 위: 앱 이름 + 새로고침 (배경은 화면 끝까지, 내용은 가운데로 모음)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Colors.MAIN)
        }
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }
        headerRow.addView(mascot(), LinearLayout.LayoutParams(dp(48), dp(48)).apply { rightMargin = dp(8) })
        val titleBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleBox.addView(text("수빈서아MOM 나들이", headerSp(20f), 0xFFFFFFFF.toInt(), bold = true))
        updatedText = text("", headerSp(14f), 0xFFFFEDE7.toInt())
        titleBox.addView(updatedText)
        headerRow.addView(titleBox, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        headerRow.addView(text("새로고침", headerSp(16f), Colors.MAIN, bold = true).apply {
            gravity = Gravity.CENTER
            minHeight = dp(44)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = rounded(0xFFFFFFFF.toInt(), dp(22))
            setOnClickListener { refresh() }
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = dp(8)
        })
        header.addView(MaxWidthFrame(this).apply { addView(headerRow) }, centerLp())
        root.addView(header)

        // 카테고리 탭과 지역 버튼은 목록을 스크롤해도 위에 고정 (큰 글씨에서도 1.3배까지만 커짐)
        val fixed = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Colors.BG)
        }
        tabRow = LinearLayout(this).apply { setPadding(dp(12), dp(10), dp(12), dp(2)) }
        fixed.addView(tabRow)
        regionRow = LinearLayout(this).apply { setPadding(dp(12), dp(6), dp(12), dp(6)) }
        fixed.addView(regionRow)
        root.addView(MaxWidthFrame(this).apply { addView(fixed) }, centerLp())

        // 안내 문구와 행사 목록만 스크롤
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        regionHint = text("", 14f, Colors.SUB).apply { setPadding(dp(20), dp(2), dp(20), dp(4)) }
        body.addView(regionHint)

        statusText = text("", 15f, Colors.SUB).apply {
            setPadding(dp(20), 0, dp(20), dp(4))
            visibility = View.GONE
        }
        body.addView(statusText)

        listBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(4), dp(14), dp(24))
        }
        body.addView(listBox)
        scroll = ScrollView(this).apply {
            addView(MaxWidthFrame(this@MainActivity).apply { addView(body) }, FrameLayout.LayoutParams(-1, -2, Gravity.CENTER_HORIZONTAL))
        }
        root.addView(scroll, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        return root
    }

    /** 맨 위 띠와 고정된 버튼은 글씨가 아무리 커져도 1.3배까지만 커지게 함 */
    private fun headerSp(sp: Float): Float {
        val fs = resources.configuration.fontScale
        return if (fs > 1.3f) sp * 1.3f / fs else sp
    }

    /** 한 줄에 쓸 수 있는 가로 폭(dp): 화면 폭과 최대 폭 중 작은 쪽에서 좌우 여백 뺀 값 */
    private fun availDp(): Int {
        val screen = (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt()
        return minOf(screen, 640) - 24
    }

    private fun refresh() {
        if (!networkEnabled) return
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
        fillChips(tabRow, CATEGORIES, headerSp(17f), 6, availDp()) { c ->
            chip(c, c == category, headerSp(17f)) { select(newCategory = c) }
        }
        // 지역은 한 줄로 두고, 화면이 좁으면 옆으로 밀어서 봄
        regionRow.removeAllViews()
        val regionChips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        REGIONS.forEachIndexed { i, r ->
            regionChips.addView(
                chip(r, r == region, headerSp(17f)) { select(newRegion = if (r == region) null else r) },
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    if (i > 0) leftMargin = dp(6)
                },
            )
        }
        regionRow.addView(HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            addView(regionChips)
        })
        regionHint.text = if (region == null) "전체 지역을 보고 있어요. 지역을 누르면 그 지역만 보여요."
        else "$region 행사만 보고 있어요. 한 번 더 누르면 전체가 보여요."

        listBox.removeAllViews()
        val today = todayProvider()
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
