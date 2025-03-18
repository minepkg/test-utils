package io.minepkg.testutils.network.c2s;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SetTimePayload(long time) implements CustomPayload {
  public static final Id<SetTimePayload> ID = new CustomPayload.Id<>(TestUtils.id("set_time"));

  public static final PacketCodec<PacketByteBuf, SetTimePayload> CODEC = PacketCodec.of(
    (payload, buf) -> {
      buf.writeLong(payload.time);
    },
    buf -> {
      // make sure it's not over 24000
      long wantedTime = buf.readLong() % 24000;

      return new SetTimePayload(wantedTime);
    }
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
