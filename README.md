# Changed: Minecraft Mod
This repository holds the source code for the Changed: Minecraft Mod. Releases are compiled and published to our [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/changed-minecraft-mod) and [Modrinth page](https://modrinth.com/mod/changed-minecraft-mod).

# How can I help?
Any aspiring developer is welcome to fork and create a pull request to submit their content. Programmers, texture artists, and 3D modelers all have a place here.
- Textures are kept in *src/main/resources/assets/changed/textures*
- Java code is in *src/main/java/net/ltxprogrammer/changed*
- 3D models are kept here *src/main/java/net/ltxprogrammer/changed/client/renderer/model*

Changes you make to the code should be on your own fork. Create a pull request when ready to submit.

# I want to make my own sub mod
Alright, grab the [forge MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html), and install intellij. Add this line in your repositories (*build.gradle*): 

```gradle
maven {
    name = "Changed"
    url = "https://raw.githubusercontent.com/LtxProgrammer/Changed-Minecraft-Mod/master/mcmodsrepo/"
}
```
Add this line in your dependancies (*build.gradle*):

```gradle
implementation fg.deobf("net.ltxprogrammer.changed:Changed-m${minecraftVersion}-f${forgeVersion}:${changedVersion}")
```
Then add a mod dependancy to *mods.toml*:

```toml
[[dependencies.your_mod_id]]
    modId="changed"
    mandatory=true
    versionRange="[0.10b]" # Replace 0.10b with the version you plan to mod
    ordering="NONE"
    side="BOTH"
```

You'll now have access to all the tools within the Changed: Minecraft Mod.
