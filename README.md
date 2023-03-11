# WAIG: Where Am I Going
Fabric mod for Minecraft, adds a minimal RPG-inspired compass HUD to the game.
This mod is client side only.

![Demo Image](demo.gif)

**Features:**

- Always know in which direction your Minecraft character is going
- Toggleable via key binding (default: F6)
- Can be configured to show the HUD only when the player carries a compass in the inventory or is holding a compass in
  any hand. See Configuration section below for details.
- Does not obstruct boss bars by moving down
- Client side only
- Scales with the GUI

**Configuration:**

- Config file location: `<minecraft-base-folder>/config/waig.config`
- A default configuration is generated when the mod is started for the first time or if the config file is missing
- `hud-show-mode`: config key to enable the compass HUD only in certain circumstances (default: `always`)
  - if set to `always` the HUD will always be displayed
  - if set to `inventory` the HUD will be displayed only if the player carries a compass in the inventory
  - if set to `hand` the HUD will be displayed only if the player holds a compass in the main hand or off-hand
- `compass-items`: config key to list all items that count as compass equivalent. When `hud-show-mode` is set to
  `inventory` or `hand`, the mod checks if any of the listed items are in the inventory or held, and if so will show
  the HUD.