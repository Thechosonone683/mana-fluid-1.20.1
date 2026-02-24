package net.thechosonone683.mana_fluid.integration;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.thechosonone683.mana_fluid.Mana_fluid;
import net.thechosonone683.mana_fluid.config.ClothConfigScreen;

public class ModConfigScreen {

    @OnlyIn(Dist.CLIENT)
    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, screen) -> createConfigScreen(screen)
                )
        );
    }

    @OnlyIn(Dist.CLIENT)
    private static Screen createConfigScreen(Screen parent) {
        // 检查 Cloth Config 是否可用
        try {
            return ClothConfigScreen.createConfigScreen(parent);
        } catch (NoClassDefFoundError e) {
            // Cloth Config 未安装时显示提示
            return new Screen(Component.translatable("config." + Mana_fluid.MODID + ".require_cloth_config")) {
                @Override
                public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                    this.renderBackground(graphics);

                    Component message = Component.translatable("config." + Mana_fluid.MODID + ".require_cloth_config");
                    int textWidth = this.font.width(message);
                    graphics.drawString(
                            this.font,
                            message,
                            (this.width - textWidth) / 2,
                            this.height / 2,
                            0xFF5555,
                            false
                    );

                    super.render(graphics, mouseX, mouseY, partialTick);
                }
            };
        }
    }
}