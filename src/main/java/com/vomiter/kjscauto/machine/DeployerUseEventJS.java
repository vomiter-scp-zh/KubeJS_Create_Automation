package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DeployerUseEventJS extends LevelEventJS {
    private final Level level;
    private final BlockPos pos;
    private final DeployerBlockEntity blockEntity;
    private final TransportedItemStackHandlerBehaviour behaviour;

    private final ItemStack heldItem;
    private final TransportedItemStack transportedItemStack;
    private final ItemStack transportedItem;
    private final Recipe<?> recipe;
    private List<ItemStack> outputs = List.of();
    private ItemStack remainder = ItemStack.EMPTY;
    private int damage;
    private boolean cancelDamage;

    private BlockContainerJS block;

    public DeployerUseEventJS(
            DeployerBlockEntity blockEntity,
            TransportedItemStack transportedItemStack,
            TransportedItemStackHandlerBehaviour behaviour,
            Recipe<?> recipe
    ) {
        this.blockEntity = blockEntity;
        this.level = blockEntity.getLevel();
        this.heldItem = blockEntity.getPlayer().getMainHandItem();
        this.transportedItemStack = transportedItemStack;
        this.transportedItem = transportedItemStack.stack;
        this.behaviour = behaviour;
        this.damage = 1;
        this.pos = blockEntity.getBlockPos();
        this.recipe = recipe;
    }

    public void setDamage(int i) {
        this.damage = i;
        this.cancelDamage = (i <= 0);
    }

    public int getDamage() {
        return damage;
    }

    @HideFromJS
    public boolean kjs$isDamageCancelled() {
        return cancelDamage;
    }

    public BlockContainerJS getBlock() {
        if (block == null) {
            block = new BlockContainerJS(getLevel(), pos);
        }
        return block;
    }

    @Info("The item that the deployer holds.")
    public ItemStack getHeldItem() {
        return heldItem;
    }

    @Info("The item that is put on the deposit or belt.")
    public ItemStack getTransportedItem() {
        return transportedItem;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @HideFromJS
    public void kjs$setOutputs(List<ItemStack> outputs) {
        // 保險：copy，避免外面改到 Create 的內部物件
        List<ItemStack> copied = new ArrayList<>(outputs.size());
        for (ItemStack s : outputs) copied.add(s.copy());
        this.outputs = copied;
    }

    @HideFromJS
    public void kjs$setRemainder(ItemStack remainder) {
        this.remainder = remainder.copy();
    }

    @Info("All outputs produced by the recipe (may be empty or multiple).")
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Info("The remaining stack after consuming 1 item from transported input.")
    public ItemStack getRemainder() {
        return remainder;
    }
}
