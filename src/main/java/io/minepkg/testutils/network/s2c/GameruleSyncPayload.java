package io.minepkg.testutils.network.s2c;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.world.GameRules;

public record GameruleSyncPayload(boolean doDaylightCycle, boolean doWeatherCycle) implements CustomPayload {
  public static final Id<GameruleSyncPayload> ID = new Id<>(TestUtils.id("weather_sync"));

  public GameruleSyncPayload(GameRules gameRules) {
    this(
      gameRules.getBoolean(GameRules.DO_DAYLIGHT_CYCLE),
      gameRules.getBoolean(GameRules.DO_WEATHER_CYCLE)
    );
  }

  public static final PacketCodec<PacketByteBuf, GameruleSyncPayload> CODEC = PacketCodec.of(
    (payload, buf) -> {
      buf.writeBoolean(payload.doDaylightCycle);
      buf.writeBoolean(payload.doWeatherCycle);
    },
    buf -> {
      boolean doDayLightCycle = buf.readBoolean();
      boolean doWeatherCycle = buf.readBoolean();

      return new GameruleSyncPayload(doDayLightCycle, doWeatherCycle);
    }
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
