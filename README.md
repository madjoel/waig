# WAIG: Where Am I Going
Fabric mod for Minecraft, adds a minimal RPG-inspired compass HUD to the game.
This mod is client side only.

![Demo Image](demo.gif)

**Features:**

- Always know in which direction your Minecraft character is going
- Toggleable via key binding (default: F6)
- Can be configured to show the HUD only when the player is holding a compass in any hand. See Configuration section 
  below for details.
- Does not obstruct boss bars by moving down
- Client side only
- Scales with the GUI

**Configuration:**

- Config file location: `<minecraft-base-folder>/config/waig.config`
- A default configuration is generated when the mod is started for the first time
- `only-show-hud-when-compass-in-hand`: boolean config key to enable the compass HUD only when the player holds a
  compass in the main hand or off-hand. **Default: false**
