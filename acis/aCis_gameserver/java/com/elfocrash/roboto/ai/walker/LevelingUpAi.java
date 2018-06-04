package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.ArmorHelper;
import com.elfocrash.roboto.helpers.WeaponHelper;
import com.elfocrash.roboto.model.WalkNode;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelingUpAi extends FakePlayerAI {

    WalkNode PathToGate = new WalkNode(141032, -123592, -1904, 1);
    WalkNode GmShopInRainbow = new WalkNode(141435, -123674, -1904, 10);
    WalkNode BufferInRainbow = new WalkNode(140904, -123736, -1904,1);

    List<WalkNode> _rainboSpringsWalkingNodes = Arrays.asList(
            new WalkNode(141080,-123896,-1904,20),
            new WalkNode(141400,-123896,-1904,15),
            new WalkNode(141016,-123992,-1904,12),
            new WalkNode(141480,-123992,-1904,10),
            new WalkNode(141464,-123816,-1904,20),
            new WalkNode(141304,-123672,-1904,15),
            new WalkNode(141416,-123704,-1904,12));

    private int RandX = 0;
    private int RandY = 0;
    private int Iterations = 0;
    private int ShotId = 0;
    private boolean isWalking = false;
    private List<WalkNode> CurrentPathToGmShop = new ArrayList<WalkNode>();
    private int Index = 0;
    private boolean StandStill = false;
    private int StandStillIteration = 0;
    boolean gearedUp = false;
    boolean buffedUp = false;


    public LevelingUpAi(FakePlayer player){
        super(player);
        Initialize();
    }

    public LevelingUpAi(FakePlayer player, int shotId){
        super(player);
        Initialize();
        ShotId = shotId;
    }
    private void Initialize(){
        RandX = Rnd.get(-30,30);
        RandY = Rnd.get(-30,30);
        isWalking = false;
        CurrentPathToGmShop.add(PathToGate);
        CurrentPathToGmShop.add(_rainboSpringsWalkingNodes.get(Rnd.get(0, _rainboSpringsWalkingNodes.size()-1)));
        CurrentPathToGmShop.add(GmShopInRainbow);
        //CheckIfShouldStand();
    }



    @Override
    public void setup(){

    }

    @Override
    public void thinkAndAct() {
        _fakePlayer.broadcastUserInfo();
        setBusyThinking(true);
        moveToGmShop();
        moveToBuffShop();
        changeAi();
        IsStanding();
        checkIfStuck();
        goToGiran();
        setBusyThinking(false);
    }

    @Override
    protected int[][] getBuffs() {
        return new int[0][];
    }

    private void moveToGmShop(){
        if(checkIfInRainboSprings()) {
            if(checkGearAvailability()) {
                if(!StandStill){
                    moveToLocation();
                    if (_fakePlayer.isInsideRadius(CurrentPathToGmShop.get(Index).getX()+RandX, CurrentPathToGmShop.get(Index).getY()+RandY, 50, false)) {
                        if(Index == CurrentPathToGmShop.size()-1){
                            gearUp();
                            gearedUp = true;
                            StandStill = true;
                        }
                        isWalking = false;
                        Index++;
                        if(Index == 1){
                            moveToLocation();
                        }
                        CheckIfShouldStand();
                    }
                }
            }
        }
    }

    private void moveToLocation(){
        if(Index <= CurrentPathToGmShop.size()-1) {
            int x = CurrentPathToGmShop.get(Index).getX() + RandX;
            int y = CurrentPathToGmShop.get(Index).getY() + RandY;
            if (!isWalking) {
                _fakePlayer.getFakeAi().moveTo(x, y, GmShopInRainbow.getZ());
                isWalking = true;
            }
        }
    }

    private void moveToBuffShop(){
        if(!StandStill){
            int x = BufferInRainbow.getX() + RandX;
            int y = BufferInRainbow.getY() + RandY;
            if(gearedUp){
                _fakePlayer.getFakeAi().moveTo(x, y, BufferInRainbow.getZ());
            }
            if(_fakePlayer.isInsideRadius(x,y, 1, false)){
                buffedUp = true;
                CheckIfShouldStand();
            }
        }

    }

    private void changeAi(){
        if(gearedUp && buffedUp && !StandStill){
            _fakePlayer.setFakeAi(new RainbowWalkerAi(_fakePlayer));
        }
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

    private void IsStanding(){
        if(StandStill){
            if(StandStillIteration != 5){
                StandStillIteration++;
            } else {
                StandStillIteration = 0;
                StandStill = false;
            }
        }
    }

    private void CheckIfShouldStand(){
        if(Rnd.get(0,10) == 5){
            StandStill = true;
        }
    }
}
