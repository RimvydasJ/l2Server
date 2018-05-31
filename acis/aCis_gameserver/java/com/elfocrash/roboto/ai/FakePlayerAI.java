package com.elfocrash.roboto.ai;

import com.elfocrash.roboto.FakePlayer;

import java.util.*;
import java.util.stream.Collectors;

import com.elfocrash.roboto.ai.walker.setCommonWalkerAI;
import com.elfocrash.roboto.helpers.FakeHelpers;
import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportType;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.*;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.*;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.model.zone.type.L2TownZone;
import net.sf.l2j.gameserver.network.serverpackets.MoveToLocation;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.network.serverpackets.StopRotation;
import net.sf.l2j.gameserver.network.serverpackets.TeleportToLocation;
import net.sf.l2j.gameserver.templates.skills.L2EffectType;

/**
 * @author Elfocrash
 *
 */
public abstract class FakePlayerAI {
	protected final FakePlayer _fakePlayer;
	protected volatile boolean _clientMoving;
	protected volatile boolean _clientAutoAttacking;
	private long _moveToPawnTimeout;
	protected int _clientMovingToPawnOffset;
	protected boolean _isBusyThinking = false;
	protected int iterationsOnDeath = 0;
	protected int iterationsOnDeath2 = 0;
	private final int toVillageIterationsOnDeath = 10;



	public FakePlayerAI(FakePlayer character) {
		_fakePlayer = character;
		setup();
		applyDefaultBuffs();

	}

	public void setup() {
		_fakePlayer.setIsRunning(true);
	}

