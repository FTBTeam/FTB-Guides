package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.client.FTBGuidesClientConfig;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
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
			Theme theme = getGui().getTheme();

			for (String s : t.text.split("\n"))
			{
				strings.addAll(theme.listFormattedStringToWidth(s, (int) (parent.maxWidth / scale)));
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
					setWidth(Math.max(width, (int) (text[i].length() * (FTBGuidesClientConfig.general.use_unicode_font ? 4 : 6) * scale)));
				}
				else
				{
					setWidth(Math.max(width, (int) (theme.getStringWidth(text[i]) * scale)));
				}
			}

			int h1 = (int) ((theme.getFontHeight() + 1D) * scale);
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
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			boolean mouseOver = isMouseOver() && (!component.getProperty("click", true).isEmpty() || !component.getProperty("hover", false).isEmpty());

			if (!icon.isEmpty())
			{
				icon.draw(x, y, 8, 8);
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
			Color4I color = theme.getContentColor((mouseOver || code) ? WidgetType.MOUSE_OVER : WidgetType.NORMAL);

			for (int i = 0; i < text.length; i++)
			{
				if (code)
				{
					for (int ci = 0; ci < text[i].length(); ci++)
					{
						theme.drawString(Character.toString(text[i].charAt(ci)), ci * (FTBGuidesClientConfig.general.use_unicode_font ? 4 : 6), h1 * i, color, 0);
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