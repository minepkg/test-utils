package io.minepkg.testutils.mixin;

import io.minepkg.testutils.network.ServerNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameRules.Rule.class)
public abstract class GameRulesMixin {

  @Inject(
    method = "changed",
    at = @At("HEAD"))
  private void onChanged(MinecraftServer server, CallbackInfo ci) {
    if((Object) this instanceof GameRules.BooleanRule) {
      GameRules.BooleanRule booleanRule = (GameRules.BooleanRule) (Object) this;
      Map<GameRules.Key<?>, GameRules.Type<?>> ruleTypes = GameRulesAccessor.getRULE_TYPES();

      if(((RuleAccessor) ((BooleanRuleAccessor) booleanRule).callGetThis()).getType().equals(ruleTypes.get(GameRules.DO_WEATHER_CYCLE))) {
        boolean whether = booleanRule.get();

        // Notify each player on the server about the weather gamerule update.
        server.getPlayerManager().getPlayerList().forEach(player -> {
          ServerNetworking.updateClients(player, whether);
        });
      }
    }
  }
}
