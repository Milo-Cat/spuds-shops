## Spud's Shops

Spud's Shops is a Minecraft mod built for the NeoForge modding toolchain. It provides a simple decorative shop system for Minecraft 1.21.11, designed to add custom shop blocks and UI support for in-game vending and retail-style displays.

### What this project is

- A Minecraft mod project using NeoForge, Java 21, and Forge-compatible mappings.
- Developed as a lightweight shop mod with decorative shop blocks and GUI integration.
- Intended for use in custom Minecraft modpacks, private servers, or single-player worlds.

### Features

- Core shop functionality implemented in the mod source.
- Resources and generated mod metadata are managed through Gradle and NeoForge.
- Designed to be extended with custom shop blocks, renderers, and UI behaviors.

### Build and run

1. Install a compatible Java 21 JDK.
2. Run the mod using the NeoForge Gradle setup in this repository.
3. Use `./gradlew runClient` from the project root to start a development client.

### Project structure

- `build.gradle` - Gradle build configuration and NeoForge run setups.
- `gradle.properties` - Minecraft and mod metadata, including mod ID, version, and author.
- `src/main/java` - Java source files for the mod implementation.
- `src/main/resources` - Resource assets, data packs, and templates included in the mod.
- `src/generated/resources` - Generated resource outputs from the NeoForge build process.

### Additional resources

- GUI generator referenced for inspiration: https://github.com/Milo-Cat/SShops_GUI_Generator
