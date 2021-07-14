package io.minepkg.testutils;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class RuleBookScreen extends CottonClientScreen {
  public RuleBookScreen(GuiDescription description) {
    super(description);
    // Request the latest "doWeatherCycle" rule value
    ClientPlayNetworking.send(TestUtils.WEATHER_GAMERULE_SYNC_REQUEST, PacketByteBufs.empty());
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
