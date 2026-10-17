# GameTest-Fabric

**Download: [Modrinth](https://modrinth.com/datapack/gametest-fabric/versions)**

**For minecraft 26.3!**

Allows datapack developers to use the gameTest Minecraft environment with mods.

## Running GameTests
1. install the current mod jar into a fabric server's `mods` directory:
2. Run the server with:
```sh
java -DbundlerMainClass="net.minecraft.gametest.Main" -jar server.jar --packs "<path/to/dir/with/datapacks>"
```