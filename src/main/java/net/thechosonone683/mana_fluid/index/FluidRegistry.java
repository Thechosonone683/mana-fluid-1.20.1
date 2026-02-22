package net.thechosonone683.mana_fluid.index;

import net.thechosonone683.mana_fluid.Mana_fluid;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thechosonone683.mana_fluid.util.ResourceLocationHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import java.awt.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import java.util.function.Consumer;

public class FluidRegistry {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Mana_fluid.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.Keys.FLUIDS, Mana_fluid.MODID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, Mana_fluid.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.Keys.ITEMS, Mana_fluid.MODID);

    // 1. 先注册 FluidType
    public static final RegistryObject<FluidType> MANA_TYPE = FLUID_TYPES.register(
            "mana_type",
            () -> new TransparentRenderedPlaceableFluidType(
                    FluidType.Properties.create()
                            .density(500)
                            .viscosity(1500)
                            .canExtinguish(true)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                            .supportsBoating(true),
                    new ResourceLocation(Mana_fluid.MODID, "block/mana_still"),
                    new ResourceLocation(Mana_fluid.MODID, "block/mana_flow")
            )
    );

    // 2. 声明 Properties，但暂时不初始化
    private static ForgeFlowingFluid.Properties manaProperties;

    // 3. 注册流体（使用静态初始化块确保 Properties 在使用前被创建）
    public static final RegistryObject<ForgeFlowingFluid> MANA_FLUID = FLUIDS.register(
            "mana",
            () -> {
                initProperties();
                return new ForgeFlowingFluid.Source(manaProperties);
            }
    );

    public static final RegistryObject<ForgeFlowingFluid> MANA_FLUID_FLOWING = FLUIDS.register(
            "mana_flowing",
            () -> {
                initProperties();
                return new ForgeFlowingFluid.Flowing(manaProperties);
            }
    );

    // 4. 注册流体方块和桶（使用 MANA_FLUID 作为 Supplier）
    public static final RegistryObject<LiquidBlock> MANA = BLOCKS.register(
            "mana_block",
            () -> new ManaFluidBlock(
                    MANA_FLUID,
                    BlockBehaviour.Properties.copy(Blocks.WATER)
            )
    );

    public static final RegistryObject<Item> MANA_BUCKET = ITEMS.register(
            "mana_bucket",
            () -> new BucketItem(
                    MANA_FLUID,
                    new Item.Properties()
                            .stacksTo(1)
                            .craftRemainder(net.minecraft.world.item.Items.BUCKET)
            )
    );

    // 5. 初始化 Properties 的方法
    private static void initProperties() {
        if (manaProperties == null) {
            manaProperties = new ForgeFlowingFluid.Properties(
                    MANA_TYPE,
                    MANA_FLUID,
                    MANA_FLUID_FLOWING
            )
                    .slopeFindDistance(5)
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .explosionResistance(100f)
                    .bucket(MANA_BUCKET)
                    .block(MANA);
        }
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    // 流体方块类
    public static class ManaFluidBlock extends LiquidBlock {
        public ManaFluidBlock(RegistryObject<ForgeFlowingFluid> fluidSupplier, BlockBehaviour.Properties properties) {
            super(fluidSupplier, properties);
        }
    }

    // 抽象的染色流体类型
    public static abstract class TintedFluidType extends FluidType {
        protected static final int NO_TINT = 0xffffffff;
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;

        public TintedFluidType(Properties properties,
                               ResourceLocation stillTexture,
                               ResourceLocation flowingTexture) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        @Override
        public void initializeClient(Consumer<net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
            consumer.accept(new net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                @Override
                public int getTintColor(FluidStack stack) {
                    return TintedFluidType.this.getTintColor(stack);
                }

                @Override
                public int getTintColor(FluidState state, net.minecraft.world.level.BlockAndTintGetter getter, BlockPos pos) {
                    return TintedFluidType.this.getTintColor(state, getter, pos);
                }

                @Override
                public @NotNull Vector3f modifyFogColor(
                        Camera camera,
                        float partialTick,
                        ClientLevel level,
                        int renderDistance,
                        float darkenWorldAmount,
                        Vector3f fluidFogColor
                ) {
                    Vector3f custom = TintedFluidType.this.getCustomFogColor();
                    return custom == null ? fluidFogColor : custom;
                }
            });
        }

        protected abstract int getTintColor(FluidStack stack);
        protected abstract int getTintColor(FluidState state, net.minecraft.world.level.BlockAndTintGetter getter, BlockPos pos);
        protected Vector3f getCustomFogColor() {
            return null;
        }
    }

    // 透明的魔力流体类型
    private static class TransparentRenderedPlaceableFluidType extends TintedFluidType {
        private final Vector3f fogColor;

        public TransparentRenderedPlaceableFluidType(Properties properties,
                                                     ResourceLocation stillTexture,
                                                     ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
            Color c = new Color(0x36C2E0);
            this.fogColor = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
        }

        @Override
        public String getDescriptionId() {
            return "fluid_type." + Mana_fluid.MODID + ".mana_fluid";
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        @Override
        public int getTintColor(FluidState state, net.minecraft.world.level.BlockAndTintGetter world, BlockPos pos) {
            return 0xEE2FD0FF;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return fogColor;
        }
    }
}