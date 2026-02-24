package net.thechosonone683.mana_fluid.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.thechosonone683.mana_fluid.Config;
import net.thechosonone683.mana_fluid.Mana_fluid;

public class ClothConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config." + Mana_fluid.MODID + ".title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("config." + Mana_fluid.MODID + ".category.general")
        );

        // manaPerMB 配置项
        general.addEntry(entryBuilder.startIntField(
                        Component.translatable("config." + Mana_fluid.MODID + ".mana_per_mb"),
                        Config.manaPerMB
                )
                .setTooltip(
                        Component.translatable("config." + Mana_fluid.MODID + ".mana_per_mb.tooltip")
                )
                .setDefaultValue(100)
                .setMin(1)
                .setMax(10000)
                .setSaveConsumer(newValue -> Config.manaPerMB = newValue)
                .build());

        // bucketManaAmount 配置项
        general.addEntry(entryBuilder.startIntField(
                        Component.translatable("config." + Mana_fluid.MODID + ".bucket_amount"),
                        Config.bucketManaAmount
                )
                .setTooltip(
                        Component.translatable("config." + Mana_fluid.MODID + ".bucket_amount.tooltip")
                )
                .setDefaultValue(100000)
                .setMin(1000)
                .setMax(10000000)
                .setSaveConsumer(newValue -> Config.bucketManaAmount = newValue)
                .build());

        // 提示信息
        general.addEntry(entryBuilder.startTextDescription(
                Component.translatable("config." + Mana_fluid.MODID + ".note")
        ).build());

        // 获取 Minecraft 实例
        Minecraft minecraft = Minecraft.getInstance();

        // 设置保存回调
        builder.setSavingRunnable(() -> {
            // 保存到文件
            Config.save();

            // 显示保存成功的提示
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(
                        Component.translatable("config." + Mana_fluid.MODID + ".saved"),
                        true
                );
            }

            System.out.println("[Mana Fluid] Config saved");
        });

        return builder.build();
    }
}