package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public class HRGuideComponent extends GuideComponent
{
	public static final HRGuideComponent INSTANCE = new HRGuideComponent();

	private static class HRWidget extends Widget implements IGuideComponentWidget
	{
		public HRWidget(ComponentPanel parent)
		{
			super(parent);
			setSize(1, 3);
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			((GuiGuide) getGui()).page.lineColor.draw(x, y + 1, ((ComponentPanel) parent).width, h - 2);
		}
	}

	private HRGuideComponent()
	{
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new HRWidget(parent);
	}

	@Override
	public boolean isInline()
	{
		return false;
	}

	public String toString()
	{
		return "";
	}
}