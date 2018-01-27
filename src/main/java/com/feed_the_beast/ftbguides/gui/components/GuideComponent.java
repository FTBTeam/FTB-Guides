package com.feed_the_beast.ftbguides.gui.components;

import com.feed_the_beast.ftbguides.events.CreateGuideComponentEvent;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class GuideComponent
{
	public GuideComponent parent = null;
	public Map<String, String> properties = null;

	public boolean isEmpty()
	{
		return false;
	}

	public abstract GuideComponent copy();

	public abstract IGuideComponentWidget createWidget(Panel parent);

	public JsonElement toJson()
	{
		return new JsonPrimitive(toString());
	}

	public boolean hasProperties()
	{
		return properties != null && !properties.isEmpty();
	}

	public final String getProperty(String key)
	{
		String p = hasProperties() ? properties.get(key) : null;
		return p == null ? (parent == null ? "" : parent.getProperty(key)) : p;
	}

	public final void setProperty(String key, String value)
	{
		if (!value.isEmpty())
		{
			if (properties == null)
			{
				properties = new HashMap<>();
			}

			properties.put(key, value);
		}
		else if (properties != null)
		{
			properties.remove(key);

			if (properties.isEmpty())
			{
				properties = null;
			}
		}
	}

	public final void setProperty(String key, @Nullable JsonElement json)
	{
		setProperty(key, json == null || json.isJsonNull() ? "" : json.getAsString());
	}

	public final GuideComponent copyProperties(GuideComponent component)
	{
		properties = component.hasProperties() ? new HashMap<>(component.properties) : null;
		return this;
	}

	public void loadProperties(JsonObject json)
	{
	}

	public static GuideComponent create(@Nullable JsonElement json)
	{
		if (json == null || json.isJsonNull())
		{
			return EmptyGuideComponent.INSTANCE;
		}

		if (json.isJsonPrimitive())
		{
			String s = json.getAsString().replace("\t", "  ").replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&");
			return s.isEmpty() ? EmptyGuideComponent.INSTANCE : new TextGuideComponent(s);
		}
		else if (json.isJsonArray())
		{
			JsonArray array = json.getAsJsonArray();

			if (array.size() == 0)
			{
				return EmptyGuideComponent.INSTANCE;
			}
			else if (array.size() == 1)
			{
				return create(array.get(0));
			}

			CombinedGuideComponent component = new CombinedGuideComponent();

			for (JsonElement e : array)
			{
				GuideComponent c1 = create(e);

				if (!c1.isEmpty())
				{
					c1.parent = component;
					component.add(c1);
				}
			}

			return component.isEmpty() ? EmptyGuideComponent.INSTANCE : component;
		}

		JsonObject object = json.getAsJsonObject();

		if (object.size() == 0)
		{
			return EmptyGuideComponent.INSTANCE;
		}
		if (object.has("br"))
		{
			return LineBreakGuideComponent.INSTANCE;
		}
		else if (object.has("hr"))
		{
			return HRGuideComponent.INSTANCE;
		}

		GuideComponent component = EmptyGuideComponent.INSTANCE;

		if (object.has("h1"))
		{
			component = create(object.get("h1"));
			component.setProperty("bold", "true");
			component.setProperty("underlined", "true");
		}
		else if (object.has("h2"))
		{
			component = create(object.get("h2"));
			component.setProperty("bold", "true");
		}
		else if (object.has("h3"))
		{
			component = create(object.get("h3"));
		}
		else if (object.has("img"))
		{
			component = new ImgGuideComponent(Icon.getIcon(object.get("img_url")));
		}
		else if (object.has("list"))
		{
			List<GuideComponent> list = new ArrayList<>();
			component = new GuideListComponent(list);

			for (JsonElement e : object.get("list").getAsJsonArray())
			{
				GuideComponent component1 = create(e);
				component1.parent = component;
				list.add(component1);
			}
		}
		else if (object.has("table"))
		{
		}
		else if (object.has("text") || object.has("code"))
		{
			component = new TextGuideComponent((object.has("text") ? object.get("text") : object.get("code")).getAsString());
			component.setProperty("bold", object.get("bold"));
			component.setProperty("italic", object.get("italic"));
			component.setProperty("underlined", object.get("underlined"));
			component.setProperty("bold", object.get("bold"));

			if (object.has("text"))
			{
				component.setProperty("code", object.get("code"));
			}
			else
			{
				component.setProperty("code", "true");
			}
		}

		if (component.isEmpty())
		{
			CreateGuideComponentEvent event = new CreateGuideComponentEvent(object);
			event.post();
			component = event.getComponent();
		}

		if (component.isEmpty())
		{
			component = new TextGuideComponent(TextFormatting.RED + object.toString());
		}
		else
		{
			component.loadProperties(object);
		}

		return component;
	}
}