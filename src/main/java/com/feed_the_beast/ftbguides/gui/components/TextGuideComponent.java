package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TextGuideComponent extends GuideComponent
{
	public static final Icon CODE_BACKGROUND = Color4I.rgba(0x33AAAAAA);

	private static class TextWidget extends Widget implements IGuideComponentWidget
	{
		public final TextGuideComponent component;
		public final String[] text;
		public final boolean bold, italic, underlined, striketrough, code;
		public final Icon icon;

		public TextWidget(Panel parent, TextGuideComponent t)
		{
			super(parent.gui);
			component = t;
			List<String> strings = new ArrayList<>();

			for (String s : t.text.split("\n"))
			{
				strings.addAll(gui.listFormattedStringToWidth(s, parent.width));
			}

			text = strings.isEmpty() ? StringUtils.EMPTY_ARRAY : strings.toArray(new String[0]);

			bold = t.getProperty("bold", true).equals("true");
			italic = t.getProperty("italic", true).equals("true");
			underlined = t.getProperty("underlined", true).equals("true");
			striketrough = t.getProperty("striketrough", true).equals("true");
			code = t.getProperty("code", true).equals("true");
			icon = Icon.getIcon(t.getProperty("icon", false));

			setWidth(0);

			for (int i = 0; i < text.length; i++)
			{
				if (bold)
				{
					text[i] = TextFormatting.BOLD + text[i];
				}

				if (italic)
				{
					text[i] = TextFormatting.ITALIC + text[i];
				}

				if (underlined)
				{
					text[i] = TextFormatting.UNDERLINE + text[i];
				}

				if (striketrough)
				{
					text[i] = TextFormatting.STRIKETHROUGH + text[i];
				}

				setWidth(Math.max(width, gui.getStringWidth(text[i])));
			}

			if (!icon.isEmpty())
			{
				setWidth(width + 10);
			}

			setHeight(text.length * 10);
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			String s = component.getProperty("hover", false);

			if (!s.isEmpty())
			{
				list.add(s);
			}
		}

		@Override
		public boolean mousePressed(MouseButton button)
		{
			if (gui.isMouseOver(this))
			{
				String s = component.getProperty("click", true);

				if (!s.isEmpty() && gui.onClickEvent(s))
				{
					GuiHelper.playClickSound();
					return true;
				}
			}

			return false;
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			boolean mouseOver = gui.isMouseOver(ax, ay, width, height) && (!component.getProperty("click", true).isEmpty() || !component.getProperty("hover", false).isEmpty());

			if (!icon.isEmpty())
			{
				icon.draw(ax, ay, 8, 8);
				ax += 10;
			}

			for (int i = 0; i < text.length; i++)
			{
				if (code)
				{
					CODE_BACKGROUND.draw(ax - 1, ay + 10 * i, gui.getStringWidth(text[i]) + 2, 10);
				}

				gui.drawString(text[i], ax, ay + 10 * i, mouseOver ? MOUSE_OVER : 0);
			}
		}
	}

	public final String text;

	public TextGuideComponent(String txt)
	{
		text = txt;
	}

	public String toString()
	{
		return text;
	}

	@Override
	public boolean isEmpty()
	{
		return text.isEmpty();
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new TextWidget(parent, this);
	}
}