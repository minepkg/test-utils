package io.minepkg.testutils;

import io.minepkg.testutils.network.s2c.OpenBookPayload;
import io.minepkg.testutils.network.s2c.WeatherGameruleSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

public class TestUtilsClient implements ClientModInitializer {

  // used by GUI instead of native game rule because we can not change them
  public static boolean doWeatherCycle = true;

  @Override
  public void onInitializeClient() {
    PayloadTypeRegistry.playS2C().register(OpenBookPayload.ID, OpenBookPayload.CODEC);
    ClientPlayNetworking.registerGlobalReceiver(OpenBookPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();

      client.execute(() -> {
        client.setScreen(new RuleBookScreen(new RuleBookGUI(client.world, client.player)));
      });
    });

    PayloadTypeRegistry.playS2C().register(WeatherGameruleSyncPayload.ID, WeatherGameruleSyncPayload.CODEC);
    ClientPlayNetworking.registerGlobalReceiver(WeatherGameruleSyncPayload.ID, (payload, context) -> {
      context.client().execute(() -> {
        TestUtilsClient.doWeatherCycle = payload.doWeatherCycle();
        // Does not work because .. minecraft
        // ((GameRules.BooleanRule)client.world.getGameRules().get(GameRules.DO_WEATHER_CYCLE)).set(doWeatherCycle, (MinecraftServer)null);
      });
    });
  }
}
