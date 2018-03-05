package com.feed_the_beast.ftbguides.cmd;

import com.feed_the_beast.ftblib.events.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBGuidesCommands
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerClientCommands(RegisterFTBClientCommandsEvent event)
	{
		event.add(new CmdOpenGuides());
	}
}