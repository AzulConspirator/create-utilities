# Create Utilities NeoForge 1.21.1 Porting Record

Last updated: 2026-04-20

This document records the current Create Utilities source-port state and the migration patterns that were required to get the project compiling on NeoForge 1.21.1.

For the reusable generic template, use `C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\docs\neoforge-porting-record-template.md`.

## 1. Project Snapshot

### Current State
- Source root: `C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\transplant-workdir\source-ports\createutilities`
- Upstream mod name: Create Utilities
- Community port source used for intake: `kuraop-side/create-utilities-1.21.1-neoforge`
- Original upstream source line: `Duqueeee/create-utilities`
- Build status: `./gradlew.bat build` passes
- Runtime status: built and runtime-validated in the target client; the final pass fixed the creative-tab duplicate path and restored void-link frequency display sync
- Output artifact: `build/libs/createutilities-0.3.1.jar`
- Java baseline: 21
- Minecraft target: 1.21.1
- NeoForge target: 21.1.167
- Validation runtime actually used: NeoForge 21.1.227 target client and the live Create Delight Remake pack copy, with the rebuilt jar redeployed and user-validated on 2026-04-20

### Important Paths
| Purpose | Path |
|---|---|
| Source repo | `C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\transplant-workdir\source-ports\createutilities` |
| Active runtime mods folder | `C:\Users\weyst\Documents\mc\.minecraft\versions\1.21.1-NeoForge_21.1.227\mods` |
| Active runtime log | `C:\Users\weyst\Documents\mc\.minecraft\versions\1.21.1-NeoForge_21.1.227\logs\latest.log` |
| Pack mods folder copy | `C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\Create-Delight-Remake\mods` |

## 2. Intake Record
- Original loader and game version: Forge-era Create addon, community-carried toward NeoForge 1.21.1
- Target loader and game version: NeoForge 1.21.1
- Direct dependencies: Create 6.0.4-53, Ponder 1.0.46, Flywheel 1.0.2, Registrate MC1.21-1.3.0+62
- Dead, missing, renamed, or successor dependencies: none were the main blocker; the dominant blockers were API drift across NeoForge, Minecraft 1.21.1, and Create 6
- Highest-risk code areas: networking, capabilities, block entity serialization, mounted storage integration, client rendering, block interactions, clipboard behavior
- Highest-risk resource areas: none identified yet as primary blockers; this pass was mostly Java-side compile migration

## 3. Reusable Porting Process Used

### Phase A. Intake And Baseline
- Confirmed Java 21, Minecraft 1.21.1, NeoForge 21.1.167, and the Create 6 dependency stack from `gradle.properties`.
- Ran the local build first to expose the full compile-error surface instead of guessing from the source tree.

### Phase B. Dependency And Namespace Mapping
- Treated the community NeoForge 1.21.1 repo as a starting point, not as a validated finished port.
- Used NeoForge and Minecraft 1.21.1 API signatures as authoritative for networking, capabilities, codec, and serialization changes.

### Phase C. Compile Stabilization
- Replaced old Forge networking with NeoForge payload registration using `RegisterPayloadHandlersEvent`, `CustomPacketPayload`, `StreamCodec`, and `IPayloadContext`.
- Replaced block-entity `getCapability` overrides with `RegisterCapabilitiesEvent` and the NeoForge capability registration model.
- Migrated provider-aware save and load paths across block entities, `SavedData`, `ItemStackHandler`, `FluidTank`, and scene NBT writes.
- Updated Create 6 and Minecraft 1.21.1 drift around `MapCodec`, `useWithoutItem`, `useItemOn`, `ResolvableProfile`, `renderToBuffer`, and vertex emission.
- Fixed Registrate and Ponder generic drift plus mounted-storage codec shape changes.

### Phase D. Bootstrap And Registration Audit
- Rewired the main mod constructor to use `IEventBus`, NeoForge capability registration, and NeoForge event-bus listeners.
- Restored client setup through a guarded client init path and registered client/common event listeners explicitly.

### Phase E. Resource And Generated Data Audit
- Not the main blocker in this pass.
- No data-side regression surfaced during the final runtime validation pass; the remaining lessons were almost entirely Java-side registration and sync issues.

### Phase F. Runtime Validation
- Deployed the rebuilt jar into the target client and pack copy.
- Reproduced and fixed the runtime loader id mismatch, empty `ItemStack` sync crash, creative-tab duplicate-entry crash, missing custom tab behavior, and void-link frequency display regression.
- Treated the live client runtime and user validation as authoritative over the compile-only baseline.

### Phase G. Handoff
- The project is now compile-clean and runtime-validated in the target client.
- The remaining work is no longer Create Utilities stabilization; future changes should be treated as normal maintenance or regression work.

## 4. Chronological Port Record

### Step 1.
What was done:
- Audited the community port tree and ran a baseline build.
- Bucketed failures into networking, capabilities, serialization, rendering, interactions, and generics.

