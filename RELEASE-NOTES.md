# Tide 2 Fabric 26.3 — Voodoo alpha 2

Full community port of Tide 2 for Minecraft **26.3**, Fabric Loader **0.19.5**,
Fabric API **0.162.0+26.3**, Cloth Config **26.3.159**, and **Java 25**.

Retains Tide's fish collection, bait, rods and accessories, fishing journal,
minigame, lava and void fishing, crates, angling table, and decorative displays.
Migrates entity persistence, rendering, loot, recipes, components and village rod
trades to Minecraft's current APIs and resource formats.

## Downloads

- `tide-fabric-26.3-2.1.1+voodoo.26.3.alpha.2.jar`: manual installation into an
  existing Fabric 26.3 instance or dedicated server, alongside Fabric API and Cloth
  Config. Install the same Tide version on server and clients. Keep only one Tide jar.
- `*-sources.jar`: corresponding port source. `SHA256SUMS.txt`: file checksums.

This release contains only the Tide 2 port. Voodoo SMP and its companion UI are
not distributed here.

## Validation and scope

Five Tide server GameTests plus Fabric's required self-test cover data loading,
casting and actual crate/catch loot, rod components, journal/fish/display
persistence, note crafting and late minigame packets. Client smoke tests exercise
fish and hook rendering, casting synchronization, journal, angling table and
minigame screens in an isolated world. A separate client run loads the installed
Voodoo SMP mods alongside Tide. Rendering checks complete with all installed mods;
Xaero World Map causes a Fabric test-harness deadlock when closing the temporary
world. The compatibility run completes successfully with that mod excluded. This
does not establish a problem during a normal launcher session; that still needs
manual playtesting.
The automatic GitHub workflow verifies compilation and server GameTests. Client
graphics tests were verified locally; the hosted Linux runner cannot currently
create the game's graphics context and that check is optional there.

This is an **alpha**, not a completed hardcore SMP playthrough. Multiplayer
progression, balance, every species/biome combination, shaders and optional
third-party fishing/season/accessory integrations still need playtesting.

Original Tide 2 by Lightning64; 26.2 migration by soleilfenley; 26.3 community port
by VoodooHQ. Source and license: https://github.com/vodoohq/java-tide2-patch.
