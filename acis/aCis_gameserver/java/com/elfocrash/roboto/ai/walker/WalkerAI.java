package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.model.WalkNode;
import com.elfocrash.roboto.model.WalkerType;
import java.util.*;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.model.L2TeleportLocation;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Gatekeeper;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public abstract class WalkerAI extends FakePlayerAI {

	protected Queue<WalkNode> _walkNodes;
	private WalkNode _currentWalkNode;
	private int currentStayIterations = 0;
	private int freezeTestInteration = 0;
	private int testLocX = 0;
	protected boolean isWalking = false;

	public WalkerAI(FakePlayer character) {
		super(character);
	}

	public Queue<WalkNode> getWalkNodes(){
		return _walkNodes;
	}

	protected void addWalkNode(WalkNode walkNode) {
		_walkNodes.add(walkNode);
	}

	@Override
	public void setup() {
		super.setup();
		_walkNodes = new LinkedList<>();
		setWalkNodes(_fakePlayer.getNearestTownId());
	}

	@Override
	public void thinkAndAct() {
		_fakePlayer.broadcastUserInfo();
		setBusyThinking(true);

			if (freezeTestInteration == 0)
				testLocX = _fakePlayer.getX();
			handleDeath();
			teleportToZone();
			freezeTest();
			if (_walkNodes.isEmpty())
				return;

			if (isWalking) {
				cancelWalkingStartAttack();
				//Default behaviour, after it reaches final destination
				if (userReachedDestination(_currentWalkNode)) {
					if (currentStayIterations < _currentWalkNode.getStayIterations()) {
						currentStayIterations++;
						setBusyThinking(false);
						targetingGkOrMob();
						return;
					}
					_currentWalkNode = null;
					currentStayIterations = 0;
					isWalking = false;
				}
			}

			if (!isWalking && _currentWalkNode == null) {
				switch (getWalkerType()) {
					//Mostly is used in towns.
					case RANDOM:
						_currentWalkNode = (WalkNode) getWalkNodes().toArray()[Rnd.get(0, getWalkNodes().size() - 1)];
						break;
					//TODO: implement LINEAR if its needed in any zone. For example: going to any loc from spawn loc.
					case LINEAR:
						_currentWalkNode = getWalkNodes().poll();
						_walkNodes.add(_currentWalkNode);
						break;
				}
				_fakePlayer.getFakeAi().moveTo(_currentWalkNode.getX(), _currentWalkNode.getY(), _currentWalkNode.getZ());
				isWalking = true;
			}

		setBusyThinking(false);

	}

	@Override
	protected int[][] getBuffs() {
		return new int[0][0];
	}

	protected boolean userReachedDestination(WalkNode targetWalkNode) {
		//TODO: Improve this with approximate equality and not strict
		if(_fakePlayer.getX() == targetWalkNode.getX()
				&& _fakePlayer.getY() == targetWalkNode.getY())
			return true;

		return false;
	}

	protected abstract WalkerType getWalkerType();
	protected abstract void setWalkNodes(int townId);

	public void teleportToZone(){
		if (_fakePlayer.getTarget() != null && _fakePlayer.getTarget() instanceof Gatekeeper) {
			//TODO If possible not needed
			//Farm zone
			if (_fakePlayer.getLevel() >= 78 && _fakePlayer.getNearestTownId() != 15) {
				List<L2TeleportLocation> locations = new ArrayList<>();

				L2TeleportLocation locEvaGarden = new L2TeleportLocation();
				locEvaGarden.setLocX(85576 + Rnd.get(-1000, 1000));
				locEvaGarden.setLocY(257000 + Rnd.get(-1000, 1000));
				locEvaGarden.setLocZ(-11664);


				L2TeleportLocation giranFarmLoc = TeleportLocationTable.getInstance().getTemplate(1060);
				locations.add(giranFarmLoc);
				//TODO kolkas viena zona tik - Anthara's Lair
				//locations.add(locEvaGarden);

				L2TeleportLocation whereToGo = locations.get(Rnd.get(0, locations.size() - 1));
				if (_fakePlayer.getFakeAi().teleportToLocation(whereToGo.getLocX(), whereToGo.getLocY(), whereToGo.getLocZ(), 20)) {
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
					if (_fakePlayer.getNearestTownId() == 13) {
						_fakePlayer.assignDefaultAI();
					}
				}
			}
			//Level up zone
			else if (_fakePlayer.getLevel() < 78) {
				if (_fakePlayer.getFakeAi().teleportToLocation(141240, -124216, -1864, 20)) {
					_fakePlayer.broadcastUserInfo();
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
				}
			}
			//Giran
			else if (_fakePlayer.getLevel() >= 78 && _fakePlayer.getNearestTownId() == 15) {
				if (_fakePlayer.getFakeAi().teleportToLocation(83448 + Rnd.get(-100, 100), 148568 + Rnd.get(-100, 100), -3473, 20)) {
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
				}
			}
		}
	}

	private void freezeTest(){
		if (freezeTestInteration <= 4) {
			freezeTestInteration++;
		} else {
			if (testLocX == _fakePlayer.getX())
				if(_fakePlayer.getNearestTownId() == 15 && _fakePlayer.getLevel() >=76){
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
				}
				else if(_fakePlayer.getNearestTownId() == 15 && _fakePlayer.getLevel() < 76) {
					_fakePlayer.assignDefaultAI();
				}
				else{
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
				}
			freezeTestInteration = 0;
		}
	}

	private void targetingGkOrMob(){
		//Gk naudojimas kai Girane
		if (_fakePlayer.isInsideZone(ZoneId.TOWN) && _fakePlayer.getKnownTypeInRadius(Gatekeeper.class, 200).size() > 0) {
			Gatekeeper gk = _fakePlayer.getKnownTypeInRadius(Gatekeeper.class, 200).get(0);
			_fakePlayer.setTarget(gk);
			_fakePlayer.getAI().setIntention(CtrlIntention.INTERACT);
		}
		//GK naudojimas kai Lvl zonoje
		else if(_fakePlayer.getNearestTownId() == 15 && _fakePlayer.getLevel() >= 78 && _fakePlayer.getKnownTypeInRadius(Gatekeeper.class, 200).size() > 0){
			Gatekeeper gk = _fakePlayer.getKnownTypeInRadius(Gatekeeper.class, 200).get(0);
			_fakePlayer.setTarget(gk);
			_fakePlayer.getAI().setIntention(CtrlIntention.INTERACT);
		}
		//Mob targinimas
		else if (!_fakePlayer.isInsideZone(ZoneId.TOWN) && _fakePlayer.getLevel() >=78) {
			_fakePlayer.assignDefaultAI();
		}
	}

	private void cancelWalkingStartAttack(){
		if (Rnd.nextDouble() <= 0.05) {
			if (!_fakePlayer.isInsideZone(ZoneId.TOWN) && !_fakePlayer.isInsideZone(ZoneId.PEACE) && _fakePlayer.getNearestTownId() != 15 && _fakePlayer.getLevel() >=78) {

				//tryTargetPlayerInPvp();
				tryTargetRandomCreatureByTypeInRadius(FakeHelpers.getTestTargetClass(), FakeHelpers.getTestTargetRange());
				if(_fakePlayer.getTarget() != null){
					_fakePlayer.assignDefaultAI();
				}
			}
			//TODO jeigu level zonoje
//			else if(_fakePlayer.isInsideZone(ZoneId.TOWN) && !_fakePlayer.isInsideZone(ZoneId.PEACE) && _fakePlayer.getNearestTownId() == 15 && _fakePlayer.getLevel() <78){
//				tryTargetMobByLevel();
//				if(_fakePlayer.getTarget() !=null)
//					_fakePlayer.setFakeAi(new LvlUpAI(_fakePlayer));
//			}
		}
	}
}
