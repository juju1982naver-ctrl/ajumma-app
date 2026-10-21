def circ(cx,cy,r): return f"M{cx-r},{cy}a{r},{r} 0 1,0 {2*r},0a{r},{r} 0 1,0 {-2*r},0"
F="font-family:'Noto Sans CJK KR',sans-serif;font-weight:700"
def face(cx,cy,r,line,blush,w):
    s=f'<path d="{circ(cx,cy,r)}" fill="#FFFFFF" stroke="{line}" stroke-width="{w}"/>'
    s+=f'<path d="{circ(cx-r*0.36,cy-r*0.08,r*0.13)}" fill="{line}"/><path d="{circ(cx+r*0.36,cy-r*0.08,r*0.13)}" fill="{line}"/>'
    s+=f'<path d="{circ(cx-r*0.62,cy+r*0.3,r*0.15)}" fill="{blush}"/><path d="{circ(cx+r*0.62,cy+r*0.3,r*0.15)}" fill="{blush}"/>'
    s+=f'<path d="M{cx-r*0.3},{cy+r*0.28}Q{cx},{cy+r*0.6} {cx+r*0.3},{cy+r*0.28}" fill="none" stroke="{line}" stroke-width="{w*0.9}" stroke-linecap="round"/>'
    return s
def label(x,y,t,c,size): return f'<text x="{x}" y="{y}" font-size="{size}" fill="{c}" text-anchor="middle" style="{F}">{t}</text>'

def butterfly(c):
    L,w=c['line'],2.0
    ul="M28,31C20,13 6,13 5.5,23.5C5,32 15,36 28,35Z"
    ll="M28,38C18,38 10,45 13,51.5C16,57 25,50 29,42Z"
    m=lambda p:f'<g transform="translate(64,0) scale(-1,1)"><path d="{p}" fill="{c["wingR"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/></g>'
    s=f'<path d="{ul}" fill="{c["wingL"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/>'+m(ul)
    s+=f'<path d="{ll}" fill="{c["wingL2"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/>'+m(ll).replace(c["wingR"],c["wingR2"])
    s+=f'<path d="M29,27Q26,19 22,17.5M35,27Q38,19 42,17.5" fill="none" stroke="{L}" stroke-width="{w*0.9}" stroke-linecap="round"/>'
    s+=f'<path d="{circ(22,17.5,1.6)}" fill="{L}"/><path d="{circ(42,17.5,1.6)}" fill="{L}"/>'
    s+=face(32,35,8.2,L,c['blush'],w)
    s+=label(15.2,28.6,"수빈",L,5.6)+label(48.8,28.6,"서아",L,5.6)
    return s
def dragonfly(c):
    L,w=c['line'],2.0
    s=''
    for side in (1,-1):
        tf='' if side==1 else ' transform="translate(64,0) scale(-1,1)"'
        s+=f'<g{tf}><ellipse cx="15" cy="26" rx="13" ry="6" transform="rotate(-10 15 26)" fill="{c["wingL"] if side==1 else c["wingR"]}" stroke="{L}" stroke-width="{w}"/>'
        s+=f'<ellipse cx="16.5" cy="38.5" rx="11" ry="4.8" transform="rotate(14 16.5 38.5)" fill="{c["wingL2"] if side==1 else c["wingR2"]}" stroke="{L}" stroke-width="{w}"/></g>'
    s+=f'<path d="M32,40L32,52" stroke="{L}" stroke-width="{w*1.6}" stroke-linecap="round"/>'
    s+=face(32,31,9,L,c['blush'],w)
    s+=label(14.5,28.2,"수빈",L,5.6)+label(49.5,28.2,"서아",L,5.6)
    return s
def angel(c):
    L,w=c['line'],2.0
    s=''
    # 둥근 꽃잎 같은 날개 4장 (위 2장 크게, 아래 2장 작게)
    for side in (1,-1):
        tf='' if side==1 else ' transform="translate(64,0) scale(-1,1)"'
        s+=f'<g{tf}><path d="M27,30C22,16 9,14 7,22C5,30 14,34 26,33Z" fill="{c["wingL"] if side==1 else c["wingR"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/>'
        s+=f'<path d="M26.5,36C16,36 11,41 13,46C15,50.5 22,47 27,40Z" fill="{c["wingL2"] if side==1 else c["wingR2"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/></g>'
    s+=f'<ellipse cx="32" cy="20.5" rx="6.5" ry="2" fill="none" stroke="{c["halo"]}" stroke-width="{w*1.1}"/>'
    s+=face(32,35,9.5,L,c['blush'],w)
    s+=label(15.6,27.4,"수빈",L,5.6)+label(48.4,27.4,"서아",L,5.6)
    return s
V=[
 ('W-1','나비',"동그란 얼굴에 나비 날개를 달았어요",butterfly,dict(bg="#FFF4D6",line="#2E3A59",wingL="#FFC8D6",wingL2="#FFE3EA",wingR="#BFE6FF",wingR2="#E1F3FF",blush="#FF9C85")),
 ('W-2','잠자리 요정',"길쭉한 날개 4장이 시원해 보여요",dragonfly,dict(bg="#E3F4EE",line="#22594C",wingL="#FFFFFF",wingL2="#FFFFFF",wingR="#FFFFFF",wingR2="#FFFFFF",blush="#FF9C85")),
 ('W-3','꼬마 천사',"후광과 둥근 날개로 귀엽게 만들었어요",angel,dict(bg="#EEE8FF",line="#4B3F8C",wingL="#FFFFFF",wingL2="#FFFFFF",wingR="#FFFFFF",wingR2="#FFFFFF",blush="#FFA3B5",halo="#F5B82E")),
]
def svg(fn,c,size,app=True):
    s=f'<svg xmlns="http://www.w3.org/2000/svg" width="{size}" height="{size}" viewBox="0 0 64 64">'
    if app: s+=f'<rect width="64" height="64" rx="14.4" fill="{c["bg"]}"/><g transform="translate(3,2) scale(0.906)">'
    else: s+='<g>'
    return s+fn(c)+'</g></svg>'
cards=''
for k,n,note,fn,c in V:
    cards+=f'''<div class="card"><div class="k">{k}</div><div class="n">{n}</div><div class="note">{note}</div>
    <div class="row">{svg(fn,c,170)}<div class="col">{svg(fn,c,120,app=False)}<div class="home">{svg(fn,c,60)}<span>홈 화면 크기</span></div></div></div></div>'''
open('sheet4.html','w').write(f'''<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#F7F5F2;font-family:'Noto Sans CJK KR',sans-serif;padding:28px}}
h1{{font-size:28px;color:#222;margin:0 0 18px}} .wrap{{display:flex;gap:22px}}
.card{{background:#fff;border:1px solid #E6E1DC;border-radius:20px;padding:20px 22px;width:370px}}
.k{{font-size:24px;font-weight:700;color:#333}} .n{{font-size:21px;color:#222;margin-top:2px}} .note{{font-size:15px;color:#777;margin:4px 0 14px}}
.row{{display:flex;align-items:center;gap:16px}} .col{{display:flex;flex-direction:column;align-items:center;gap:8px}}
.home{{display:flex;align-items:center;gap:6px;font-size:13px;color:#555;white-space:nowrap;background:#C9D6E3;padding:6px 8px;border-radius:12px}}
</style></head><body><h1>상미맘 나들이 · 날개 4개 캐릭터 3가지</h1><div class="wrap">{cards}</div></body></html>''')
