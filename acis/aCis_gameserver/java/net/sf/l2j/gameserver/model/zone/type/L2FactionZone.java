package net.sf.l2j.gameserver.model.zone.type;
import net.sf.l2j.gameserver.custom.AutoBuffs;
import net.sf.l2j.gameserver.custom.FactionHelper;
import net.sf.l2j.gameserver.custom.FactionZoneManager;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;

public class L2FactionZone extends L2SpawnZone {


    public L2FactionZone(int id){
        super(id);
    }


    @Override
    protected void onEnter(Creature character) {
        if (character instanceof Player) {
            if(FactionZoneManager.getInstance().getCurrentFactionZoneId() != this.getId()) {
                Location loc = MapRegionTable.getTown(9).getSpawnLoc();
                character.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), 100);
                return;
            }

            if (!character.isInsideZone(ZoneId.FACTION))
                ((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);

            Player player = (Player) character;
            player.setInsideZone(ZoneId.FACTION, true);
            FactionHelper.getInstance().onZoneEntrance(player);
            player.sendMessage("There are: " + getCharactersInside().size() + " players in zone.");
            player.broadcastTitleInfo();
            player.broadcastCharInfo();
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (character instanceof Player) {
            if (!character.isInsideZone(ZoneId.FACTION))
                ((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);

            Player player = (Player) character;
            player.setInsideZone(ZoneId.FACTION,false);
            FactionHelper.getInstance().onZoneExit(player);
            player.broadcastTitleInfo();
            player.broadcastCharInfo();
        }
    }

    @Override
    public void onDieInside(Creature character) {

    }

    @Override
    public void onReviveInside(Creature character) {
        Player player = (Player)character;
        AutoBuffs.getInstance().BuffMe(player);
    }
}
