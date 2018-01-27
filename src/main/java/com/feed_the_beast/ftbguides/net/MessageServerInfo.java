package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.ServerInfoPage;
import com.feed_the_beast.ftbguides.events.ServerInfoEvent;
import com.feed_the_beast.ftbguides.gui.Guides;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageServerInfo extends MessageToClient<MessageServerInfo>
{
	public static class CommandInfo
	{
		private static final DataOut.Serializer<CommandInfo> SERIALIZER = (data, object) -> object.writeData(data);
		private static final DataIn.Deserializer<CommandInfo> DESERIALIZER = CommandInfo::new;

		public final String name;
		public final Collection<ITextComponent> info;
		public final Collection<CommandInfo> subcommands;

		private CommandInfo(DataIn data)
		{
			name = data.readString();
			info = data.readCollection(DataIn.TEXT_COMPONENT);
			subcommands = data.readCollection(null, DESERIALIZER);
		}

		public CommandInfo(String n, Collection<ITextComponent> t, Collection<CommandInfo> c)
		{
			name = n;
			info = t;
			subcommands = c;
		}

		public void writeData(DataOut data)
		{
			data.writeString(name);
			data.writeCollection(info, DataOut.TEXT_COMPONENT);
			data.writeCollection(subcommands, SERIALIZER);
		}
	}

	public JsonArray mainPage;
	public CommandInfo commands;
	public JsonElement serverGuide;

	public MessageServerInfo()
	{
	}

	public MessageServerInfo(EntityPlayerMP player)
	{
		long now = player.getEntityWorld().getTotalWorldTime();
		List<ITextComponent> list = ServerInfoPage.getMainPage(player, now);
		new ServerInfoEvent(player, now, list::add).post();

		mainPage = new JsonArray();

		for (ITextComponent component : list)
		{
			mainPage.add(JsonUtils.serializeTextComponent(component));
		}

		commands = ServerInfoPage.getCommands(player);
		serverGuide = ServerInfoPage.getServerGuide();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBGuidesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeJson(mainPage);
		data.write(CommandInfo.SERIALIZER, commands);
		data.writeJson(serverGuide);
	}

	@Override
	public void readData(DataIn data)
	{
		mainPage = data.readJson().getAsJsonArray();
		commands = data.read(CommandInfo.DESERIALIZER);
		serverGuide = data.readJson();
	}

	@Override
	public void onMessage(MessageServerInfo m, EntityPlayer player)
	{
		Guides.readServerInfoPage(m);
	}
}