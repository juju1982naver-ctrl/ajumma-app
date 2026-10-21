LINE="#5E4B4B"; SKIN="#FFE6D8"; HAIR="#9A7058"; HAIR_D="#7A5442"
_id=[0]
def uid(): _id[0]+=1; return f"q{_id[0]}"
def balloon(cx,cy,r,c1,c2,tilt=0):
    g=uid(); ry=r*1.13
    body=(f"M{cx},{cy+ry}C{cx-r*0.6},{cy+ry*0.92} {cx-r},{cy+ry*0.42} {cx-r},{cy-ry*0.06}"
          f"C{cx-r},{cy-ry*0.64} {cx-r*0.56},{cy-ry} {cx},{cy-ry}"
          f"C{cx+r*0.56},{cy-ry} {cx+r},{cy-ry*0.64} {cx+r},{cy-ry*0.06}"
          f"C{cx+r},{cy+ry*0.42} {cx+r*0.6},{cy+ry*0.92} {cx},{cy+ry}Z")
    k=r*0.14
    return (f'<defs><radialGradient id="{g}" cx="0.35" cy="0.3" r="0.85"><stop offset="0" stop-color="#FFFFFF" stop-opacity="0.9"/><stop offset="0.25" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></radialGradient></defs>'
     f'<g transform="rotate({tilt} {cx} {cy+ry})"><path d="M{cx},{cy+ry-0.2}L{cx-k},{cy+ry+k*1.3}L{cx+k},{cy+ry+k*1.3}Z" fill="{c2}" stroke="{LINE}" stroke-width="0.6" stroke-linejoin="round"/>'
     f'<path d="{body}" fill="url(#{g})" stroke="{LINE}" stroke-width="0.8"/>'
     f'<ellipse cx="{cx-r*0.4}" cy="{cy-ry*0.45}" rx="{r*0.2}" ry="{r*0.12}" transform="rotate(-35 {cx-r*0.4} {cy-ry*0.45})" fill="#FFFFFF" opacity="0.85"/></g>')
def girl():
    s=''
    # 뒤쪽 양갈래 머리 + 리본
    s+=f'<ellipse cx="11.6" cy="37.5" rx="3.4" ry="4.6" transform="rotate(20 11.6 37.5)" fill="{HAIR}" stroke="{LINE}" stroke-width="0.7"/>'
    s+=f'<path d="M12.2,32.3l-2.8,-1.6l0.2,3.2Z M12.2,32.3l2.6,-2l0.4,3.1Z" fill="#FF9DB5" stroke="{LINE}" stroke-width="0.5" stroke-linejoin="round"/>'
    # 다리·신발
    s+=f'<path d="M19.5,56.5L19,61.2M24,56.5L24.8,61" stroke="{SKIN}" stroke-width="2.2" stroke-linecap="round"/>'
    s+=f'<path d="M17.2,61.6q1.8,-1.4 3.6,-0.2q-0.2,1.1 -3.6,0.2Z M23.6,61.4q1.9,-1.2 3.5,0.1q-0.4,1 -3.5,-0.1Z" fill="#B57A62" stroke="{LINE}" stroke-width="0.6"/>'
    # 원피스
    s+=f'<path d="M18,42.5Q21.5,41 25.5,42.5L28.6,56.8Q21.5,58.6 14.6,56.8Z" fill="#FFB8C9" stroke="{LINE}" stroke-width="0.8" stroke-linejoin="round"/>'
    s+=f'<path d="M15.6,54.4Q21.5,56 27.6,54.4" fill="none" stroke="#FFFFFF" stroke-width="0.8" opacity="0.8"/>'
    # 위로 든 팔과 손
    s+=f'<path d="M24.5,44.5Q30,42.5 34.6,38.6" fill="none" stroke="{LINE}" stroke-width="3.1" stroke-linecap="round"/>'
    s+=f'<path d="M24.5,44.5Q30,42.5 34.6,38.6" fill="none" stroke="{SKIN}" stroke-width="2.1" stroke-linecap="round"/>'
    s+=f'<circle cx="35.2" cy="38.1" r="1.75" fill="{SKIN}" stroke="{LINE}" stroke-width="0.6"/>'
    # 머리(뒤) → 얼굴 → 앞머리
    s+=f'<circle cx="20.6" cy="31" r="10.6" fill="{HAIR}" stroke="{LINE}" stroke-width="0.8"/>'
    s+=f'<path d="M14.8,31.5C14.8,25.5 30.5,24.5 30.4,32.5C30.2,38.6 26.4,41.4 22.2,41.4C17.6,41.4 14.8,37.4 14.8,31.5Z" fill="{SKIN}" stroke="{LINE}" stroke-width="0.8"/>'
    s+=f'<path d="M12.6,30.2C12.4,22 19,19.6 23.5,20.6C28.6,21.6 31.6,25 31.2,30.4C29.2,27.6 27.4,26.4 25.2,27.6C23.6,25.6 22.4,26.6 21.4,28.4C19.8,26.8 17.6,27.4 16.6,29.4C15.4,28.6 14,29.4 12.6,30.2Z" fill="{HAIR}" stroke="{LINE}" stroke-width="0.8" stroke-linejoin="round"/>'
    s+=f'<path d="M18.5,23.2Q22.5,21.8 26.4,23.6" fill="none" stroke="#FFFFFF" stroke-width="0.8" opacity="0.45" stroke-linecap="round"/>'
    # 눈·볼·입 (오른쪽 위 풍선을 바라봄)
    s+=f'<ellipse cx="26.2" cy="32.6" rx="1.15" ry="1.5" fill="#3A2E2E"/><circle cx="26.6" cy="32.1" r="0.4" fill="#FFFFFF"/>'
    s+=f'<ellipse cx="20.6" cy="32.8" rx="1.05" ry="1.4" fill="#3A2E2E"/><circle cx="21" cy="32.3" r="0.35" fill="#FFFFFF"/>'
    s+=f'<ellipse cx="18.2" cy="36" rx="1.8" ry="1.1" fill="#FF9DA6" opacity="0.7"/><ellipse cx="28.4" cy="35.8" rx="1.5" ry="1" fill="#FF9DA6" opacity="0.7"/>'
    s+=f'<path d="M22.6,36.8Q23.9,38 25.2,36.8" fill="none" stroke="{LINE}" stroke-width="0.7" stroke-linecap="round"/>'
    return s
