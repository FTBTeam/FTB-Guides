package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftbguides.gui.Guides;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuidesFinals.MOD_ID, value = Side.CLIENT)
public class FTBUClientEventHandler
{
	/*
	@SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBGuidesClient.KEY_GUIDE.isPressed())
		{
			Guides.openGui();
		}
	}
}