package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KJSCAuto.MODID)
public class KJSCAuto
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kjscauto";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // You can use SubscribeEvent and let the Event Bus discover methods to call
}
