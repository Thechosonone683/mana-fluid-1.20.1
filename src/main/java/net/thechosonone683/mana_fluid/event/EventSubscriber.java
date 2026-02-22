package net.thechosonone683.mana_fluid.event;

import net.thechosonone683.mana_fluid.Config;
import net.thechosonone683.mana_fluid.Mana_fluid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.thechosonone683.mana_fluid.handler.ManaPoolFluidHandler;
import net.thechosonone683.mana_fluid.index.FluidRegistry;
import net.thechosonone683.mana_fluid.util.ResourceLocationHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

@Mod.EventBusSubscriber(modid = Mana_fluid.MODID)
public class EventSubscriber {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof ManaPoolBlockEntity pool) {
            ResourceLocation id = ResourceLocationHelper.of("mana_fluid");

            LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new ManaPoolFluidHandler(pool));

            ICapabilitySerializable<net.minecraft.nbt.CompoundTag> provider = new ICapabilitySerializable<>() {
                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, net.minecraft.core.Direction side) {
                    if (cap == ForgeCapabilities.FLUID_HANDLER) {
                        return fluidHandler.cast();
                    }
                    return LazyOptional.empty();
                }

                @Override
                public net.minecraft.nbt.CompoundTag serializeNBT() {
                    return new net.minecraft.nbt.CompoundTag();
                }

                @Override
                public void deserializeNBT(net.minecraft.nbt.CompoundTag nbt) {}
            };

            event.addCapability(id, provider);
            event.addListener(fluidHandler::invalidate);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockEntity tile = level.getBlockEntity(event.getPos());

        if (!(tile instanceof ManaPoolBlockEntity pool)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        BlockPos pos = event.getPos();

        final int manaDelta = Config.bucketManaAmount;
        boolean handled = false;

        // 只在服务端处理逻辑
        if (!level.isClientSide()) {
            if (held.getItem() == FluidRegistry.MANA_BUCKET.get() && !player.isCrouching()) {
                // 倒出魔力到池子
                if (pool.getMaxMana() - pool.getCurrentMana() >= manaDelta) {
                    pool.receiveMana(manaDelta);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        held.shrink(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!player.getInventory().add(emptyBucket)) {
                            player.drop(emptyBucket, false);
                        }
                    }
                    handled = true;
                }
            } else if (held.getItem() == Items.BUCKET) {
                // 从池子舀出魔力
                if (pool.getCurrentMana() >= manaDelta) {
                    pool.receiveMana(-manaDelta);
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        held.shrink(1);
                        ItemStack manaBucket = new ItemStack(FluidRegistry.MANA_BUCKET.get());
                        if (!player.getInventory().add(manaBucket)) {
                            player.drop(manaBucket, false);
                        }
                    }
                    handled = true;
                }
            }
        } else {
            // 客户端只处理动画和反馈
            if ((held.getItem() == FluidRegistry.MANA_BUCKET.get() && !player.isCrouching()) ||
                    held.getItem() == Items.BUCKET) {
                handled = true;
            }
        }

        if (handled) {
            player.swing(event.getHand());
            if (!level.isClientSide()) {
                pool.setChanged();
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    // 完全移除 onRightClickItem 方法，因为 RightClickBlock 已经能处理所有情况
}