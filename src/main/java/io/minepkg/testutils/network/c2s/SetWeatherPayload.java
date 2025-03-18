package io.minepkg.testutils.network.c2s;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SetWeatherPayload(short weather) implements CustomPayload {
  public static final Id<SetWeatherPayload> ID = new Id<>(TestUtils.id("set_weather"));

  public static final PacketCodec<PacketByteBuf, SetWeatherPayload> CODEC = PacketCodec.of(
    (payload, buf) -> buf.writeShort(payload.weather),
    buf -> new SetWeatherPayload(buf.readShort())
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
