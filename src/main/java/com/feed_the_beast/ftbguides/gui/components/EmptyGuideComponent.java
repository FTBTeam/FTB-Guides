package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public class EmptyGuideComponent extends GuideComponent
{
	public static final EmptyGuideComponent INSTANCE = new EmptyGuideComponent();

	private static class EmptyComponentWidget extends Widget implements IGuideComponentWidget
	{
		private EmptyComponentWidget(GuiBase gui)
		{
			super(gui);
		}
	}

	private EmptyGuideComponent()
	{
	}

	public String toString()
	{
		return "";
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public GuideComponent copy()
	{
		return this;
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new EmptyComponentWidget(parent.gui);
	}
}