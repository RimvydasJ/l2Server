package net.sf.l2j.gameserver.custom;
import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.zone.type.L2FactionZone;
import net.sf.l2j.gameserver.util.Broadcast;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class FactionZoneManager {
    private static FactionZoneManager ourInstance = new FactionZoneManager();
    private static Logger _log = Logger.getLogger(FactionZoneManager.class.getName());
    private Collection<L2FactionZone> _factionZones;
    private L2FactionZone _currentZone;
    public static FactionZoneManager getInstance() {
        return ourInstance;
    }

    private FactionZoneManager() {
        _factionZones= ZoneManager.getInstance().getAllZones(L2FactionZone.class);
    }
    public void changeFactionZoneRandom(){
        _currentZone = (L2FactionZone) _factionZones.toArray()[Rnd.get(0,_factionZones.size()-1)];
        _log.info("Faction zone randomly changed to: " + _currentZone.zoneName);
        Broadcast.announceToOnlinePlayers("Faction zone randomly changed to: " + _currentZone.zoneName);
    }

    public int getCurrentFactionZoneId(){
        return _currentZone.getId();
    }

    public Location getTeleportLoc(){
        return _currentZone.getSpawnLoc();
    }

    public void setTimeFactionZoneRandomChange(){
        TimerRegistrator.getInstance().cleanFactionChangeTimes();
        List<String> times = Arrays.asList(Config.FACTION_ZONE_CHANGE_TIMES.split("\\s*;\\s*"));
        times.forEach(x-> TimerRegistrator.getInstance().addFactionZoneChangeEvent(x));
        _log.info("Timetable with " + times.size() + " times set successfully");
    }

}
