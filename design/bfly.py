import sys
from wings import circ, face, F
C=dict(bg="#FFF4D6",line="#2E3A59",wingL="#FFC8D6",wingL2="#FFE3EA",wingR="#BFE6FF",wingR2="#E1F3FF",blush="#FF9C85")
def ch(x,y,t,c,size): return f'<text x="{x}" y="{y}" font-size="{size}" fill="{c}" text-anchor="middle" dominant-baseline="central" style="{F}">{t}</text>'
def butterfly(c=C):
    L,w=c['line'],2.0
    ul="M29.5,30C24,7 3,3 2.5,19.5C2.2,31 15,35.5 29.5,34Z"
    ll="M29.5,37.5C16,36 5,44 8.5,54.5C12,63 26,56 30.5,42.5Z"
    m=lambda p,f:f'<g transform="translate(64,0) scale(-1,1)"><path d="{p}" fill="{f}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/></g>'
    s=f'<path d="{ul}" fill="{c["wingL"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/>'+m(ul,c["wingR"])
    s+=f'<path d="{ll}" fill="{c["wingL2"]}" stroke="{L}" stroke-width="{w}" stroke-linejoin="round"/>'+m(ll,c["wingR2"])
    s+=f'<path d="M30.2,28.5Q29.5,21 27,16.5M33.8,28.5Q34.5,21 37,16.5" fill="none" stroke="{L}" stroke-width="{w*0.9}" stroke-linecap="round"/>'
    s+=f'<path d="{circ(27,16.5,1.5)}" fill="{L}"/><path d="{circ(37,16.5,1.5)}" fill="{L}"/>'
    s+=face(32,35.5,7.6,L,c['blush'],w)
    s+=ch(14.5,21.5,"수",L,11)+ch(17.5,48.5,"빈",L,10)+ch(49.5,21.5,"서",L,11)+ch(46.5,48.5,"아",L,10)
    return s
def svg(size,mode="app"):
    s=f'<svg xmlns="http://www.w3.org/2000/svg" width="{size}" height="{size}" viewBox="0 0 64 64">'
    if mode=="app": s+=f'<rect width="64" height="64" rx="14.4" fill="{C["bg"]}"/><g transform="translate(3.2,3.2) scale(0.9)">'
    elif mode=="square": s+=f'<rect width="64" height="64" fill="{C["bg"]}"/><g transform="translate(3.2,3.2) scale(0.9)">'
    elif mode=="fg": s+='<g transform="translate(13.9,13.9) scale(0.565)">'  # 적응형 아이콘 안전 영역(66/108)
    else: s+='<g>'
    return s+butterfly()+'</g></svg>'
if __name__=="__main__":
    open('bfly_sheet.html','w').write(f'''<html><head><meta charset="utf-8"><style>body{{margin:0;background:#F7F5F2;font-family:'Noto Sans CJK KR',sans-serif;padding:28px}}
    .row{{display:flex;gap:28px;align-items:center}} .home{{background:#C9D6E3;padding:10px;border-radius:14px;display:flex;gap:10px;align-items:center;font-size:14px;color:#555}}
    .hdr{{background:#E8664A;border-radius:16px;padding:12px 18px;display:flex;align-items:center;gap:10px;color:#fff;font-size:26px;font-weight:700}}
    h1{{font-size:26px;margin:0 0 16px}}</style></head><body><h1>W-1 나비 · 날개 키우고 이름 세로로</h1>
    <div class="row">{svg(220)}{svg(170,"plain")}<div style="display:flex;flex-direction:column;gap:14px"><div class="home">{svg(64)}{svg(64)}<span>홈 화면 크기</span></div>
    <div class="hdr">{svg(56,"plain")}<span>상미맘 나들이</span></div></div></div></body></html>''')
