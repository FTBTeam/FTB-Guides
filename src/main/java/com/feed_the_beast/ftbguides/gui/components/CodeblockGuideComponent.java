package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;

import java.util.List;

/**
 * @author LatvianModder
 */
public class CodeblockGuideComponent extends GuideComponent
{
	private static class CodeblockWidget extends Widget implements IGuideComponentWidget
	{
		public static final Icon CODE_BACKGROUND = Color4I.rgba(0x33AAAAAA);

		public final CodeblockGuideComponent component;
		public final String[] text;

		public CodeblockWidget(ComponentPanel parent, CodeblockGuideComponent t)
		{
			super(parent);
			component = t;
			text = component.toString().split("\n");

			setWidth(0);

			for (String s : text)
			{
				setWidth(Math.max(width, getStringWidth(s)));
			}

			setHeight(text.length * 10);
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			CODE_BACKGROUND.draw(ax, ay, width, height);

			for (int i = 0; i < text.length; i++)
			{
				drawString(text[i], ax, ay + 10 * i);
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
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new CodeblockWidget(parent, this);
	}
}