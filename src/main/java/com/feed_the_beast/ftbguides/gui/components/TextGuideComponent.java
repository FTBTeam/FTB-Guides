package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
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
	private static class TextWidget extends Widget implements IGuideComponentWidget
	{
		public final TextGuideComponent component;
		public final String[] text;
		public final boolean bold, italic, underlined, striketrough, code;

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

			bold = t.getProperty("bold").equals("true");
			italic = t.getProperty("italic").equals("true");
			underlined = t.getProperty("underlined").equals("true");
			striketrough = t.getProperty("striketrough").equals("true");
			code = t.getProperty("code").equals("true");

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

			setHeight(text.length * 10);
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			String s = component.getProperty("hover");

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
				String s = component.getProperty("click");

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

			boolean mouseOver = gui.isMouseOver(ax, ay, width, height) && (!component.getProperty("click").isEmpty() || !component.getProperty("hover").isEmpty());

			for (int i = 0; i < text.length; i++)
			{
				if (code)
				{
					Color4I.BLACK_A[20].draw(ax - 1, ay + 10 * i, gui.getStringWidth(text[i]) + 2, 10);
				}

				gui.drawString(text[i], ax, ay + 10 * i, mouseOver ? MOUSE_OVER : 0);
			}
		}
		
		/*
		public List<String> text = Collections.emptyList();
		public int textFlags = 0;
		public boolean autoSizeWidth, autoSizeHeight;

		public TextField(GuiBase gui, int x, int y, int w, int h, String txt, int flags)
		{
			super(gui, x, y, w, h);
			textFlags = flags;
			autoSizeWidth = w <= 0;
			autoSizeHeight = h <= 0;
			setText(txt);
		}

		public TextField(GuiBase gui, int x, int y, int w, int h, String txt)
		{
			this(gui, x, y, w, h, txt, 0);
		}

		public TextField setText(String txt)
		{
			text = null;

			if (!txt.isEmpty())
			{
				text = new ArrayList<>(autoSizeWidth ? CommonUtils.asList(txt.split("\n")) : gui.listFormattedStringToWidth(txt, width));
			}

			if (text == null || text.isEmpty())
			{
				text = Collections.emptyList();
			}

			if (autoSizeWidth)
			{
				setWidth(0);

				for (String s : text)
				{
					setWidth(Math.max(width, gui.getStringWidth(s)));
				}
			}

			if (autoSizeHeight)
			{
				int h1 = gui.getFontHeight() + 1;
				setHeight(text.isEmpty() ? h1 : h1 * text.size());
			}

			return this;
		}
		*/
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
	public GuideComponent copy()
	{
		return new TextGuideComponent(text).copyProperties(this);
	}

	@Override
	public IGuideComponentWidget createWidget(Panel parent)
	{
		return new TextWidget(parent, this);
	}
}