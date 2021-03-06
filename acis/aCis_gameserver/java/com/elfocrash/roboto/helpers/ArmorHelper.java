package com.elfocrash.roboto.helpers;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.helpers.Enums.ItemGrade;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorHelper {

    public class Armor {
        //40 lvl items
        public List<Integer> Robe40 = Arrays.asList(2454,471,439,2414,2497,854,854,119,886,886);
        public List<Integer> Heavy40 = Arrays.asList(356,2414,2438,2462,854,854,119,886,886);
        public List<Integer> Light40 = Arrays.asList(398,418,2431,2414,2462,854,854,119,886,886);

        //52 lvl
        public List<Integer> Robe52 = Arrays.asList(2406,2464,600,2415,110,864,864,926,895,895);
        public List<Integer> Heavy52 = Arrays.asList(358,2380,2487,2439,2416,864,864,926,895,895);
        public List<Integer> Light52 = Arrays.asList(2392,2475,601,2417,864,864,926,895,895);

        //61 lvl
        public List<Integer> Robe61 = Arrays.asList(2407,512,5779,858,889,920,889,858,924,893,893,862,862);
        public List<Integer> Heavy61 = Arrays.asList(2382,5768,5780,547,924,893,893,862,862);
        public List<Integer> Light61 = Arrays.asList(2385,2389,5766,5778,512,924,893,893,862,862);

        //76 lvl
        public List<Integer> Robe76a = Arrays.asList(2407, 512, 5767, 5779, 858, 858, 889, 889, 920);
        public List<Integer> Robe76b = Arrays.asList(6383,6386,6384,6385,858, 858, 889, 889, 920);
        public List<Integer> Heavy76 = Arrays.asList(6373, 6374, 6375, 6376, 6378, 858, 858, 889, 889, 920);
        public List<Integer> Light76 = Arrays.asList(6379, 6380, 6381, 6382, 858, 858, 889, 889, 920);
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
                } else if (level >= 61 && level < 76){
                    itemIds = new Armor().Heavy61;
                }else if (level >= 76 && player.sGradePvpBot){
                    itemIds = new Armor().Heavy76;
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
                } else if (level >= 61 && level < 76){
                    itemIds = new Armor().Robe61;
                }else if (level >= 76 && player.sGradePvpBot){
                    if(Rnd.nextDouble() < 0.5) {
                        itemIds = new Armor().Robe76a;
                    }
                    else {
                        itemIds = new Armor().Robe76b;
                    }
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
                } else if (level >= 61 && level < 76){
                    itemIds = new Armor().Light61;
                }else if (level >= 76 && player.sGradePvpBot){
                    itemIds = new Armor().Light76;
                }
                break;
        }
        return itemIds;
    }

    public List<Integer> getAllOneGradeGear(ItemGrade.Grade grade){

        List<Integer> armorList = new ArrayList<>();
        Armor armor = new Armor();
        switch(grade){
            case C:
                armorList.addAll(armor.Robe40);
                armorList.addAll(armor.Heavy40);
                armorList.addAll(armor.Light40);
                break;
            case B:
                armorList.addAll(armor.Robe52);
                armorList.addAll(armor.Heavy52);
                armorList.addAll(armor.Light52);
                break;
            case A:
                armorList.addAll(armor.Robe61);
                armorList.addAll(armor.Heavy61);
                armorList.addAll(armor.Light61);
                break;
            case S:
                armorList.addAll(armor.Robe76a);
                armorList.addAll(armor.Robe76b);
                armorList.addAll(armor.Light76);
                armorList.addAll(armor.Heavy76);
                break;
        }

        return armorList;
    }
}
