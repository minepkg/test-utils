package io.minepkg.testutils.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.Rule.class)
public interface RuleAccessor {
  @Accessor
  GameRules.Type<?> getType();
}
