# ExampleHologramPlugin
Depends on the [HologramLib](https://github.com/max1mde/HologramLib)


## Commands
Use `/testholos` with the following subcommands:

- `/testholos spawn <type> <id>` - Spawn a hologram (types: text, item, block, leaderboard)
- `/testholos remove <id>` - Remove a hologram
- `/testholos list` - List all active holograms
- `/testholos modify <id> <option> <value>` - Modify a hologram
  - Scale: `modify <id> scale <x> <y> <z>`
  - Translation: `modify <id> translation <x> <y> <z>`
  - Billboard: `modify <id> billboard <type>`
  - Text: `modify <id> text <new text>`
  - Item: `modify <id> item <item type>`
  - Block: `modify <id> block <material>`
  - Glow: `modify <id> glow <true/false>`
- `/testholos attach <id> <player>` - Attach hologram to player
- `/testholos viewer <id> <add/remove/clear> [player]` - Manage hologram viewers

![image](https://github.com/max1mde/ExampleHologramPlugin/assets/114857048/c1944d95-3787-45b4-98af-c27ded0e3444)
