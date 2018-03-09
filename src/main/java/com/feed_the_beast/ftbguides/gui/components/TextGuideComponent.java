package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
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
		public final double scale;
		public final String[] text;
		public final boolean bold, italic, underlined, striketrough, code;
		public final Icon icon;

		public TextWidget(ComponentPanel parent, TextGuideComponent t)
		{
			super(parent);
			component = t;
			scale = MathHelper.clamp(Double.parseDouble(component.getProperty("text_scale", true, "1")), 0.25D, 4D);
			List<String> strings = new ArrayList<>();

			for (String s : t.text.split("\n"))
			{
				strings.addAll(listFormattedStringToWidth(s, (int) (parent.maxWidth / scale)));
			}

			text = strings.isEmpty() ? StringUtils.EMPTY_ARRAY : strings.toArray(new String[0]);

			bold = component.getProperty("bold", true).equals("true");
			italic = component.getProperty("italic", true).equals("true");
			underlined = component.getProperty("underlined", true).equals("true");
			striketrough = component.getProperty("striketrough", true).equals("true");
			code = component.getProperty("code", true).equals("true");
			icon = Icon.getIcon(component.getProperty("icon", false));

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

				if (code)
				{
					setWidth(Math.max(width, (int) (text[i].length() * 4 * scale)));
				}
				else
				{
					setWidth(Math.max(width, (int) (getStringWidth(text[i]) * scale)));
				}
			}

			int h1 = (int) ((getFontHeight() + 1D) * scale);
			setHeight(text.length == 0 ? h1 : h1 * text.length);

			if (!icon.isEmpty())
			{
				setWidth(width + 10);
			}
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
			if (isMouseOver())
			{
				String s = component.getProperty("click", true);

				if (!s.isEmpty() && handleClick(s))
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
			boolean mouseOver = isMouseOver() && (!component.getProperty("click", true).isEmpty() || !component.getProperty("hover", false).isEmpty());
			int ax = getAX();
			int ay = getAY();

			if (!icon.isEmpty())
			{
				icon.draw(ax, ay, 8, 8);
				ax += 10;
			}

			int h1 = getFontHeight() + 1;

			if (scale != 1D)
			{
				GuiHelper.setFixUnicode(false);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(ax, ay, 0);
			GlStateManager.scale(scale, scale, 1D);

			for (int i = 0; i < text.length; i++)
			{
				if (code)
				{
					for (int ci = 0; ci < text[i].length(); ci++)
					{
						drawString(Character.toString(text[i].charAt(ci)), ci * 4, h1 * i, mouseOver ? MOUSE_OVER : 0);
					}
				}
				else
				{
					drawString(text[i], 0, h1 * i, mouseOver ? MOUSE_OVER : 0);
				}
			}

			GlStateManager.popMatrix();

			if (scale != 1D)
			{
				GuiHelper.setFixUnicode(getGui().fixUnicode);
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
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new TextWidget(parent, this);
	}
}