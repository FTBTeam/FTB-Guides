package com.feed_the_beast.mods.ftbguides;

import com.feed_the_beast.ftblib.events.SidebarButtonCreatedEvent;
import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID, value = Side.CLIENT)
public class FTBGuidesEventHandler
{
	private static final ResourceLocation GUIDES_BUTTON = new ResourceLocation(FTBGuides.MOD_ID, "guides");

	@SubscribeEvent
	public static void onSidebarButtonCreated(SidebarButtonCreatedEvent event)
	{
		if (event.getButton().id.equals(GUIDES_BUTTON))
		{
			event.getButton().setCustomTextHandler(() -> {
				if (FTBGuidesConfig.general.flash_guides && !FTBGuidesConfig.general.modpack_guide_version.isEmpty() && System.currentTimeMillis() % 1000L >= 500L && !FTBGuidesLocalConfig.general.last_guide_version.equals(FTBGuidesConfig.general.modpack_guide_version))
				{
					return "[!]";
				}

				return "";
			});

			event.getButton().setTooltipHandler(list -> {
				if (FTBGuidesConfig.general.flash_guides && !FTBGuidesConfig.general.modpack_guide_version.isEmpty() && !FTBGuidesLocalConfig.general.last_guide_version.equals(FTBGuidesConfig.general.modpack_guide_version))
				{
					list.add(TextFormatting.GRAY + I18n.format("sidebar_button.ftbguides.guides.new_version"));
				}
			});
		}
	}

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBGuides.KEY_GUIDE.isPressed())
		{
			FTBGuides.openGuidesGui("");
		}
	}

	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent.RightClickItem event)
	{
		if (event.getWorld().isRemote && event.getItemStack().getItem() == Items.BOOK && event.getItemStack().hasTagCompound() && (event.getItemStack().getTagCompound().hasKey("Guide") || event.getItemStack().getTagCompound().hasKey("guide")))
		{
			FTBGuides.openGuidesGui(event.getItemStack().getTagCompound().hasKey("Guide") ? event.getItemStack().getTagCompound().getString("Guide") : event.getItemStack().getTagCompound().getString("guide"));
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getNamespace().equals(FTBGuides.MOD_ID))
		{
			switch (event.getID().getPath())
			{
				case "reload":
					FTBGuides.setShouldReload();
					break;
				case "open_gui":
					FTBGuides.openGuidesGui("");
					break;
			}

			event.setCanceled(true);
		}
		else if (event.getID().getNamespace().equals("guide"))
		{
			FTBGuides.openGuidesGui(event.getID().getPath());
			event.setCanceled(true);
		}
	}
}