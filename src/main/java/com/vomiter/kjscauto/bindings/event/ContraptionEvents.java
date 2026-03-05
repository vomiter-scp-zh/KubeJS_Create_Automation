package com.vomiter.kjscauto.bindings.event;

import com.vomiter.kjscauto.machine.ContraptionAfterBlockDestroyEventJS;
import com.vomiter.kjscauto.machine.ContraptionBeforeBlockDestroyEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface ContraptionEvents {
    EventGroup GROUP = EventGroup.of("ContraptionEvents");
    EventHandler BEFORE_BLOCK_DESTROY = GROUP.common("beforeBlockDestroy", () -> ContraptionBeforeBlockDestroyEventJS.class).hasResult();
    EventHandler AFTER_BLOCK_DESTROY = GROUP.common("afterBlockDestroy", () -> ContraptionAfterBlockDestroyEventJS.class).hasResult();
}
