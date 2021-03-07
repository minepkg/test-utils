package io.minepkg.testutils.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanRule.class)
public interface BooleanRuleAccessor {
  @Invoker
  GameRules.BooleanRule callGetThis();
}
