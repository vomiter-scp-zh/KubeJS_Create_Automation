package com.vomiter.kjscauto.bindings.event;

import com.vomiter.kjscauto.machine.*;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface KJSCAutoEvents {

    EventGroup GROUP = EventGroup.of("KJSCAutoEvents");
    EventHandler DEPLOYER_USE = GROUP.common("deployerUse", () -> DeployerUseEventJS.class);
    EventHandler BASIN_OPERATION = GROUP.common("basinOperation", () -> BasinOperationEventJS.class).hasResult();
    EventHandler BLOCK_DESTROY = GROUP.common("blockDestroy", () -> BlockDestroyEventJS.class).hasResult();
}
