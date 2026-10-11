// Regenerate version-specific resources without changing the upstream inputs.
import fs from 'node:fs';
import path from 'node:path';
const output='fabric-26.3/src/main/resources';
const inputs=['src/generated-1.21.1/resources','src/main/resources'];
const write=(relative,json)=>{const file=path.join(output,relative);fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,JSON.stringify(json,null,2)+'\n');};
const model=name=>({type:'minecraft:model',model:`tide:item/${name}`});
function files(dir){return fs.readdirSync(dir,{withFileTypes:true}).flatMap(entry=>entry.isDirectory()?files(path.join(dir,entry.name)):[path.join(dir,entry.name)]);}
function ingredient(value){
 if(Array.isArray(value))return value.map(ingredient);
 if(value&&typeof value==='object'){
  if(value.item)return value.item;
  if(value.tag)return '#'+value.tag;
 }
 return value;
}
const condition=values=>values.length===1?values[0]:{type:'minecraft:all_of',terms:values};
function renameItems(value) {
 if(value==='minecraft:chain')return 'minecraft:iron_chain';
 if(Array.isArray(value))return value.map(renameItems);
 if(value&&typeof value==='object')return Object.fromEntries(Object.entries(value).map(([k,v])=>[k,renameItems(v)]));
 return value;
}
function blockProvider(provider) {
 const state=s=>({id:s.Name||s.id,...((s.Properties||s.properties)?{properties:s.Properties||s.properties}:{})});
 if(provider.type==='minecraft:simple_state_provider')return state(provider.state);
 if(provider.type==='minecraft:weighted_state_provider')return {type:'minecraft:weighted',entries:provider.entries.map(e=>({...e,data:state(e.data)}))};
 return provider;
}
function loot(value){
 if(Array.isArray(value))return value.map(loot);
 if(!value||typeof value!=='object')return value;
 const result={};
 for(const [key,entry] of Object.entries(value)){
  if(key==='function'||(key==='condition'&&typeof entry==='string'))result.type=entry;
  else if(key==='conditions') {if(entry.length)result.condition=condition(entry.map(loot));}
  else if(key==='functions') {if(entry.length)result.modifier=entry.length===1?loot(entry[0]):entry.map(loot);}
  else if(key==='predicate'&&entry&&typeof entry==='object'){
   result.predicate=Object.fromEntries(Object.entries(entry).map(([k,v])=>[k.includes(':')?k:'minecraft:'+k,loot(v)]));
  } else result[key]=loot(entry);
 }
 return result;
}
let counts={models:0,recipes:0,loot:0};
const registeredItems=new Set([
 ...[...fs.readFileSync('src/main/java/com/li64/tide/registries/TideItems.java','utf8').matchAll(/register\("([\w]+)"/g)].map(m=>m[1]),
 ...[...fs.readFileSync('src/main/java/com/li64/tide/registries/TideFish.java','utf8').matchAll(/new Builder\("([\w]+)"/g)].flatMap(m=>[m[1],m[1]+'_spawn_egg',m[1]+'_bucket'])
]);
for(const input of inputs){
 for(const file of files(input).filter(f=>f.endsWith('.json'))){
  const relative=path.relative(input,file).replaceAll('\\','/');
  let data=renameItems(JSON.parse(fs.readFileSync(file,'utf8')));
  const optionalFishing=relative.match(/^data\/([^/]+)\/fishing\/(fish|loot|crates)\//);
  if(optionalFishing&&!['minecraft','tide'].includes(optionalFishing[1])){
   data.associated_mods=data.associated_mods||[optionalFishing[1]];
   write(relative,data);
  }
  if(relative.startsWith('assets/tide/models/item/')){
   const name=path.basename(file,'.json');
   delete data.overrides;
   if(name.startsWith('fishing_bobber'))data.textures={...data.textures,'1':data.textures?.['1']||'tide:item/red_fishing_bobber',particle:'#1'};
   if(name==='fish_sword')data.textures={...data.textures,layer0:data.textures?.layer0||'tide:item/fish_slice',particle:'#layer0'};
   if(name==='duck_bobber')data.textures.particle='#1';
   write(relative,data);
   let definition=model(name);
   if(name.endsWith('_fishing_rod'))definition={type:'minecraft:condition',property:'minecraft:fishing_rod/cast',on_false:model(name),on_true:model(name+'_cast')};
   if(name==='fish_satchel')definition={type:'minecraft:range_dispatch',property:'tide:satchel_state',fallback:model(name),entries:[{threshold:0.3,model:model(name+'_open')},{threshold:0.6,model:model(name+'_fish')},{threshold:1,model:model(name+'_two_fish')}]};
   if(name==='starlight_bow')definition={type:'minecraft:condition',property:'minecraft:using_item',on_false:model(name),on_true:{type:'minecraft:range_dispatch',property:'minecraft:use_duration',scale:0.05,fallback:model(name+'_pulling_0'),entries:[{threshold:0.65,model:model(name+'_pulling_1')},{threshold:0.9,model:model(name+'_pulling_2')}]}};
   write(`assets/tide/items/${name}.json`,{model:registeredItems.has(name)?definition:{type:'minecraft:empty'}});counts.models++;
  }
  if(relative.startsWith('assets/tide/models/block/jelly_')){
   data.textures.torch='tide:block/jelly_torch';write(relative,data);
  }
  if(relative.includes('/recipe/')){
   if(data.key)data.key=Object.fromEntries(Object.entries(data.key).map(([k,v])=>[k,ingredient(v)]));
   for(let key of ['ingredient','ingredients','base','addition','template'])if(data[key])data[key]=ingredient(data[key]);
   write(relative,data);counts.recipes++;
  }
  if(relative.includes('/loot_table/')){write(relative,loot(data));counts.loot++;}
  if(relative.includes('/fishing/crates/')&&data.block){data.block=blockProvider(data.block);write(relative,data);}
  if(relative.includes('/advancement/')){
   for(const criterion of Object.values(data.criteria||{})){
    const conditions=criterion.conditions;
    if(criterion.trigger==='minecraft:recipe_unlocked'&&conditions?.recipe){
     conditions.recipes=[conditions.recipe==='minecraft:obsidian_from_fragments'?'tide:obsidian_from_fragments':conditions.recipe];
     delete conditions.recipe;
    }
    if(Array.isArray(conditions?.player))conditions.player=condition(conditions.player.map(loot));
   }
   write(relative,data);
  }
 }
}
const torchTexture=path.join(output,'assets/tide/textures/block/jelly_torch.png');
fs.mkdirSync(path.dirname(torchTexture),{recursive:true});
fs.copyFileSync('src/main/resources/assets/tide/textures/item/jelly_torch.png',torchTexture);
write('pack.mcmeta',{pack:{description:'Tide 2 — Voodoo Fabric 26.3 port',min_format:[97,1],max_format:[97,1]}});
console.log(`Migrated ${counts.models} item models, ${counts.recipes} recipes, ${counts.loot} loot tables.`);
