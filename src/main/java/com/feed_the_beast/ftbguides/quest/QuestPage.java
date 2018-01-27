package com.feed_the_beast.ftbguides.quest;

import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class QuestPage extends FinalIDObject implements IQuestParent
{
	public final List<Quest> quests;

	public QuestPage(QuestList questList, String id, JsonObject json)
	{
		super(id);
		quests = new ArrayList<>();

		if (json.has("quests"))
		{
			for (Map.Entry<String, JsonElement> entry : json.get("quests").getAsJsonObject().entrySet())
			{
				quests.add(new Quest(questList, this, entry.getKey(), entry.getValue().getAsJsonObject()));
			}
		}
	}

	@Override
	public boolean isPage()
	{
		return true;
	}

	@Override
	public QuestPage getPage()
	{
		return this;
	}
}