package com.elfocrash.roboto.ai;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.walker.setCommonWalkerAI;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.model.BotSkill;
import com.elfocrash.roboto.model.HealingSpell;
import com.elfocrash.roboto.model.OffensiveSpell;
import com.elfocrash.roboto.model.SupportSpell;

import java.io.Console;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.datatables.AccessLevels;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.usercommandhandlers.OlympiadStat;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.instance.OlympiadManagerNpc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.holder.SkillUseHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.type.ActionType;
import net.sf.l2j.gameserver.model.olympiad.*;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import sun.rmi.runtime.Log;

public abstract class CombatAI extends FakePlayerAI {
	int freezeTestInteration = 0;
	int testLocX = 0;
	//private boolean _ClanDecisionPassed = false;
	public CombatAI(FakePlayer character) {
		super(character);
	}

	protected void tryAttackingUsingMageOffensiveSkill() {
		if (_fakePlayer.getTarget() != null) {
			BotSkill botSkill = getRandomAvaiableMageSpellForTarget();
			if (botSkill == null)
				return;

			L2Skill skill = _fakePlayer.getSkill(botSkill.getSkillId());
			if (skill != null)
				castSpell(skill);
		}
	}

	protected void tryAttackingUsingFighterOffensiveSkill() {
		if (_fakePlayer.getTarget() != null && _fakePlayer.getTarget() instanceof Creature) {
			_fakePlayer.forceAutoAttack((Creature) _fakePlayer.getTarget());
			_fakePlayer.getAI().setIntention(CtrlIntention.ATTACK);
			//_fakePlayer.doAttack((Creature) _fakePlayer.getTarget());
			double value = Rnd.nextDouble();
			if (value < 0.1) {
				if (getOffensiveSpells() != null && !getOffensiveSpells().isEmpty()) {
					L2Skill skill = getRandomAvaiableFighterSpellForTarget();
					if (skill != null && _fakePlayer.getCurrentMp() > 200) {
						castSpell(skill);
					}
				}
			}
			else {
				_fakePlayer.getAI().setIntention(CtrlIntention.ATTACK);
				_fakePlayer.forceAutoAttack((Creature) _fakePlayer.getTarget());
			}
		}
	}

	@Override
	public void thinkAndAct() {
		//Freeze test
//		if(freezeTestInteration == 0)
//			testLocX = _fakePlayer.getX();
//		if(freezeTestInteration <= 7){
//			freezeTestInteration++;
//		}
//		else{
//			if(testLocX == _fakePlayer.getX())
//				_fakePlayer.setFakeAi(new setCommonWalkerAI(_fakePlayer));
//			freezeTestInteration = 0;
//		}
		handleDeath();
		changeBotBehaviorDependsOnZone();
		CheckPartyMembersLocation();
		restoreMP();
		if(_fakePlayer.getLevel() >=79)
		handleCpPots();
		if(_fakePlayer.getClan() == null)
			DoClanActions();

	}

	protected int getShotId() {
		int playerLevel = _fakePlayer.getLevel();
		if (playerLevel < 20)
			return getShotType() == ShotType.SOULSHOT ? 1835 : 3947;
		if (playerLevel >= 20 && playerLevel < 40)
			return getShotType() == ShotType.SOULSHOT ? 1463 : 3948;
		if (playerLevel >= 40 && playerLevel < 52)
			return getShotType() == ShotType.SOULSHOT ? 1464 : 3949;
		if (playerLevel >= 52 && playerLevel < 61)
			return getShotType() == ShotType.SOULSHOT ? 1465 : 3950;
		if (playerLevel >= 61 && playerLevel < 76)
			return getShotType() == ShotType.SOULSHOT ? 1466 : 3951;
		if (playerLevel >= 76)
			return getShotType() == ShotType.SOULSHOT ? 1467 : 3952;

		return 0;
	}

	protected int getArrowId() {
		int playerLevel = _fakePlayer.getLevel();
		if (playerLevel < 20)
			return 17; // wooden arrow
		if (playerLevel >= 20 && playerLevel < 40)
			return 1341; // bone arrow
		if (playerLevel >= 40 && playerLevel < 52)
			return 1342; // steel arrow
		if (playerLevel >= 52 && playerLevel < 61)
			return 1343; // Silver arrow
		if (playerLevel >= 61 && playerLevel < 76)
			return 1344; // Mithril Arrow
		if (playerLevel >= 76)
			return 1345; // shining

		return 0;
	}

	protected void handleShots() {
		if (_fakePlayer.getInventory().getItemByItemId(getShotId()) != null) {
			if (_fakePlayer.getInventory().getItemByItemId(getShotId()).getCount() <= 20) {
				_fakePlayer.getInventory().addItem("", getShotId(), 500, _fakePlayer, null);
			}
		} else {
			_fakePlayer.getInventory().addItem("", getShotId(), 500, _fakePlayer, null);
		}

//		if (_fakePlayer.getAutoSoulShot().isEmpty()) {
			_fakePlayer.addAutoSoulShot(getShotId());
			_fakePlayer.rechargeShots(true, true);
//		}
	}

