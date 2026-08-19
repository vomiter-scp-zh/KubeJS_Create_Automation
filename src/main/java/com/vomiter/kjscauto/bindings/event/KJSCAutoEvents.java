package com.vomiter.kjscauto.bindings.event;

import com.vomiter.kjscauto.custom_process.CustomFanJEIEventJS;
import com.vomiter.kjscauto.custom_process.CustomFanRegisterEventJS;
import com.vomiter.kjscauto.machine.*;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface KJSCAutoEvents {

    EventGroup GROUP = EventGroup.of("KJSCAutoEvents");
    EventHandler DEPLOYER_USE = GROUP.common("deployerUse", () -> DeployerUseEventJS.class);
    EventHandler ARM_COLLECT = GROUP.common("armCollect", () -> MechanicalArmInteractionEventJS.class);
    EventHandler ARM_DEPOSIT = GROUP.common("armDeposit", () -> MechanicalArmDepositEventJS.class);
    EventHandler BASIN_OPERATION = GROUP.common("basinOperation", () -> BasinOperationEventJS.class).hasResult();
    EventHandler BLOCK_DESTROY = GROUP.common("blockDestroy", () -> BlockDestroyEventJS.class).hasResult();

    EventHandler CUSTOM_FAN_REGISTRY = GROUP.startup("customFanRegistry", () -> CustomFanRegisterEventJS.class);
    EventHandler CUSTOM_FAN_JEI = GROUP.startup("customFanJei", () -> CustomFanJEIEventJS.class);

}
