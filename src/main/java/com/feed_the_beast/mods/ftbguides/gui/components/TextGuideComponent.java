package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.ftbguides.FTBGuidesLocalConfig;
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

		public TextWidget(ComponentPanel parent, TextGuideComponent t)
		{
			super(parent);
			component = t;
			scale = MathHelper.clamp(component.textScale, 0.25D, 4D);
			List<String> strings = new ArrayList<>();
			Theme theme = getGui().getTheme();

			for (String s : t.text.split("\n"))
			{
				strings.addAll(theme.listFormattedStringToWidth(s, (int) (parent.maxWidth / scale)));
			}

			text = strings.isEmpty() ? StringUtils.EMPTY_ARRAY : strings.toArray(new String[0]);

			setWidth(0);

			for (int i = 0; i < text.length; i++)
			{
				if (component.bold)
				{
					text[i] = TextFormatting.BOLD + text[i];
				}

				if (component.italic)
				{
					text[i] = TextFormatting.ITALIC + text[i];
				}

				if (component.underlined)
				{
					text[i] = TextFormatting.UNDERLINE + text[i];
				}

				if (component.strikethrough)
				{
					text[i] = TextFormatting.STRIKETHROUGH + text[i];
				}

				if (component.code)
				{
					setWidth(Math.max(width, (int) (text[i].length() * (FTBGuidesLocalConfig.general.use_unicode_font ? 4 : 6) * scale)));
				}
				else
				{
					setWidth(Math.max(width, (int) (theme.getStringWidth(text[i]) * scale)));
				}
			}

			int h1 = (int) ((theme.getFontHeight() + 1D) * scale);
			setHeight(text.length == 0 ? h1 : h1 * text.length);

			if (!component.icon.isEmpty())
			{
				setWidth(width + 10);
			}
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			if (!component.hover.isEmpty())
			{
				list.add(component.hover);
			}
		}

		@Override
		public boolean mousePressed(MouseButton button)
		{
			if (isMouseOver())
			{
				if (!component.click.isEmpty() && handleClick(component.click))
				{
					GuiHelper.playClickSound();
					return true;
				}
			}

			return false;
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			boolean mouseOver = isMouseOver() && (!component.click.isEmpty() || !component.hover.isEmpty());

			if (!component.icon.isEmpty())
			{
				component.icon.draw(x, y, 8, 8);
				x += 10;
			}

			int h1 = theme.getFontHeight() + 1;

			if (scale != 1D)
			{
				GuiHelper.setFixUnicode(false);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.scale(scale, scale, 1D);
			Color4I color = theme.getContentColor((mouseOver || component.code) ? WidgetType.MOUSE_OVER : WidgetType.NORMAL);

			for (int i = 0; i < text.length; i++)
			{
				if (component.code)
				{
					for (int ci = 0; ci < text[i].length(); ci++)
					{
						theme.drawString(Character.toString(text[i].charAt(ci)), ci * (FTBGuidesLocalConfig.general.use_unicode_font ? 4 : 6), h1 * i, color, 0);
					}
				}
				else
				{
					theme.drawString(text[i], 0, h1 * i, color, 0);
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
	public Icon icon = Icon.EMPTY;
	public boolean bold = false;
	public boolean italic = false;
	public boolean underlined = false;
	public boolean strikethrough = false;
	public boolean code = false;
	public String click = "";
	public String hover = "";
	public double textScale = 1D;

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