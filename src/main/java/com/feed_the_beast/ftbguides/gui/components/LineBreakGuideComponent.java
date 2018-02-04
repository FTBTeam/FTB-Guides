package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
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
		private LineBreakWidget(GuiBase gui)
		{
			super(gui);
			setHeight(10);
		}

		@Override
		public boolean isInline()
		{
			return false;
		}
	}

	private LineBreakGuideComponent()
	{
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new LineBreakWidget(parent.gui);
	}

	public String toString()
	{
		return "";
	}
}