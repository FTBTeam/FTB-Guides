package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.gui.GuiGuide;
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
			((GuiGuide) getGui()).page.lineColor.draw(getAX(), getAY() + 1, ((ComponentPanel) parent).getMaxWidth(), height - 2);
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

	public String toString()
	{
		return "";
	}
}