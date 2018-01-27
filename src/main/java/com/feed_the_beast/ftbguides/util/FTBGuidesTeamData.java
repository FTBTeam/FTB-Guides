package com.feed_the_beast.ftbguides.util;

import com.feed_the_beast.ftbguides.handlers.FTBLibIntegration;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FTBGuidesTeamData implements INBTSerializable<NBTTagCompound>
{
	public static FTBGuidesTeamData get(ForgeTeam team)
	{
		return team.getData().get(FTBLibIntegration.DATA_ID);
	}

	public final ForgeTeam team;
	public final List<ResourceLocation> completedQuests = new ArrayList<>();
	public final List<ItemStack> rewards = new ArrayList<>();

	public FTBGuidesTeamData(ForgeTeam t)
	{
		team = t;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
	}

	/*
	public void addConfig(ForgeTeamConfigEvent event)
	{
		String group = FTBGuidesFinals.MOD_ID;
		event.getConfig().setGroupName(group, new TextComponentString(FTBGuidesFinals.MOD_NAME));
		event.getConfig().add(group, "explosions", explosions);
		event.getConfig().add(group, "blocks_edit", editBlocks);
		event.getConfig().add(group, "blocks_interact", interactWithBlocks);
		event.getConfig().add(group, "attack_entities", attackEntities);
	}
	*/
}