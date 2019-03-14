package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public class EmptyGuideComponent extends GuideComponent
{
	public static final EmptyGuideComponent INSTANCE = new EmptyGuideComponent();

	private static class EmptyComponentWidget extends Widget implements IGuideComponentWidget
	{
		private EmptyComponentWidget(ComponentPanel panel)
		{
			super(panel);
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
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new EmptyComponentWidget(parent);
	}
}