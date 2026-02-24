package net.thechosonone683.mana_fluid.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.thechosonone683.mana_fluid.Mana_fluid;

public class I18nHelper {

    /**
     * 获取格式化的翻译文本
     */
    public static String translate(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }

    /**
     * 获取带 modid 前缀的翻译文本
     */
    public static String translatePrefixed(String key, Object... args) {
        return Component.translatable(Mana_fluid.MODID + "." + key, args).getString();
    }

    /**
     * 获取配置相关的翻译文本
     */
    public static String translateConfig(String key, Object... args) {
        return Component.translatable("config." + Mana_fluid.MODID + "." + key, args).getString();
    }

    /**
     * 检查客户端是否有某个语言的翻译
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean hasTranslation(String key) {
        return I18n.exists(key);
    }
}