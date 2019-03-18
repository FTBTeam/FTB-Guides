package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ImageGuideComponent extends GuideComponent
{
	public static class ImgWidget extends Widget implements IGuideComponentWidget
	{
		private final ImageGuideComponent component;

		public ImgWidget(ComponentPanel parent, ImageGuideComponent c)
		{
			super(parent);
			component = c;
			setWidth((int) (component.getWidth() / (double) getScreen().getScaleFactor()));
			setHeight((int) (component.getHeight() / (double) getScreen().getScaleFactor()));

			if (width > parent.maxWidth)
			{
				int w = Math.min(parent.maxWidth, width);
				double h = height * (w / (double) width);
				setWidth(w);
				setHeight(Math.max((int) h, 0));
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
			component.image.draw(x, y, w, h);
		}
	}

	public final Icon image;
	private int width, height;
	public String click = "";
	public String hover = "";

	public ImageGuideComponent(Icon i, int w, int h)
	{
		image = i;
		width = w;
		height = h;
	}

	public ImageGuideComponent(Icon i)
	{
		image = i;
		width = -1;
		height = -1;
	}

	public String toString()
	{
		return image.toString();
	}

	@Override
	public boolean isEmpty()
	{
		return image.isEmpty();
	}

	private void loadSizeFromImage()
	{
		if (width == -1 || height == -1)
		{
			try
			{
				BufferedImage img = image.readImage();

				if (width == -1)
				{
					width = img.getWidth();
				}

				if (height == -1)
				{
					height = img.getHeight();
				}
			}
			catch (Exception ex)
			{
				if (width == -1)
				{
					width = 32;
				}

				if (height == -1)
				{
					height = 32;
				}
			}
		}
	}

	public int getWidth()
	{
		loadSizeFromImage();
		return width;
	}

	public int getHeight()
	{
		loadSizeFromImage();
		return height;
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new ImgWidget(parent, this);
	}
}