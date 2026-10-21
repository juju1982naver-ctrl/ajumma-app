package com.ajumma.five

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** 행사 목록 파일 주소. 이 파일만 바꾸면 앱을 다시 설치하지 않아도 목록이 바뀝니다. */
const val EVENTS_URL =
    "https://raw.githubusercontent.com/juju1982naver-ctrl/ajumma-app/main/data/events.json"

val CATEGORIES = listOf("공연·전시", "체험·교육", "축제·장터")
val REGIONS = listOf("전체", "양주", "의정부", "동두천", "연천", "포천")

data class Event(
    val id: String,
    val category: String,
    val region: String,
    val title: String,
    val start: LocalDate,
    val end: LocalDate,
    val time: String,
    val place: String,
    val fee: String,
    val description: String,
    val url: String,
) {
    fun dateText(): String =
        if (start == end) formatDay(start) else "${formatDay(start)} ~ ${formatDay(end)}"

    fun toJson(): String = JSONObject()
        .put("id", id).put("category", category).put("region", region).put("title", title)
        .put("startDate", start.toString()).put("endDate", end.toString())
        .put("time", time).put("place", place).put("fee", fee)
        .put("description", description).put("url", url)
        .toString()

    companion object {
        fun fromJson(o: JSONObject): Event {
            val start = LocalDate.parse(o.getString("startDate"))
            val endText = o.optString("endDate")
            return Event(
                id = o.optString("id"),
                category = o.optString("category"),
                region = o.optString("region"),
                title = o.optString("title"),
                start = start,
                end = if (endText.isBlank()) start else LocalDate.parse(endText),
                time = o.optString("time"),
                place = o.optString("place"),
                fee = o.optString("fee"),
                description = o.optString("description"),
                url = o.optString("url"),
            )
        }
    }
}

private fun formatDay(d: LocalDate): String {
    val week = d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    val year = if (d.year != LocalDate.now().year) "${d.year}년 " else ""
    return "$year${d.monthValue}월 ${d.dayOfMonth}일($week)"
}

class EventList(val updated: String, val events: List<Event>)

fun parseEvents(text: String): EventList {
    val root = JSONObject(text)
    val arr: JSONArray = root.getJSONArray("events")
    val list = (0 until arr.length()).mapNotNull {
        runCatching { Event.fromJson(arr.getJSONObject(it)) }.getOrNull()
    }
    return EventList(root.optString("updated"), list)
}

/** 마지막으로 받은 목록을 휴대폰에 저장해 두고, 인터넷이 안 될 때 보여줍니다. */
object EventStore {
    private const val PREFS = "events"
    private const val KEY_JSON = "json"

    /** 저장된 목록과 앱에 들어 있는 목록 중 더 최신 것을 돌려줍니다. */
    fun loadLocal(context: Context): EventList {
        val bundled = context.assets.open("events.json").bufferedReader().use { it.readText() }
            .let(::parseEvents)
        val cachedText = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_JSON, null) ?: return bundled
        val cached = runCatching { parseEvents(cachedText) }.getOrNull() ?: return bundled
        return if (cached.updated >= bundled.updated) cached else bundled
    }

    /** 인터넷에서 새 목록을 받아 저장합니다. 실패하면 null. 백그라운드에서 부르세요. */
    fun fetch(context: Context): EventList? = runCatching {
        val conn = (URL("$EVENTS_URL?t=${System.currentTimeMillis()}").openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 15_000
            useCaches = false
        }
        try {
            if (conn.responseCode != 200) return null
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val list = parseEvents(text)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_JSON, text).apply()
            list
        } finally {
            conn.disconnect()
        }
    }.getOrNull()
}
