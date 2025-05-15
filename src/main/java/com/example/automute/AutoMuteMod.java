
package com.example.automute;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("automute")
public class AutoMuteMod {
    private final InsultConfig config = new InsultConfig();

    public AutoMuteMod() {
        MinecraftForge.EVENT_BUS.register(new ChatEventHandler(config));
        MinecraftForge.EVENT_BUS.register(new ModCommands(config));
    }
}