	protected void applyDefaultBuffs() {
		for (int[] buff : getBuffs()) {
			try {
				Map<Integer, L2Effect> activeEffects = Arrays.stream(_fakePlayer.getAllEffects())
						.filter(x -> x.getEffectType() == L2EffectType.BUFF)
						.collect(Collectors.toMap(x -> x.getSkill().getId(), x -> x));

				if (!activeEffects.containsKey(buff[0]))
					SkillTable.getInstance().getInfo(buff[0], buff[1]).getEffects(_fakePlayer, _fakePlayer);
				else {
					if ((activeEffects.get(buff[0]).getPeriod() - activeEffects.get(buff[0]).getTime()) <= 20) {
						SkillTable.getInstance().getInfo(buff[0], buff[1]).getEffects(_fakePlayer, _fakePlayer);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void handleDeath() {
		if(_fakePlayer.isDead()) {
			if (_fakePlayer.isReviveRequested())
				_fakePlayer.doRevive();
			if (_fakePlayer.getParty() != null) {
				boolean leaderIsDead = _fakePlayer.getParty().getLeader().isDead();
				if (!leaderIsDead && MathUtil.calculateDistance(_fakePlayer, _fakePlayer.getParty().getLeader(), true) < 2000) {
					return;
				}
			}

			if (_fakePlayer.isDead()) {
				if (iterationsOnDeath >= toVillageIterationsOnDeath) {
					toVillageOnDeath();
				}
				iterationsOnDeath++;
				return;
			}
			iterationsOnDeath = 0;

		}
	}

	public void setBusyThinking(boolean thinking) {
		_isBusyThinking = thinking;
	}

	public boolean isBusyThinking() {
		return _isBusyThinking;
	}

	public boolean teleportToLocation(int x, int y, int z, int randomOffset) {
		_fakePlayer.stopMove(null);
		_fakePlayer.abortAttack();
		_fakePlayer.abortCast();
		_fakePlayer.setIsTeleporting(true);
		_fakePlayer.setTarget(null);
		_fakePlayer.getAI().setIntention(CtrlIntention.ACTIVE);
		if (randomOffset > 0) {
			x += Rnd.get(-randomOffset, randomOffset);
			y += Rnd.get(-randomOffset, randomOffset);
		}
		z += 5;
		_fakePlayer.broadcastPacket(new TeleportToLocation(_fakePlayer, x, y, z));
		_fakePlayer.decayMe();
		_fakePlayer.setXYZ(x, y, z);
		_fakePlayer.onTeleported();
		_fakePlayer.revalidateZone(true);

		return true;
	}

	protected void tryTargetRandomCreatureByTypeInRadius(Class<? extends Creature> creatureClass, int radius) {
		if (_fakePlayer.getTarget() == null) {

			List<Creature> raidTargets = _fakePlayer.getKnownTypeInRadius(creatureClass, radius).stream().filter(x->x.getAI().getIntention()== CtrlIntention.ATTACK && x.isRaid()).collect(Collectors.toList());
			List<Creature> targets = _fakePlayer.getKnownTypeInRadius(creatureClass, radius).stream().filter(x -> !x.isDead() && !x.isRaid()).collect(Collectors.toList());
			if(!raidTargets.isEmpty()) {
				Creature target = raidTargets.get(Rnd.get(0, raidTargets.size() - 1));
				tryTargetPlayerInPvp();
				_fakePlayer.setTarget(target);
			}
			else if (!targets.isEmpty()){
				Creature target = targets.get(Rnd.get(0, targets.size() - 1));
				_fakePlayer.setTarget(target);
			}
		} else {
			if (((Creature) _fakePlayer.getTarget()).isDead())
				_fakePlayer.setTarget(null);
		}
	}

	protected void tryTargetMobByLevel(){
		int level = _fakePlayer.getLevel();
		if(_fakePlayer.getTarget() == null){
			List<Creature> targets  = _fakePlayer.getKnownTypeInRadius(Monster.class,2000).stream().filter(x-> x.getLevel() >= level-9 && x.getLevel() < level+9 && !x.isDead()).collect(Collectors.toList());
			Collections.sort(targets,(Creature x1, Creature x2) -> Double.compare(MathUtil.calculateDistance(_fakePlayer,x1,true),MathUtil.calculateDistance(_fakePlayer,x2,true)));
			//Collections.reverse(targets);
			if(!targets.isEmpty()){
				try {
					Creature target = targets.get(Rnd.get(0, targets.size()-1));
					_fakePlayer.setTarget(target);
				}
				catch (Exception e){}

			}
		} else {
			if (((Creature) _fakePlayer.getTarget()).isDead())
				_fakePlayer.setTarget(null);
		}
	}

	protected void tryTargetPlayerInPvp() {
		WorldObject p = _fakePlayer.getTarget();
		if (_fakePlayer.getTarget() != null && _fakePlayer.getTarget() instanceof Player && (((Player) _fakePlayer.getTarget()).getPvpFlag() == 0 && ((Player) _fakePlayer.getTarget()).getKarma() == 0)) {
			_fakePlayer.setTarget(null);
			_fakePlayer.getAI().setIntention(CtrlIntention.ACTIVE);
			//TODO: fix this

		}
		if(Rnd.nextDouble() <= 0.3) {
			if (_fakePlayer.getTarget() == null) {
				List<Player> pvpTarget = _fakePlayer.getKnownTypeInRadius(Player.class, 1500).stream().filter(x -> (x.getPvpFlag() == 1 || x.getKarma() > 0) && (x.getClan() == null || (_fakePlayer.getClan() != null && x.getClan().getClanId() != _fakePlayer.getClan().getClanId()))).collect(Collectors.toList());
				if (!pvpTarget.isEmpty()) {
					try {
						Player target = pvpTarget.get(Rnd.get(0, pvpTarget.size()-1));
						_fakePlayer.setTarget(target);
					}
					catch (Exception e){}
				}
			}
		}
	}

	protected void tryTargetPlayerInToPk() {
		if(_fakePlayer.getLevel() >=79) {
			if (Rnd.nextDouble() <= 0.01) {
				//_fakePlayer.setTarget(null);
				if (_fakePlayer.getTarget() == null) {
					List<Player> pvpTarget = _fakePlayer.getKnownTypeInRadius(Player.class, 1000).stream().filter(x -> !x.isDead() && (x.getClan() == null  || (_fakePlayer.getClan() != null && x.getClan().getClanId() != _fakePlayer.getClan().getClanId()))).collect(Collectors.toList());
					if (!pvpTarget.isEmpty()) {
						try {
							Player target = pvpTarget.get(Rnd.get(0, pvpTarget.size()-1));
							_fakePlayer.setTarget(target);
						}
						catch (Exception e){}
					}
				}
			}
		}
	}

	public void castSpell(L2Skill skill) {
		if (!_fakePlayer.isCastingNow()) {

			if (skill.getTargetType() == SkillTargetType.TARGET_GROUND) {
				if (maybeMoveToPosition((_fakePlayer).getCurrentSkillWorldPosition(), skill.getCastRange())) {
					_fakePlayer.setIsCastingNow(false);
					return;
				}
			} else {
				if (checkTargetLost(_fakePlayer.getTarget())) {
					if (skill.isOffensive() && _fakePlayer.getTarget() != null)
						_fakePlayer.setTarget(null);

					_fakePlayer.setIsCastingNow(false);
					return;
				}

				if (_fakePlayer.getTarget() != null) {
					if (maybeMoveToPawn(_fakePlayer.getTarget(), skill.getCastRange())) {
						return;
					}
				}

				if (_fakePlayer.isSkillDisabled(skill)) {
					return;
				}
			}

			if (skill.getHitTime() > 50 && !skill.isSimultaneousCast())
				clientStopMoving(null);

			_fakePlayer.doCast(skill);
		} else {
			_fakePlayer.forceAutoAttack((Creature) _fakePlayer.getTarget());
		}
	}

	protected void castSelfSpell(L2Skill skill) {
		if (!_fakePlayer.isCastingNow() && !_fakePlayer.isSkillDisabled(skill)) {


			if (skill.getHitTime() > 50 && !skill.isSimultaneousCast())
				clientStopMoving(null);

			_fakePlayer.doCast(skill);
		}
	}

	protected void toVillageOnDeath() {
		Location location = MapRegionTable.getInstance().getLocationToTeleport(_fakePlayer, TeleportType.TOWN);

		location  = MapRegionTable.getTown(9).getSpawnLoc();

		if (_fakePlayer.isDead())
			_fakePlayer.doRevive();

		_fakePlayer.getFakeAi().teleportToLocation(location.getX(), location.getY(), location.getZ(), 20);

	}

	protected void clientStopMoving(SpawnLocation loc) {
		if (_fakePlayer.isMoving())
			_fakePlayer.stopMove(loc);

		_clientMovingToPawnOffset = 0;

		if (_clientMoving || loc != null) {
			_clientMoving = false;

			_fakePlayer.broadcastPacket(new StopMove(_fakePlayer));

			if (loc != null)
				_fakePlayer.broadcastPacket(new StopRotation(_fakePlayer.getObjectId(), loc.getHeading(), 0));
		}
	}

	protected boolean checkTargetLost(WorldObject target) {
		if (target instanceof Player) {
			final Player victim = (Player) target;
			if (victim.isFakeDeath()) {
				victim.stopFakeDeath(true);
				return false;
			}
		}

		if (target == null) {
			_fakePlayer.getAI().setIntention(CtrlIntention.ACTIVE);
			return true;
		}
		return false;
	}

	protected boolean maybeMoveToPosition(Location worldPosition, int offset) {
		if (worldPosition == null) {
			return false;
		}

		if (offset < 0)
			return false;

		if (!_fakePlayer.isInsideRadius(worldPosition.getX(), worldPosition.getY(), (int) (offset + _fakePlayer.getCollisionRadius()), false)) {
			if (_fakePlayer.isMovementDisabled())
				return true;

			int x = _fakePlayer.getX();
			int y = _fakePlayer.getY();

			double dx = worldPosition.getX() - x;
			double dy = worldPosition.getY() - y;

			double dist = Math.sqrt(dx * dx + dy * dy);

			double sin = dy / dist;
			double cos = dx / dist;

			dist -= offset - 5;

			x += (int) (dist * cos);
			y += (int) (dist * sin);

			moveTo(x, y, worldPosition.getZ());
			return true;
		}

		return false;
	}

	protected void moveToPawn(WorldObject pawn, int offset) {
		if (!_fakePlayer.isMovementDisabled()) {
			if (offset < 10)
				offset = 10;

			boolean sendPacket = true;
			if (_clientMoving && (_fakePlayer.getTarget() == pawn)) {
				if (_clientMovingToPawnOffset == offset) {
					if (System.currentTimeMillis() < _moveToPawnTimeout)
						return;

					sendPacket = false;
				} else if (_fakePlayer.isOnGeodataPath()) {
					if (System.currentTimeMillis() < _moveToPawnTimeout + 1000)
						return;
				}
			}

			_clientMoving = true;
			_clientMovingToPawnOffset = offset;
			_fakePlayer.setTarget(pawn);
			_moveToPawnTimeout = System.currentTimeMillis() + 1000;

			if (pawn == null)
				return;

			_fakePlayer.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);

			if (!_fakePlayer.isMoving()) {
				return;
			}

			if (pawn instanceof Creature) {
				if (_fakePlayer.isOnGeodataPath()) {
					_fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));
					_clientMovingToPawnOffset = 0;
				} else if (sendPacket)
					_fakePlayer.broadcastPacket(new MoveToPawn(_fakePlayer, pawn, offset));
			} else
				_fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));
		}
	}

