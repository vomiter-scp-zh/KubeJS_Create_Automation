package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.custom_process.CustomFanJEIEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KJSCAuto.MODID)
public class KJSCAuto
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kjscauto";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceLocation modloc(String path){
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static IEventBus modBus;

    public KJSCAuto(FMLJavaModLoadingContext context) {
        var modBus = context.getModEventBus();
        LOGGER.info("KJSCauto Initiated as a mod.");
        modBus.addListener(this::onRegisterEvent);
        modBus.addListener(this::onCommonSetup);
        KJSCAuto.modBus = modBus;
    }

    void onRegisterEvent(RegisterEvent event){
    }

    void onCommonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            KJSCAutoEvents.CUSTOM_FAN_JEI.post(ScriptType.STARTUP, new CustomFanJEIEventJS());
        });
    }
}
