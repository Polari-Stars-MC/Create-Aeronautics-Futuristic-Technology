# Change Log

## 2026-04-24

- Added Future Energy Core system
- Registered `future_energy_core`, `energy_conduit`, and `heat_sink`
- Added `FutureEnergyCoreBlockEntity` with tick logic, NBT sync, multiblock validation, AF storage, and overheat control
- Added controller block states `ACTIVE` and `INTEGRITY`
- Added multiblock structure check for 4 conduits and 12 heat sinks around the controller
- Added AF config values for capacity, efficiency, heat, input, output, and scan interval
- Added client sync packet and block entity glow renderer
- Added JEI and EMI compatibility hook placeholders
- Added blockstate, model, item model, and localization resource files

