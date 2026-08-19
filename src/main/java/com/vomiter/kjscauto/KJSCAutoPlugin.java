package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.custom_process.FanUtils;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import org.slf4j.Logger;


public class KJSCAutoPlugin implements KubeJSPlugin {
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void init() {
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(ContraptionEvents.GROUP);
        registry.register(KJSCAutoEvents.GROUP);
    }

    @Override
    public void initStartup() {
    }

    public void registerBindings(BindingRegistry event) {
        event.add("FanUtils", FanUtils.class);
    }


}
