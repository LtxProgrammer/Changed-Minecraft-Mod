![A laboratory in the Changed:MC mod.](https://i.imgur.com/r713ONU.png)</a> <a href="https://discord.com/invite/MpynqpRN6p" rel="Discord">![Discord](https://raw.githubusercontent.com/Y1rd/Y1rd/main/discord-custom_vector.svg)</a>  <a href="https://www.patreon.com/ltxprogrammer" rel="Patreon">![Discord](https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/donate/patreon-singular_vector.svg)</a> <a href="https://modrinth.com/mod/changed-minecraft-mod" rel="Patreon">![Discord](https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/modrinth_vector.svg)</a> <a href="https://www.curseforge.com/minecraft/mc-mods/changed-minecraft-mod" rel="Patreon">![Discord](https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/curseforge_vector.svg)</a>

This repository holds the source code for the **Changed: Minecraft Mod**. Releases are compiled and published to both mod hosting websites.

All credits for contributors are available on [GitHub Insights](https://github.com/LtxProgrammer/Changed-Minecraft-Mod/graphs/contributors) and in the mod menu.

## How can I help?
Any aspiring developer is welcome to fork and create a pull request to submit their content. **Programmers, texture artists, and 3D modelers** all have a place here.
- Textures are kept in *src/main/resources/assets/changed/textures*
- Java code is in *src/main/java/net/ltxprogrammer/changed*
- 3D models are kept in *src/main/java/net/ltxprogrammer/changed/client/renderer/model*

Even if **you aren't a developer**, you can help with **translations, documentation, or other simple issues**. However, any changes you make to the code or files should be on your own fork. **Create a pull request** when you are ready to submit any changes.

## How can I make my own sub-mod?
First, grab the [Forge MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html), and install **Intellij**. Add this line in your repositories (*build.gradle*): 

```gradle
maven {
    name = "Changed"
    url = "https://raw.githubusercontent.com/LtxProgrammer/Changed-Minecraft-Mod/master/mcmodsrepo/"
}
```
Add this line in **your dependencies** (*build.gradle*):

```gradle
implementation fg.deobf("net.ltxprogrammer.changed:Changed-m${minecraftVersion}-f${forgeVersion}:${changedVersion}")
```
Then add a **mod dependency** to *mods.toml*:

```toml
[[dependencies.your_mod_id]]
    modId="changed"
    mandatory=true
    versionRange="[0.12b]" # Replace with the version you plan to mod
    ordering="NONE"
    side="BOTH"
```

You'll now have access to **all the tools** within the Changed: Minecraft Mod, good luck!

## General FAQ

**I'm trying to make the accessory mod to your mod, but I can't start the game because of the mixin.**
Can you please check if this line of code is missing?

```
buildscript {
    repositories {
        maven { url = 'https://maven.minecraftforge.net' }
        mavenCentral()
    }
    dependencies {
        classpath group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '5.1.+', changing: true
        classpath 'org.spongepowered:mixingradle:0.7.+'
    }
}
apply plugin: 'net.minecraftforge.gradle'
apply plugin: 'eclipse'
apply plugin: 'maven-publish'
apply plugin: 'org.spongepowered.mixin'
```
```
dependencies {
    minecraft 'net.minecraftforge:forge:1.18.2-40.2.17'
    implementation fg.deobf("net.ltxprogrammer.changed:Changed-m1.18.2-f40.2.0:v0.13.1")

    annotationProcessor 'org.spongepowered:mixin:0.8.5:processor'
}
```

**Will there be a Fabric/Quilt version?**

There are no current plans to port to another modloader, this is subject to change.

**Can you port the mod to 1.xx.x?**

We will not be porting the mod until it is deemed complete.

**Can you make the mod for Pocket Edition?**

If you're asking this, you're probably too young to be getting into Changed...

**What mods are compatible?**

There (hopefully) shouldn't be too many mods that are incompatible. If you do find a mod which behaves weird with Changed:MC, feel free to report it.

**What are the special forms for?**

Anyone who is a Tier 4 Patron get to design their own transfur variant exclusive to them, otherwise, the special syringe doesn't do anything.

**Will I lose my special form when I'm no longer a patron?**

No, but you will lose the in-game title and access to some discord channels.
