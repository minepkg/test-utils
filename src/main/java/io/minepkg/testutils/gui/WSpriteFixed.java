package io.minepkg.testutils.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

// Temporary workaround for WSprite texture blending, to be remove once fixed in LibGui
public class WSpriteFixed extends WSprite {
  public WSpriteFixed(Identifier image) {
    super(image);
  }

  @Override
  protected void paintFrame(MatrixStack matrices, int x, int y, Texture texture) {
    RenderSystem.enableBlend();
    ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), texture, tint);
    RenderSystem.disableBlend();
  }
}
