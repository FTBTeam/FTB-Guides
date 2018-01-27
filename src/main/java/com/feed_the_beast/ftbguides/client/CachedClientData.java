package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.quest.QuestPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CachedClientData
{
	public static final List<QuestPage> QUEST_PAGES = new ArrayList<>();

	public static void clear()
	{
		QUEST_PAGES.clear();
	}
}