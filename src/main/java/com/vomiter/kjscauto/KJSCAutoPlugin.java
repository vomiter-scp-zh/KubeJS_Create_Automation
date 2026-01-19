package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import org.slf4j.Logger;


public class KJSCAutoPlugin extends KubeJSPlugin{
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void init() {
    }

    @Override
    public void registerEvents() {
        KJSCAutoEvents.GROUP.register();
    }

}
