package com.feed_the_beast.mods.ftbguides.gui.components;

import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;

/**
 * @author LatvianModder
 */
public class VideoGuideComponent extends ImageGuideComponent
{
	private static final Icon OVERLAY = Icon.getIcon("https://i.imgur.com/s946ApP.png");

	public static class VideoWidget extends ImgWidget
	{
		public VideoWidget(ComponentPanel parent, ImageGuideComponent c)
		{
			super(parent, c);
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			super.draw(theme, x, y, w, h);
			OVERLAY.draw(x, y, w, h, Color4I.WHITE.withAlpha(150));
		}
	}

	public VideoGuideComponent(Icon i, int w, int h)
	{
		super(i, w, h);
	}

	@Override
	public IGuideComponentWidget createWidget(ComponentPanel parent)
	{
		return new VideoWidget(parent, this);
	}
}