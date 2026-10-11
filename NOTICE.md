# Attribution and licensing

Tide 2 is originally developed by **Lightning64 / Lightning-64**:
https://github.com/Lightning-64/Tide-2 and https://modrinth.com/mod/tide.
The original upstream source at `876b95f31328f4e698d5150f7d840ab033d1b06d` is preserved in Git history.

This port builds on **soleilfenley**'s unfinished Fabric 26.2 migration:
https://github.com/soleilfenley/Tide-2/tree/26.2-port, commit
`cf13f1a1386589c34c2641c7f0fb16e233e3d6c9`. Thank you for the migration work.

The Fabric 26.3 migration and Voodoo SMP integration are maintained by **VoodooHQ**.
This is an independent community fork, not an official Lightning64 release.

Tide source and modifications remain under the **Mozilla Public License 2.0**,
provided in LICENSE.txt. Downloadable binaries include that license; corresponding
source is available here and as a release sources jar.

Releases distribute only the Tide port and its corresponding source. Required
dependencies such as Fabric API and Cloth Config retain their own licenses and
are installed separately. Voodoo SMP and its companion UI are not released here.

The upstream `compat/fishingreal/FishingConversion.java` helper adapts code from
**ExpensiveKoala's Fishing Real**, licensed under MIT:
https://github.com/ExpensiveKoala/Fishing-Real. Its original MIT copyright and
license are preserved in `licenses/Fishing-Real-MIT.txt` and bundled with the jars.

The 26.3 compatibility migration used substantial generative AI assistance for
code, resource-conversion tooling, build scripts, tests, and documentation, with
a human in the loop providing scope, direction, and feedback. Original gameplay
and artwork are retained from upstream. No new AI artwork was introduced.
