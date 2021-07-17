package io.minepkg.testutils.gui;

import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class WClickableLabel extends WLabel {
  private Runnable onClick;

  public WClickableLabel(Text text) {
    super(text);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public InputResult onClick(int x, int y, int button) {
    super.onClick(x, y, button);

    if (isWithinBounds(x, y)) {
      MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

      if (onClick != null) onClick.run();
    }

    return InputResult.PROCESSED;
  }

  public WClickableLabel setOnClick(Runnable r) {
    this.onClick = r;
    return this;
  }
}
