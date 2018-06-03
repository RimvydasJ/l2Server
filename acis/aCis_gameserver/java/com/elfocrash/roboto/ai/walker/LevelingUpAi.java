package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.ArmorHelper;
import com.elfocrash.roboto.helpers.WeaponHelper;
import com.elfocrash.roboto.model.WalkNode;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.Arrays;
import java.util.List;

public class LevelingUpAi extends FakePlayerAI {


    WalkNode GmShopInRainbow = new WalkNode(141435, -123674, -1904, 1);
    WalkNode BufferInRainbow = new WalkNode(140904, -123736, -1904,1);

    List<WalkNode> _rainboSpringsWalkingNodes = Arrays.asList(
            new WalkNode(141224,-123480,-1904,1),
            new WalkNode(141656,-123464,-1904,1),
            new WalkNode(142360,-123240,-1912,1),
            new WalkNode(142312,-123528,-1888,1),
            new WalkNode(141096,-122920,-1920,1),
            new WalkNode(140584,-123272,-1904,1),
            new WalkNode(141336,-122392,-1928,1),
            new WalkNode(141224,-123480,-1904,1),
            new WalkNode(142600,-122232,-1880,1),
            new WalkNode(142664,-122520,-1856,1),
            new WalkNode(140488,-122104,-1896,1),
            new WalkNode(140488,-122072,-1936,1),
            new WalkNode(140680,-122760,-1904,1),
            new WalkNode(140696,-122920,-1896,1),
            new WalkNode(140200,-12336,-1904,1),
            new WalkNode(141208,-122056,-1936,1),
            new WalkNode(140792,-123016,-1904,1));

    private int RandX = 0;
    private int RandY = 0;
    private int Iterations = 0;
    private int ShotId = 0;
    private boolean isWalking = false;
    public LevelingUpAi(FakePlayer player){
        super(player);
        RandX = Rnd.get(-10,10);
        RandY = Rnd.get(-10,10);
        isWalking = false;
    }

    public LevelingUpAi(FakePlayer player, int shotId){
        super(player);
        RandX = Rnd.get(-10,10);
        RandY = Rnd.get(-10,10);
        ShotId = shotId;
    }

    @Override
    public void setup(){

    }

    @Override
    public void thinkAndAct() {
        _fakePlayer.broadcastUserInfo();
        setBusyThinking(true);
        changeGearAndBuffs();
        checkIfStuck();
        goToGiran();
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
            boolean buffedUp = false;
            if(checkGearAvailability()){
                int x = GmShopInRainbow.getX()+RandX;
                int y = GmShopInRainbow.getY()+RandY;

                if(!isWalking) {
                    _fakePlayer.getFakeAi().moveTo(x, y, GmShopInRainbow.getZ());
                    isWalking = true;
                }
                if (_fakePlayer.isInsideRadius(x, y, 50, false)) {
                    gearUp();
                    gearedUp = true;
                    isWalking = false;
                }
            }

            int x = BufferInRainbow.getX() +RandX;
            int y = BufferInRainbow.getY() + RandY;

            if(gearedUp){
                _fakePlayer.getFakeAi().moveTo(x, y, BufferInRainbow.getZ());
            }

            if(_fakePlayer.isInsideRadius(x,y, 50, false)){
                _fakePlayer.setFakeAi(new RainbowWalkerAi(_fakePlayer));
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

        _fakePlayer.addAutoSoulShot(ShotId);

        if(_fakePlayer.getLevel() < 61){
            super.setGearB(true);
        }else {
            super.setGearA(true);
        }

    }

    private void buffUp(){

    }

    private void goToGiran(){
        if(_fakePlayer.getLevel() >= 78 && checkIfInRainboSprings()){
            int x = 140904+RandX;
            int y = -124056+RandY;
            int z = -1904;
            _fakePlayer.getFakeAi().moveTo(x, y, z);
            if(_fakePlayer.isInsideRadius(x,y, 50, false)){
                if (_fakePlayer.getFakeAi().teleportToLocation(83448 + Rnd.get(-100, 100), 148568 + Rnd.get(-100, 100), -3473, 20)) {
                    _fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
                }
            }
        }
    }

    public void checkIfStuck(){
        if(!_fakePlayer.isMoving()){
            if(Iterations > 20){
                Iterations = 0;
                _fakePlayer.assignDefaultAI();
            }
            Iterations++;
        } else {
            Iterations = 0;
        }

    }
}
