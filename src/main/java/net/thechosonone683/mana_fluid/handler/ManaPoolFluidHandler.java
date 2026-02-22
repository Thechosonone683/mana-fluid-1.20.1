package net.thechosonone683.mana_fluid.handler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.thechosonone683.mana_fluid.Config;
import net.thechosonone683.mana_fluid.index.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

public class ManaPoolFluidHandler implements IFluidHandler {

    private final ManaPoolBlockEntity pool;
    private int accumulatedMana; // 改用整数存储，避免浮点精度问题

    public ManaPoolFluidHandler(ManaPoolBlockEntity pool) {
        this.pool = pool;
        this.accumulatedMana = pool.getCurrentMana();
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidStack.EMPTY;
        }
        resyncFromPool();

        // 整数除法，向下取整
        int wholeMB = accumulatedMana / Config.manaPerMB;
        return new FluidStack(FluidRegistry.MANA_FLUID.get(), wholeMB);
    }

    @Override
    public int getTankCapacity(int tank) {
        return (tank == 0) ? pool.getMaxMana() / Config.manaPerMB : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (tank != 0) return false;
        return stack.getFluid().equals(FluidRegistry.MANA_FLUID.get());
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.getFluid().equals(FluidRegistry.MANA_FLUID.get())) {
            return 0;
        }
        resyncFromPool();

        int requestedMB = resource.getAmount();
        int requestedMana = requestedMB * Config.manaPerMB;
        int availableSpace = pool.getMaxMana() - accumulatedMana;

        int manaToAdd = Math.min(requestedMana, availableSpace);
        int mbToAdd = manaToAdd / Config.manaPerMB;

        // 确保不会因为整除问题添加超过容量的魔力
        if (manaToAdd > 0 && action == FluidAction.EXECUTE) {
            pool.receiveMana(manaToAdd);
            pool.setChanged();
            accumulatedMana += manaToAdd;
        }

        return mbToAdd;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.getFluid().equals(FluidRegistry.MANA_FLUID.get())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        resyncFromPool();

        int maxDrainMana = maxDrain * Config.manaPerMB;
        int manaToRemove = Math.min(maxDrainMana, accumulatedMana);
        int mbToRemove = manaToRemove / Config.manaPerMB;

        // 确保移除的魔力是 Config.manaPerMB 的整数倍
        int actualManaRemoved = mbToRemove * Config.manaPerMB;

        if (actualManaRemoved > 0 && action == FluidAction.EXECUTE) {
            pool.receiveMana(-actualManaRemoved);
            pool.setChanged();
            accumulatedMana -= actualManaRemoved;
        }

        return new FluidStack(FluidRegistry.MANA_FLUID.get(), mbToRemove);
    }

    private void resyncFromPool() {
        accumulatedMana = pool.getCurrentMana();
    }
}