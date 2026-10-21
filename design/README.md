# 나비 캐릭터 만들기

- `bfly.py`: 수빈·서아 나비 그림(SVG). 색과 모양은 여기서 바꿉니다. `wings.py`의 얼굴·글씨 함수를 씁니다.
- 아이콘 PNG 다시 만들기 (Chromium + Playwright 필요):

```
cd design
python3 -c "
from bfly import svg; import json
jobs=[]
for d,px in [('mdpi',48),('hdpi',72),('xhdpi',96),('xxhdpi',144),('xxxhdpi',192)]:
    jobs.append((f'mipmap-{d}/ic_launcher.png',svg(px,'app'),px))
    fg=int(px*108/48); jobs.append((f'mipmap-{d}/ic_launcher_foreground.png',svg(fg,'fg'),fg))
jobs.append(('drawable-nodpi/mascot.png',svg(512,'plain'),512))
json.dump(jobs,open('jobs.json','w'))"
node render.js ../app/src/main/res
```
