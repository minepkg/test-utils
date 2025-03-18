package io.minepkg.testutils;

import io.minepkg.testutils.network.s2c.GameruleSyncPayload;
import io.minepkg.testutils.network.s2c.OpenBookPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

public class TestUtilsClient implements ClientModInitializer {

  // used to sync the gamerules to the GUI
  public static boolean doDaylightCycle = true;
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

    PayloadTypeRegistry.playS2C().register(GameruleSyncPayload.ID, GameruleSyncPayload.CODEC);
    ClientPlayNetworking.registerGlobalReceiver(GameruleSyncPayload.ID, (payload, context) -> {
      context.client().execute(() -> {
        TestUtilsClient.doDaylightCycle = payload.doDaylightCycle();
        TestUtilsClient.doWeatherCycle = payload.doWeatherCycle();
      });
    });
  }
}
