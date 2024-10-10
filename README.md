<p align="center">
    <img src="images/icon@400.png" alt="Mod Logo" width="200"><br><br>
    <a href="https://discord.com/invite/MpynqpRN6p" rel="Discord"><img src="/images/discord-custom_vector.svg"></a>
    <a href="https://www.patreon.com/ltxprogrammer" rel="Discord"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/donate/patreon-singular_vector.svg"></a>
    <a href="https://modrinth.com/mod/changed-minecraft-mod" rel="Discord"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/modrinth_vector.svg"></a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/changed-minecraft-mod" rel="Discord"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/curseforge_vector.svg"></a>
</p>

---

This repository holds the source code for the **Changed: Minecraft Mod**. Releases are compiled and published to both Modrinth and Curseforge. Credits for contributors are available on [GitHub Insights](https://github.com/LtxProgrammer/Changed-Minecraft-Mod/graphs/contributors) and in the mod menu.

## How can I help?
Any aspiring developer is welcome to fork and create a pull request to submit their content. Programmers, texture artists, and 3D modelers all have a place here.
- Textures are kept in *src/main/resources/assets/changed/textures*
- Java code is in *src/main/java/net/ltxprogrammer/changed*
- 3D models are kept in *3dmodels*

Even if you aren't a developer, you can help with translations, documentation, or other simple issues. However, any changes you make to the code or files should be on your own fork. Create a pull request when you are ready to submit any changes.

## How can I make my own sub-mod?
First, grab the [Forge MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html), and install **Intellij**.
Changed:MC uses many mixins to alter code for compatibility and functionality.
Add this line to your buildscript dependencies and repositories (*build.gradle*):
```gradle
buildscript {
    repositories {
        maven { url = 'https://repo.spongepowered.org/repository/maven-public/' }
    }
    dependencies {
        classpath 'org.spongepowered:mixingradle:0.7-SNAPSHOT'
    }
}
```

And add it as a plugin (*build.gradle*):
```gradle
apply plugin: 'org.spongepowered.mixin'
```

Add this line in your repositories (*build.gradle*): 

```gradle
repositories {
    maven {
        name = "Changed"
        url = "https://raw.githubusercontent.com/LtxProgrammer/Changed-Minecraft-Mod/master/mcmodsrepo/"
    }
}
```
Add this line in **your dependencies** (*build.gradle*):

```gradle
dependencies {
    implementation fg.deobf("net.ltxprogrammer.changed:Changed-m${minecraftVersion}-f${forgeVersion}:${changedVersion}")
}
```
Make sure you specify which environment versions you are using in the line above. This can be done by directly subsituting (e.g. `Changed-m1.18.2-f40.2.0:v0.13.1`), or in you *gradle.properties*:
```properties
minecraftVersion = 1.18.2
forgeVersion = 40.2.0
changedVersion = v0.13.1
```

Then add a **mod dependency** to *mods.toml*:

```toml
[[dependencies.your_mod_id]]
    modId="changed"
    mandatory=true
    versionRange="[0.13.1]" # Replace with the version you plan to mod
    ordering="NONE"
    side="BOTH"
```

*Note: any issues relating to gradle/mixin should be properly be researched (Googled) before creating an issue.*

You'll now have access to **all the tools** within the Changed: Minecraft Mod, good luck!

## How can I compile the mod?

Without a IDE and assuming you have `git` installed and Java 17 as your default java:
- Clone the repo `git clone https://github.com/LtxProgrammer/Changed-Minecraft-Mod.git`
- Navigate into the directory `cd Changed-Minecraft-Mod`
- Run Gradlew `./gradlew build` (Linux/MacOS) or `gradlew build` (Windows)
- Once completed, check builds/libs for the results.

Now you can test the latest commit of the mod, enjoy.
