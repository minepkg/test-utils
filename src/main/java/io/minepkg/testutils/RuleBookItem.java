package io.minepkg.testutils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RuleBookItem extends Item {
  public RuleBookItem(Settings settings) {
    super(settings);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
    playerEntity.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
    MinecraftClient.getInstance().openScreen(new RuleBookScreen(new RuleBookGUI(world)));
    return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
  }
}
