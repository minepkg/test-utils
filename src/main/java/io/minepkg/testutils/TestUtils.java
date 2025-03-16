package io.minepkg.testutils;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.PacketByteBuf;
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

  public static final Identifier WEATHER_GAMERULE_SYNC = TestUtils.id("weather_sync");
  public static final Identifier WEATHER_GAMERULE_SYNC_REQUEST = TestUtils.id("weather_sync_request");

  public static final Identifier SET_TIME_PACKET_ID = TestUtils.id("set_time");
  public static final Identifier SET_RULE_PACKET_ID = TestUtils.id("set_rule");
  public static final Identifier SET_WEATHER_PACKET_ID = TestUtils.id("set_weather");

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

    ServerPlayNetworking.registerGlobalReceiver(WEATHER_GAMERULE_SYNC_REQUEST, (server, player, handler, buf, sender) -> {
      sendWeatherRule(player);
    });

    // client wants to set the time
    ServerPlayNetworking.registerGlobalReceiver(SET_TIME_PACKET_ID, (server, player, handler, buf, responseSender) -> {
      // make sure its not over 24000
      long wantedTime = buf.getLong(0) % 24000;

      // Execute on the main thread
      server.execute(() -> {
        ServerWorld world = player.getServerWorld();
        // set the time
        world.setTimeOfDay(wantedTime);
      });
    });

    // client wants to set the weather
    ServerPlayNetworking.registerGlobalReceiver(SET_WEATHER_PACKET_ID, (server, player, handler, buf, responseSender) -> {
      short weather = buf.getShort(0);
      ServerWorld world = player.getServerWorld();

      // Execute on the main thread
      server.execute(() -> {
        switch (weather) {
          case WEATHER_CLEAR   -> world.setWeather(120000, 0, false, false);
          case WEATHER_RAIN    -> world.setWeather(0, 24000, true, false);
          case WEATHER_THUNDER -> world.setWeather(0, 24000, true, true);
        }
      });
    });

    // client wants to set a rule (eg. freeze the time)
    ServerPlayNetworking.registerGlobalReceiver(SET_RULE_PACKET_ID, (server, player, handler, buf, sender) -> {
      short ruleID = buf.getShort(0);
      boolean value = buf.getBoolean(2);
      ServerWorld world = player.getServerWorld();

      // Execute on the main thread
      server.execute(() -> {

        GameRules rules = world.getGameRules();

        switch(ruleID) {
          // daylight cycle
          case DO_DAYLIGHT_CYCLE_RULE -> {
            BooleanRule rule = rules.get(GameRules.DO_DAYLIGHT_CYCLE);
            rule.set(value, world.getServer());
          }
          case DO_WEATHER_CYCLE_RULE -> {
            BooleanRule rule = rules.get(GameRules.DO_WEATHER_CYCLE);
            rule.set(value, world.getServer());
            broadcastWeatherRuleChange(server, value);
          }
          default ->
            LOGGER.error(
              "Player {} requested to change an unsupported rule id ({}). (client might be outdated)",
              player.getName().getString(),
              ruleID
            );
        }
      });
    });
  }

  public void broadcastWeatherRuleChange(MinecraftServer server, boolean value) {
    PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
    packet.writeBoolean(value);

    // Notify each player on the server about the weather gamerule update.
    server.getPlayerManager().getPlayerList().forEach(player -> {
      ServerPlayNetworking.send(player, WEATHER_GAMERULE_SYNC, packet);
    });
  }

  public void sendWeatherRule(ServerPlayerEntity player) {
    ServerWorld world = player.getServerWorld();
    boolean doWeatherCycle = world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);
    PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
    packet.writeBoolean(doWeatherCycle);
    ServerPlayNetworking.send(player, WEATHER_GAMERULE_SYNC, packet);
  }
}
