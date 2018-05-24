package com.feed_the_beast.ftbguides.gui.components;

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
		public void draw()
		{
			super.draw();
			OVERLAY.draw(getAX(), getAY(), width, height, Color4I.WHITE.withAlpha(150));
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