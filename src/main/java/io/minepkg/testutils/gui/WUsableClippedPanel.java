package io.minepkg.testutils.gui;

import io.github.cottonmc.cotton.gui.widget.WClippedPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

public class WUsableClippedPanel extends WClippedPanel {
  public WUsableClippedPanel() {
    super();
  }

	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.setParent(this);
		w.setLocation(x, y);
		if (w.canResize()) {
			w.setSize(18, 18);
		}
	}

	public void add(WWidget w, int x, int y, int width, int height) {
		children.add(w);
		w.setParent(this);
		w.setLocation(x, y);
		if (w.canResize()) {
			w.setSize(width, height);
		}
  }

  @Override
  public void expandToFit(WWidget w) {}
}
