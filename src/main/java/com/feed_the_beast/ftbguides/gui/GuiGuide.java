package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.FTBGuidesConfig;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftbguides.client.FTBGuidesClientConfig;
import com.feed_the_beast.ftbguides.client.GuideTheme;
import com.feed_the_beast.ftbguides.gui.components.ComponentPanel;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ImageIcon;
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
	private static final int SCROLLBAR_SIZE = 7;

	public static class GuideGuiTheme extends Theme
	{
		public final GuidePage page;

		public GuideGuiTheme(GuidePage p)
		{
			page = p;
		}

		@Override
		public Color4I getContentColor(WidgetType type)
		{
			return type == WidgetType.MOUSE_OVER ? page.textColorMouseOver : page.textColor;
		}

		@Override
		public void drawGui(int x, int y, int w, int h, WidgetType type)
		{
		}

		@Override
		public void drawWidget(int x, int y, int w, int h, WidgetType type)
		{
		}

		@Override
		public void drawSlot(int x, int y, int w, int h, WidgetType type)
		{
		}

		@Override
		public void drawPanelBackground(int x, int y, int w, int h)
		{
		}

		@Override
		public void drawScrollBarBackground(int x, int y, int w, int h, WidgetType type)
		{
		}

		@Override
		public void drawScrollBar(int x, int y, int w, int h, WidgetType type, boolean vertical)
		{
			getContentColor(type).withAlpha(100).draw(x + 1, y + 1, w - 2, h - 2);
		}
	}

	private static class ButtonSelectPage extends Button
	{
		private GuidePage page;

		public ButtonSelectPage(Panel panel, @Nullable GuidePage p)
		{
			super(panel, p == null ? "/" : p.title.getFormattedText(), Icon.EMPTY);
			setSize(getGui().getTheme().getStringWidth(getTitle()), 9);
			page = p;
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (page != null)
			{
				GuiHelper.playClickSound();
				FTBGuidesClient.openGuidesGui(page.getPath());
			}
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			boolean mouseOver = page != null && isMouseOver();
			theme.drawString(getTitle(), x, y, mouseOver ? Theme.MOUSE_OVER : 0);
		}
	}

	private static class ButtonSpecial extends Button
	{
		private final SpecialGuideButton specialInfoButton;

		public ButtonSpecial(Panel panel, SpecialGuideButton b)
		{
			super(panel);
			setSize(12, 12);
			specialInfoButton = b;
			setTitle(specialInfoButton.title.getFormattedText());
		}

		@Override
		public void onClicked(MouseButton button)
		{
			if (handleClick(specialInfoButton.click))
			{
				GuiHelper.playClickSound();
			}
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			specialInfoButton.icon.draw(x + 2, y + 2, 8, 8);
		}
	}

	private Theme guideTheme;
	public final GuidePage page;
	public final ComponentPanel panelText;
	public final Panel panelTitle, panelSpecialButtons;
	public final PanelScrollBar scrollBarH, scrollBarV;

	public GuiGuide(GuidePage p)
	{
		page = p;
		guideTheme = new GuideGuiTheme(page);

		if (FTBLibConfig.debugging.print_more_info)
		{
			FTBGuides.LOGGER.info("Gui opened for page " + p.getPath());
		}

		setUnicode(FTBGuidesClientConfig.general.use_unicode_font);

		panelText = new ComponentPanel(this)
		{
			@Override
			public List<GuideComponent> getComponents()
			{
				return page.components;
			}

			@Override
			public void addWidgets()
			{
				setPosAndSize(2, 1 + panelSpecialButtons.height, getGui().width - 3 - SCROLLBAR_SIZE, getGui().height - 2 - panelSpecialButtons.height - SCROLLBAR_SIZE);
				maxWidth = width;
			}

			@Override
			public void alignWidgets()
			{
				scrollBarH.setMaxValue(totalWidth);
				scrollBarV.setMaxValue(totalHeight);
			}
		};

		panelTitle = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				List<ButtonSelectPage> list = new ArrayList<>(1);
				list.add(new ButtonSelectPage(this, page));
				addToList(page, list);
				Collections.reverse(list);
				addAll(list);
			}

			@Override
			public void alignWidgets()
			{
				setWidth(align(WidgetLayout.HORIZONTAL));
			}

			private void addToList(GuidePage page, List<ButtonSelectPage> list)
			{
				if (page.parent != null)
				{
					list.add(new ButtonSelectPage(this, null));
					list.add(new ButtonSelectPage(this, page.parent));
					addToList(page.parent, list);
				}
			}

			@Override
			public void drawBackground(Theme theme, int x, int y, int w, int h)
			{
			}
		};

		panelTitle.setPosAndSize(3, 2, 0, 8);
		panelTitle.setUnicode(FTBGuidesClientConfig.general.use_unicode_font);

		panelSpecialButtons = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				add(new ButtonSpecial(this, new SpecialGuideButton(new TextComponentTranslation("ftbguides_client.general.theme").appendText(": ").appendSibling(GuideTheme.get(FTBGuidesClientConfig.general.theme).title), GuiIcons.COLOR_RGB, "theme:/")));
				add(new ButtonSpecial(this, new SpecialGuideButton(new TextComponentTranslation("selectServer.refresh"), GuiIcons.REFRESH, "refresh:" + page.getPath())));

				JsonElement url = page.getProperty("browser_url");

				if (url.isJsonPrimitive() && !url.getAsString().isEmpty())
				{
					add(new ButtonSpecial(this, new SpecialGuideButton(new TextComponentTranslation("ftbguides.lang.open_in_browser"), GuiIcons.GLOBE, url.getAsString())));
				}

				add(new ButtonSpecial(this, new SpecialGuideButton(new TextComponentTranslation("gui.close"), GuiIcons.CLOSE, "close:/")));
			}

			@Override
			public void alignWidgets()
			{
				setWidth(align(WidgetLayout.HORIZONTAL));
			}

			@Override
			public void drawBackground(Theme theme, int x, int y, int w, int h)
			{
			}
		};

		panelSpecialButtons.setHeight(12);

		scrollBarH = new PanelScrollBar(this, PanelScrollBar.Plane.HORIZONTAL, panelText);
		scrollBarH.setCanAlwaysScrollPlane(false);
		scrollBarH.setScrollStep(10);

		scrollBarV = new PanelScrollBar(this, panelText);
		scrollBarV.setCanAlwaysScrollPlane(false);
		scrollBarV.setScrollStep(30);
	}

	@Override
	public void addWidgets()
	{
		add(scrollBarH);
		add(scrollBarV);
		add(panelText);
		add(panelTitle);
		add(panelSpecialButtons);
	}

	@Override
	public void alignWidgets()
	{
		panelSpecialButtons.alignWidgets();
		panelSpecialButtons.setPos(width - panelSpecialButtons.width, 0);
		scrollBarH.setPosAndSize(0, height - SCROLLBAR_SIZE, width - SCROLLBAR_SIZE, SCROLLBAR_SIZE);
		scrollBarV.setPosAndSize(width - SCROLLBAR_SIZE, panelSpecialButtons.height - 1, SCROLLBAR_SIZE, height - SCROLLBAR_SIZE);
		panelText.alignWidgets();
	}

	@Override
	public boolean onInit()
	{
		if (page == page.getRoot() && !FTBGuidesClientConfig.general.last_guide_version.equals(FTBGuidesConfig.general.modpack_guide_version) && page.getSubRaw("modpack_guide") != null)
		{
			FTBGuidesClientConfig.general.last_guide_version = FTBGuidesConfig.general.modpack_guide_version;
			FTBGuidesClientConfig.sync();
			FTBGuidesClient.openGuidesGui("/modpack_guide");
		}

		return setFullscreen();
	}

	@Override
	public void drawBackground(Theme theme, int x, int y, int w, int h)
	{
		GuiHelper.drawHollowRect(0, 0, w, h, page.lineColor, false);
		page.lineColor.draw(scrollBarV.getX(), scrollBarV.getY(), 1, scrollBarV.height);
		page.lineColor.draw(0, scrollBarH.getY(), scrollBarH.width, 1);
		page.lineColor.draw(0, panelSpecialButtons.height - 1, w, 1);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}

	@Override
	public Theme getTheme()
	{
		return guideTheme;
	}

	@Override
	public boolean drawDefaultBackground()
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

		return false;
	}

	@Override
	public boolean keyPressed(int key, char keyChar)
	{
		if (key == Keyboard.KEY_F5)
		{
			return handleClick("refresh:" + page.getPath());
		}
		else if (key == Keyboard.KEY_HOME)
		{
			FTBGuidesClient.openGuidesGui("/");
			return true;
		}
		else if (key == Keyboard.KEY_BACK)
		{
			if (page.parent != null)
			{
				FTBGuidesClient.openGuidesGui(page.parent.getPath());
			}

			return true;
		}
		else if (key == Keyboard.KEY_NEXT)
		{
			//TODO: Implement going forward
			return true;
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
			return keyPressed(Keyboard.KEY_BACK, '\0');
		}
		else if (button.id == 4)
		{
			return keyPressed(Keyboard.KEY_NEXT, '\0');
		}
		else
		{
			return super.mousePressed(button);
		}
	}

	@Override
	public boolean handleClick(String scheme, String path)
	{
		if (scheme.isEmpty() || scheme.equals("page"))
		{
			GuidePage p = page.getSubFromPath(path);

			if (p != null)
			{
				FTBGuidesClient.openGuidesGui(p.getPath());
				return true;
			}

			return false;
		}
		else if (scheme.equals("close"))
		{
			closeGui();
			return true;
		}
		else if (scheme.equals("refresh"))
		{
			if (path.equals("/") || page.textURI == null)
			{
				FTBGuidesClient.setShouldReload();
			}
			else
			{
				page.textLoadingState = GuidePage.STATE_NOT_LOADING;
				new ThreadLoadPage(page).start();
			}

			FTBGuidesClient.openGuidesGui(path);
			return true;
		}
		else if (scheme.equals("theme"))
		{
			FTBGuidesClientConfig.general.theme = GuideTheme.get(FTBGuidesClientConfig.general.theme).next.getName();
			FTBGuidesClientConfig.sync();
			page.getRoot().updateCachedProperties(true);
			refreshWidgets();
			return true;
		}

		return super.handleClick(scheme, path);
	}

	@Override
	@Nullable
	public GuiScreen getPrevScreen()
	{
		return null;
	}

	@Override
	public boolean onClosedByKey(int key)
	{
		return super.onClosedByKey(key) || FTBGuidesClient.KEY_GUIDE.isActiveAndMatches(key);
	}
}