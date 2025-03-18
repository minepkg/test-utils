package io.minepkg.testutils.network.s2c;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class OpenBookPayload implements CustomPayload {
  public static final Id<OpenBookPayload> ID = new CustomPayload.Id<>(TestUtils.id("open_book"));

  public static final PacketCodec<PacketByteBuf, OpenBookPayload> CODEC = PacketCodec.of(
    (payload, buf) -> {},
    buf -> new OpenBookPayload()
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
