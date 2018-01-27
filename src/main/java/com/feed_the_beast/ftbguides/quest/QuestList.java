package com.feed_the_beast.ftbguides.quest;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class QuestList
{
	public final Map<ResourceLocation, Quest> questMap;
	public final Map<ResourceLocation, QuestPage> pageMap;

	public QuestList()
	{
		questMap = new HashMap<>();
		pageMap = new HashMap<>();
	}
}