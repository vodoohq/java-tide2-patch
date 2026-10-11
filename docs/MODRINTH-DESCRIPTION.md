# Tide 2 Community Port

An unofficial compatibility port of [Lightning64's Tide 2](https://modrinth.com/mod/tide)
for **Minecraft 26.3 on Fabric**. Its purpose is to upgrade the existing fishing
mod to this Minecraft version while retaining the original fishing features.

Tide adds over 100 fish, bait, fishing rods and accessories, an angling table,
a fishing journal, a catch minigame, loot crates, fish displays, and lava and void
fishing. This port updates the APIs, resource formats, rendering, and persistence
needed to run those features on 26.3.

## Installation

- Minecraft **26.3**, Fabric Loader **0.19.5 or later**, and **Java 25**.
- Install **Fabric API 0.162.0+26.3** and **Cloth Config 26.3.159 for Fabric**.
- Put the Tide port jar in `mods/` on the server and every player's client.
- Keep one Tide jar installed; this port uses the original `tide` mod ID.
- This is an **alpha**. Full multiplayer progression, balance, shaders, and
  optional third-party integrations still need playtesting.

## AI assistance and human involvement

This is a version upgrade of an existing mod, developed with a human in the loop
providing scope, direction, and feedback. Generative AI contributed substantially
to the 26.3 compatibility code, fixes, resource-conversion tooling, build scripts,
automated tests, and this project description. The original fishing gameplay and
artwork are retained from the upstream project. No new artwork was generated with
AI for this port or this listing. The disclosure describes the work on this port;
it does not characterize the upstream authors' development process.

## Credits and source

- **Lightning64 / Lightning-64**: original [Tide 2](https://github.com/Lightning-64/Tide-2).
- **soleilfenley**: the unfinished [Fabric 26.2 migration](https://github.com/soleilfenley/Tide-2/tree/26.2-port) this port builds on.
- **VoodooHQ**: the 26.3 community port and maintenance.
- **ExpensiveKoala**: the MIT-licensed Fishing Real helper adapted by upstream Tide;
  its license notice is included in the distribution.

This is an independent community fork. It is not an official Lightning64 release.
The Voodoo SMP modpack is not included or published with this project.

The port retains **MPL-2.0**, with the existing third-party MIT notice preserved.
[Source and license](https://github.com/vodoohq/java-tide2-patch),
[issue tracker](https://github.com/vodoohq/java-tide2-patch/issues),
and [release notes](https://github.com/vodoohq/java-tide2-patch/blob/voodoo/26.3-port/RELEASE-NOTES.md).
