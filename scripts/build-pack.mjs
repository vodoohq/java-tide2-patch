import fs from 'node:fs/promises';
import path from 'node:path';
import crypto from 'node:crypto';
import {execFileSync} from 'node:child_process';
const root=process.cwd();
const version='2.1.1+voodoo.26.3.alpha.2';
const jarName=`tide-fabric-26.3-${version}.jar`;
const jar=path.join(root,'fabric-26.3/build/libs',jarName);
const index=JSON.parse(await fs.readFile('pack/modrinth.index.json','utf8'));
const javaJar=process.env.JAVA_HOME?path.join(process.env.JAVA_HOME,'bin',process.platform==='win32'?'jar.exe':'jar'):'jar';
await fs.mkdir('build/release',{recursive:true});
const uiFile=index.files.find(file=>file.path==='mods/voodoo-ui-1.0.0+26.3.jar');
if(!uiFile)throw new Error('Voodoo UI dependency missing');
const uiName=path.basename(uiFile.path);
let uiData;
try { uiData=await fs.readFile(path.join('build/downloads',uiName)); }
catch(error) {
 if(error.code!=='ENOENT')throw error;
 const response=await fetch(uiFile.downloads[0]);
 if(!response.ok)throw new Error(`Voodoo UI download: HTTP ${response.status}`);
 uiData=Buffer.from(await response.arrayBuffer());
}
if(crypto.createHash('sha512').update(uiData).digest('hex')!==uiFile.hashes.sha512)throw new Error('Voodoo UI hash mismatch');
await fs.writeFile(path.join('build/release',uiName),uiData);
// A unique directory prevents stale overrides from leaking into a later export.
const staging=await fs.mkdtemp(path.join(root,'build/pack-'));
await fs.cp('pack/client-overrides',path.join(staging,'client-overrides'),{recursive:true});
await fs.mkdir(path.join(staging,'overrides/mods'),{recursive:true});
await fs.copyFile(jar,path.join(staging,'overrides/mods',jarName));
await fs.mkdir(path.join(staging,'server-overrides'),{recursive:true});
await fs.writeFile(path.join(staging,'server-overrides/server.properties'),'hardcore=true\ndifficulty=hard\nmotd=Voodoo SMP\n');
await fs.writeFile(path.join(staging,'modrinth.index.json'),JSON.stringify(index,null,2)+'\n');
execFileSync(javaJar,['--create','--file',path.join(root,'build/release/VoodooSMP-26.3-tide-alpha.2.mrpack'),'--no-manifest','-C',staging,'.']);
const addon=await fs.mkdtemp(path.join(root,'build/tide-addon-'));
const dependencies=index.files.filter(file=>file.path.startsWith('mods/fabric-api-')||file.path.includes('cloth-config'));
if(dependencies.length!==2)throw new Error('Tide starter pack requires exactly Fabric API and Cloth Config');
await fs.mkdir(path.join(addon,'overrides/mods'),{recursive:true});
await fs.copyFile(jar,path.join(addon,'overrides/mods',jarName));
await fs.writeFile(path.join(addon,'modrinth.index.json'),JSON.stringify({...index,name:'Tide 2 Voodoo Fabric Port',files:dependencies},null,2)+'\n');
execFileSync(javaJar,['--create','--file',path.join(root,'build/release/Tide2-Fabric-26.3-alpha.2.mrpack'),'--no-manifest','-C',addon,'.']);
for(const name of [jarName,jarName.replace('.jar','-sources.jar')])await fs.copyFile(path.join(root,'fabric-26.3/build/libs',name),path.join(root,'build/release',name));
const checksums=[];
for(const name of (await fs.readdir('build/release')).filter(name=>name!== 'SHA256SUMS.txt').sort()){
 const data=await fs.readFile(path.join('build/release',name));
 checksums.push(`${crypto.createHash('sha256').update(data).digest('hex')}  ${name}`);
}
await fs.writeFile('build/release/SHA256SUMS.txt',checksums.join('\n')+'\n');
console.log('Built full Voodoo SMP pack, Tide starter pack, jars and checksums in build/release.');
