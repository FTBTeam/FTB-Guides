package com.feed_the_beast.ftbguides.integration;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.events.ClientGuideEvent;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.GuideTitlePage;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.ImageGuideComponent;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.TinkerMaterials;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID, value = Side.CLIENT)
public class TinkersConstructIntegration
{
	@SubscribeEvent
	public static void onGuideEvent(ClientGuideEvent event)
	{
		if (Loader.isModLoaded(OtherMods.TINKERS_CONSTRUCT))
		{
			onGuideEvent0(event);
		}
	}

	private static void onGuideEvent0(ClientGuideEvent event)
	{
		GuideTitlePage page = event.getModPage(OtherMods.TINKERS_CONSTRUCT);
		page.icon = ItemIcon.getItemIcon(OtherMods.TINKERS_CONSTRUCT + ":toolforge");
		page.println(HRGuideComponent.INSTANCE);
		int i;

		GuidePage pageIntro = loadPage("intro", page);

		if (pageIntro != null)
		{
			pageIntro.title = new TextComponentString("Introduction");
			pageIntro.icon = ItemIcon.getItemIcon(OtherMods.TINKERS_CONSTRUCT + ":tooltables");
			page.addSub(pageIntro);
		}

		GuidePage toolMaterials = page.getSub("materials");
		toolMaterials.title = new TextComponentString("Materials");
		toolMaterials.icon = ItemIcon.getItemIcon(Items.IRON_PICKAXE);

		ImmutableList mats = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.manyullyn);

		for (Material material : TinkerRegistry.getAllMaterials())
		{
			if (material.isHidden() || !material.hasItems())
			{
				continue;
			}

			GuidePage page1 = toolMaterials.getSub(material.getIdentifier());
			page1.icon = ItemIcon.getItemIcon(material.getRepresentativeItem());
			page1.title = new TextComponentString(material.getLocalizedName());

			for (IMaterialStats stats : material.getAllStats())
			{
				ITextComponent component = new TextComponentString(stats.getLocalizedName());
				component.getStyle().setUnderlined(true);
				page1.println(component);

				//List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());
				//allTraits.addAll(traits);

				i = 0;

				for (IToolPart part : TinkerRegistry.getToolParts())
				{
					if (part.hasUseForStat(stats.getIdentifier()))
					{
						page1.components.add(new ImageGuideComponent(ItemIcon.getItemIcon(part.getItemstackWithMaterial(material))));
						i++;

						if (i % 8 == 0)
						{
							page1.println("");
						}
					}
				}

				for (i = 0; i < stats.getLocalizedInfo().size(); i++)
				{
					ITextComponent component1 = new TextComponentString(transformString(stats.getLocalizedInfo().get(i)));
					component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(transformString(stats.getLocalizedDesc().get(i)))));
					page1.println(component1);
				}

				page1.println("");
			}
		}

		GuidePage modifiers = page.getSub("modifiers");
		modifiers.title = new TextComponentString("Modifiers");
		modifiers.icon = ItemIcon.getItemIcon(Items.REDSTONE);

		for (IModifier modifier : TinkerRegistry.getAllModifiers())
		{
			if (modifier.isHidden() || !modifier.hasItemsToApplyWith())
			{
				continue;
			}

			try
			{
				JsonElement json = DataReader.get(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(OtherMods.TINKERS_CONSTRUCT, "book/en_US/modifiers/" + modifier.getIdentifier() + ".json"))).json();

				if (json.isJsonObject())
				{
					JsonObject o = json.getAsJsonObject();
					GuidePage page1 = modifiers.getSub(modifier.getIdentifier());
					page1.title = new TextComponentString(modifier.getLocalizedName());
					page1.println(transformString(modifier.getLocalizedDesc()));

					if (o.has("text"))
					{
						page1.println("");
						for (JsonElement e : o.get("text").getAsJsonArray())
						{
							page1.println(JsonUtils.deserializeTextComponent(e));
						}
					}

					if (o.has("effects"))
					{
						page1.println("");
						page1.println("Effects:");
						for (JsonElement e : o.get("effects").getAsJsonArray())
						{
							page1.println(JsonUtils.deserializeTextComponent(e));
						}
					}

					if (o.has("demoTool"))
					{
						i = 0;

						for (JsonElement e : o.get("demoTool").getAsJsonArray())
						{
							Item item = Item.getByNameOrId(e.getAsString());

							if (item instanceof ToolCore)
							{
								page1.components.add(new ImageGuideComponent(ItemIcon.getItemIcon(((ToolCore) item).buildItemForRendering(mats.subList(0, ((ToolCore) item).getRequiredComponents().size())))));
								i++;

								if (i % 8 == 0)
								{
									page1.println("");
								}
							}
						}
					}
				}
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					ex.printStackTrace();
				}
			}
		}

		modifiers.sort(false);

		GuidePage pageSmeltry = loadPage("smeltery", page);

		if (pageSmeltry != null)
		{
			pageSmeltry.title = new TextComponentString("Smeltry");
			pageSmeltry.icon = ItemIcon.getItemIcon(OtherMods.TINKERS_CONSTRUCT + ":toolstation");
			page.addSub(pageSmeltry);
		}

        /*
		GuidePage searedFurnace = pagePage.getSub("seared_furnace");
        searedFurnace.println("Seared Furnace");

        GuidePage tinkerTank = pagePage.getSub("tinker_tank");
        tinkerTank.println("Tinker Tank");
        */
	}

	private static String transformString(String s)
	{
		return s.replace("\\n", "\n").trim();
	}

	@Nullable
	private static GuidePage loadPage(String id, GuidePage p) //FIXME
	{
		/*
		try
		{
			JsonElement json = JsonUtils.fromJson(ClientUtils.MC.getResourceManager().getResource(new ResourceLocation(OtherMods.TINKERS_CONSTRUCT, "book/en_US/sections/" + id + ".json")));

			if (json.isJsonArray())
			{
				GuidePage page = new GuidePage(id, p);

				for (JsonElement e : json.getAsJsonArray())
				{
				}

				return page;
			}
		}
		catch (Exception ex)
		{
		}
		*/

		return null;
	}
}