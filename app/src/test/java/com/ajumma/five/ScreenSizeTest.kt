package com.ajumma.five

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate

/** 여러 화면 크기를 흉내 내서 그림으로 찍고, 깨지는 곳을 찾아냅니다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ScreenSizeTest {

    private class Dev(val name: String, val w: Int, val h: Int)

    private val devs = listOf(
        Dev("작은폰 320x568", 320, 568),
        Dev("보통폰 360x800", 360, 800),
        Dev("큰폰 412x915", 412, 915),
        Dev("폴드 접음 344x884", 344, 884),
        Dev("폴드 펼침 673x841", 673, 841),
        Dev("태블릿 800x1280", 800, 1280),
        Dev("폰 가로 800x360", 800, 360),
    )
    private val scales = listOf(1.0f, 1.3f, 2.0f)

    private val outDir = File(System.getProperty("shots.dir") ?: "build/ui-shots").apply { mkdirs() }
    private val violations = mutableListOf<String>()
    private val report = StringBuilder()

    private val koFont: Typeface? = runCatching {
        val p = System.getenv("KO_FONT") ?: "/usr/share/fonts/truetype/nanum/NanumGothic.ttf"
        if (File(p).exists()) Typeface.createFromFile(p) else null
    }.getOrNull()

    private fun applyFont(v: View) {
        if (v is TextView && koFont != null) {
            val bold = v.typeface?.isBold == true
            v.typeface = Typeface.create(koFont, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
        if (v is ViewGroup) for (i in 0 until v.childCount) applyFont(v.getChildAt(i))
    }

    private fun density() = RuntimeEnvironment.getApplication().resources.displayMetrics.density

    /** 화면 하나를 찍는다. 같은 화면을 스크롤 없이 위쪽만. */
    private fun shot(label: String, d: Dev, fs: Float, make: () -> Activity): Bitmap {
        RuntimeEnvironment.setQualifiers("w${d.w}dp-h${d.h}dp-xhdpi")
        RuntimeEnvironment.setFontScale(fs)
        val act = make()
        val dens = density()
        val wpx = (d.w * dens).toInt()
        val hpx = (d.h * dens).toInt()
        val root = act.window.decorView
        applyFont(root)
        fun layout() {
            root.measure(
                View.MeasureSpec.makeMeasureSpec(wpx, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(hpx, View.MeasureSpec.EXACTLY),
            )
            root.layout(0, 0, wpx, hpx)
        }
        layout(); layout()
        check(label, d, fs, root, wpx, dens)
        val bmp = Bitmap.createBitmap(wpx, hpx, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bmp))
        return bmp
    }

    private fun check(label: String, d: Dev, fs: Float, root: View, wpx: Int, dens: Float) {
        val tag = "[${d.name} 글자x$fs $label]"
        fun walk(v: View, inH: Boolean = false) {
            val loc = IntArray(2)
            v.getLocationInWindow(loc)
            if (v.visibility == View.VISIBLE && v.width > 0) {
                if (!inH && (loc[0] + v.width > wpx + 1 || loc[0] < -1)) violations += "$tag 가로로 넘침: ${desc(v)}"
                if (v is TextView) {
                    val t = v.text.toString()
                    val lay = v.layout
                    if (lay != null) {
                        for (i in 0 until lay.lineCount) if (lay.getEllipsisCount(i) > 0) {
                            violations += "$tag 글씨가 잘림: '$t'"; break
                        }
                        val short = t.length <= 8 && !t.contains("\n")
                        if (short && v.isClickable && v.lineCount > 1) violations += "$tag 짧은 글씨가 두 줄로 꺾임: '$t'"
                        if (v.isClickable && v.height / dens < 40f) violations += "$tag 누르는 곳이 너무 낮음(${(v.height / dens).toInt()}dp): '$t'"
                    }
                    if (v.isClickable && v.width / dens > 700f) violations += "$tag 버튼이 너무 넓음(${(v.width / dens).toInt()}dp): '${t.take(10)}'"
                }
            }
            if (v is ViewGroup) for (i in 0 until v.childCount) walk(v.getChildAt(i), inH || v is android.widget.HorizontalScrollView)
        }
        walk(root)
    }

    private fun desc(v: View) = if (v is TextView) "'${v.text.toString().take(12)}'" else v.javaClass.simpleName

    private fun main(): Activity =
        Robolectric.buildActivity(MainActivity::class.java).setup().get()

    private fun detail(e: Event): Activity {
        val i = Intent(RuntimeEnvironment.getApplication(), DetailActivity::class.java).putExtra("event", e.toJson())
        return Robolectric.buildActivity(DetailActivity::class.java, i).setup().get()
    }

    private fun sheet(name: String, title: String, cells: List<Pair<String, Bitmap>>) {
        val pad = 24; val head = 70; val gap = 24
        val maxH = 1500
        val scaled = cells.map { (cap, b) ->
            val s = if (b.height > maxH) maxH.toFloat() / b.height else 1f
            Triple(cap, b, s)
        }
        val width = scaled.sumOf { (it.second.width * it.third).toInt() + gap } + pad
        val height = head + scaled.maxOf { (it.second.height * it.third).toInt() } + pad + 50
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        c.drawColor(Color.rgb(60, 60, 60))
        val p = Paint().apply { color = Color.WHITE; textSize = 34f; isAntiAlias = true; typeface = koFont }
        c.drawText(title, pad.toFloat(), 46f, p)
        p.textSize = 26f
        var x = pad
        for ((cap, b, s) in scaled) {
            val w = (b.width * s).toInt(); val h = (b.height * s).toInt()
            c.drawText(cap, x.toFloat(), (head + 24).toFloat(), p)
            c.drawBitmap(b, null, android.graphics.Rect(x, head + 36, x + w, head + 36 + h), Paint(Paint.FILTER_BITMAP_FLAG))
            x += w + gap
        }
        File(outDir, "$name.png").outputStream().use { out.compress(Bitmap.CompressFormat.PNG, 90, it) }
    }

    @Test
    fun allScreens() {
        todayProvider = { LocalDate.of(2026, 10, 21) }
        networkEnabled = false

        val all = EventStore.loadLocal(RuntimeEnvironment.getApplication()).events
        val longest = all.maxByOrNull { it.description.length + it.title.length }!!
        val noAddr = all.first { it.address.isBlank() }

        // 1) 목록 화면: 글자 크기별로 기기들을 한 장에
        for (fs in scales) {
            val phones = devs.take(4).map { it.name to shot("목록", it, fs) { main() } }
            sheet("main-phones-x$fs", "목록 화면 · 글자 x$fs · 폰들", phones)
            val big = devs.drop(4).map { it.name to shot("목록", it, fs) { main() } }
            sheet("main-big-x$fs", "목록 화면 · 글자 x$fs · 폴드 펼침/태블릿/가로", big)
        }
        // 2) 상세 화면
        for (fs in listOf(1.0f, 2.0f)) {
            val a = devs.map { it.name to shot("상세", it, fs) { detail(longest) } }
            sheet("detail-x$fs-a", "상세(설명 긴 행사) · 글자 x$fs", a.take(4))
            sheet("detail-x$fs-b", "상세(설명 긴 행사) · 글자 x$fs", a.drop(4))
        }
        val noA = devs.take(3).map { it.name to shot("상세-주소없음", it, 1.0f) { detail(noAddr) } }
        sheet("detail-noaddr", "상세(주소 없음)", noA)

        // 3) 지역을 골랐을 때(비어 있는 경우 포함)
        val prefs = RuntimeEnvironment.getApplication().getSharedPreferences("com.ajumma.five.MainActivity", 0)
        prefs.edit().putString("region", "동두천").putString("category", "공연·전시").commit()
        val states = devs.take(3).map { it.name to shot("지역선택", it, 1.0f) { main() } }
        sheet("state-region", "동두천 선택(빈 화면 가능)", states)

        report.append("# 화면 크기 점검\n\n점검한 화면 ${devs.size}종 x 글자 ${scales.size}단계. 문제 ${violations.distinct().size}건\n\n")
        violations.distinct().forEach { report.append("- $it\n") }
        File(outDir, "report.md").writeText(report.toString())
        assertTrue("화면 문제 ${violations.distinct().size}건:\n" + violations.distinct().joinToString("\n"), violations.isEmpty())
    }
}
