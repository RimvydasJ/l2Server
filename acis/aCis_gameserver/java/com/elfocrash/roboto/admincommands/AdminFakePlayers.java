package com.elfocrash.roboto.admincommands;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.FakePlayerManager;
import com.elfocrash.roboto.FakePlayerTaskManager;
import com.elfocrash.roboto.ai.EnchanterAI;
import com.elfocrash.roboto.ai.LvlUpAI;
import com.elfocrash.roboto.ai.walker.setCommonWalkerAI;

import com.elfocrash.roboto.task.AITaskRunner;
import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Elfocrash
 *
 */
public class AdminFakePlayers implements IAdminCommandHandler
{
	private final String fakesFolder = "data/html/admin/fakeplayers/";
	private static final String[] ADMIN_COMMANDS =
	{
			"admin_getallcommands",
			"admin_takecontrol",
			"admin_releasecontrol",
			"admin_fakes",
			"admin_spawnrandom",
			"admin_deletefake",
			"admin_spawnenchanter",
			"admin_spawnwalker",
			"admin_spawnparty",
			"admin_fakeinfo",
			"admin_spawnlow",
			"admin_deletefakes"

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
		if(command.equals("admin_getallcommands")){
			for(String adminCommand: ADMIN_COMMANDS){
				String[] secondCommandPart = adminCommand.split("_");
				if(secondCommandPart.length == 2 ) {
					activeChar.sendMessage("//"+secondCommandPart[1]);
				}
			}
		}

		if (command.startsWith("admin_fakes"))
		{
			showFakeDashboard(activeChar);
		}
		
		if(command.equals("admin_deletefake")) {
			if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
				FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
				fakePlayer.despawnPlayer();
			}
			else{
				List<FakePlayer> all = FakePlayerManager.INSTANCE.getFakePlayers();
				List<L2Clan> allClans = FakePlayerManager.INSTANCE.getFakeClans();

				for(int i = 0; i < allClans.size(); i++){
					ClanTable.getInstance().destroyClan(allClans.get(i));
				}

				for(int i = 0; i < all.size(); i++){
					all.get(i).despawnPlayer();
				}
			}
			return true;
		}

		if(command.equals("admin_deletefakes")) {
			if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
				FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();


				List<FakePlayer> all = FakePlayerManager.INSTANCE.getFakePlayers();
				List<L2Clan> allClans = FakePlayerManager.INSTANCE.getFakeClans();

				for (int i = 0; i < allClans.size(); i++) {
					ClanTable.getInstance().destroyClan(allClans.get(i));
				}

				for (int i = 0; i < all.size(); i++) {
					if (all.get(i).getObjectId() != fakePlayer.getObjectId())
						all.get(i).despawnPlayer();
				}
			}
			return true;
		}
		
		if(command.startsWith("admin_spawnwalker")) {
			if(command.contains(" ")) {
				String locationName = command.split(" ")[1];
				FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(),null);
				switch(locationName) {
					case "giran":
						fakePlayer.setFakeAi(new setCommonWalkerAI(fakePlayer));
					break;
				}
				return true;
			}
			
			return true;
		}
		
		if(command.startsWith("admin_spawnenchanter")) {
			FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(),null);
			fakePlayer.setFakeAi(new EnchanterAI(fakePlayer));
			return true;
		}

		if(command.startsWith("admin_fakeinfo")) {
			FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
			System.out.println("Before " + fakePlayer.getFakeAi().getClass().getName());
			activeChar.sendMessage("Before " + fakePlayer.getFakeAi().getClass().getName());
			activeChar.sendMessage("Before " + fakePlayer.getFakeAi().getClass().getName());
			//fakePlayer.setFakeAi(new setCommonWalkerAI(fakePlayer));
			//System.out.println("After " +fakePlayer.getFakeAi().getClass().getName());
			return true;
		}

		if(command.startsWith("admin_spawnparty")) {

			FakePlayer fakePlayer1 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.SAGGITARIUS);
			FakePlayer fakePlayer2 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.SAGGITARIUS);
			FakePlayer fakePlayer3 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.CARDINAL);
			FakePlayer fakePlayer4 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.SAGGITARIUS);
			FakePlayer fakePlayer5 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.ARCHMAGE);
			FakePlayer fakePlayer6 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.MYSTIC_MUSE);
			FakePlayer fakePlayer7 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.MYSTIC_MUSE);
			FakePlayer fakePlayer8 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.ARCHMAGE);
			FakePlayer fakePlayer9 = FakePlayerManager.INSTANCE.spawnPartyPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ(), ClassId.SAGGITARIUS);
			fakePlayer1.setParty(new Party(fakePlayer1,fakePlayer2, Party.LootRule.ITEM_RANDOM));
			fakePlayer1.getParty().addPartyMember(fakePlayer5);
			fakePlayer1.getParty().addPartyMember(fakePlayer6);
			fakePlayer1.getParty().addPartyMember(fakePlayer7);
			fakePlayer1.getParty().addPartyMember(fakePlayer8);
			fakePlayer1.getParty().addPartyMember(fakePlayer9);
			fakePlayer1.getParty().addPartyMember(fakePlayer4);
			fakePlayer1.getParty().addPartyMember(fakePlayer3);
			fakePlayer1.assignDefaultAI();
			fakePlayer2.assignDefaultAI();
			fakePlayer4.assignDefaultAI();
			fakePlayer5.assignDefaultAI();
			fakePlayer6.assignDefaultAI();
			fakePlayer7.assignDefaultAI();
			fakePlayer8.assignDefaultAI();
			fakePlayer9.assignDefaultAI();
			fakePlayer3.assignDefaultAI();


		}
		
		if (command.startsWith("admin_spawnrandom")) {
			String clanName = command.split(" ")[1];

			if(command.split(" ").length == 2) {
				FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ(), clanName);
				fakePlayer.assignDefaultAI();
			}
			else if(command.split(" ").length == 3){
				int count = Integer.parseInt(command.split(" ")[2]);

				int x = activeChar.getX();
				int y = activeChar.getY();
				int z = activeChar.getZ();

				for (int i = 0; i < count; i++){
					FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(x+Rnd.get(-100,100), y+Rnd.get(-100,100), z+100, clanName);
					fakePlayer.assignDefaultAI();
				}
			}

/*			if(command.contains(" ")) {
				String arg = command.split(" ")[1];
				if(arg.equalsIgnoreCase("htm")) {
					showFakeDashboard(activeChar);
				}
			}*/
			return true;
		}

		if (command.startsWith("admin_spawnlow")) {
			List<LowSpawnLoc> locs = new ArrayList(){};

			locs.add(new LowSpawnLoc(140990,-123861,-1908));
			locs.add(new LowSpawnLoc(141573,-123996,-1908));
			locs.add(new LowSpawnLoc(141736,-124326,-1908));

			for(int i = 0; i < 100; i++) {
				int id = Rnd.get(0, locs.size() - 1);
				LowSpawnLoc lowSpawnLoc = locs.get(id);

				FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnLowPlayer(lowSpawnLoc.x + Rnd.get(-100, 100), lowSpawnLoc.y + Rnd.get(-100, 100), lowSpawnLoc.z);

				fakePlayer.setFakeAi(new setCommonWalkerAI(fakePlayer));
			}
			return true;
		}

		if (command.startsWith("admin_takecontrol"))
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
		}
		return true;
	}

	public class LowSpawnLoc {
		public int x;
		public int y;
		public int z;

		public LowSpawnLoc(int x, int y, int z){
			this.x =x;
			this.y = y;
			this.z = z;
		}
	}
}