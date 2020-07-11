package io.minepkg.testutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.BooleanRule;

public class TestUtils implements ModInitializer {

  public static final Identifier SET_TIME_PACKET_ID = new Identifier("testutils", "set_time");
  public static final Identifier SET_RULE_PACKET_ID = new Identifier("testutils", "set_rule");
  public static final Identifier SET_WEATHER_PACKET_ID = new Identifier("testutils", "set_weather");

  public static final short DO_DAYLIGHT_CYCLE_RULE = 1;
  public static final short DO_WEATHER_CYCLE_RULE = 2;

  public static final short WEATHER_CLEAR = 1;
  public static final short WEATHER_RAIN = 2;
  public static final short WEATHER_THUNDER = 3;

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    Registry.register(
      Registry.ITEM,
      new Identifier("testutils", "rulebook"),
      new RuleBookItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1))
    );

    // client wants to set the time
    ServerSidePacketRegistry.INSTANCE.register(SET_TIME_PACKET_ID, (packetContext, attachedData) -> {
      // make sure its not over 24000
      long wantedTime = attachedData.getLong(0) % 24000;

      // Execute on the main thread
      packetContext.getTaskQueue().execute(() -> {
        ServerWorld world = (ServerWorld) packetContext.getPlayer().world;
        // set the time
        world.method_29199(wantedTime);
      });
    });

    // client wants to set the weather
    ServerSidePacketRegistry.INSTANCE.register(SET_WEATHER_PACKET_ID, (packetContext, attachedData) -> {
      short weather = attachedData.getShort(0);
      ServerWorld world = (ServerWorld) packetContext.getPlayer().world;

      // Execute on the main thread
      packetContext.getTaskQueue().execute(() -> {

        if (weather == WEATHER_CLEAR) {
          world.method_27910(120000, 0, false, false);
          return;
        }

        if (weather == WEATHER_RAIN) {
          world.method_27910(0, 24000, true, false);
          return;
        }

        if (weather == WEATHER_THUNDER) {
          world.method_27910(0, 24000, true, true);
          return;
        }
      });
    });

    // client wants to set a rule (eg. freeze the time)
    ServerSidePacketRegistry.INSTANCE.register(SET_RULE_PACKET_ID, (packetContext, attachedData) -> {
      short ruleID = attachedData.getShort(0);
      boolean value = attachedData.getBoolean(2);
      ServerWorld world = (ServerWorld) packetContext.getPlayer().world;

      // Execute on the main thread
      packetContext.getTaskQueue().execute(() -> {

        GameRules rules = world.getGameRules();
        BooleanRule rule;

        switch(ruleID) {
          // daylight cycle
          case DO_DAYLIGHT_CYCLE_RULE:
            rule = rules.get(GameRules.DO_DAYLIGHT_CYCLE);
            rule.set(value, world.getServer());
            break;
          case DO_WEATHER_CYCLE_RULE:
            rule = rules.get(GameRules.DO_WEATHER_CYCLE);
            rule.set(value, world.getServer());
            break;
          default:
            System.err.printf(
              "Player %s requested to change an unsupported rule id (%d). (client might be outdated)",
              packetContext.getPlayer().getName().asString(),
              ruleID
            );
          }
      });
    });
  }
}
