package net.sf.l2j.gameserver.custom;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.actor.instance.Player;

import java.util.concurrent.ThreadLocalRandom;

public class FactionHelper {
    private static FactionHelper ourInstance = new FactionHelper();

    public static FactionHelper getInstance() {
        return ourInstance;
    }
    private RandomString rndString;
    private FactionHelper() {
        rndString = new RandomString(8,ThreadLocalRandom.current());
    }

    public void onZoneEntrance(Player player){
        Faction faction = player.getFaction();
        player.setFaction(faction);
        player.setTitle("FactionZone");
        player.getAppearance().setTitleColor(getColor(faction));
        player.getAppearance().setNameColor(getColor(faction));
        player.setName(getRandomName());
        player.setPvpFlag(0);
    }

    public void onZoneExit(Player player){
        player.setTitle(player._titleF);
        player.getAppearance().setTitleColor(player._titleColorF);
        player.getAppearance().setNameColor(player._nameColortF);
        player.setName(player._nameF);
        player.setPvpFlag(0);
        //player.setFaction(FactionId.NON);
    }

    //TODO iskelti i singeltona
    private String getRandomName(){
        return rndString.nextString();
    }

    private int getColor(Faction faction){
        switch (faction){
            case Blue:
                return 0x0000FF;
            case Red:
                return 0xFF0000;
            default:
                return 0xFFFFFF;
        }
    }
}
