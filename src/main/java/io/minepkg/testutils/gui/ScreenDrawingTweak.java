package io.minepkg.testutils.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ScreenDrawingTweak {
  public static void texturedRect(int x, int y, int width, int height, Identifier texture, int color, float opacity) {
    MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

    //float scale = 0.00390625F;

    if (width <= 0) width = 1;
    if (height <= 0) height = 1;

    float r = (color >> 16 & 255) / 255.0F;
    float g = (color >> 8 & 255) / 255.0F;
    float b = (color & 255) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    RenderSystem.enableBlend();
    //GlStateManager.disableTexture2D();
    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE); //I thought GL_QUADS was deprecated but okay, sure.
    buffer.vertex(x,         y + height, 0).color(r, g, b, opacity).texture(0f, 1f).next();
    buffer.vertex(x + width, y + height, 0).color(r, g, b, opacity).texture(1f, 1f).next();
    buffer.vertex(x + width, y,          0).color(r, g, b, opacity).texture(1f, 0f).next();
    buffer.vertex(x,         y,          0).color(r, g, b, opacity).texture(0f, 0f).next();
    tessellator.draw();
    //GlStateManager.enableTexture2D();
    RenderSystem.disableBlend();
  }
}
