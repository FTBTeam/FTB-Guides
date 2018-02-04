package com.feed_the_beast.ftbguides.cmd;

import com.feed_the_beast.ftbguides.gui.Guides;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdOpenGuide extends CmdBase
{
	public CmdOpenGuide()
	{
		super("open_guides", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		Guides.openGui();
	}
}