package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.FTBGuidesConfig;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftbguides.client.FTBGuidesClientConfig;
import com.feed_the_beast.ftblib.events.CustomSidebarButtonTextEvent;
import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.ftblib.events.client.GuideEvent;
import net.minecraft.init.Items;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID, value = Side.CLIENT)
public class FTBGuidesClientEventHandler
{
	private static final ResourceLocation GUIDES_BUTTON = new ResourceLocation(FTBGuides.MOD_ID, "guides");

	@SubscribeEvent
	public static void onCustomSidebarButtonText(CustomSidebarButtonTextEvent event)
	{
		if (FTBGuidesConfig.general.flash_guides && !FTBGuidesConfig.general.modpack_guide_version.isEmpty() && event.getButton().id.equals(GUIDES_BUTTON))
		{
			if (System.currentTimeMillis() % 1000L >= 500L && !FTBGuidesClientConfig.general.last_guide_version.equals(FTBGuidesConfig.general.modpack_guide_version))
			{
				event.setText(" ! ");
			}
		}
	}

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBGuidesClient.KEY_GUIDE.isPressed())
		{
			FTBGuidesClient.openGuidesGui("");
		}
	}

	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent.RightClickItem event)
	{
		if (event.getWorld().isRemote && event.getItemStack().getItem() == Items.BOOK && event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("Guide"))
		{
			FTBGuidesClient.openGuidesGui(event.getItemStack().getTagCompound().getString("Guide"));
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void checkGuide(GuideEvent.Check event)
	{
		event.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void openGuide(GuideEvent.Open event)
	{
		FTBGuidesClient.openGuidesGui(event.getPath());
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getResourceDomain().equals(FTBGuides.MOD_ID))
		{
			switch (event.getID().getResourcePath())
			{
				case "reload":
					FTBGuidesClient.setShouldReload();
					break;
				case "open_gui":
					FTBGuidesClient.openGuidesGui("");
					break;
			}

			event.setCanceled(true);
		}
	}
}