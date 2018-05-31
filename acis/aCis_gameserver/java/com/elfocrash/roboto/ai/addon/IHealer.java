package com.elfocrash.roboto.ai.addon;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.CombatAI;
import com.elfocrash.roboto.model.HealingSpell;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.xml.internal.bind.v2.TODO;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;

public interface IHealer {
	
	default void tryTargetingLowestHpTargetInRadius(FakePlayer player, Class<? extends Creature> creatureClass, int radius) {

		if(player.getTarget() == null) {
			List<Creature> targets = player.getKnownTypeInRadius(creatureClass, radius).stream()
					.filter(x->!x.isDead() && x.getParty() != null && x.getParty() != null && x.getParty().containsPlayer(player))
					.collect(Collectors.toList());

			if(!player.isDead())
				targets.add(player);

			List<Creature> sortedTargets = targets.stream().sorted((x1, x2) -> Double.compare(x1.getCurrentHp(), x2.getCurrentHp())).collect(Collectors.toList());

			if(!sortedTargets.isEmpty()) {
				Creature target = sortedTargets.get(0);
				player.setTarget(target);
			}
		}else {
			if(((Creature)player.getTarget()).isDead())
				player.setTarget(null);
		}
	}
	
//	default void tryHealingTarget(FakePlayer player) {
//
//		L2ClanMember[] allClanMembers = player.getClan().getMembers();
//
//		List<Player> deadClanMembers = Arrays.stream(allClanMembers).filter(x->x.getPlayerInstance() instanceof Player).map(x->x.getPlayerInstance()).collect(Collectors.toList()).stream().filter(x->x.isDead()).collect(Collectors.toList());
//		if(deadClanMembers.size() >= 2) {
//			player.getFakeAi().castSpell(player.getSkill(1254));
//		}
//		Collections.shuffle(Arrays.asList(allClanMembers));
//
//		for(int i =0; i < allClanMembers.length; i++) {
//			Player target = allClanMembers[i].getPlayerInstance();
//
//			if (target != null && target.isInsideRadius(player,3500,true,true)) {
//				if (target.isDead()) {
//					player.setTarget(target);
//					player.getFakeAi().castSpell(player.getSkill(1016));
//				}
//				if (player.getFakeAi() instanceof CombatAI) {
//					HealingSpell skill = ((CombatAI) player.getFakeAi()).getRandomAvaiableHealingSpellForTarget();
//					if (skill != null) {
//						switch (skill.getCondition()) {
//							case LESSHPPERCENT:
//								double currentHpPercentage = Math.round(100.0 / target.getMaxHp() * target.getCurrentHp());
//								if (currentHpPercentage <= skill.getConditionValue() && !target.isDead()) {
//									player.setTarget(target);
//									player.getFakeAi().castSpell(player.getSkill(skill.getSkillId()));
//								}
//								break;
//							default:
//								break;
//						}
//
//					}
//				}
//			}
//		}
//
///*		if(player.getTarget() != null && player.getTarget() instanceof Creature)
//		{
//			Creature target = (Creature) player.getTarget();
//			if(player.getFakeAi() instanceof CombatAI) {
//				HealingSpell skill = ((CombatAI)player.getFakeAi()).getRandomAvaiableHealingSpellForTarget();
//				if(skill != null) {
//					switch(skill.getCondition()){
//						case LESSHPPERCENT:
//							double currentHpPercentage = Math.round(100.0 / target.getMaxHp() * target.getCurrentHp());
//							if(currentHpPercentage <= skill.getConditionValue()) {
//								player.getFakeAi().castSpell(player.getSkill(skill.getSkillId()));
//							}
//							break;
//						default:
//							break;
//					}
//
//				}
//			}
//		}*/
//	}

	default void tryPartyHealing(FakePlayer player) {

		List<Player> partyPlayers = player.getParty().getMembers();

		List<Player> deadPartyMembers = partyPlayers.stream().filter(x->x.isDead()).collect(Collectors.toList());
		if(deadPartyMembers.size() >= 2) {
			player.getFakeAi().castSpell(player.getSkill(1254));
		}

		List<Player> lowHpPartyMembers = partyPlayers.stream().filter(x->Math.round(100.0 / x.getMaxHp() * x.getCurrentHp()) < 60.0 && !x.isDead()).collect(Collectors.toList());
		List<Player> veryLowPartyMembers = partyPlayers.stream().filter(x->Math.round(100.0 / x.getMaxHp() * x.getCurrentHp()) < 30.0 && !x.isDead()).collect(Collectors.toList());
		//Balance
		if(lowHpPartyMembers.size()>=3 || veryLowPartyMembers.size()>=1){
			player.getFakeAi().castSpell(player.getSkill(1335));
		}
//		//Greater Group Battle Heal
		if(lowHpPartyMembers.size() >= 2){
			player.getFakeAi().castSpell(player.getSkill(1402));
		}
//
		Collections.shuffle(Arrays.asList(partyPlayers));
		//Greater Battle Heal
		for(int i =0; i < partyPlayers.size(); i++) {
			Player target = partyPlayers.get(i);

			if (target != null && target.isInsideRadius(player,1500,true,true)) {
				if (target.isDead()) {
					player.setTarget(target);
					player.getFakeAi().castSpell(player.getSkill(1016));
				}

				if (player.getFakeAi() instanceof CombatAI) {
					HealingSpell skill = ((CombatAI) player.getFakeAi()).getRandomAvaiableHealingSpellForTarget();
					if (skill != null) {
						switch (skill.getCondition()) {
							case LESSHPPERCENT:
								double currentHpPercentage = Math.round(100.0 / target.getMaxHp() * target.getCurrentHp());
								if (currentHpPercentage <= skill.getConditionValue() && !target.isDead()) {
									player.setTarget(target);
									player.getFakeAi().castSpell(player.getSkill(skill.getSkillId()));
								}
								break;
							default:
								break;
						}

					}
				}
			}
		}
	}
}
