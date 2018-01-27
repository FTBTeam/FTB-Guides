package com.feed_the_beast.ftbguides.quest;

import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.google.gson.JsonObject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class Quest extends FinalIDObject implements IQuestParent
{
	public final IQuestParent parent;
	public final Set<Quest> dependencies;

	public Quest(QuestList questList, IQuestParent p, String id, JsonObject json)
	{
		super(id);
		parent = p;
		dependencies = new LinkedHashSet<>();
	}

	public JsonObject toJson()
	{
		JsonObject json = new JsonObject();

		if (!parent.isPage())
		{
			QuestPage parentPage = parent.getPage();
			json.addProperty("parent", parentPage.equals(getPage()) ? parent.getName() : (parentPage.getName() + ":" + parent.getName()));
		}

		return json;
	}

	@Override
	public boolean isPage()
	{
		return false;
	}

	@Override
	public QuestPage getPage()
	{
		return parent.getPage();
	}
}