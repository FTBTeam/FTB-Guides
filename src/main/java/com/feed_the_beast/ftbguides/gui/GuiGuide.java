package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.client.FTBGuidesClientConfig;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ImageIcon;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.google.gson.JsonElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiGuide extends GuiBase
{
	public static class GuideTheme extends Theme
	{
		public final GuidePage page;

		public GuideTheme(GuidePage p)
		{
			page = p;
		}

		@Override
		public Color4I getContentColor(boolean mouseOver)
		{
			return mouseOver ? page.textColorMouseOver : page.textColor;
		}

		@Override
		public Icon getGui(boolean mouseOver)
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getWidget(boolean mouseOver)
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getSlot(boolean mouseOver)
		{
			return Icon.EMPTY;
		}

		@Override
		public Icon getPanelBackground()
		{
			return CommonUtils.DEV_ENV && Keyboard.isKeyDown(Keyboard.KEY_B) ? Icon.EMPTY.withOutline(Color4I.RED, false) : Icon.EMPTY;
		}
	}

	private static class ButtonSelectPage extends Button
	{
		private GuidePage page;

		public ButtonSelectPage(GuiBase gui, @Nullable GuidePage p, boolean addicon)
		{
			super(gui, 0, 0, 0, 9, getTitle(gui, p), addicon && p != null ? p.icon : Icon.EMPTY);
			setWidth(gui.getStringWidth(getTitle()) + (icon.isEmpty() ? 0 : 10));
			page = p;
		}

		private static String getTitle(GuiBase gui, @Nullable GuidePage p)
		{
			if (p == null)
			{
				return "/";
			}
			else if (gui instanceof GuiGuide && ((GuiGuide) gui).page == p)
			{
				return StringUtils.bold(p.title.createCopy(), true).getFormattedText();
			}
			else
			{
				return p.title.getFormattedText();
			}
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (page != null && page != ((GuiGuide) gui).page)
			{
				GuiHelper.playClickSound();
				Guides.openGui(page);
			}
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			//GuiHelper.drawHollowRect(ax, ay, width, height, Color4I.RED, false);
			boolean mouseOver = page != null && page != ((GuiGuide) gui).page && gui.isMouseOver(ax, ay, width, height);

			if (icon.isEmpty())
			{
				gui.drawString(getTitle(), ax, ay - 1, mouseOver ? MOUSE_OVER : 0);
			}
			else
			{
				Icon icon = getIcon();
				icon.draw(ax, ay, 8, 8);
				gui.drawString(getTitle(), ax + 10, ay - 1, mouseOver ? MOUSE_OVER : 0);
			}
		}
	}

	private static class ButtonSpecial extends Button
	{
		private final SpecialGuideButton specialInfoButton;

		public ButtonSpecial(GuiBase gui, SpecialGuideButton b)
		{
			super(gui, 0, 0, 12, 12);
			specialInfoButton = b;
			setTitle(specialInfoButton.title.getFormattedText());
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (gui.onClickEvent(specialInfoButton.click))
			{
				GuiHelper.playClickSound();
			}
		}

		@Override
		public void draw()
		{
			specialInfoButton.icon.draw(getAX() + 2, getAY() + 2, 8, 8);
		}
	}

	public final GuidePage page;
	public final Panel panelText, panelTitle, panelSpecialButtons;
	public final PanelScrollBar scrollBarV;

	public GuiGuide(GuidePage p)
	{
		super(0, 0);
		page = p;
		addFlags(UNICODE);

		panelText = new Panel(this, 4, 14, 0, 16)
		{
			private final WidgetLayout LAYOUT = new WidgetLayout.Vertical(1, 0, 4);

			@Override
			public void addWidgets()
			{
				for (GuidePage p : page.pages)
				{
					add(new ButtonSelectPage(gui, p, true));
				}

				for (GuideComponent component : page.components)
				{
					add((Widget) component.createWidget(panelText));
				}

				if (!widgets.isEmpty())
				{
					int s = align(LAYOUT);
					scrollBarV.setElementSize(s);
					scrollBarV.setSrollStepFromOneElementSize((s - 6) / widgets.size());
				}
			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getPanelBackground();
			}
		};

		panelText.addFlags(DEFAULTS | UNICODE);

		panelTitle = new Panel(this, 3, 2, 0, 8)
		{
			@Override
			public void addWidgets()
			{
				List<ButtonSelectPage> list = new ArrayList<>(1);
				list.add(new ButtonSelectPage(gui, page, false));
				addToList(page, list);
				Collections.reverse(list);
				addAll(list);
				setWidth(align(WidgetLayout.HORIZONTAL));
			}

			private void addToList(GuidePage page, List<ButtonSelectPage> list)
			{
				if (page.parent != null)
				{
					list.add(new ButtonSelectPage(gui, null, false));
					list.add(new ButtonSelectPage(gui, page.parent, false));
					addToList(page.parent, list);
				}
			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getPanelBackground();
			}
		};

		panelTitle.addFlags(DEFAULTS | UNICODE);

		panelSpecialButtons = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				if (page == GuidePageRoot.INSTANCE || page == GuideTitlePage.SERVER_INFO)
				{
					add(new ButtonSpecial(gui, new SpecialGuideButton(GuiLang.REFRESH.textComponent(null), GuiIcons.REFRESH, "refresh:/")));
				}

				for (SpecialGuideButton button : page.specialButtons)
				{
					add(new ButtonSpecial(gui, button));
				}

				JsonElement url = page.getProperty("browser_url");

				if (url.isJsonPrimitive())
				{
					add(new ButtonSpecial(gui, new SpecialGuideButton(new TextComponentTranslation("ftbguides.lang.open_in_browser"), GuiIcons.GLOBE, url.getAsString())));
				}

				setWidth(align(WidgetLayout.HORIZONTAL));
			}

			@Override
			public int getAX()
			{
				return gui.width - width;
			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getPanelBackground();
			}
		};

		panelSpecialButtons.addFlags(DEFAULTS);
		panelSpecialButtons.setHeight(12);

		scrollBarV = new PanelScrollBar(this, 0, 10, 12, 0, 18, panelText)
		{
			@Override
			public int getAX()
			{
				return width - 10 - scrollBarV.width;
			}
		};
	}

	@Override
	public void addWidgets()
	{
		add(scrollBarV);
		add(panelText);
		add(panelTitle);
		add(panelSpecialButtons);
	}

	@Override
	public boolean onInit()
	{
		if (!page.textURL.isEmpty())
		{
			new ThreadLoadPage(page).start();
			return false;
		}

		return setFullscreen();
	}

	@Override
	public void onPostInit()
	{
		panelText.setWidth(width - scrollBarV.width - 7);
		panelText.setHeight(height - 15);
		scrollBarV.setHeight(height - 20);
	}

	@Override
	public void drawBackground()
	{
		page.background.bindTexture();

		if (page.background instanceof ImageIcon)
		{
			GuiHelper.drawTexturedRect(0, 0, width, height, Color4I.WHITE.withAlpha(FTBGuidesClientConfig.general.background_alpha), 0D, 0D, width / 128D, height / 128D);
		}
		else
		{
			page.background.draw(0, 0, width, height, Color4I.WHITE.withAlpha(FTBGuidesClientConfig.general.background_alpha));
		}

		GuiHelper.drawHollowRect(0, 0, width, height, page.lineColor, false);
		page.lineColor.draw(width - scrollBarV.width, 12, 1, height - 12);
		page.lineColor.draw(0, 11, width, 1);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}

	@Override
	public Theme createTheme()
	{
		return new GuideTheme(page);
	}

	@Override
	public boolean drawDefaultBackground()
	{
		return false;
	}

	@Override
	public boolean keyPressed(int key, char keyChar)
	{
		if (key == Keyboard.KEY_F5)
		{
			return onClickEvent("refresh:/");
		}
		else if (key == Keyboard.KEY_HOME)
		{
			Guides.openGui(GuidePageRoot.INSTANCE);
			return true;
		}
		else if (key == Keyboard.KEY_BACK)
		{
			return mousePressed(MouseButton.get(3));
		}
		else if (key == Keyboard.KEY_NEXT)
		{
			return mousePressed(MouseButton.get(4));
		}
		else
		{
			return super.keyPressed(key, keyChar);
		}
	}

	@Override
	public boolean mousePressed(MouseButton button)
	{
		if (button.id == 3)
		{
			if (page.parent != null)
			{
				Guides.openGui(page.parent);
			}

			return true;
		}
		else if (button.id == 4)
		{
			System.out.println("Forward pressed!");
			return true;
		}
		else
		{
			return super.mousePressed(button);
		}
	}

	@Override
	public boolean onClickEvent(String scheme, String path)
	{
		if (scheme.isEmpty() || scheme.equals("page"))
		{
			GuidePage page = this.page.getSubFromPath(path);

			if (page != null)
			{
				Guides.openGui(page);
				return true;
			}

			return false;
		}
		else if (scheme.equals("refresh"))
		{
			Guides.setShouldReload();
			GuidePage page = GuidePageRoot.INSTANCE.getSubFromPath(path);

			if (page != null)
			{
				Guides.openGui(page);
			}
			else
			{
				Guides.openGui();
			}

			return true;
		}
		else
		{
			return super.onClickEvent(scheme, path);
		}
	}

	@Override
	@Nullable
	public GuiScreen getPrevScreen()
	{
		return null;
	}
}