package com.feed_the_beast.mods.ftbguides.gui.components;

/**
 * @author LatvianModder
 */
public abstract class GuideComponent
{
	public GuideComponent parent = null;

	public boolean isEmpty()
	{
		return false;
	}

	public abstract IGuideComponentWidget createWidget(ComponentPanel parent);

	public boolean isInline()
	{
		return true;
	}

	public String toString()
	{
		return getClass().getSimpleName();
	}
}