package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import net.minecraft.util.text.ITextComponent;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author LatvianModder
 */
public class GuideTheme extends FinalIDObject
{
	public static final HashMap<String, GuideTheme> THEMES = new LinkedHashMap<>();

	public static GuideTheme get(String id)
	{
		GuideTheme theme = THEMES.get(id);
		return theme == null ? THEMES.get("paper") : theme;
	}

	public ITextComponent title;
	public Icon background = Icon.EMPTY;
	public Color4I text = Icon.EMPTY, textMouseOver = Icon.EMPTY, lines = Icon.EMPTY;
	public GuideTheme next = this;

	public GuideTheme(String id)
	{
		super(id);
	}
}