Why it mattered:
- The initial error count was large enough that single-line patching would have wasted time and obscured the real migration themes.

### Step 2.
What was done:
- Rewrote packet registration and clientbound packets to the NeoForge payload system.
- Replaced old Forge capability exposure with NeoForge capability event registration.

Why it mattered:
- These were core architectural breakpoints, not cosmetic renames.

### Step 3.
What was done:
- Migrated provider-aware NBT and `SavedData` paths across void chest, void tank, void battery, and shared link behavior.
- Updated clipboard and scene item-stack serialization to use modern registry-aware methods.

Why it mattered:
- Minecraft 1.21.1 moved serialization expectations into registry-aware APIs across many subsystems.

### Step 4.
What was done:
- Updated rendering and interaction call sites for Create 6 and Minecraft 1.21.1.
- Added `MapCodec` overrides to blocks that now require them.

Why it mattered:
- These failures show up as a mix of abstract-method and signature errors unless handled as a version-drift cluster.

### Step 5.
What was done:
- Patched mounted storage and Ponder generic drift.
- Replaced removed `FriendlyByteBuf` item/profile helpers and old `NbtUtils` game-profile helpers with explicit serialization.

Why it mattered:
- This cleared the final compile blockers and left the build green.

### Step 6.
What was done:
- Fixed the runtime metadata mismatch so the jar loaded under `createutilities` instead of the stale `create_utilities` id.
- Reworked the custom creative tab so Registrate remained the only content owner for `createutilities:base`.
- Restored safe void-link behaviour sync with `isSafeNBT()` plus `ItemStack.saveOptional(...)` and `parseOptional(...)` for frequency stacks.
- Redeployed the jar and validated the result in the target runtime.

Why it mattered:
- The real blockers after compile success were runtime registration ownership and client-sync semantics, not missing imports or signatures.

## 5. Change Summary
- Main bootstrap moved to NeoForge event registration and capability registration.
- Old Forge/Create packet code was replaced with NeoForge payloads.
- Block entities and `SavedData` were migrated to provider-aware serialization.
- Block interaction methods were updated to `useWithoutItem` and `useItemOn` where required.
- Several blocks and mounted storage types were updated to `MapCodec`-based registration.
- Client rendering calls were updated for 1.21.1 rendering signatures.
- `VoidLinkBehaviour` now advertises safe client NBT and mirrors Create 6 frequency-stack serialization via `ItemStack.saveOptional(...)`.
- `CUCreativeTabs` now stays a tab shell only, with Registrate left as the sole item-content owner for the custom tab.
- The project has been redeployed and runtime-validated in the target client.
- The project now builds successfully as `createutilities-0.3.1.jar`.

## 6. High-Value Files To Check First
| File | Why it matters |
|---|---|
| `src/main/java/me/duquee/createutilities/CreateUtilities.java` | Main mod bootstrap and capability registration entry. |
| `src/main/java/me/duquee/createutilities/networking/CUPackets.java` | NeoForge payload registration now lives here. |
| `src/main/java/me/duquee/createutilities/tabs/CUCreativeTabs.java` | The custom tab now only defines the tab shell; Registrate's existing creative-tab modifiers are still the active content owner, so manual `displayItems(...)` population will duplicate entries. |
| `src/main/java/me/duquee/createutilities/blocks/voidtypes/VoidLinkBehaviour.java` | Shared frequency, owner, clipboard, registry-aware stack serialization, and client-sync safety now live here. |
| `src/main/java/me/duquee/createutilities/ponder/VoidScenes.java` | Ponder scene item-stack writes need the same empty-safe serialization rules as gameplay code on 1.21.1. |
| `src/main/java/me/duquee/createutilities/blocks/voidtypes/motor/VoidMotorNetworkHandler.java` | Custom network-key serialization and saved-data key shape live here. |
| `src/main/java/me/duquee/createutilities/mountedstorage/VoidChestMountedStorage.java` | Mounted storage codec drift had to be updated to `MapCodec`. |
| `src/main/java/me/duquee/createutilities/ponder/CUPonders.java` | Ponder registration generics changed and will regress easily if touched casually. |

