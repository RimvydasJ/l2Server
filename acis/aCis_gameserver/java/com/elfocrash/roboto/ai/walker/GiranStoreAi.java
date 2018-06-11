package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.model.WalkNode;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.tradelist.TradeList;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;

import java.util.ArrayList;
import java.util.List;

public class GiranStoreAi extends FakePlayerAI {


    WalkNode walkNode;
    boolean reachedNode = true;
    boolean sellItems = true;
    int rndX =0;
    int rndY =0;
    public GiranStoreAi(FakePlayer fakePlayer){
        super(fakePlayer);
        Init();
    }

    private void Init(){
        walkNode = GetSpotForStore();
        rndX = Rnd.get(-200,200);
        rndY = Rnd.get(-200,200);
    }

    @Override
    public void thinkAndAct() {
        startWalking();
        startStore();
        changeFakeAiToCommonWalker();
    }

    @Override
    protected int[][] getBuffs() {
        return FakeHelpers.getFighterBuffs();
    }

    private void startWalking() {
        if (reachedNode && !_fakePlayer.isInStoreMode()) {
            _fakePlayer.getFakeAi().moveTo(walkNode.getX()+rndX, walkNode.getY()+rndY, walkNode.getZ());
            reachedNode = false;
        }

        if (_fakePlayer.isInsideRadius(walkNode.getX()+rndX, walkNode.getY()+rndY, 10, false)) {
            reachedNode = true;
        }
    }

    private WalkNode GetSpotForStore(){
        List<WalkNode> walkNodes = new ArrayList<WalkNode>(){{
            add(new WalkNode(82552,149096,-3464,1));
            add(new WalkNode(82568,148600,-3464,1));
            add(new WalkNode(82552,148136,-3464,1));
            add(new WalkNode(81944,148072,-3464,1));
            add(new WalkNode(81352,148120,-3464,1));
            add(new WalkNode(81272,148600,-3464,1));
            add(new WalkNode(81336,149176,-3464,1));
            add(new WalkNode(81880,149208,-3464,1));
            add(new WalkNode(82600,148856,-3464,1));
            add(new WalkNode(82264,148088,-3464,1));
            add(new WalkNode(81608,148104,-3464,1));
            add(new WalkNode(81288,148376,-3464,1));
            add(new WalkNode(81304,148904,-3464,1));
            add(new WalkNode(81624,149160,-3464,1));
            add(new WalkNode(82216,149160,-3464,1));
        }};
        return walkNodes.get(Rnd.get(0,walkNodes.size()-1));
    }

    private void startStore() {
        if (sellItems && reachedNode && !_fakePlayer.isInStoreMode()) {
            _fakePlayer.getInventory().addItem("newItem",728,500,_fakePlayer,null);
            _fakePlayer.getInventory().addItem("newItem",57,5000000,_fakePlayer,null);
            TradeList list = _fakePlayer.getSellList();
            ItemInstance itemInstance = _fakePlayer.getInventory().getItemByItemId(728);
            int objectId = itemInstance.getObjectId();
            list.addItem(objectId,6,30);
            list.setTitle(itemInstance.getItemName());
            _fakePlayer.setActiveTradeList(list);
            _fakePlayer.sitDown();
            _fakePlayer.setStoreType(Player.StoreType.SELL);
            _fakePlayer.broadcastCharInfo();
            _fakePlayer.broadcastPacket(new PrivateStoreMsgSell(_fakePlayer));
            sellItems = false;
        }
    }

    private void changeFakeAiToCommonWalker(){
        if(!_fakePlayer.isInStoreMode() && !sellItems){
            _fakePlayer.standUp();
            _fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
        }
    }
}
