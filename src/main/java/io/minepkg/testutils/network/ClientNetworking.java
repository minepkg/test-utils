package io.minepkg.testutils.network;

import io.minepkg.testutils.TestUtilsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworking {

  public static void init() {
    ClientPlayNetworking.registerGlobalReceiver(ServerNetworking.WEATHER_GAMERULE_SYNC, (client, clientPlayNetworkHandler, packet, packetSender) -> {
      boolean doWeatherCycle = packet.readBoolean();

      client.execute(() -> {
        TestUtilsClient.doWeatherCycle = doWeatherCycle;
      });
    });
  }

  private ClientNetworking() {
    // NO-OP
  }
}
