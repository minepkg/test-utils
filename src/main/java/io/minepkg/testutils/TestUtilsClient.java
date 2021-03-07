package io.minepkg.testutils;

import io.minepkg.testutils.network.ClientNetworking;
import net.fabricmc.api.ClientModInitializer;

public class TestUtilsClient implements ClientModInitializer {

  public static boolean doWeatherCycle = true;

  @Override
  public void onInitializeClient() {
    ClientNetworking.init();
  }
}
