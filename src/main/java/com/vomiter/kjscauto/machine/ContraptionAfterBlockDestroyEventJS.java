package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ContraptionAfterBlockDestroyEventJS extends ContraptionBeforeBlockDestroyEventJS{

    List<ItemStack> drops;

    public ContraptionAfterBlockDestroyEventJS(Level level, AbstractContraptionEntity contraptionEntity, BlockPos actorPos, BlockState actorState, BlockPos targetPos, float animationSpeed, float breakSpeed, float hardness) {
        super(level, contraptionEntity, actorPos, actorState, targetPos, animationSpeed, breakSpeed, hardness);
    }

    public List<ItemStack> getDrops(){
        return drops;
    }

    @HideFromJS
    public void kjs$setDrops(List<ItemStack> outputs) {
        List<ItemStack> copied = new ArrayList<>(outputs.size());
        for (ItemStack s : outputs) copied.add(s.copy());
        this.drops = copied;
    }

    @Info("This event is not cancellable. You could clear the drop list instead. Or use the 'before' version of this event.")
    @Override
    public Object cancel(Context context) throws EventExit {
        return super.cancel(context);
    }

}
