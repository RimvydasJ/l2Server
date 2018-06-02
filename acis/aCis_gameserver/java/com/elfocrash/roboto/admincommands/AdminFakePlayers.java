package com.elfocrash.roboto.admincommands;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.FakePlayerManager;
import com.elfocrash.roboto.FakePlayerTaskManager;
import com.elfocrash.roboto.ai.walker.CommonWalkerAi;

import com.elfocrash.roboto.helpers.MapSpawnHelper;
import com.mchange.v2.cfg.PropertiesConfigSource;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.List;
import java.util.Random;

/**
 * @author Elfocrash
 *
 */
public class AdminFakePlayers implements IAdminCommandHandler {
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
                    "admin_spawnlowlevel",
                    "admin_fakeinfo",
            };

    @Override
    public String[] getAdminCommandList() {
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
    public boolean useAdminCommand(String command, Player activeChar) {
        if (command.startsWith("admin_fakes")) {
            showFakeDashboard(activeChar);
        }

        if (command.startsWith("admin_deletefake")) {
            if (activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
                FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();
                fakePlayer.despawnPlayer();
            } else {
                List<FakePlayer> allFakes = FakePlayerManager.INSTANCE.getFakePlayers();
                for (int i = 0; i < allFakes.size(); i++) {
                    allFakes.get(i).despawnPlayer();
                }
            }
            return true;
        }

        if (command.startsWith("admin_spawnrandom")) {
            String[] params = command.split(" ");

            //2 - clan name provided
            if (params.length == 2) {
                //TODO Clan things
            }

            //3 - clan name && count
            else if (params.length == 3) {
                Thread t1 = new Thread(() -> {
                    System.out.println("Start spawning: " + params[2] + " bots");
                    for (int i = 0; i < Integer.parseInt(params[2]); i++) {
                        FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX() + Rnd.get(-200, 200), activeChar.getY() + Rnd.get(-200, 200), activeChar.getZ());
                        fakePlayer.assignDefaultAI();
                    }
                    System.out.println("Finish");
                });
                t1.start();
            } else {
                FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                fakePlayer.assignDefaultAI();
            }
            return true;
        }

        if (command.startsWith("admin_spawnwalker")) {
            if (command.contains(" ")) {
                String locationName = command.split(" ")[1];
                FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                switch (locationName) {
                    case "giran":
                        fakePlayer.setFakeAi(new CommonWalkerAi(fakePlayer));
                        break;
                }
                return true;
            }
            return true;
        }

        if (command.startsWith("admin_spawnlowlevel")) {
            String[] params = command.split(" ");
            int characterCount = 1;

            if (params.length == 2) {
                characterCount = Integer.parseInt(params[1]);
            }
            final int count = characterCount;

            Thread charCreator = new Thread(() -> {
                for (int i = 0; i < count; i++) {
                    int coordId = Rnd.nextInt(MapSpawnHelper.RainbowSprings.size()-1);
                    int x = MapSpawnHelper.RainbowSprings.get(coordId).X;
                    int y = MapSpawnHelper.RainbowSprings.get(coordId).Y;
                    int z = MapSpawnHelper.RainbowSprings.get(coordId).Z;
                    FakePlayer fakePlayer = FakePlayerManager.INSTANCE.spawnPlayer(x, y, z, 40);
                    fakePlayer.assignDefaultAI();
                }
            });
            charCreator.start();

            return true;
        }

        if (command.startsWith("admin_fakeinfo")) {
            if (activeChar.getTarget() instanceof FakePlayer) {
                FakePlayer target = (FakePlayer) activeChar.getTarget();
                activeChar.sendMessage(target.getFakeAi().getClass().getSimpleName() + " Level: " + target.getLevel());
                if (target.isInsideZone(ZoneId.TOWN))
                    activeChar.sendMessage("Is in town");
            }
            return true;
        }


        return true;
    }
}
