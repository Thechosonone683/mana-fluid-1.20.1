package net.thechosonone683.mana_fluid;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = Mana_fluid.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MANA_PER_MB = BUILDER
            .comment(" How much Mana equals 1 mB of Mana Fluid",
                    " 多少魔力等于 1 mB 魔力流体")
            .defineInRange("manaPerMB", 100, 1, 10000);

    private static final ForgeConfigSpec.IntValue BUCKET_MANA_AMOUNT = BUILDER
            .comment(" How much Mana a full bucket contains (should be manaPerMB * 1000)",
                    " 一整桶魔力流体包含多少魔力（应该是 manaPerMB * 1000）")
            .defineInRange("bucketManaAmount", 100000, 1000, 10000000);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int manaPerMB = 100;
    public static int bucketManaAmount = 100000;

    private static Path configPath;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        ModConfig config = (ModConfig) event.getConfig();

        if (config.getModId().equals(Mana_fluid.MODID)) {
            // 保存配置文件路径
            if (configPath == null) {
                configPath = config.getFullPath();
            }

            // 加载配置值
            manaPerMB = MANA_PER_MB.get();
            bucketManaAmount = BUCKET_MANA_AMOUNT.get();

            // 简单的验证
            if (bucketManaAmount % manaPerMB != 0) {
                LOGGER.warn("[Mana Fluid] bucketManaAmount ({}) is not a multiple of manaPerMB ({}).",
                        bucketManaAmount, manaPerMB);
            }

            LOGGER.info("[Mana Fluid] Config loaded: manaPerMB={}, bucketManaAmount={}",
                    manaPerMB, bucketManaAmount);
        }
    }

    /**
     * 保存配置到文件
     */
    public static void save() {
        if (configPath == null) {
            LOGGER.error("[Mana Fluid] Config path not initialized");
            return;
        }

        try {
            File configFile = configPath.toFile();

            // 确保目录存在
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 直接写入文件，不需要备份
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write("#Mana Fluid Configuration\n");
                writer.write("\n");
                writer.write("# How much Mana equals 1 mB of Mana Fluid\n");
                writer.write("# 多少魔力等于 1 mB 魔力流体\n");
                writer.write("#Range: 1 ~ 10000\n");
                writer.write("manaPerMB = " + manaPerMB + "\n");
                writer.write("\n");
                writer.write("# How much Mana a full bucket contains (should be manaPerMB * 1000)\n");
                writer.write("# 一整桶魔力流体包含多少魔力（应该是 manaPerMB * 1000）\n");
                writer.write("#Range: 1000 ~ 10000000\n");
                writer.write("bucketManaAmount = " + bucketManaAmount + "\n");
            }

            LOGGER.info("[Mana Fluid] Config saved to: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("[Mana Fluid] Failed to save config: {}", e.getMessage());
        }
    }
}