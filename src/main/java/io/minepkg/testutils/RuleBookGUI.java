package io.minepkg.testutils;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Color.RGB;
import io.minepkg.testutils.gui.WClickableLabel;
import io.minepkg.testutils.gui.WGradient;
import io.minepkg.testutils.gui.WSpriteButton;
import io.minepkg.testutils.gui.WTiledSprite;
import io.minepkg.testutils.gui.WUsableClippedPanel;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

class WEnvMonitor extends WUsableClippedPanel {
  private static Identifier SNOW_GRASS = new Identifier("minecraft:textures/block/grass_block_snow.png");
  private static Identifier NORMAL_GRASS = new Identifier("minecraft:textures/block/grass_block_side.png");

  private boolean canSnow = false;
  WSprite sun = new WSprite(new Identifier("testutils:sun.png"));
  WGradient bg = new WGradient();
  WTiledSprite grass = new WTiledSprite(NORMAL_GRASS, 12, 12);
  WTiledSprite rain = new WTiledSprite(new Identifier("minecraft:textures/environment/rain.png"), 42, 120);
  WTiledSprite snow = new WTiledSprite(new Identifier("minecraft:textures/environment/snow.png"), 42, 140);
  // WTiledSprite snowLayer = new WTiledSprite(new Identifier("minecraft:textures/block/snow.png"), 12, 4);

  RGB startColorDay = new RGB(0xFF_8cb6fc);
  RGB endColorDay = new RGB(0xFF_b5d1ff);

  RGB startColorNight = new RGB(0xFF_00081a);
  RGB endColorNight = new RGB(0xFF_000e2d);

  WEnvMonitor() {
    setBackgroundPainter(BackgroundPainter.SLOT);

    add(bg, 0, 0, 200, 35);

    add(snow, 0, 0, 200, 35);
    add(rain, 0, 0, 200, 35);

    add(sun, 0, 0, 6, 6);
    add(grass, 0, 23, 100, 12);
    // add(snowLayer, 0, 20, 100, 1);
  }

  public void setTimeOfDay(long time) {
    double offset = 3800;

    double realPercent = (double)time / (24000);
    double percent = ((double)time + offset) / (24000);

    double x = Math.cos((realPercent + 0.5) * Math.PI * 2) * 41;
    double y = Math.sin((realPercent + 0.5) * Math.PI * 2) * 20;

    sun.setLocation((int) x + 41, (int) y+ 22);

    if (percent <= 1) {
      bg.colorFrom = WGradient.interpolateColors(startColorDay, startColorNight, percent);
      bg.colorTo = WGradient.interpolateColors(endColorDay, endColorNight, percent);

      grass.setTint(WGradient.interpolateColors(new RGB(0xFF_FFFFFF), new RGB(0xFF_222222), percent).toRgb());
    } else {
      bg.colorFrom = WGradient.interpolateColors(endColorNight, startColorDay, (percent - 1) * 3);
      bg.colorTo = WGradient.interpolateColors(endColorNight, new RGB(0xFF_ffde78), (percent - 1) * 3);

      grass.setTint(WGradient.interpolateColors(new RGB(0xFF_222222), new RGB(0xFF_FFFFFF), (percent - 1) * 3).toRgb());
    }
  }

  public void setRain(boolean raining) {
    // HACK: modify size until i figure out how to fade that stuff
    if (!raining) {
      snow.setSize(0, 0);
      rain.setSize(0, 0);
      grass.setImage(NORMAL_GRASS);
      return;
    }
    if (this.canSnow) {
      snow.setSize(200, 35);
      rain.setSize(0, 0);
      grass.setImage(SNOW_GRASS);
    } else {
      snow.setSize(0, 0);
      rain.setSize(200, 35);
      grass.setImage(NORMAL_GRASS);
    }
  }

  public void setSnowBiome(boolean canSnow) {
    System.out.println("IS SNOW: " + canSnow);
    this.canSnow = canSnow;
  }
}

public class RuleBookGUI extends LightweightGuiDescription {
  World world;
  long timeOfDay;
  int preventTickUpdates = 0;

  WPlainPanel root = new WPlainPanel();
  WSlider timeSlider = new WSlider(0, 23999, Axis.HORIZONTAL);
  WEnvMonitor envBox = new WEnvMonitor();

  WToggleButton btnLockTime = new WToggleButton(new LiteralText("freeze time"));
  WToggleButton btnLockWeather = new WToggleButton(new LiteralText("lock weather"));

  WClickableLabel sliderDayLabel = new WClickableLabel("Day");
  WClickableLabel sliderNightLabel = new WClickableLabel("Night");