## 7. Failure Patterns
| Symptom | Root cause | Fix used | Reusable lesson |
|---|---|---|---|
| Old Forge networking classes unresolved | NeoForge 1.21.1 no longer uses the old `SimpleChannel` pattern here | Rebuilt the packet layer around payload handlers and stream codecs | Treat packet migration as an architectural rewrite, not a rename sweep |
| Block entities stopped compiling on `read` and `write` | 1.21.1 serialization is provider-aware | Threaded `HolderLookup.Provider` through block entities, `SavedData`, tank, and item storage paths | Provider-aware serialization is a repeated 1.21.1 migration theme |
| `codec()` abstract-method failures on blocks | Several block superclasses now require `MapCodec` overrides | Added `CODEC` fields and `codec()` overrides | Abstract block-method drift is easy to miss until late compile passes |
| Clipboard methods no longer matched | Create clipboard interfaces now require registry access | Updated clipboard signatures and stack parsing to use the provided registries | Interface drift often hides behind one or two abstract-method errors |
| `FriendlyByteBuf` item/profile helpers missing | The old convenience helpers are gone | Replaced them with explicit resource-id, UUID, and string serialization | Favor explicit wire formats when convenience helpers disappear |
| Creative inventory crashes when opening the inventory tab, and later the tab disappears entirely | `BuildCreativeModeTabContentsEvent` was already receiving Registrate-provided entries for `createutilities:base`, so a second manual `displayItems(...)` pass duplicated the same stacks | Keep `CUCreativeTabs` as the tab definition only, remove manual `displayItems(...)` population, and leave explicit `REGISTRATE.setCreativeTab(...)` calls out of `CUBlocks` and `CUItems` | For custom tabs on current Registrate/Create, settle on one content owner; if Registrate is already tagging items for the tab, a manual namespace scan will crash on duplicates |
| World tick crash with `Cannot encode empty ItemStack` | `VoidLinkBehaviour` and related scene helpers still called `ItemStack.save(...)` on empty frequency stacks, which 1.21.1 rejects during block-entity sync | Wrote empty-safe stack serializers that return an empty tag for empty stacks and normalized empty frequencies before storage | Any remaining `ItemStack.save(...)` call needs an explicit empty-stack guard on 1.21.1 |
| Frequency items still function server-side but do not render on the block | `VoidLinkBehaviour` was not marked safe for client-side behaviour NBT, and its custom frequency serialization had drifted from Create's own `saveOptional(...)` path | Restored `isSafeNBT()` and switched frequency writes back to `ItemStack.saveOptional(...)` with `parseOptional(...)` on read | If block functionality works but client visuals stay stale, check behaviour sync semantics before changing renderer code |

## 8. Rules That Saved Time
- Rebuild after each migration slice instead of trying to patch every suspected file first.
- Treat Create 6 and Minecraft 1.21.1 drift as separate from simple NeoForge package migration.
- Search for old serialization calls in bulk after the first provider-aware fix lands.
- Do not trust a public community port to be runtime-ready until it builds locally and survives the target client.

## 9. Commands Used

### Build
```powershell
cd "C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\transplant-workdir\source-ports\createutilities"
.\gradlew.bat build
```

### Deploy To Runtime
```powershell
Remove-Item "C:\Users\weyst\Documents\mc\.minecraft\versions\1.21.1-NeoForge_21.1.227\mods\createutilities-*.jar" -ErrorAction SilentlyContinue
Copy-Item "C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\transplant-workdir\source-ports\createutilities\build\libs\createutilities-0.3.1.jar" "C:\Users\weyst\Documents\mc\.minecraft\versions\1.21.1-NeoForge_21.1.227\mods\"
```

### Deploy To Pack Copy
```powershell
Remove-Item "C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\Create-Delight-Remake\mods\createutilities-*.jar" -ErrorAction SilentlyContinue
Copy-Item "C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\transplant-workdir\source-ports\createutilities\build\libs\createutilities-0.3.1.jar" "C:\Users\weyst\Documents\Minecraft-Create-Delight-Aero\Create-Delight-Remake\mods\"
```

## 10. Resume Checklist
1. Rebuild immediately to confirm the compile-clean baseline still holds.
2. If a regression appears, redeploy the jar to the target runtime and pack copy before retesting.
3. Recheck void chest, void tank, void battery, mounted contraption storage, and ponder scenes after any future Create or NeoForge version bump.
4. Read `latest.log` before changing code if any runtime issue appears.
5. If runtime issues appear around frequencies or ownership, check `VoidLinkBehaviour`, `VoidMotorNetworkHandler`, and `CUCreativeTabs` first.
6. Record any new regression here before touching another subsystem.

## 11. Known Risks
- The original community port snapshot carried a hardcoded `create_utilities` metadata id while the code and resources used `createutilities`; that mismatch is fixed here and should be kept aligned if metadata is regenerated later.
- `CUCreativeTabs` should stay a tab shell only; reintroducing manual `displayItems(...)` population on top of Registrate-driven item insertion can recreate duplicate creative entries. The exact builder path assigning items to `createutilities:base` is still implicit, so revisit Registrate internals first if the tab disappears again.
- The pack currently runs Create 6.0.9, Ponder 1.0.81, and Flywheel 1.0.6, but those exact Create/Ponder versions are not resolvable from the current Maven repositories used by this source tree; direct source compilation against the exact runtime jars would require local file dependencies or upstream Maven publication.
- The custom frequency serialization in `VoidMotorNetworkHandler.NetworkKey` stores item identity and count only, not full item components; that is probably fine for normal frequency items, but it is narrower than full `ItemStack` persistence.
- The build still emits deprecation warnings around Create's `DistExecutor` and `BlockBuilder.addLayer`; they do not block the build, but they should be revisited if the upstream APIs move again.