package net.thechosonone683.mana_fluid.index;

import net.thechosonone683.mana_fluid.Mana_fluid;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Mana_fluid.MODID);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
