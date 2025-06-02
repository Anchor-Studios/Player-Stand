# PlayerStand

<img src="https://img.shields.io/badge/Platform-Minecraft-blue" alt="Minecraft"> <img src="https://img.shields.io/badge/License-Custom-blue" alt="License"> <img src="https://img.shields.io/badge/Status-Alpha-orange" alt="Status">

**Minecraft mod** that adds a customizable, player-shaped armor stand that support skins, armor and interactions.

## Features

- Replaces armor stand model with the **player model**
- Applies **real Minecraft skins** via UUID (NBT or default)
- Keeps armor equipping behavior fully functional
- Visuals update live on the client
- Poseable and extendable for animation or decoration mods

## Compatibility

- 🎮 Minecraft 1.20.x  
- ⚙️ Works in singleplayer and multiplayer  

## How It Works

1. An armor stand is placed in the world.
2. If it contains a `SkinOwner` UUID tag, it will use that player’s skin.
3. The client renders it using the **player model** instead of the default armor stand.
4. Equipped armor is displayed just like on a real player.

## Installation

1. Download the mod JAR from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/player-stand) or [Modrinth](https://modrinth.com/mod/player-stand) pages.
2. Place it in the `mods/` folder of both your **server** and **client**.
3. Start Minecraft as usual.

## For Developers

### Requirements

- Java 17 or newer
- Gradle 7+
- Minecraft mod loader

### Build Steps

- Clone the repository using your preferred Git client.
- Open the project in your IDE (IntelliJ, Eclipse, etc.)
- Run the Gradle `build` task.
- The built JAR will be located in the `build/libs/` directory.

## License

This project is licensed under the [Custom License](LICENSE).

## Support

Have a question or bug to report?  
🐛 Open an issue [here](https://www.anchorstudios.site/issues).

---

Made by Anchor Studios
