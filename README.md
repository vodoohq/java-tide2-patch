# Tide 2 — Voodoo Fabric 26.3 port

A public, open source community port of Lightning64's full Tide 2 fishing mod for
Voodoo SMP. Fish, bait, rod upgrades, the journal, minigame, lava and void fishing,
crates and decorative displays are retained. This fork is currently an alpha.

**Download:** https://github.com/vodoohq/java-tide2-patch/releases

The 26.3 migration used substantial AI assistance for compatibility code, tooling,
tests, and documentation, with human direction and feedback. Original gameplay
and artwork are retained from upstream. See [the publication description](docs/MODRINTH-DESCRIPTION.md).

## Install

Download the Tide release jar and put it in your existing pack's or server's `mods/`
directory. Install
Fabric API 0.162.0+26.3 and Cloth Config 26.3.159 (Fabric). Use Minecraft 26.3,
Fabric Loader 0.19.5 and Java 25. The server and every player need this Tide version.
Remove any other Tide jar first. This repository distributes only the Tide 2 port;
Voodoo SMP is not released here.

## Build and verify

Install JDK 25. Generated resources are committed, so a normal build only needs Java.

```sh
./gradlew build
./gradlew :fabric-26.3:runGameTest
./gradlew :fabric-26.3:runClientGameTest
```

On Windows use `gradlew.bat`. On Linux run the client test inside `xvfb-run -a`
when no display is available. Outputs are under `fabric-26.3/build/libs/`.
GitHub Actions builds changes and runs server GameTests; version tags publish
prerelease assets. Client smoke tests pass on the local Windows graphics setup.
They are optional in a manually dispatched workflow because the hosted Linux
runner cannot currently create Minecraft 26.3's graphics context.

With Node.js installed, `node scripts/migrate-resources.mjs` regenerates the
Minecraft 26.3 resources from the preserved upstream inputs.
`node scripts/package-release.mjs` packages the Tide jar, source jar and checksums
in `build/tide-release/`. Public release uploads are restricted to those files.

For an isolated compatibility test against another pack, set `TIDE_PACK_MODS` to
its `mods` directory before running the client test. Its worlds are never opened.
The installed Xaero World Map version currently deadlocks with Fabric's test harness
when closing the test world. Set `TIDE_PACK_SKIP_WORLD_MAP=1` for a complete automated
compatibility run; this only excludes it from that test, not from your local pack.

## Status

Five Tide server tests plus Fabric's self-test cover fishing data, cast hooks,
crate/catch loot, persistence, accessories and note recipes. Client tests create
a temporary world and exercise actual hook/fish rendering and the fishing screens.
See [release notes](RELEASE-NOTES.md) for coverage and remaining playtesting.

Only **Fabric 26.3** is a supported build target in this fork. Legacy loader and
version sources remain for reference; their old builds are not maintained here.

## Credits and license

Original mod: [Lightning64's Tide 2](https://github.com/Lightning-64/Tide-2).
This port builds on [soleilfenley's 26.2 migration](https://github.com/soleilfenley/Tide-2/tree/26.2-port).
The 26.3 migration is maintained by [VoodooHQ](https://github.com/vodoohq).
This is an independent community fork.

Tide source and modifications retain [MPL-2.0](LICENSE.txt).
See [NOTICE.md](NOTICE.md) for attribution, and
[the preserved upstream README](docs/UPSTREAM-README.md) for the original feature guide.
