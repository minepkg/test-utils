package io.minepkg.testutils;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class RuleBookScreen extends CottonClientScreen {
  public RuleBookScreen(GuiDescription description) {
    super(description);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void tick() {
    ((RuleBookGUI)this.getDescription()).tick();
  }
}
