package io.minepkg.testutils.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerNetworking {

  public static final Identifier WEATHER_GAMERULE_SYNC = new Identifier("test-utils", "weather_sync");

  public static void updateClients(ServerPlayerEntity player, boolean enabled) {
    PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
    packet.writeBoolean(enabled);
    ServerPlayNetworking.send(player, WEATHER_GAMERULE_SYNC, packet);
  }

  private ServerNetworking() {
    // NO-OP
  }
}