  public RuleBookGUI(World w, PlayerEntity player) {
    this.timeOfDay = w.getTimeOfDay();
    this.world = w;

    BlockPos playerPos = player.getBlockPos();
    Biome bio = w.getBiome(player.getBlockPos());

    if (bio.getTemperature(playerPos) >= 0.15F) {
      this.envBox.setSnowBiome(false);
    } else {
      this.envBox.setSnowBiome(playerPos.getY() >= 0 && playerPos.getY() < 256);
    }
    this.envBox.setRain(world.isRaining());

    setRootPanel(root);
    root.setSize(100, 100);

    sliderDayLabel.setAlignment(Alignment.CENTER);
    sliderNightLabel.setAlignment(Alignment.CENTER);

    sliderDayLabel.setOnClick(() -> setTime(5500));
    sliderNightLabel.setOnClick(() -> setTime(18500));

    WSpriteButton btnWeatherClear = new WSpriteButton(new Identifier("testutils:sun.png"));
    WSpriteButton btnWeatherRain = new WSpriteButton(new Identifier("testutils:rain.png"));
    WSpriteButton btnWeatherThunder = new WSpriteButton(new Identifier("testutils:thunder.png"));

    btnWeatherClear.setOnClick(() -> setWeather(TestUtils.WEATHER_CLEAR));
    btnWeatherRain.setOnClick(() -> setWeather(TestUtils.WEATHER_RAIN));
    btnWeatherThunder.setOnClick(() -> setWeather(TestUtils.WEATHER_THUNDER));

    envBox.add(btnWeatherClear, 87, -1, 13, 13);
    envBox.add(btnWeatherRain, 87, 11, 13, 13);
    envBox.add(btnWeatherThunder, 87, 23, 13, 13);

    // add all time controls to root panel
    root.add(envBox, 0, 0, 6, 2);
    root.add(timeSlider, 0, 35, 100, 20);
    root.add(sliderDayLabel, 0, 52, 50, 20);
    root.add(sliderNightLabel, 50, 52, 50, 20);

    // add lock controls
    root.add(btnLockTime, 0, 70, 50, 20);
    root.add(btnLockWeather, 0, 85, 90, 20);

    // set initial timeOfDay
    int timeOfDay = (int)w.getTimeOfDay() % 24000;
    timeSlider.setValue(timeOfDay);
    envBox.setTimeOfDay(timeOfDay);

    btnLockTime.setToggle(!w.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE));
    btnLockWeather.setToggle(!w.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE));

    timeSlider.setValueChangeListener((time) -> {
      this.preventTickUpdates = 1000;
      envBox.setTimeOfDay(time);
    });
    timeSlider.setDraggingFinishedListener((value)-> {
      setTime((long) value);
      preventTickUpdates = 3;
    });

    // enabling the button locks the time
    btnLockTime.setOnToggle((toggled) -> setRule(TestUtils.DO_DAYLIGHT_CYCLE_RULE, !toggled));

    // enabling the button locks the weather
    btnLockWeather.setOnToggle((toggled) -> setRule(TestUtils.DO_WEATHER_CYCLE_RULE, !toggled));

    // can't do this sooner, no idea why
    envBox.setSize(100, 35);
    root.validate(this);
  }

  public void tick() {
    // do not update stuff if currently dragging
    // this is a bit hacky, but tick timing can be hard
    if (this.preventTickUpdates > 0) {
      this.preventTickUpdates--;
      return;
    }
    long time = world.getTimeOfDay() % 24000;
    this.envBox.setTimeOfDay(time);
    this.envBox.setRain(world.isRaining());
    this.timeSlider.setValue((int)time);

    // update if someone else changed the game rules
    btnLockTime.setToggle(!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE));
    btnLockWeather.setToggle(!world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE));
  }

  private void setTime(long timeOfDay) {
    preventTickUpdates += 1;
    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
    passedData.writeLong(timeOfDay);
    // Send packet to server to change the time
    ClientSidePacketRegistry.INSTANCE.sendToServer(TestUtils.SET_TIME_PACKET_ID, passedData);
    timeSlider.setValue((int)timeOfDay, false);
    envBox.setTimeOfDay(timeOfDay);
  }

  private void setRule(short id, boolean value) {
    preventTickUpdates += 1;
    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
    // eg. 0 is day cycle
    passedData.writeShort(id);
    // enabling the button locks the weather
    passedData.writeBoolean(value);
    ClientSidePacketRegistry.INSTANCE.sendToServer(TestUtils.SET_RULE_PACKET_ID, passedData);
  }

  private void setWeather(short weather) {
    preventTickUpdates += 1;
    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
    passedData.writeShort(weather);
    ClientSidePacketRegistry.INSTANCE.sendToServer(TestUtils.SET_WEATHER_PACKET_ID, passedData);
  }
}
