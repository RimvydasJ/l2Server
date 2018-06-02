package com.elfocrash.roboto.helpers;

import com.elfocrash.roboto.FakePlayer;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorHelper {

    public class Armor {
        //40 lvl items
        public List<Integer> Robe40 = Arrays.asList(2454,471,439,2414,2497);
        public List<Integer> Heavy40 = Arrays.asList(356,2414,2438,2462);
        public List<Integer> Light40 = Arrays.asList(398,418,2431,2414,2462);

        //52 lvl
        public List<Integer> Robe52 = Arrays.asList(2406,2464,600,2415,110);
        public List<Integer> Heavy52 = Arrays.asList(358,2380,2487,2439,2416);
        public List<Integer> Light52 = Arrays.asList(2392,2475,601,2417);

        //61 lvl
        public List<Integer> Robe61 = Arrays.asList(2407,512,5779,858,889,920,889,858);
        public List<Integer> Heavy61 = Arrays.asList(2382,5768,5780,547);
        public List<Integer> Light61 = Arrays.asList(2385,2389,5766,5778,512);
    }


    public void giveArmorsByClass(FakePlayer player, int level) {
        List<Integer> itemIds = returnArmorIdList(player, level);
        for (int id : itemIds) {
            player.getInventory().addItem("Armors", id, 1, player, null);
            ItemInstance item = player.getInventory().getItemByItemId(id);
            // enchant the item??
            player.getInventory().equipItemAndRecord(item);
            player.getInventory().reloadEquippedItems();
            player.broadcastCharInfo();
        }
    }

    public List<Integer> returnArmorIdList(FakePlayer player, int level){
        List<Integer> itemIds = new ArrayList<>();

        ClassId classid = player.getClassId();

        switch (classid) {
            case DUELIST:
            case DREADNOUGHT:
            case TITAN:
            case PHOENIX_KNIGHT:
            case SWORD_MUSE:
            case HELL_KNIGHT:
            case EVAS_TEMPLAR:
            case SHILLIEN_TEMPLAR:
            case MAESTRO:
                if(level >= 40 && level < 52){
                    itemIds = new Armor().Heavy40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Armor().Heavy52;
                } else if (level >= 61){
                    itemIds = new Armor().Heavy61;
                }
                break;
            case ARCHMAGE:
                case SOULTAKER:
               case HIEROPHANT:
               case ARCANA_LORD:
               case CARDINAL:
               case MYSTIC_MUSE:
               case ELEMENTAL_MASTER:
               case EVAS_SAINT:
               case STORM_SCREAMER:
               case SPECTRAL_MASTER:
               case SHILLIEN_SAINT:
               case DOMINATOR:
               case DOOMCRYER:
                if(level >= 40 && level < 52){
                    itemIds = new Armor().Robe40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Armor().Robe52;
                } else if (level >= 61){
                    itemIds = new Armor().Robe61;
                }
                break;
            case FORTUNE_SEEKER:
            case GHOST_HUNTER:
            case WIND_RIDER:
            case ADVENTURER:
            case SAGGITARIUS:
            case MOONLIGHT_SENTINEL:
            case GHOST_SENTINEL:
            case GRAND_KHAVATARI:
                if(level >= 40 && level < 52){
                    itemIds = new Armor().Light40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Armor().Light52;
                } else if (level >= 61){
                    itemIds = new Armor().Light61;
                }
                break;
        }
        return itemIds;
    }
}