	public void moveTo(int x, int y, int z) {

		if (!_fakePlayer.isMovementDisabled()) {
			_clientMoving = true;
			_clientMovingToPawnOffset = 0;
			_fakePlayer.moveToLocation(x, y, z, 0);

			_fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));

		}
	}

	protected boolean maybeMoveToPawn(WorldObject target, int offset) {

		if (target == null || offset < 0)
			return false;

		offset += _fakePlayer.getCollisionRadius();
		if (target instanceof Creature)
			offset += ((Creature) target).getCollisionRadius();

		if (!_fakePlayer.isInsideRadius(target, offset, false, false)) {
			if (_fakePlayer.isMovementDisabled()) {
				if (_fakePlayer.getAI().getIntention() == CtrlIntention.ATTACK)
					_fakePlayer.getAI().setIntention(CtrlIntention.IDLE);
				return true;
			}

			if (target instanceof Creature && !(target instanceof Door)) {
				if (((Creature) target).isMoving())
					offset -= 30;

				if (offset < 5)
					offset = 5;
			}

			moveToPawn(target, offset);

			return true;
		}

		if (!GeoEngine.getInstance().canSeeTarget(_fakePlayer, _fakePlayer.getTarget())) {
			_fakePlayer.setIsCastingNow(false);
			moveToPawn(target, 50);
			return true;
		}

		return false;
	}

	public void changeBotBehaviorDependsOnZone(){
		//Is in TOWN
		//TODO: possible its not needed
		if(iterationsOnDeath2>=15) {
			if((_fakePlayer.getFakeAi() instanceof setCommonWalkerAI)) {
				moveTo(_fakePlayer.getX() + Rnd.get(-350, 350), _fakePlayer.getY() + Rnd.get(-350, 350), _fakePlayer.getZ());
			}
			if (_fakePlayer.isInsideZone(ZoneId.TOWN)) {

				_fakePlayer.setFakeAi(new setCommonWalkerAI(_fakePlayer));
				//System.out.println(_fakePlayer.getName() + " is now WALKER " + _fakePlayer.getFakeAi().getClass().getName() );
			}
//			else if(!_fakePlayer.isInsideZone(ZoneId.TOWN) && _fakePlayer.getLevel() < 78 && _fakePlayer.getFakeAi().getClass() != LvlUpAI.class){
//				_fakePlayer.setFakeAi(new setCommonWalkerAI(_fakePlayer));
//			}
			else {
				//System.out.println(_fakePlayer.getName() +"  "+ _fakePlayer.isInsideZone(ZoneId.TOWN) + " " + _fakePlayer.getFakeAi().getClass().getName() + "---------------------");
			}
			iterationsOnDeath2 = 0;
		}

		//TODO:Implement remaining behaviors
		iterationsOnDeath2++;
	}


	public abstract void thinkAndAct();


	protected abstract int[][] getBuffs();
}
