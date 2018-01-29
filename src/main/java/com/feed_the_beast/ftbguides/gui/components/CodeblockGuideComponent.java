package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CodeblockGuideComponent extends GuideComponent
{
	private static class CodeblockWidget extends Widget implements IGuideComponentWidget
	{
		public final CodeblockGuideComponent component;
		public final String[] text;

		public CodeblockWidget(Panel parent, CodeblockGuideComponent t)
		{
			super(parent.gui);
			component = t;
			List<String> strings = new ArrayList<>();

			for (String s : component.toString().split("\n"))
			{
				strings.addAll(gui.listFormattedStringToWidth(s, parent.width));
			}

			text = strings.isEmpty() ? StringUtils.EMPTY_ARRAY : strings.toArray(new String[0]);

			setWidth(0);

			for (String s : text)
			{
				setWidth(Math.max(width, gui.getStringWidth(s)));
			}

			setHeight(text.length * 10);
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			TextGuideComponent.CODE_BACKGROUND.draw(ax, ay, width, height);

			for (int i = 0; i < text.length; i++)
			{
				gui.drawString(text[i], ax, ay + 10 * i);
			}
		}
	}

	public final List<String> text;

	public CodeblockGuideComponent(List<String> txt)
	{
		text = txt;
	}

	public String toString()
	{
		return StringJoiner.with('\n').join(text);
	}

	@Override
	public boolean isEmpty()
	{
		for (String s : text)
		{
			if (!s.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new CodeblockWidget(parent, this);
	}
}