package io.minepkg.testutils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RuleBookItem extends Item {
  public RuleBookItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
    if (world.isClient) {
      playerEntity.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
    } else {
      ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;
      TestUtils.sendGameRules(serverPlayer);
      TestUtils.sendOpenBookPacket(serverPlayer);
    }

    return ActionResult.SUCCESS;
  }
}
