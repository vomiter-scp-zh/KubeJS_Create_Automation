package com.vomiter.kjscauto.custom_process;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

@Info("Start up only." +
        "You may set JEI title, secondary icon item, catalyst block / fluid for a custom fan type in this event.")
public class CustomFanJEIEventJS implements KubeEvent {

    @Info("This has to be identical as the name used in CustomFanRegisterEvent(event => event.register(...))")
    public CustomFanJEIBuilder assign(String name){

        for (FanType type : FanType.getTypes()) {
            if (Objects.equals(type.typeName(), name)) return new CustomFanJEIBuilder(type);
        }
        ScriptType.STARTUP.console.error(name + "does not match any registered fan types.");

        return new CustomFanJEIBuilder();
    }

    public static class CustomFanJEIBuilder{
        private FanType fanType;
        private ItemStack secondaryIcon = ItemStack.EMPTY;
        private Block catalystBlock = Blocks.AIR;
        private Fluid catalystFluid = Fluids.EMPTY;
        private Component title = Component.empty();

        CustomFanJEIBuilder(FanType fanType){
            this.fanType = fanType;
        }

        CustomFanJEIBuilder(){
        }

        public CustomFanJEIBuilder setTitle(Component title) {
            this.title = title;
            return this;
        }


        public CustomFanJEIBuilder setSecondaryIcon(ItemStack secondaryIcon) {
            this.secondaryIcon = secondaryIcon;
            return this;
        }

        public CustomFanJEIBuilder setCatalystBlock(Block catalystBlock) {
            this.catalystBlock = catalystBlock;
            return this;
        }

        public CustomFanJEIBuilder setCatalystFluid(Fluid catalystFluid) {
            this.catalystFluid = catalystFluid;
            return this;
        }

        public void save(){
            if (fanType == null) return;
            fanType.setCatalystBlock(catalystBlock);
            fanType.setCatalystFluid(catalystFluid);
            fanType.setSecondaryIcon(secondaryIcon);
            fanType.setTitle(title);
        }

    }


}
