const { chromium } = require('playwright');
const fs=require('fs'),path=require('path');
const jobs=JSON.parse(fs.readFileSync('jobs.json'));const out=process.argv[2];
(async()=>{const b=await chromium.launch();
for(const [rel,svg,px] of jobs){const p=await b.newPage({viewport:{width:px,height:px}});
await p.setContent(`<html><body style="margin:0;background:transparent">${svg}</body></html>`);
const f=path.join(out,rel);fs.mkdirSync(path.dirname(f),{recursive:true});
await p.screenshot({path:f,omitBackground:true,clip:{x:0,y:0,width:px,height:px}});await p.close();}
await b.close();})();
