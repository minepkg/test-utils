package io.minepkg.testutils.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Color.RGB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

public class WGradient extends WWidget {

  public RGB colorFrom = new RGB(0xFF_8cb6fc);
  public RGB colorTo = new RGB(0xFF_b5d1ff);

  public WGradient() {
    super();
  }

  @Override
  public boolean canResize() {
      return true; // set to false if you want a static size
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
    for (int yPos = 0; yPos < getHeight(); yPos++) {
      double percent = yPos / (double)getHeight();
      RGB color = WGradient.interpolateColors(colorFrom, colorTo, percent);
      ScreenDrawing.coloredRect(x, y+yPos, getWidth(), 1, color.toRgb());
    }
  }

  public static RGB interpolateColors(RGB color1, RGB color2, double percent){
    double a = (color2.getA() - color1.getA()) * percent + color1.getA();
    double r = (color2.getR() - color1.getR()) * percent + color1.getR();
    double g = (color2.getG() - color1.getG()) * percent + color1.getG();
    double b = (color2.getB() - color1.getB()) * percent + color1.getB();
    return new RGB((int)a, (int)r, (int)g, (int)b);
  }
}
