package io.minepkg.testutils.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.util.Identifier;

public class WSpriteButton extends WButton {
  private Identifier image;
  private int spriteWidth;
  private int spriteHeight;

  public WSpriteButton(Identifier image) {
    super();
    this.image = image;
  }

  // @Override
  // public boolean canResize() {
  //   return false;
  // }

  @Override
	public void setSize(int x, int y) {
    this.height = x;
    this.width = y;
	}

  @Override
  public void paintBackground(int x, int y, int mouseX, int mouseY) {
    boolean hovered = (mouseX>=0 && mouseY>=0 && mouseX<getWidth() && mouseY<getHeight());
    int state = 1; //1=regular. 2=hovered. 0=disabled.
    if (!isEnabled()) state = 0;
    else if (hovered) state = 2;

    ScreenDrawing.coloredRect(x, y, getWidth(), getHeight(), 0xFF_7b7b7b);
    ScreenDrawing.coloredRect(x+1, y+1, getWidth()-2, getHeight()-2, state == 2 ? 0xFF_ababab : 0xFF_b7b7b7);

    int sw = getSpriteWidth();
    int sh = getSpriteHeight();
    int xOffset = (getWidth() - sw) / 2;
    int yOffset = (getHeight() - sh) / 2;
    ScreenDrawing.texturedRect(
      x + xOffset,
      y + yOffset,
      sw,
      sh,
      image,
      0xFF_FFFFFF
    );
  }

  public int getSpriteWidth() {
    if (this.spriteWidth != 0) return this.spriteWidth;
    double w = (double)getWidth() * 0.7;
    return (int)w;
  }

  public int getSpriteHeight() {
    if (this.spriteHeight != 0) return this.spriteHeight;
    double h = (double)getHeight() * 0.7;
    return (int)h;
  }
}
