package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.helpers.ArmorHelper;
import com.elfocrash.roboto.helpers.Enums.ItemGrade;
import com.elfocrash.roboto.helpers.Enums.TownIds;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.helpers.StandingImitation;
import com.elfocrash.roboto.helpers.ZoneChecker;
import com.elfocrash.roboto.model.WalkNode;
import com.elfocrash.roboto.model.WalkerType;
import java.util.*;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2TeleportLocation;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Gatekeeper;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
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

	@Override
	public void setup() {
		super.setup();
		_walkNodes = new LinkedList<>();
		setWalkNodes(_fakePlayer.getNearestTownId());
	}

	@Override
	public void thinkAndAct() {
		_fakePlayer.broadcastUserInfo();
		giranStandingImitation();
		if (ZoneChecker.checkIfInRainboSprings(_fakePlayer)) {
			_fakePlayer.setFakeAi(new RainbowWalkerAi(_fakePlayer));
		}
		if(_fakePlayer.getClan() == null){
			checkIfAbleToCreateAClan();
		}
		setBusyThinking(true);

		if (freezeTestInteration == 0)
			testLocX = _fakePlayer.getX();
		handleDeath();
		teleportToZone();
		freezeTest();
		if (_walkNodes.isEmpty())
			return;

		if (isWalking) {
			//Galbut nutraukti ejima ir pradeti musti
			cancelWalkingStartAttack();

			//Default behaviour, after it reaches final destination
			if (userReachedDestination(_currentWalkNode)) {
				if (currentStayIterations < _currentWalkNode.getStayIterations()) {
					currentStayIterations++;
					_fakePlayer.updateAndBroadcastStatus(1);
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
					_fakePlayer.destinationWalkNode = _currentWalkNode;
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
			if (_fakePlayer.getLevel() >= 78) {
				List<L2TeleportLocation> locations = new ArrayList<>();

				L2TeleportLocation locEvaGarden = new L2TeleportLocation();
				locEvaGarden.setLocX(85576 + Rnd.get(-1000, 1000));
				locEvaGarden.setLocY(257000 + Rnd.get(-1000, 1000));
				locEvaGarden.setLocZ(-11664);


				L2TeleportLocation giranFarmLoc = TeleportLocationTable.getInstance().getTemplate(1060);
				locations.add(giranFarmLoc);
				locations.add(locEvaGarden);

				L2TeleportLocation whereToGo = locations.get(Rnd.get(0, locations.size() - 1));
				if (_fakePlayer.getFakeAi().teleportToLocation(whereToGo.getLocX(), whereToGo.getLocY(), whereToGo.getLocZ(), 20)) {
					if(ZoneChecker.checkIfInLoa(_fakePlayer)) {
						_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
					}
					if (ZoneChecker.checkIfInEvaGarden(_fakePlayer)) {
						_fakePlayer.assignDefaultAI();
					}
				}
			}
			//Level up zone
			else if (_fakePlayer.getLevel() < 78) {
				if (_fakePlayer.getFakeAi().teleportToLocation(141240, -124216, -1864, 20)) {
					_fakePlayer.setFakeAi(new CommonWalkerAi(_fakePlayer));
				}
			}
			//Giran
			else if (_fakePlayer.getLevel() >= 78 && ZoneChecker.checkIfInRainboSprings(_fakePlayer)) {
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
		//Mob targinimas
		else if (!_fakePlayer.isInsideZone(ZoneId.TOWN) && _fakePlayer.getLevel() >=78) {
			_fakePlayer.assignDefaultAI();
		}
	}

	private void cancelWalkingStartAttack(){
		if (Rnd.nextDouble() <= 0.05 && ZoneChecker.checkIfInLoa(_fakePlayer)) {
				tryTargetRandomCreatureByTypeInRadius(FakeHelpers.getTestTargetClass(), 2000);
				if(_fakePlayer.getTarget() != null){
					_fakePlayer.assignDefaultAI();
				}

		}
	}

	private void giranStandingImitation(){
		if(ZoneChecker.checkIfInGiran(_fakePlayer) && ZoneChecker.checkIfInSquare(_fakePlayer,
				StandingImitation.GetGiranSquareForStandingImitation()) && Rnd.nextDouble() < 0.1){
			if(currentStayIterations < 10){
				_fakePlayer.stopMove(null);
				currentStayIterations++;
				return;
			}
			else{
			currentStayIterations = 0;
			isWalking =false;
			_currentWalkNode = null;
			}
		}
	}

	private void checkIfAbleToCreateAClan(){
		// TODO: Nepamirsti ideti random
		if(_fakePlayer.getLevel() == 80 && checkIfSGradeGear() && Rnd.get(0,100) == 50){
			Collection<L2Clan> clans = ClanTable.getInstance().getClans().stream().filter(x->x.IsClanFake()).collect(Collectors.toList());
			if(clans.size() < 10){
				createNewClan();
			} else{
				return;
			}
		}

	}

	private boolean checkIfSGradeGear(){
		List<Integer> allSGradeItems = new ArmorHelper().getAllOneGradeGear(ItemGrade.Grade.S);
		for(ItemInstance item :_fakePlayer.getInventory().getAllEquipedItems()){
			if(allSGradeItems.contains(item.getItemId())){
				return true;
			}
		}
		return false;
	}

	private void createNewClan(){
		String clanName = FakeHelpers.getRandomClanName();
		L2Clan existing = ClanTable.getInstance().getClanByName(clanName);
		if (existing == null) {
			L2Clan clan = ClanTable.getInstance().createClan(_fakePlayer, clanName);
			if(clan != null){
				clan.SetFakeClan(true);
				_fakePlayer.setTitle(clanName);
				int id = CrestCache.getInstance().getRandomCrestId();
				clan.setLevel(5);
				clan.changeClanCrest(id);
			}

		}
	}
}
