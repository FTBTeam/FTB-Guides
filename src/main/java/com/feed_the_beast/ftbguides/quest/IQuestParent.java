package com.feed_the_beast.ftbguides.quest;

import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public interface IQuestParent extends IStringSerializable
{
	boolean isPage();

	QuestPage getPage();
}