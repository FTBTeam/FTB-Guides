package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.client.FTBGuidesClientConfig;
import com.feed_the_beast.ftbguides.client.GuideTheme;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftbguides.gui.components.ImageGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.LineBreakGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.icon.URLImageIcon;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GuidePage extends FinalIDObject implements Comparable<GuidePage>
{
	public static final Icon DEFAULT_ICON = ItemIcon.getItemIcon(new ItemStack(Items.BOOK));
	public static final Collection<String> STANDARD_KEYS = new HashSet<>(Arrays.asList("id", "title", "icon", "icon_url", "buttons", "pages"));

	public static final int STATE_NOT_LOADING = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_LOADED = 2;

	public final GuidePage parent;
	public final List<GuideComponent> components = new ArrayList<>();
	public final List<GuidePage> pages = new ArrayList<>(0);
	public ITextComponent title;
	public Icon icon = DEFAULT_ICON;
	public final List<SpecialGuideButton> specialButtons = new ArrayList<>(0);
	public URI textURI = null;
	public int textLoadingState = STATE_NOT_LOADING;
	public final HashMap<String, JsonElement> properties = new HashMap<>();

	public GuideType type = GuideType.OTHER;
	public Icon background = Icon.EMPTY;
	public Color4I textColor = Icon.EMPTY;
	public Color4I textColorMouseOver = Icon.EMPTY;
	public Color4I lineColor = Icon.EMPTY;

	public GuidePage(String id, @Nullable GuidePage p)
	{
		super(id);
		parent = p;
	}

	public String getPath()
	{
		return parent == null ? "/" : (parent.getPath() + getName() + '/');
	}

	public GuidePage getRoot()
	{
		return parent == null ? this : parent.getRoot();
	}

	public ITextComponent getDisplayName()
	{
		return title == null ? new TextComponentString(getName()) : title;
	}

	public void println(GuideComponent component)
	{
		if (!components.isEmpty() && component.isInline())
		{
			components.add(LineBreakGuideComponent.INSTANCE);
		}

		components.add(component);
	}

	public void println(String text)
	{
		println(new TextGuideComponent(text));
	}

	public void println(Icon icon)
	{
		println(new ImageGuideComponent(icon));
	}

	public void println(@Nullable ITextComponent component)
	{
		if (component != null)
		{
			for (ITextComponent c : component)
			{
				TextGuideComponent t = new TextGuideComponent(c.getUnformattedComponentText());
				//FIXME: Formatting
				components.add(t);
			}
		}

		components.add(LineBreakGuideComponent.INSTANCE);
	}

	public GuidePage addSub(GuidePage c)
	{
		Objects.requireNonNull(c, "Page can't be null!");
		pages.remove(c);
		pages.add(c);
		return c;
	}

	@Nullable
	public GuidePage getSubRaw(String id)
	{
		for (GuidePage p : pages)
		{
			if (p.getName().equalsIgnoreCase(id))
			{
				return p;
			}
		}

		return null;
	}

	public GuidePage getSub(String id)
	{
		GuidePage p = getSubRaw(id);

		if (p == null)
		{
			p = addSub(new GuidePage(id, this));
		}

		return p;
	}

	@Nullable
	public GuidePage getSubFromPath(String path)
	{
		if (path.isEmpty())
		{
			return this;
		}

		int i = path.indexOf('/');

		if (i == -1)
		{
			return getSubRaw(path);
		}
		else if (i == 0)
		{
			return path.length() == 1 ? getRoot() : getRoot().getSubFromPath(path.substring(1));
		}
		else if (i == path.length() - 1)
		{
			return getSubFromPath(path.substring(0, path.length() - 1));
		}

		String s = path.substring(0, i);
		String path1 = path.substring(i + 1);

		if (s.equals("."))
		{
			return getSubFromPath(path1);
		}
		else if (s.equals(".."))
		{
			return (parent == null ? this : parent).getSubFromPath(path1);
		}

		GuidePage page1 = getSubRaw(s);
		return page1 == null ? null : page1.getSubFromPath(path1);
	}

	public void clear()
	{
		components.clear();
		pages.clear();
		properties.clear();
	}

	public void cleanup()
	{
		pages.forEach(GuidePage::cleanup);
		pages.removeIf(GuidePage::isEmpty);
	}

	public boolean isEmpty()
	{
		if (!pages.isEmpty())
		{
			return false;
		}

		for (GuideComponent component : components)
		{
			if (!component.isEmpty())
			{
				return false;
			}
		}

		return textURI == null;
	}

	public void sort(boolean tree)
	{
		pages.sort(null);

		if (tree)
		{
			for (GuidePage p : pages)
			{
				p.sort(true);
			}
		}
	}

	public void readProperties(JsonObject json)
	{
		properties.clear();

		for (Map.Entry<String, JsonElement> entry : json.entrySet())
		{
			if (!STANDARD_KEYS.contains(entry.getKey()))
			{
				properties.put(entry.getKey(), entry.getValue() == null ? JsonNull.INSTANCE : entry.getValue());
			}
		}
	}

	public final JsonElement getProperty(String key)
	{
		JsonElement json = properties.get(key);
		return json == null ? (parent == null ? JsonNull.INSTANCE : parent.getProperty(key)) : json;
	}

	public void updateCachedProperties(boolean tree)
	{
		GuideTheme theme = GuideTheme.get(FTBGuidesClientConfig.general.theme);
		JsonElement e = getProperty("background");
		background = JsonUtils.isNull(e) ? theme.background : Icon.getIcon(e);
		e = getProperty("text_color");
		textColor = JsonUtils.isNull(e) ? theme.text : Color4I.fromJson(getProperty("text_color"));
		e = getProperty("text_color_mouse_over");
		textColorMouseOver = JsonUtils.isNull(e) ? theme.textMouseOver : Color4I.fromJson(getProperty("text_color_mouse_over"));
		e = getProperty("line_color");
		lineColor = JsonUtils.isNull(e) ? theme.lines : Color4I.fromJson(getProperty("line_color"));

		if (tree)
		{
			for (GuidePage page : pages)
			{
				page.updateCachedProperties(true);
			}
		}
	}

	public Icon getIcon(String path)
	{
		if (path.indexOf(':') == -1 && textURI != null)
		{
			return new URLImageIcon(textURI.resolve("./" + path));
		}

		return Icon.getIcon(path);
	}

	@Override
	public int compareTo(GuidePage o)
	{
		int i = type.compareTo(o.type);
		return i == 0 ? getDisplayName().getUnformattedText().compareToIgnoreCase(o.getDisplayName().getUnformattedText()) : i;
	}
}