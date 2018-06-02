package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.ArmorHelper;
import com.elfocrash.roboto.helpers.WeaponHelper;
import com.elfocrash.roboto.model.WalkNode;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.scripting.quests.Q643_RiseAndFallOfTheElrokiTribe;

import java.util.List;

public class LevelingUpAi extends FakePlayerAI {


    WalkNode GmShopInRainbow = new WalkNode(141435, -123674, -1908, 1);
    WalkNode BufferInRainbow = new WalkNode(140904, -123736, -1904,1);

    public LevelingUpAi(FakePlayer player){
        super(player);
    }

    @Override
    public void setup(){

    }

    @Override
    public void thinkAndAct() {
        _fakePlayer.broadcastUserInfo();
        setBusyThinking(true);
        changeGearAndBuffs();
        setBusyThinking(false);
    }

    @Override
    protected int[][] getBuffs() {
        return new int[0][];
    }

    private void changeGearAndBuffs(){
        //If in RainboSprings up area
        if(checkIfInRainboSprings()){
            boolean gearedUp = false;
            if(checkGearAvailability()){
                _fakePlayer.getFakeAi().moveTo(GmShopInRainbow.getX(), GmShopInRainbow.getY(), GmShopInRainbow.getZ());
                if(_fakePlayer.isInsideRadius(GmShopInRainbow.getX(),GmShopInRainbow.getY(), 50, false)){
                    gearUp();
                    gearedUp = true;
                }
            }

            if(gearedUp){
                _fakePlayer.getFakeAi().moveTo(BufferInRainbow.getX(), BufferInRainbow.getY(), BufferInRainbow.getZ());
            }

            if(_fakePlayer.isInsideRadius(BufferInRainbow.getX(),BufferInRainbow.getY(), 50, false)){
                _fakePlayer.assignDefaultAI();
            }

            //Kol nera npc ideta
            /*
            if(_fakePlayer.getKnownTypeInRadius(Npc.class, 100).size() > 0){

            }*/




        }

        //If in Giran

        //If somewhere else
    }

    // TODO: REfacotor this
    private void gearUp(){
        // Armor
        ArmorHelper armorHelper = new ArmorHelper();
        List<Integer> armorList = armorHelper.returnArmorIdList(_fakePlayer, _fakePlayer.getLevel()-8);
        for(int armorId : armorList){
            ItemInstance item = _fakePlayer.getInventory().getItemByItemId(armorId);
            _fakePlayer.getInventory().destroyItemByItemId("removeItems", armorId, 1, _fakePlayer, item);
        }
        _fakePlayer.getInventory().reloadEquippedItems();
        _fakePlayer.broadcastCharInfo();
        armorHelper.giveArmorsByClass(_fakePlayer, _fakePlayer.getLevel());

        //Weapon
        WeaponHelper weaponHelper = new WeaponHelper();
        List<Integer> weaponList = weaponHelper.returnWeaponId(_fakePlayer, _fakePlayer.getLevel() - 8);
        for(int weaponId : weaponList){
            ItemInstance item = _fakePlayer.getInventory().getItemByItemId(weaponId);
            _fakePlayer.getInventory().destroyItemByItemId("removeItems", weaponId, 1, _fakePlayer, item);
        }
        _fakePlayer.getInventory().reloadEquippedItems();
        _fakePlayer.broadcastCharInfo();
        weaponHelper.giveWeaponsByClass(_fakePlayer, false, _fakePlayer.getLevel());

        if(_fakePlayer.getLevel() < 61){
            super.setGearB(true);
        }else {
            super.setGearA(true);
        }

    }

    private void buffUp(){

    }
}
