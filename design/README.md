# 캐릭터(풍선 든 여자아이) 만들기

- `girl.py`: 그림 원본(SVG). 머리색·옷색·풍선색·위치는 여기서 바꿉니다.
- 아이콘 PNG 다시 만들기 (Chromium + Playwright 필요):

```
cd design
python3 -c "
from girl import V,svg; import json
v=V[1]; jobs=[]
for d,px in [('mdpi',48),('hdpi',72),('xhdpi',96),('xxhdpi',144),('xxxhdpi',192)]:
    jobs.append((f'mipmap-{d}/ic_launcher.png',svg(v,px,'app'),px))
    fg=int(px*108/48); jobs.append((f'mipmap-{d}/ic_launcher_foreground.png',svg(v,fg,'fg'),fg))
jobs.append(('drawable-nodpi/mascot.png',svg(v,512,'plain'),512))
json.dump(jobs,open('jobs.json','w'))"
node render.js ../app/src/main/res
```
