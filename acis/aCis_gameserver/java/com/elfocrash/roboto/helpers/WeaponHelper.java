package com.elfocrash.roboto.helpers;

import com.elfocrash.roboto.FakePlayer;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeaponHelper {

    public static class Weapon {

        public List<Integer> Dagger40 = Arrays.asList(4776);
        public List<Integer> Dagger52 = Arrays.asList(4780);
        public List<Integer> Dagger61 = Arrays.asList(5618);

        public List<Integer> Sword40 = Arrays.asList(4708,2497);
        public List<Integer> Sword52 = Arrays.asList(4717,110);
        public List<Integer> Sword61 = Arrays.asList(5648,641);

        public List<Integer> Blunt40 = Arrays.asList(4745,2497);
        public List<Integer> Blunt52 = Arrays.asList(4754,110);
        public List<Integer> Blunt61 = Arrays.asList(5064,641);

        public List<Integer> TwoHanded40 = Arrays.asList(6348);
        public List<Integer> TwoHanded52 = Arrays.asList(4724);
        public List<Integer> TwoHanded61 = Arrays.asList(5645);

        public List<Integer> Dual40 = Arrays.asList(2561);
        public List<Integer> Dual52 = Arrays.asList(2562);
        public List<Integer> Dual61 = Arrays.asList(5705);

        public List<Integer> Pole40 = Arrays.asList(4853);
        public List<Integer> Pole52 = Arrays.asList(4859);
        public List<Integer> Pole61 = Arrays.asList(5634);

        public List<Integer> Staff40 = Arrays.asList(6313);
        public List<Integer> Staff52 = Arrays.asList(8117);
        public List<Integer> Staff61 = Arrays.asList(5643);

        public List<Integer> Fist40 = Arrays.asList(4795);
        public List<Integer> Fist52 = Arrays.asList(4804);
        public List<Integer> Fist61 = Arrays.asList(5624);

        public List<Integer> Bow40 = Arrays.asList(4822);
        public List<Integer> Bow52 = Arrays.asList(4829);
        public List<Integer> Bow61 = Arrays.asList(5608);



    }

    public void giveWeaponsByClass(FakePlayer player, boolean randomlyEnchant, int level) {
        List<Integer> itemIds = giveWeapon(player, level);
        for (int id : itemIds) {
            player.getInventory().addItem("Weapon", id, 1, player, null);
            ItemInstance item = player.getInventory().getItemByItemId(id);
            if(randomlyEnchant)
                item.setEnchantLevel(Rnd.get(7, 20));
            player.getInventory().equipItemAndRecord(item);
            player.getInventory().reloadEquippedItems();
        }
    }

    private List<Integer> giveWeapon(FakePlayer player, int level){
        List<Integer> itemIds = new ArrayList<>();

        ClassId classid = player.getClassId();
        switch (classid) {
            case GLADIATOR:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().Dual40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().Dual52;
                } else if (level >= 61){
                    itemIds = new Weapon().Dual61;
                }
                break;
            case DESTROYER:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().TwoHanded40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().TwoHanded52;
                } else if (level >= 61){
                    itemIds = new Weapon().TwoHanded61;
                }
                break;
            case WARLORD:
            case SORCERER:
            case NECROMANCER:
            case BISHOP:
            case SPELLSINGER:
            case SPELLHOWLER:
            case OVERLORD:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().Staff40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().Staff52;
                } else if (level >= 61){
                    itemIds = new Weapon().Staff61;
                }
                break;
            case TREASURE_HUNTER:
            case PLAINS_WALKER:
            case ABYSS_WALKER:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().Dagger40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().Dagger52;
                } else if (level >= 61){
                    itemIds = new Weapon().Dagger61;
                }
                break;
            case HAWKEYE:
            case SILVER_RANGER:
            case PHANTOM_RANGER:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().Bow40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().Bow52;
                } else if (level >= 61){
                    itemIds = new Weapon().Bow61;
                }
                break;
            case TYRANT:
                if(level >= 40 && level < 52){
                    itemIds = new Weapon().Fist40;
                } else if (level >= 52 && level < 61) {
                    itemIds = new Weapon().Fist52;
                } else if (level >= 61){
                    itemIds = new Weapon().Fist61;
                }
                break;
        }
        return itemIds;
    }
}
