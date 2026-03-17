package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
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

}
