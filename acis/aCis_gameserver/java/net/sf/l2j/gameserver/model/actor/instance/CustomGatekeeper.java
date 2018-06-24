package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.custom.FactionZoneManager;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2TeleportLocation;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.type.L2FactionZone;
import net.sf.l2j.gameserver.model.zone.type.L2FarmZone;
import net.sf.l2j.gameserver.model.zone.type.L2LevelZone;
import net.sf.l2j.gameserver.model.zone.type.L2TownZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

public class CustomGatekeeper extends Folk {
    public CustomGatekeeper(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val)
    {
        String filename = "";
        if (val == 0)
            filename = "" + npcId;
        else
            filename = npcId + "-" + val;

        return "data/html/customTeleporter/" + filename + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command)
    {
        player.sendPacket(ActionFailed.STATIC_PACKET);
        if (command.startsWith("goto")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();

            if (st.countTokens() <= 0)
                return;
            L2TeleportLocation loc = TeleportLocationTable.getInstance().getTemplate(Integer.parseInt(st.nextToken()));
            if (loc == null)
                return;
            player.teleToLocation(loc.getLocX(), loc.getLocY(), loc.getLocZ(), 20);
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
        if(command.startsWith("faction")){
            Location loc = ZoneManager.getInstance().getZoneById(FactionZoneManager.getInstance().getCurrentFactionZoneId(),L2FactionZone.class).getSpawnLoc();
            player.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), 20);
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
        if(command.startsWith("goe")){
            Location locEvaGarden = new Location(85576 + Rnd.get(-1000, 1000),257000 + Rnd.get(-1000, 1000),-11664);
            player.teleToLocation(locEvaGarden,0);
        }
        if(command.startsWith("loa")){
            L2TeleportLocation loc = TeleportLocationTable.getInstance().getTemplate(1060);
            player.teleToLocation(loc.getLocX(),loc.getLocY(),loc.getLocZ(),50);
        }

    }

    @Override
    public void showChatWindow(Player player)
    {
        String filename = getHtmlPath(getNpcId(), 0);

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        int factionZoneId = FactionZoneManager.getInstance().getCurrentFactionZoneId();
        L2ZoneType currentFactionZone = ZoneManager.getInstance().getZoneById(factionZoneId);
        html.replace("%onlineInGiran%",ZoneManager.getInstance().getZone(83400,148104,-3400,L2TownZone.class).getKnownTypeInside(Player.class).size());
        html.replace("%onlineInFaction%",currentFactionZone.getKnownTypeInside(Player.class).size());
        html.replace("%onlineInLevelZone%",ZoneManager.getInstance().getZone(141064,-123864,-1904, L2LevelZone.class).getKnownTypeInside(Player.class).size());
        html.replace("%onlineInGoe%",ZoneManager.getInstance().getZoneById(9000, L2FarmZone.class).getKnownTypeInside(Player.class).size());
        html.replace("%onlineInLoa%",ZoneManager.getInstance().getZoneById(9001, L2FarmZone.class).getKnownTypeInside(Player.class).size());
        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);
    }
}
