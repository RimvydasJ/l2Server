package com.elfocrash.roboto;

import com.elfocrash.roboto.admincommands.AdminFakePlayers;
import com.elfocrash.roboto.helpers.FakeHelpers;

import java.util.List;
import java.util.stream.Collectors;

import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.custom.Faction;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportType;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.entity.Siege.SiegeSide;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Elfocrash
 *
 */
public enum FakePlayerManager {
	INSTANCE;

	private FakePlayerManager() {

	}

	public void initialise() {
		FakePlayerNameManager.INSTANCE.initialise();
		FakePlayerTaskManager.INSTANCE.initialise();
	}

	public FakePlayer spawnPlayer(int x, int y, int z){
		FakePlayer fakePlayer = spawnPlayer(x,y,z,80,true);
		return fakePlayer;
	}

	public FakePlayer spawnPlayer(int x, int y, int z, int level, boolean sGradeBot) {
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer(level,sGradeBot);
		World.getInstance().addPlayer(activeChar);
		handlePlayerClanOnSpawn(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		activeChar.setClan(null);
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2)
				&& activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportType.TOWN);

		activeChar.heal();

		insertFakePlayersToDb(activeChar);

		return activeChar;
	}

	public void despawnFakePlayer(int objectId) {
		Player player = World.getInstance().getPlayer(objectId);
		if (player instanceof FakePlayer) {
			FakePlayer fakePlayer = (FakePlayer) player;
			fakePlayer.despawnPlayer();
		}
	}

	private static void handlePlayerClanOnSpawn(FakePlayer activeChar) {
		final L2Clan clan = activeChar.getClan();
		if (clan != null) {
			clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);

			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN)
					.addCharName(activeChar);
			final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(activeChar);

			// Send packets to others members.
			for (Player member : clan.getOnlineMembers()) {
				if (member == activeChar)
					continue;

				member.sendPacket(msg);
				member.sendPacket(update);
			}

			for (Castle castle : CastleManager.getInstance().getCastles()) {
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;

				final SiegeSide type = siege.getSide(clan);
				if (type == SiegeSide.ATTACKER)
					activeChar.setSiegeState((byte) 1);
				else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
					activeChar.setSiegeState((byte) 2);
			}
		}
	}

	public int getFakePlayersCount() {
		return getFakePlayers().size();
	}

	public List<FakePlayer> getFakePlayers() {
		return World.getInstance().getPlayers().stream().filter(x -> x instanceof FakePlayer).map(x -> (FakePlayer) x)
				.collect(Collectors.toList());
	}

	private void insertFakePlayersToDb(FakePlayer activeChar){
		int factionId = Rnd.get(1,2);
		String factionName = activeChar.selectDefaultFactionName(factionId);
		if(factionName == null){
			if(factionId == 1){
				factionName = "RED";
			}
			else {
				factionName = "BLUE";
			}
		}
		activeChar.setFaction((Faction.values()[factionId]));
		activeChar.setFactionName(factionName);
		activeChar.store();
	}

	public void deleteFakes(){

	}
}
