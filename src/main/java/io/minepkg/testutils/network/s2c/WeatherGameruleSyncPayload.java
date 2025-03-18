package io.minepkg.testutils.network.s2c;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record WeatherGameruleSyncPayload(boolean doWeatherCycle) implements CustomPayload {
  public static final Id<WeatherGameruleSyncPayload> ID = new Id<>(TestUtils.id("weather_sync"));

  public static final PacketCodec<PacketByteBuf, WeatherGameruleSyncPayload> CODEC = PacketCodec.of(
    (payload, buf) -> buf.writeBoolean(payload.doWeatherCycle),
    buf -> new WeatherGameruleSyncPayload(buf.readBoolean())
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
