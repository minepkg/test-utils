package io.minepkg.testutils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class TestUtilsClient implements ClientModInitializer {

  // used by GUI instead of native game rule because we can not change them
  public static boolean doWeatherCycle = true;

  @Override
  public void onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(TestUtils.WEATHER_GAMERULE_SYNC, (client, clientPlayNetworkHandler, packet, packetSender) -> {
      boolean doWeatherCycle = packet.readBoolean();

      client.execute(() -> {
        TestUtilsClient.doWeatherCycle = doWeatherCycle;
        // Does not work because .. minecraft
        // ((GameRules.BooleanRule)client.world.getGameRules().get(GameRules.DO_WEATHER_CYCLE)).set(doWeatherCycle, (MinecraftServer)null);
      });
    });
  }
}
