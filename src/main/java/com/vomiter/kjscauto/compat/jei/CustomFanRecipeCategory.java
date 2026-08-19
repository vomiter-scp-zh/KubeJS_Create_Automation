package com.vomiter.kjscauto.compat.jei;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.vomiter.kjscauto.custom_process.FanType;
import com.vomiter.kjscauto.custom_process.KJSFanProcessingRecipe;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class CustomFanRecipeCategory extends ProcessingViaFanCategory<KJSFanProcessingRecipe> {

    private final FanType fanType;

    public CustomFanRecipeCategory(Info<KJSFanProcessingRecipe> info, FanType fanType) {
        super(info);
        this.fanType = fanType;
    }

    @Override
    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }

    @Override
    protected void renderAttachedBlock(@NotNull GuiGraphics graphics) {

        if(!fanType.getCatalystBlock().defaultBlockState().isAir()) {
            GuiGameElement.of(fanType.getCatalystBlock().defaultBlockState())
                    .scale(SCALE)
                    .atLocal(0, 0, 2)
                    .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                    .render(graphics);
        }

        else if (!fanType.getCatalystFluid().isSame(Fluids.EMPTY)) GuiGameElement.of(fanType.getCatalystFluid())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    public FanType getFanType() {
        return fanType;
    }

    @Override
    public @NotNull Component getTitle() {
        return fanType.getTitle();
    }


}
