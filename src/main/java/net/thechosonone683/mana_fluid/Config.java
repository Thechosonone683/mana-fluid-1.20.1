package net.thechosonone683.mana_fluid;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int manaPerMB;
    public static int bucketManaAmount;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        manaPerMB = MANA_PER_MB.get();
        bucketManaAmount = BUCKET_MANA_AMOUNT.get();

        // 验证配置的一致性
        if (bucketManaAmount % manaPerMB != 0) {
            LOGGER.warn("[Mana Fluid] bucketManaAmount ({}) is not a multiple of manaPerMB ({}). " +
                    "This may cause issues with bucket operations.", bucketManaAmount, manaPerMB);
        }

        int expectedBucketMana = manaPerMB * 1000;
        if (bucketManaAmount != expectedBucketMana) {
            LOGGER.info("[Mana Fluid] bucketManaAmount is set to {} (default would be {}). " +
                    "Make sure this is intentional.", bucketManaAmount, expectedBucketMana);
        }

        LOGGER.info("[Mana Fluid] Config loaded: manaPerMB={}, bucketManaAmount={}",
                manaPerMB, bucketManaAmount);
    }
}