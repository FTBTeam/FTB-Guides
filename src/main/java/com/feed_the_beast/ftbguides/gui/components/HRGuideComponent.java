package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public class HRGuideComponent extends GuideComponent
{
	public static final HRGuideComponent INSTANCE = new HRGuideComponent();

	private static class HRWidget extends Widget implements IGuideComponentWidget
	{
		public HRWidget(Panel parent)
		{
			super(parent.gui);
			setWidth(1);
			setHeight(3);
		}

		@Override
		public boolean isInline()
		{
			return false;
		}

		@Override
		public void draw()
		{
			((GuiGuide) gui).page.lineColor.draw(getAX(), getAY() + 1, parent.width, height - 2);
		}
	}

	private HRGuideComponent()
	{
	}

	@Override
	public GuideComponent copy()
	{
		return this;
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new HRWidget(parent.gui);
	}

	public String toString()
	{
		return "";
	}
}