def strings(H,knots):
    return ''.join(f'<path d="M{x},{y}Q{(x+H[0])/2+1.5},{(y+H[1])/2} {H[0]},{H[1]}" fill="none" stroke="{LINE}" stroke-width="0.55"/>' for x,y in knots)+f'<path d="M{H[0]},{H[1]+1}q-0.6,2.6 0.6,4.6" fill="none" stroke="{LINE}" stroke-width="0.55"/>'
def scene():
    H=(35.4,37.4)
    b=[(41.5,12.5,7.6,'#D9EFA9','#B5D67E',-8),(52.8,11.5,8.2,'#FFC9D8','#F49AB3',6),(54.5,25.5,7.2,'#FFD9B0','#F7B27A',14)]
    knots=[(cx+ (r*1.13+r*0.18)*__import__('math').sin(__import__('math').radians(-t)) , cy+r*1.13+r*0.18) for cx,cy,r,_,_,t in b]
    return strings(H,knots)+''.join(balloon(cx,cy,r,c1,c2,t) for cx,cy,r,c1,c2,t in b)+girl()
V=[('P-1','전신',"아이 전체와 풍선을 다 보여줘요",'#EAF6FF',(0,0,1)),
   ('P-2','크게 확대',"아이 얼굴과 풍선을 크게 잘라 작은 아이콘에서도 잘 보여요",'#FFF3F6',(-2,0.8,0.96)),
   ('P-3','확대 + 하늘 배경',"P-2 구도에 하늘색 바탕과 구름을 더했어요",'#D7EEFF',(-2,0.8,0.96))]
def svg(v,size,mode="app"):
    k,n,note,bg,(tx,ty,sc)=v
    s=f'<svg xmlns="http://www.w3.org/2000/svg" width="{size}" height="{size}" viewBox="0 0 64 64"><defs><clipPath id="c{k}{size}{mode}"><rect width="64" height="64" rx="{14.4 if mode=="app" else 0}"/></clipPath></defs>'
    if mode in("app","square"):
        s+=f'<g clip-path="url(#c{k}{size}{mode})"><rect width="64" height="64" fill="{bg}"/>'
        if k=='P-3': s+='<path d="M4,52q2,-4 6,-2q2,-4 6,-1q4,0 3,4h-15Z M44,58q2,-3 5,-1.5q2,-3 5,-0.5q3,0 2.5,3h-12.5Z" fill="#FFFFFF" opacity="0.9"/><rect y="58" width="64" height="6" fill="#BFE3A8"/>'
        s+=f'<g transform="translate({tx+1},{ty+1}) scale({sc*0.97})">'+scene()+'</g></g>'
    elif mode=="fg": s+=f'<g transform="translate(13.9,13.9) scale(0.565)"><g transform="translate({tx},{ty}) scale({sc})">'+scene()+'</g></g>'
    else: s+='<g>'+scene()+'</g>'
    return s+'</svg>'
if __name__=="__main__":
    cards=''
    for v in V:
        cards+=f'''<div class="card"><div class="k">{v[0]}</div><div class="n">{v[1]}</div><div class="note">{v[2]}</div>
        <div class="row">{svg(v,180)}<div class="col"><div class="home">{svg(v,48)}{svg(v,48)}{svg(v,48)}<span>홈 화면</span></div>
        <div class="home dark">{svg(v,48)}<span>어두운 배경</span></div></div></div></div>'''
    open('sheet7.html','w').write(f'''<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#F7F5F2;font-family:'Noto Sans CJK KR',sans-serif;padding:28px}}
h1{{font-size:28px;color:#222;margin:0 0 18px}} .wrap{{display:flex;gap:22px}}
.card{{background:#fff;border:1px solid #E6E1DC;border-radius:20px;padding:20px 22px;width:400px}}
.k{{font-size:24px;font-weight:700;color:#333}} .n{{font-size:21px;color:#222;margin-top:2px}} .note{{font-size:15px;color:#777;margin:4px 0 14px}}
.row{{display:flex;align-items:center;gap:16px}} .col{{display:flex;flex-direction:column;gap:10px}}
.home{{display:flex;align-items:center;gap:6px;font-size:13px;color:#555;white-space:nowrap;background:#C9D6E3;padding:6px 8px;border-radius:12px}} .home.dark{{background:#222;color:#ccc}}
</style></head><body><h1>상미맘 나들이 · 풍선 든 여자아이</h1><div class="wrap">{cards}</div></body></html>''')
