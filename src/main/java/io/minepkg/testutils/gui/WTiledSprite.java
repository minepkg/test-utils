package io.minepkg.testutils.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

// It's a Sprite that can be tiled, but does not support animations
public class WTiledSprite extends WSprite {
  public int tileWidth = 20;
  public int tileHeight = 20;

  WTiledSprite(Identifier image) {
    super(image);
  }

  public WTiledSprite(Identifier image, int tileWidth, int tileHeight) {
    super(image);
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void paintBackground(int x, int y, int mouseX, int mouseY) {
    // Y Direction (down)
    for (int tileYOffset = 0; tileYOffset < height; tileYOffset+=tileHeight) {
      // X Direction (right)
      for (int tileXOffset = 0; tileXOffset < width; tileXOffset+=tileWidth) {
        // draw the texture
        ScreenDrawing.texturedRect(
          // at the correct position using tileXOffset and tileYOffset
          x + tileXOffset,
          y + tileYOffset,
          // but using the set tileWidth and tileHeight instead of the full height and width
          tileWidth,
          tileHeight,
          // inherited texture from WSprite. only supports the first frame (no animated sprites)
          frames[0],
          // clips the texture if wanted
          u1, v1, u2, v2,
          tint
        );
      }
    }
  }
}
