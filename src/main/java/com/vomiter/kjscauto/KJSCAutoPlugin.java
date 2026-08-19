package com.vomiter.kjscauto;

import com.mojang.logging.LogUtils;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.custom_process.FanUtils;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import org.slf4j.Logger;


public class KJSCAutoPlugin extends KubeJSPlugin{
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void init() {
    }

    @Override
    public void registerEvents() {
        ContraptionEvents.GROUP.register();
        KJSCAutoEvents.GROUP.register();
    }

    @Override
    public void initStartup() {
    }

    public void registerBindings(BindingsEvent event) {
        event.add("FanUtils", FanUtils.class);
    }


}
