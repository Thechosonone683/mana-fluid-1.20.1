package net.thechosonone683.mana_fluid.util;

import net.minecraft.resources.ResourceLocation;
import net.thechosonone683.mana_fluid.Mana_fluid;

public class ResourceLocationHelper {

    // 简单直接的方法，接受过时警告
    public static ResourceLocation of(String path) {
        return new ResourceLocation(Mana_fluid.MODID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}