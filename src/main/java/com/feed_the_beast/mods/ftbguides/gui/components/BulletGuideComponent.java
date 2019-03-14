package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.mods.ftbguides.gui.GuiGuide;

/**
 * @author LatvianModder
 */
public class BulletGuideComponent extends GuideComponent
{
	public static final BulletGuideComponent INSTANCE = new BulletGuideComponent();

	private static class BulletWidget extends Widget implements IGuideComponentWidget
	{
		public BulletWidget(ComponentPanel parent)
		{
			super(parent);
			setSize(8, 8);
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			((GuiGuide) getGui()).page.lineColor.draw(x + 2, y + 2, 4, 4);
		}
	}

	private BulletGuideComponent()
	{
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new BulletWidget(parent);
	}

	@Override
	public boolean isInline()
	{
		return false;
	}

	public String toString()
	{
		return "*";
	}
}