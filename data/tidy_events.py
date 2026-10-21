"""data/events.json 정리: 지난 행사 삭제, 중복 제거, 형식 검사, 날짜순 정렬, id 다시 매기기.

사용법: python3 data/tidy_events.py [기준일 YYYY-MM-DD] [--force]
기준일을 안 주면 오늘(한국 시간) 기준.
안전장치: 정리 뒤 남은 행사가 20건 미만이거나 직전 커밋 목록의 절반 미만이면
파일을 쓰지 않고 실패한다(--force로 무시).
"""
import json
import re
import subprocess
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path

PATH = Path(__file__).resolve().parent / "events.json"
CATEGORIES = ["공연·전시", "체험·교육", "축제·장터"]
REGIONS = ["양주", "의정부", "동두천", "연천", "포천"]
FIELDS = ["id", "category", "region", "title", "startDate", "endDate",
          "time", "place", "address", "fee", "description", "url"]
DATE = re.compile(r"^\d{4}-\d{2}-\d{2}$")


def key(e):
    title = re.sub(r"[\s\W_]+", "", e["title"]).lower()
    return (e["region"], title, e["startDate"])


MIN_EVENTS = 20


def committed_count():
    try:
        out = subprocess.run(["git", "show", "HEAD:data/events.json"], cwd=PATH.parent,
                             capture_output=True, text=True, check=True).stdout
        return len(json.loads(out)["events"])
    except Exception:
        return 0


def main():
    args = [a for a in sys.argv[1:] if a != "--force"]
    force = "--force" in sys.argv
    today = args[0] if args else \
        datetime.now(timezone(timedelta(hours=9))).strftime("%Y-%m-%d")
    data = json.loads(PATH.read_text(encoding="utf-8"))
    errors, kept, seen = [], [], set()
    removed_past = removed_dup = 0
    for e in data["events"]:
        for f in FIELDS:
            e.setdefault(f, "")
        name = e.get("title") or e.get("id")
        if e["category"] not in CATEGORIES:
            errors.append(f"{name}: category '{e['category']}'")
        if e["region"] not in REGIONS:
            errors.append(f"{name}: region '{e['region']}'")
        if not DATE.match(e["startDate"]) or not DATE.match(e["endDate"]):
            errors.append(f"{name}: 날짜 형식")
            continue
        if e["endDate"] < e["startDate"]:
            errors.append(f"{name}: endDate가 startDate보다 빠름")
        if not e["title"] or not e["url"]:
            errors.append(f"{name}: title/url 비어 있음")
        if e["endDate"] < today:
            removed_past += 1
            continue
        k = key(e)
        if k in seen:
            removed_dup += 1
            continue
        seen.add(k)
        kept.append({f: e[f] for f in FIELDS})
    if errors:
        print("형식 오류:\n  " + "\n  ".join(errors))
        sys.exit(1)
    before = committed_count()
    if not force and (len(kept) < MIN_EVENTS or len(kept) < before / 2):
        print(f"안전장치: 남은 행사 {len(kept)}건 (직전 {before}건). 너무 많이 줄어 파일을 쓰지 않았습니다.")
        sys.exit(2)
    kept.sort(key=lambda e: (REGIONS.index(e["region"]), e["startDate"], e["title"]))
    for i, e in enumerate(kept, 1):
        e["id"] = f"{e['region']}-{i}"
    data = {"updated": today, "events": kept}
    PATH.write_text(json.dumps(data, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    counts = {r: sum(e["region"] == r for e in kept) for r in REGIONS}
    print(f"지난 행사 삭제 {removed_past}, 중복 삭제 {removed_dup}, 남은 {len(kept)}건")
    print("지역별: " + ", ".join(f"{r} {n}" for r, n in counts.items()))


if __name__ == "__main__":
    main()
