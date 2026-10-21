# 행사 정보 주간 수집 절차

매주 한 번 Claude 예약 작업이 이 문서대로 `data/events.json`을 갱신합니다.
앱은 `main` 브랜치의 이 파일만 읽으므로, 앱을 다시 빌드할 필요는 없습니다.

## 규칙

- 카테고리는 `공연·전시` / `체험·교육` / `축제·장터` 3개뿐. 지역은 `양주`·`의정부`·`동두천`·`연천`·`포천`.
- 오늘(한국 시간) 이후 열리거나 진행 중인 것만. 교육은 접수중·예정·대기접수만 넣고, 마감·종료는 뺀다.
- 어른만 대상인 강좌(성인·시니어·중학생 이상)는 넣지 않는다. 아이와 같이 가거나 아이가 듣는 것 위주.
- 페이지에서 확인한 것만 쓴다. 시간·요금·주소를 모르면 빈칸(`""`)으로 두고 추측하지 않는다.
- `address`는 도로명 주소 + 괄호 안 장소 이름 (예: `경기도 의정부시 의정로 1 (의정부예술의전당)`). 확인된 주소만.
- `description`은 3~4문장, 엄마들끼리 말하듯 쉬운 존댓말("~해요"). 무엇을 하는지, 대상 나이, 신청 방법·주의할 점, 문의 전화.
- 도서관 강좌는 `startDate`~`endDate`에 **신청 기간**을 넣고, 설명에 "위 날짜는 신청 기간"이라고 쓴다. 수업 날짜를 알면 `time`에 적는다.
- 하나의 행사는 한 번만. 이미 있는 항목은 새로 확인한 내용으로 고친다(새로 만들지 않음).
- `url`은 상세 페이지가 있으면 상세, 없으면 목록 페이지.

## 출처 (차례대로 읽기)

| 지역 | 출처 | 주소 |
|---|---|---|
| 의정부 | 의정부문화재단 공연 | https://www.uac.or.kr/newuac/performance/performance_03.php |
| 의정부 | 의정부문화재단 전시 | https://www.uac.or.kr/newuac/performance/performance_06.php |
| 의정부 | 과학도서관 강좌 | https://www.uilib.go.kr/science/module/teach/index.do?menu_idx=24 |
| 의정부 | 영어도서관 강좌 | https://www.uilib.go.kr/english/module/teach/index.do?menu_idx=24 |
| 포천 | 포천문화재단(반월아트홀) | https://www.pcfac.or.kr/ , https://www.pcfac.or.kr/sub02/sub01-1.php |
| 포천 | 면암중앙도서관 강좌 | https://lib.pocheon.go.kr/center/menu/10134/program/30014/lectureList.do |
| 연천 | 수레울아트홀 | https://yccs.or.kr |
| 연천 | 전곡선사박물관 | https://jgpm.ggcf.kr/ |
| 연천 | 연천군도서관 강좌 | https://library.yeoncheon.go.kr/menu/10051/program/30013/lectureList.do |
| 동두천 | 경기북부어린이박물관 | https://ngcm.ggcf.kr/ |
| 양주 | 양주시도서관 강좌 | https://www.libyj.go.kr/ojlake/lectureList.do?manageCd=ALL |
| 5곳 | 문체부 지역축제(경기) | https://www.mcst.go.kr/site/s_culture/festival/festivalList.jsp?pSido=10 |
| 5곳 | 뉴스 검색 | "양주 축제", "의정부 축제", "동두천 행사", "연천 축제", "포천 축제" + 이번 달·다음 달 |

시·군청 사이트(행사·공지 게시판 몇 페이지만):

| 지역 | 주소 |
|---|---|
| 양주 | https://www.yangju.go.kr |
| 의정부 | https://www.ui4u.go.kr |
| 동두천 | https://www.ddc.go.kr/ddc/selectClturCldrTotalList.do?key=2327 (행사일정), https://www.ddc.go.kr/ddc/selectBbsNttList.do?bbsNo=25&key=105 (행사·교육) |
| 연천 | https://www.yeoncheon.go.kr |
| 포천 | https://www.pocheon.go.kr |

시·군청은 WebFetch로는 열리지 않으므로(robots 거부) 터미널의 `curl -A "Mozilla/5.0"`으로 읽는다.
클라우드 환경에서 이 도메인들이 허용되지 않았으면 건너뛰고 보고에 "못 읽음"으로 남긴다.

## 순서

1. `main` 브랜치를 최신으로 받고 `data/events.json`을 읽어 지금 목록을 파악한다.
2. 위 출처를 읽고 새 행사는 추가, 바뀐 내용은 고친다(새 항목의 `id`는 아무 값이나 넣어도 됨).
3. `python3 data/tidy_events.py`를 실행한다. 지난 행사 삭제·중복 제거·형식 검사·id 정리를 한다.
   형식 오류가 나오면 고치고 다시 실행.
4. 커밋 메시지에 추가·삭제 건수, 지역별 건수, 못 읽은 출처를 적고 `main`에 바로 올린다.
   (`data/`만 바뀌므로 APK는 다시 빌드되지 않는다.)

## 안전장치 (올리지 않고 멈추는 경우)

- `tidy_events.py`가 "안전장치"로 멈춤: 남은 행사가 20건 미만이거나 지난주의 절반 미만.
- 출처의 절반 이상을 못 읽음.
- 형식 오류를 고치지 못함.

이때는 `main`에 아무것도 올리지 않는다(앱은 지난주 목록을 그대로 보여줌).
변경은 `collect/YYYY-MM-DD` 브랜치에만 올리고, 프로젝트 채팅에 무엇이 실패했는지 쉬운 말로 알린다.
