package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftblib.lib.gui.Widget;

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
		public void draw()
		{
			((GuiGuide) getGui()).page.lineColor.draw(getAX() + 2, getAY() + 2, 4, 4);
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