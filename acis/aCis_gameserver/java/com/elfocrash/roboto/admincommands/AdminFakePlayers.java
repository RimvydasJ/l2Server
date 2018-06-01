package com.elfocrash.roboto.admincommands;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.FakePlayerManager;
import com.elfocrash.roboto.FakePlayerTaskManager;
import com.elfocrash.roboto.ai.walker.CommonWalkerAi;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.List;

/**
 * @author Elfocrash
 *
 */
public class AdminFakePlayers implements IAdminCommandHandler
{
	private final String fakesFolder = "data/html/admin/fakeplayers/";
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_takecontrol",
		"admin_releasecontrol",
		"admin_fakes",
		"admin_spawnrandom",
		"admin_deletefake",
		"admin_spawnenchanter",
		"admin_spawnwalker",
			"admin_fakeinfo",
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showFakeDashboard(Player activeChar) {
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(fakesFolder + "index.htm");
		html.replace("%fakecount%", FakePlayerManager.INSTANCE.getFakePlayersCount());
		html.replace("%taskcount%", FakePlayerTaskManager.INSTANCE.getTaskCount());
		activeChar.sendPacket(html);
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_fakes"))
		{
			showFakeDashboard(activeChar);
		}
		
		if(command.startsWith("admin_deletefake")) {
			if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
				FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
				fakePlayer.despawnPlayer();
			}
			else{
				List<FakePlayer> allFakes = FakePlayerManager.INSTANCE.getFakePlayers();
				for(int i = 0; i <allFakes.size(); i++){
					allFakes.get(i).despawnPlayer();
				}
			}
			return true;
		}

		if (command.startsWith("admin_spawnrandom")) {
			String[] params = command.split(" ");

			//2 - clan name provided
			if(params.length == 2){
				//TODO Clan things
			}

			//3 - clan name && count
			else if(params.length == 3){
				Thread t1 = new Thread(() -> {
					System.out.println("Start spawning: " + params[2]+ " bots");
					for(int i = 0; i < Integer.parseInt(params[2]); i++){
						FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX()+ Rnd.get(-200,200),activeChar.getY()+Rnd.get(-200,200),activeChar.getZ());
						fakePlayer.assignDefaultAI();
					}
					System.out.println("Finish");
				});
				t1.start();
			}
			else{
				FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ());
					fakePlayer.assignDefaultAI();
			}
				return true;
			}

			if(command.startsWith("admin_spawnwalker")) {
				if(command.contains(" ")) {
					String locationName = command.split(" ")[1];
					FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ());
				switch(locationName) {
					case "giran":
						fakePlayer.setFakeAi(new CommonWalkerAi(fakePlayer));
						break;
				}
				return true;
			}
			return true;
		}
		if(command.startsWith("admin_fakeinfo")){
			if(activeChar.getTarget() instanceof  FakePlayer) {
				FakePlayer fake = (FakePlayer)activeChar.getTarget();
				FakePlayer target = (FakePlayer) activeChar.getTarget();
				activeChar.sendMessage(target.getFakeAi().getClass().getSimpleName());
				if (target.isInsideZone(ZoneId.TOWN))
					activeChar.sendMessage("Is in town");
			}
		}
//		if(command.startsWith("admin_spawnenchanter")) {
//			FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ());
//			fakePlayer.setFakeAi(new EnchanterAI(fakePlayer));
//			return true;
//		}
		/*if (command.startsWith("admin_takecontrol"))
		{
			if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
				FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
				fakePlayer.setUnderControl(true);
				activeChar.setPlayerUnderControl(fakePlayer);
				activeChar.sendMessage("You are now controlling: " + fakePlayer.getName());
				return true;
			}
			
			activeChar.sendMessage("You can only take control of a Fake Player");
			return true;
		}
		if (command.startsWith("admin_releasecontrol"))
		{
			if(activeChar.isControllingFakePlayer()) {
				FakePlayer fakePlayer = activeChar.getPlayerUnderControl();
				activeChar.sendMessage("You are no longer controlling: " + fakePlayer.getName());
				fakePlayer.setUnderControl(false);
				activeChar.setPlayerUnderControl(null);
				return true;
			}
			
			activeChar.sendMessage("You are not controlling a Fake Player");
			return true;
		}*/
		return true;
	}
}