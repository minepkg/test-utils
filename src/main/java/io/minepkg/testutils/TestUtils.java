package io.minepkg.testutils;

import io.minepkg.testutils.network.c2s.SetRulePayload;
import io.minepkg.testutils.network.c2s.SetTimePayload;
import io.minepkg.testutils.network.c2s.SetWeatherPayload;
import io.minepkg.testutils.network.s2c.OpenBookPayload;
import io.minepkg.testutils.network.s2c.WeatherGameruleSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.BooleanRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestUtils implements ModInitializer {
  public static final String MOD_ID = "testutils"; // Note: currently different from fabric.mod.json id
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  public static final short DO_DAYLIGHT_CYCLE_RULE = 1;
  public static final short DO_WEATHER_CYCLE_RULE = 2;

  public static final short WEATHER_CLEAR = 1;
  public static final short WEATHER_RAIN = 2;
  public static final short WEATHER_THUNDER = 3;

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.
    RuleBookItem TestUtils_Item = new RuleBookItem(new Item.Settings().maxCount(1));
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TestUtils_Item));

    Registry.register(
      Registries.ITEM,
      TestUtils.id("rulebook"),
      TestUtils_Item
    );

    // sync weather rule on player connect
    ServerPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      sendWeatherRule(handler.player);
    });

    // client wants to set the time
    PayloadTypeRegistry.playC2S().register(SetTimePayload.ID, SetTimePayload.CODEC);
    ServerPlayNetworking.registerGlobalReceiver(SetTimePayload.ID, (payload, context) -> {
      // Execute on the main thread
      context.player().server.execute(() -> {
        ServerWorld world = context.player().getServerWorld();
        // set the time
        world.setTimeOfDay(payload.time());
      });
    });

    // client wants to set the weather
    PayloadTypeRegistry.playC2S().register(SetWeatherPayload.ID, SetWeatherPayload.CODEC);
    ServerPlayNetworking.registerGlobalReceiver(SetWeatherPayload.ID, (payload, context) -> {
      ServerWorld world = context.player().getServerWorld();

      // Execute on the main thread
      context.player().server.execute(() -> {
        switch (payload.weather()) {
          case WEATHER_CLEAR   -> world.setWeather(120000, 0, false, false);
          case WEATHER_RAIN    -> world.setWeather(0, 24000, true, false);
          case WEATHER_THUNDER -> world.setWeather(0, 24000, true, true);
        }
      });
    });

    // client wants to set a rule (e.g. freeze the time)
    PayloadTypeRegistry.playC2S().register(SetRulePayload.ID, SetRulePayload.CODEC);
    ServerPlayNetworking.registerGlobalReceiver(SetRulePayload.ID, (payload, context) -> {
      ServerPlayerEntity player = context.player();
      MinecraftServer server = player.server;
      ServerWorld world = player.getServerWorld();

      // Execute on the main thread
      server.execute(() -> {

        GameRules rules = world.getGameRules();

        switch(payload.ruleID()) {
          // daylight cycle
          case DO_DAYLIGHT_CYCLE_RULE -> {
            BooleanRule rule = rules.get(GameRules.DO_DAYLIGHT_CYCLE);
            rule.set(payload.value(), server);
          }
          case DO_WEATHER_CYCLE_RULE -> {
            BooleanRule rule = rules.get(GameRules.DO_WEATHER_CYCLE);
            rule.set(payload.value(), server);
            broadcastWeatherRuleChange(server, payload.value());
          }
          default ->
            LOGGER.error(
              "Player {} requested to change an unsupported rule id ({}). (client might be outdated)",
              player.getName().getString(),
              payload.ruleID()
            );
        }
      });
    });
  }

  public static void sendOpenBookPacket(ServerPlayerEntity player) {
    ServerPlayNetworking.send(player, new OpenBookPayload());
  }

  public static void broadcastWeatherRuleChange(MinecraftServer server, boolean value) {
    var payload = new WeatherGameruleSyncPayload(value);

    // Notify each player on the server about the weather gamerule update.
    server.getPlayerManager().getPlayerList().forEach(player -> {
      ServerPlayNetworking.send(player, payload);
    });
  }

  public static void sendWeatherRule(ServerPlayerEntity player) {
    ServerWorld world = player.getServerWorld();
    boolean doWeatherCycle = world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);
    var payload = new WeatherGameruleSyncPayload(doWeatherCycle);
    ServerPlayNetworking.send(player, payload);
  }
}
