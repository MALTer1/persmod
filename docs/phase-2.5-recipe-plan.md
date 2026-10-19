# Eclipse Ashes Phase 2.5 — Recipe and Production Plan

## Locked decisions
- Blacksmith Hammer: early-game, crafted from iron ingots and sticks.
- Mold: craft a reusable basic Mold, then configure material and part category at the Part Maker.
- Finished weapons: custom Eclipse Ashes weapon items with material-specific combat statistics.
- Balance: materials have distinct processing requirements and costs; progression is not just renamed ingots.
- Implementation: JSON for ordinary crafting recipes; Java for metadata, station interactions, validation, processing costs, and custom weapon behavior.
- Testing: implement one small slice at a time and play-test it before expanding.

## Existing production behavior to preserve
- Metal Forge processing takes 200 ticks (10 seconds at 20 ticks/second) per item.
- Each successful Metal Forge cycle consumes 1,000 mB of lava and one input item.
- The tank holds up to 10,000 mB (10 buckets).
- Vanilla iron/copper/gold ore and raw metals produce their ingots in one cycle.
- Vanilla iron/copper/gold ingots, Netherite ingots, and Diamonds become Heated Metal in one cycle.
- Custom metals use their currently implemented raw → unrefined → refined → finished-ingot stages where supported; finished custom ingots then become Heated Metal.
- Casting and workshop shaping are Java-driven mechanics. A configured Mold and Heated Metal must match materials. Weapon parts must all use the same material.
- Block forging is a separate workshop-anvil operation: nine matching supported ingots plus a Blacksmith Hammer.

## Initial ordinary crafting recipes
These are intentionally simple first-pass recipes. Adjust ingredient counts during playtesting if they make the early game too easy or grindy.

### Blacksmith Hammer
Pattern:
```
II 
IS 
 S 
```
- I = Iron Ingot
- S = Stick
- Output = 1 Blacksmith Hammer

### Basic Mold
Pattern:
```
III
I I
   
```
- I = Iron Ingot
- Output = 1 Mold
- The Mold is a reusable tool/item: the Part Maker writes its material and category metadata; crafting does not choose a material or category.

## Material progression proposal
This is a balance proposal, not a claim that every tier is fully implemented yet:
- **Tier 1 — Crimson Iron:** introductory custom metal; straightforward raw → unrefined → refined → ingot → Heated Metal.
- **Tier 2 — Moonsteel and Sunsteel:** mid-progression options; keep the existing stages, then tune ore rarity and acquisition conditions during world-generation work.
- **Tier 3 — Dragonite and Void Crystal:** advanced materials; give each a distinct cost or prerequisite rather than making both simple copies of Tier 1.
- **Tier 4 — Celestial Alloy and Eclipse:** endgame materials; reserve higher acquisition and processing costs for later balancing.
- Avoid arbitrary extra crafting steps before there is a concrete gameplay reason. Prefer explicit station recipes/cost data and readable guide entries.

## Implementation sequence
1. **Recipe foundation:** add the hammer and basic Mold JSON recipes; verify identifiers and recipe discovery in a clean test world.
2. **Station recipe audit:** document every supported Metal Forge input/output and the exact Part Maker, Casting Station, anvil, Assembly Station, and Maintenance Station costs. Fix guide descriptions when they disagree with code.
3. **Material costs:** implement distinct, explicit costs/prerequisites in Java for the special stations, starting with one introductory material and one advanced material; play-test before expanding.
4. **Custom weapons:** register custom weapon items and define material-specific attack damage/speed and durability using the project's Minecraft 26.3 APIs. Replace the current vanilla Iron Sword output only after the custom items are registered and tested.
5. **Progression expansion:** extend the validated data to the remaining materials; then update guide recipes and descriptions from the same source of truth where practical.

## Current known gap
The Maintenance Station currently outputs a renamed vanilla Iron Sword regardless of material. The chosen design is custom Eclipse Ashes weapons, so this is a required later implementation step, not a completed feature.
