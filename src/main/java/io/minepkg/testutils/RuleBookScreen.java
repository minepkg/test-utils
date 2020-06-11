package io.minepkg.testutils;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class RuleBookScreen extends CottonClientScreen {
  public RuleBookScreen(GuiDescription description) {
      super(description);
  }

	public boolean pausesGame() {
		return false;
  }

	public boolean isPauseScreen() {
		return false;
  }

  @Override
  public void tick() {
    ((RuleBookGUI)this.getDescription()).tick();
  }
}
