// Capture distributable dependencies and selected settings, never worlds or account data.
import fs from 'node:fs/promises';
import path from 'node:path';
import crypto from 'node:crypto';
const profile=process.argv[2];
if(!profile)throw new Error('Usage: node scripts/capture-pack.mjs <Minecraft profile>');
const headers={'User-Agent':'VoodooHQ/java-tide2-patch (https://github.com/vodoohq/java-tide2-patch)'};
async function api(route,options={}){
 const response=await fetch('https://api.modrinth.com/v2/'+route,{...options,headers:{...headers,...options.headers}});
 if(!response.ok)throw new Error(`${route}: HTTP ${response.status}`);
 return response.json();
}
const entries=[];
for(const folder of ['mods','resourcepacks']){
 for(const name of await fs.readdir(path.join(profile,folder))){
  if(!name.endsWith(folder==='mods'?'.jar':'.zip')||name.startsWith('tide-fabric-26.3-'))continue;
  const data=await fs.readFile(path.join(profile,folder,name));
  entries.push({path:folder+'/'+name,data,hashes:Object.fromEntries(['sha1','sha512'].map(algorithm=>[algorithm,crypto.createHash(algorithm).update(data).digest('hex')]))});
 }
}
const cloth=await api('version/fg2uyxOW');
const clothFile=cloth.files.find(file=>file.primary)||cloth.files[0];
if(!entries.some(entry=>entry.hashes.sha512===clothFile.hashes.sha512)){
 const response=await fetch(clothFile.url,{headers});if(!response.ok)throw new Error('Cloth Config download failed');
 const data=Buffer.from(await response.arrayBuffer());
 if(crypto.createHash('sha512').update(data).digest('hex')!==clothFile.hashes.sha512)throw new Error('Cloth Config hash mismatch');
 entries.push({path:'mods/'+clothFile.filename,data,hashes:clothFile.hashes});
 await fs.mkdir('build/downloads',{recursive:true});await fs.writeFile('build/downloads/'+clothFile.filename,data);
}
const versions=await api('version_files',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({hashes:entries.map(e=>e.hashes.sha512),algorithm:'sha512'})});
const projectIds=[...new Set(Object.values(versions).map(v=>v.project_id))];
const projects=await api('projects?ids='+encodeURIComponent(JSON.stringify(projectIds)));
const files=[],omitted=[];
for(const entry of entries){
 const version=versions[entry.hashes.sha512];
 if(version){
  const file=version.files.find(f=>f.hashes.sha512===entry.hashes.sha512);
  const project=projects.find(p=>p.id===version.project_id);
  files.push({path:entry.path,hashes:entry.hashes,env:{client:project.client_side==='unsupported'?'unsupported':'required',server:project.server_side==='unsupported'?'unsupported':'required'},downloads:[file.url],fileSize:entry.data.length});
 }else if(entry.path==='mods/voodoo-ui-1.0.0+26.3.jar'){
  files.push({path:entry.path,hashes:entry.hashes,env:{client:'required',server:'unsupported'},downloads:['https://github.com/vodoohq/java-tide2-patch/releases/download/v2.1.1-voodoo.26.3-alpha.2/'+encodeURIComponent(path.basename(entry.path))],fileSize:entry.data.length});
  await fs.mkdir('build/downloads',{recursive:true});await fs.writeFile('build/downloads/'+path.basename(entry.path),entry.data);
 }else if(entry.path.startsWith('mods/'))throw new Error('Unrecognized mod: '+entry.path);
 else omitted.push(entry.path);
}
await fs.mkdir('pack',{recursive:true});
await fs.writeFile('pack/modrinth.index.json',JSON.stringify({formatVersion:1,game:'minecraft',versionId:'26.3-tide-alpha.2',name:'Voodoo SMP',summary:'Voodoo SMP with the full Tide 2 Fabric 26.3 alpha port.',files,dependencies:{minecraft:'26.3','fabric-loader':'0.19.5'}},null,2)+'\n');
// Only settings with a known purpose; generated identities, caches and telemetry are excluded.
const settings=['badoptimizations.txt','continuity.json','entityculling.json','forgeconfigapiport.toml','immediatelyfast.json','lithium.properties','lootr-client.jsonc','lootr-common.jsonc','simplehudenhanced.toml','sodium-extra-options.json','sodium-extra.properties','sodium-mixins.properties','sodium-options.json','toms_storage.json','travelersbackpack-client.toml','travelersbackpack-common.toml','travelersbackpack-server.toml','visualworkbench-client.toml','visualworkbench-server.toml','fancymenu/options.txt'];
for(const setting of settings){
 const destination=path.join('pack/client-overrides/config',setting);
 await fs.mkdir(path.dirname(destination),{recursive:true});
 const contents=await fs.readFile(path.join(profile,'config',setting),'utf8');
 await fs.writeFile(destination,contents.replace(/[ \t]+$/gm,''));
}
await fs.writeFile('pack/omitted-local-files.json',JSON.stringify(omitted,null,2)+'\n');
const packOptions=await fs.readFile(path.join(profile,'options.txt'),'utf8');
const selectedLine=packOptions.split(/\r?\n/).find(line=>line.startsWith('resourcePacks:'));
const includedPacks=new Set(files.filter(file=>file.path.startsWith('resourcepacks/')).map(file=>'file/'+path.basename(file.path)));
const selectedPacks=selectedLine?JSON.parse(selectedLine.slice('resourcePacks:'.length)).filter(id=>includedPacks.has(id)||['vanilla','continuity:default','continuity:glass_pane_culling_fix'].includes(id)):['vanilla'];
await fs.writeFile('pack/client-overrides/options.txt','resourcePacks:'+JSON.stringify(selectedPacks)+'\nincompatibleResourcePacks:[]\n');
console.log(`Captured ${files.length} verified dependencies; ${omitted.length} local resource packs excluded from distribution.`);
