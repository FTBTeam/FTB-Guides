package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public class LineBreakGuideComponent extends GuideComponent
{
	public static final LineBreakGuideComponent INSTANCE = new LineBreakGuideComponent();

	private static class LineBreakWidget extends Widget implements IGuideComponentWidget
	{
		private LineBreakWidget(Panel panel)
		{
			super(panel);
			setHeight(10);
		}
	}

	private LineBreakGuideComponent()
	{
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new LineBreakWidget(parent);
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