package net.thechosonone683.mana_fluid;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.thechosonone683.mana_fluid.handler.FogHandler;
import net.thechosonone683.mana_fluid.index.FluidInteractionsRegistry;
import net.thechosonone683.mana_fluid.index.FluidRegistry;
import org.slf4j.Logger;

@Mod(Mana_fluid.MODID)
public class Mana_fluid {

    public static final String MODID = "mana_fluid";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Mana_fluid() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // 注册模组内容
        FluidRegistry.register();

        eventBus.addListener(this::addCreative);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FluidInteractionsRegistry.register();
            // 打印配置信息
            LOGGER.info("[Mana Fluid] Config loaded: manaPerMB={}, bucketManaAmount={}",
                    Config.manaPerMB, Config.bucketManaAmount);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 将魔力桶添加到工具标签页
        if(event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(FluidRegistry.MANA_BUCKET);
        }
    }

    private void setupClient(final FMLClientSetupEvent event) {
        // 设置流体渲染
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.MANA_FLUID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.MANA_FLUID_FLOWING.get(), RenderType.translucent());

        // 注册雾效处理器
        MinecraftForge.EVENT_BUS.register(FogHandler.class);
    }
}