package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;

/**
 * @author LatvianModder
 */
public enum EnumGuideTheme
{
	PAPER,
	DARK,
	LIGHT;

	public Icon background;
	public Color4I text, textMouseOver, lines;

	public EnumGuideTheme next()
	{
		return values()[(ordinal() + 1) % values().length];
	}
}