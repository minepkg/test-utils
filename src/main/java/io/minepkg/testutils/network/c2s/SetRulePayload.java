package io.minepkg.testutils.network.c2s;

import io.minepkg.testutils.TestUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SetRulePayload(short ruleID, boolean value) implements CustomPayload {
  public static final Id<SetRulePayload> ID = new Id<>(TestUtils.id("set_rule"));

  public static final PacketCodec<PacketByteBuf, SetRulePayload> CODEC = PacketCodec.of(
    (payload, buf) -> {
      buf.writeShort(payload.ruleID);
      buf.writeBoolean(payload.value);
    },
    buf -> {
      short ruleID = buf.readShort();
      boolean value = buf.readBoolean();
      return new SetRulePayload(ruleID, value);
    }
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
