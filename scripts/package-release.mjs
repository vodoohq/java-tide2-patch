// Public releases contain only the Tide port and its corresponding source.
import fs from 'node:fs/promises';
import path from 'node:path';
import crypto from 'node:crypto';

const build = await fs.readFile('fabric-26.3/build.gradle', 'utf8');
const version = build.match(/^version\s*=\s*'([^']+)'/m)?.[1];
if (!version) throw new Error('Cannot determine Tide version');
const jar = `tide-fabric-26.3-${version}.jar`;
const names = [jar, jar.replace('.jar', '-sources.jar')];
const output = 'build/tide-release';
await fs.mkdir(output, {recursive: true});
const allowed = new Set([...names, 'SHA256SUMS.txt']);
for (const existing of await fs.readdir(output)) {
    if (!allowed.has(existing)) throw new Error(`Move stale output out of ${output}: ${existing}`);
}
const checksums = [];
for (const name of names.sort()) {
    const bytes = await fs.readFile(path.join('fabric-26.3/build/libs', name));
    await fs.writeFile(path.join(output, name), bytes);
    checksums.push(`${crypto.createHash('sha256').update(bytes).digest('hex')}  ${name}`);
}
await fs.writeFile(path.join(output, 'SHA256SUMS.txt'), checksums.join('\n') + '\n');
console.log(`Packaged ${names.length} Tide jars and checksums in ${output}.`);
