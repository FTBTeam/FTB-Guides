package com.feed_the_beast.ftbguides.gui.components;

/**
 * @author LatvianModder
 */
public interface IGuideComponentWidget
{
	default boolean isInline()
	{
		return true;
	}

	default int getMaxWidth()
	{
		return Integer.MAX_VALUE;
	}
}