	protected void handleCpPots() {
		if(Rnd.nextDouble() <= 0.3 && !_fakePlayer.isInsideZone(ZoneId.TOWN)) {
			if (_fakePlayer.getInventory().getItemByItemId(5592) != null) {
				if (_fakePlayer.getInventory().getItemByItemId(5592).getCount() <= 20) {
					_fakePlayer.getInventory().addItem("", 5592, 500, _fakePlayer, null);
				}
			} else {
				_fakePlayer.getInventory().addItem("", 5592, 500, _fakePlayer, null);
			}

			if (_fakePlayer.getCurrentCp() < _fakePlayer.getMaxCp() * 0.9) {
				ItemInstance item = _fakePlayer.getInventory().getItemByItemId(5592);
				IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getEtcItem());
				if (handler != null)
					handler.useItem(_fakePlayer, item, false);

			}
		}
	}

	protected void handelSpiritOre() {
		if (_fakePlayer.getInventory().getItemByItemId(3031) != null) {
			if (_fakePlayer.getInventory().getItemByItemId(3031).getCount() <= 100) {
				_fakePlayer.getInventory().addItem("", 3031, 500, _fakePlayer, null);
			}
		} else {
			_fakePlayer.getInventory().addItem("", 3031, 500, _fakePlayer, null);
		}
	}

	protected void restoreMP() {
		if (_fakePlayer.getCurrentMp() < 200) {
			_fakePlayer.setCurrentMp(_fakePlayer.getMaxMp());
		}
	}

	public HealingSpell getRandomAvaiableHealingSpellForTarget() {

		if (getHealingSpells().isEmpty())
			return null;

		List<HealingSpell> spellsOrdered = getHealingSpells().stream().sorted((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority())).collect(Collectors.toList());
		int skillListSize = spellsOrdered.size();
		BotSkill skill = waitAndPickAvailablePrioritisedSpell(spellsOrdered, skillListSize);
		return (HealingSpell) skill;
	}

	protected BotSkill getRandomAvaiableMageSpellForTarget() {
		if (getOffensiveSpells().isEmpty()) {
			return null;
		}

		List<OffensiveSpell> spellsOrdered = getOffensiveSpells().stream().sorted((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority())).collect(Collectors.toList());
		int skillListSize = spellsOrdered.size();
		BotSkill skill = waitAndPickAvailablePrioritisedSpell(spellsOrdered, skillListSize);

		return skill;
	}

	private BotSkill waitAndPickAvailablePrioritisedSpell(List<? extends BotSkill> spellsOrdered, int skillListSize) {
		int skillIndex = 0;
		BotSkill botSkill = spellsOrdered.get(skillIndex);
		//neaiski vieta
		try {
			_fakePlayer.getCurrentSkill().setCtrlPressed(!_fakePlayer.getTarget().isInsideZone(ZoneId.PEACE));
		} catch (Exception ex) {
			//_fakePlayer.forceAutoAttack((Creature)_fakePlayer.getTarget());
			return null;
		}

		L2Skill skill = _fakePlayer.getSkill(botSkill.getSkillId());
		if (skill != null && skill.getCastRange() > 0) {
			if (!GeoEngine.getInstance().canSeeTarget(_fakePlayer, _fakePlayer.getTarget())) {
				moveToPawn(_fakePlayer.getTarget(), 100);//skill.getCastRange()
				return null;
			}
		}

		while (!_fakePlayer.checkUseMagicConditions(skill, true, false)) {
			_isBusyThinking = true;
			if (_fakePlayer.isDead() || _fakePlayer.isOutOfControl()) {
				return null;
			}
			if ((skillIndex < 0) || (skillIndex >= skillListSize)) {
				return null;
			}
			skill = _fakePlayer.getSkill(spellsOrdered.get(skillIndex).getSkillId());
			botSkill = spellsOrdered.get(skillIndex);
			skillIndex++;
		}
		return botSkill;
	}

	protected L2Skill getRandomAvaiableFighterSpellForTarget() {
		List<OffensiveSpell> spellsOrdered = getOffensiveSpells().stream().sorted(Comparator.comparingInt(BotSkill::getPriority)).collect(Collectors.toList());
		int skillIndex = 0;
		int skillListSize = spellsOrdered.size();

		L2Skill skill = _fakePlayer.getSkill(spellsOrdered.get(skillIndex).getSkillId());

		_fakePlayer.getCurrentSkill().setCtrlPressed(!_fakePlayer.getTarget().isInsideZone(ZoneId.PEACE));
		while (!_fakePlayer.checkUseMagicConditions(skill, true, false)) {
			if ((skillIndex < 0) || (skillIndex >= skillListSize)) {
				return null;
			}
			skill = _fakePlayer.getSkill(spellsOrdered.get(skillIndex).getSkillId());
			skillIndex++;
		}

		if (!_fakePlayer.checkUseMagicConditions(skill, true, false)) {
			_fakePlayer.forceAutoAttack((Creature) _fakePlayer.getTarget());
			return null;
		}

		return skill;
	}

	protected void selfSupportBuffs() {
		List<Integer> activeEffects = Arrays.stream(_fakePlayer.getAllEffects())
				.map(x -> x.getSkill().getId())
				.collect(Collectors.toList());

		for (SupportSpell selfBuff : getSelfSupportSpells()) {
			if (activeEffects.contains(selfBuff.getSkillId()))
				continue;

			L2Skill skill = SkillTable.getInstance().getInfo(selfBuff.getSkillId(), _fakePlayer.getSkillLevel(selfBuff.getSkillId()));

			if (!_fakePlayer.checkUseMagicConditions(skill, true, false))
				continue;

			switch (selfBuff.getCondition()) {
				case LESSHPPERCENT:
					if (Math.round(100.0 / _fakePlayer.getMaxHp() * _fakePlayer.getCurrentHp()) <= selfBuff.getConditionValue()) {
						castSelfSpell(skill);
					}
					break;
				case MISSINGCP:
					if (getMissingHealth() >= selfBuff.getConditionValue()) {
						castSelfSpell(skill);
					}
					break;
				case NONE:
					castSelfSpell(skill);
				default:
					break;
			}

		}
	}

	private void DoClanActions(){
		if(_fakePlayer.getLevel() >=78 && !_fakePlayer.getClanDecissionCondition()) {
			_fakePlayer.setClanDecisionPassed();
			Collection<L2Clan> clans = ClanTable.getInstance().getClans().stream().filter(x->x.IsClanFake()).collect(Collectors.toList());
			if (clans.size() == 0) {
				String clanName = FakeHelpers.getRandomClanName();
				L2Clan existing = ClanTable.getInstance().getClanByName(clanName);
				if (existing == null) {
					L2Clan clan = ClanTable.getInstance().createClan(_fakePlayer, clanName.toString());
					clan.SetFakeClan(true);
					_fakePlayer.setTitle(clanName);
					int id = CrestCache.getInstance().getRandomCrestId();
					clan.setLevel(5);
					clan.changeClanCrest(id);
				}
			}
			else if (clans.size()>=1) {
				List<L2Clan> tempClans = ClanTable.getInstance().getClans().stream().filter(x->x.IsClanFake()).collect(Collectors.toList());
				L2Clan oneClan = tempClans.get(Rnd.get(0, tempClans.size() - 1));

				if (Rnd.nextDouble() <= 0.5) {
					_fakePlayer.setClan(oneClan);
					oneClan.addClanMember(_fakePlayer);
					_fakePlayer.setTitle(oneClan.getName());
				} else {
					if (Rnd.nextDouble() <= 0.5) {
						String clanName = FakeHelpers.getRandomClanName();
						L2Clan existing = ClanTable.getInstance().getClanByName(clanName);
						if (existing == null) {
							L2Clan clan = ClanTable.getInstance().createClan(_fakePlayer, clanName);
							_fakePlayer.setTitle(clanName);
							int id = CrestCache.getInstance().getRandomCrestId();
							clan.SetFakeClan(true);
							clan.setLevel(5);
							clan.changeClanCrest(id);
						}
					}
				}
			}
			_fakePlayer.broadcastUserInfo();
			System.out.println("Player: " + _fakePlayer.getName() + " join clan: " + (_fakePlayer.getClan() !=null? _fakePlayer.getClan().getName():" no clan"));
			_fakePlayer.setFakeAi(new setCommonWalkerAI(_fakePlayer));

		}

	}


	public void JoinOlympiad() {
		//Check if oly online, random 10%, if nobles
		if (Olympiad.getInstance().inCompPeriod() && Rnd.nextDouble() <= 0.1 && _fakePlayer.isNoble()) {

			OlympiadManager.getInstance().registerNoble(_fakePlayer,CompetitionType.NON_CLASSED);
		}
	}

	private double getMissingHealth() {
		return _fakePlayer.getMaxCp() - _fakePlayer.getCurrentCp();
	}

	protected double changeOfUsingSkill() {
		return 0.3;
	}

	protected abstract ShotType getShotType();

	protected abstract List<OffensiveSpell> getOffensiveSpells();

	protected abstract List<HealingSpell> getHealingSpells();

	protected abstract List<SupportSpell> getSelfSupportSpells();

	private void CheckPartyMembersLocation(){
		if(_fakePlayer.getParty()!=null){
			List<Player> partyPlayers = _fakePlayer.getParty().getMembers().stream().filter(x->x.getObjectId() !=
					_fakePlayer.getObjectId()).collect(Collectors.toList());
			Collections.sort(partyPlayers,(Creature x1, Creature x2) -> Double.compare(MathUtil.calculateDistance(_fakePlayer,x1,true),
					MathUtil.calculateDistance(_fakePlayer,x2,true)));
			Player awayPlayer = partyPlayers.get(partyPlayers.size()-1);
			if(MathUtil.calculateDistance(_fakePlayer,awayPlayer,true) > 1200 && MathUtil.calculateDistance(_fakePlayer,awayPlayer,true) <= 2000 ){
				//moveToPawn(awayPlayer,500);
				moveTo(awayPlayer.getX() + Rnd.get(-100,100),awayPlayer.getY()+ Rnd.get(-100,100),awayPlayer.getZ());
			}
		}
	}
}
