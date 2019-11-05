package me.dags.relighter;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "relighter", serverSideOnly = true, acceptableRemoteVersions = "*")
public class Relighter {

    @Mod.EventHandler
    public static void init(FMLServerStartingEvent event) {
        event.registerServerCommand(new RelightCommand());
    }
}
