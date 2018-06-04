package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.model.WalkNode;
import net.sf.l2j.commons.random.Rnd;

import java.util.Arrays;
import java.util.List;

public class RainbowWalkerAi extends FakePlayerAI {

    private int RandX = 0;
    private int RandY = 0;

    List<List<WalkNode>> WalkingLinearNodes;
    int PathId = 0;
    List<WalkNode> walkNodes;
    int index = 0;
    boolean reachedNode = true;
    int Iterations = 0;

    private boolean StandStill = false;
    private int StandStillIteration = 0;


    public RainbowWalkerAi(FakePlayer fakePlayer){
        super(fakePlayer);
        Init();

    }

    public RainbowWalkerAi(FakePlayer fakePlayer, boolean standStill){
        super(fakePlayer);
        Init();
        if(standStill) {
            CheckIfShouldStand();
        }
    }

    void Init(){
        WalkingLinearNodes = GetWalkingNodesBasedOnLvl();
        PathId = Rnd.get(0, WalkingLinearNodes.size() - 1);
        RandX = Rnd.get(-100, 100);
        RandY = Rnd.get(-100, 100);
        walkNodes = WalkingLinearNodes.get(PathId);
    }
    @Override
    public void thinkAndAct() {
        if(!StandStill) {
            getBuffs();
            startWalking();
            checkIfStuck();
        }
        IsStanding();
    }

    @Override
    protected int[][] getBuffs() {
        return FakeHelpers.getMageBuffs();
    }

    private void startWalking() {
        if (index > walkNodes.size() - 1) {
            _fakePlayer.assignDefaultAI();
        }
        if (reachedNode) {
            if(index <= walkNodes.size() - 1) {
                _fakePlayer.getFakeAi().moveTo(walkNodes.get(index).getX() + RandX, walkNodes.get(index).getY() + RandY, walkNodes.get(index).getZ());
                reachedNode = false;
            }
        }


        if (index <= walkNodes.size() - 1 &&_fakePlayer.isInsideRadius(walkNodes.get(index).getX() + RandX, walkNodes.get(index).getY() + RandY, 50, false)) {
            index++;
            if(index <= walkNodes.size() - 1){
                _fakePlayer.getFakeAi().moveTo(walkNodes.get(index).getX() + RandX, walkNodes.get(index).getY() + RandY, walkNodes.get(index).getZ());
            }

        }
    }


    private  List<List<WalkNode>> GetWalkingNodesBasedOnLvl(){
        if(_fakePlayer.getLevel() < 52){
            return LowLevelLinearWalk;
        }
        if(_fakePlayer.getLevel() < 61){
            return MediumLevelLinearWalk;
        }

        if(_fakePlayer.getLevel() < 78){
            return HighLevelLinearWalk;
        }

        return LowLevelLinearWalk;
    }

    public void checkIfStuck() {
        if (!_fakePlayer.isMoving()) {
            if (Iterations > 5) {
                Iterations = 0;
                _fakePlayer.setFakeAi(new RainbowWalkerAi(_fakePlayer));
            }
            Iterations++;
        } else {
            Iterations = 0;
        }
    }

    List<List<WalkNode>> LowLevelLinearWalk = Arrays.asList(
            Arrays.asList(new WalkNode(141029,-123563,-1909,1),
                    new WalkNode(141336,-123384,-1912,1),
                    new WalkNode(141560,-123240,-1912,1),
                    new WalkNode(141704,-123096,-1896,1)),
            Arrays.asList(new WalkNode(141096,-123240,-1912,1),
                    new WalkNode(141672,-123480,-1904,1),
                    new WalkNode(142408,-123800,-1896,1)),
            Arrays.asList( new WalkNode(140936,-123528,-1904,1),
                    new WalkNode(140520,-123368,-1904,1)),
            Arrays.asList( new WalkNode(140904,-123224,-1912,1),
                    new WalkNode(140632,-122936,-1896,1)));

    List<List<WalkNode>> MediumLevelLinearWalk = Arrays.asList(
            Arrays.asList(new WalkNode(141064,-123384,-1912,1),
                    new WalkNode(141176,-122568,-1928,1),
                    new WalkNode(140856,-121880,-1936,1)),
            Arrays.asList(new WalkNode(141000,-123480,-1904,1),
                    new WalkNode(140920,-122728,-1919,1),
                    new WalkNode(141256,-122168,-1936,1)),
            Arrays.asList( new WalkNode(141176,-123384,-1912,1),
                    new WalkNode(141584,-122483,-1904,1),
                    new WalkNode(141720,-122136,-1896,1)),
            Arrays.asList( new WalkNode(141000,-123032,-1920,1),
                    new WalkNode(140520,-122440,-1920,1)));

    List<List<WalkNode>> HighLevelLinearWalk = Arrays.asList(
            Arrays.asList(new WalkNode(141048,-123432,-1904,1),
                    new WalkNode(141320,-122392,-1928,1),
                    new WalkNode(141384,-121720,-1944,1)),
            Arrays.asList(new WalkNode(141096,-123160,-1912,1),
                    new WalkNode(141736,-122248,-1888,1),
                    new WalkNode(142376,-121672,-1880,1)),
            Arrays.asList( new WalkNode(140968,-123112,-1920,1),
                    new WalkNode(141192,-122088,-1936,1),
                    new WalkNode(141656,-121384,-1928,1)),
            Arrays.asList( new WalkNode(141017,-123070,-1920,1),
                    new WalkNode(141674,-122123,-1917,1),
                    new WalkNode(142072,-121608,-1880,1)));

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
        if(Rnd.get(0,5) == 3){
            StandStill = true;
        }
    }
}
