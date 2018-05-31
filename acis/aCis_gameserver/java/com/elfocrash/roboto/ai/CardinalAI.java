package com.elfocrash.roboto.ai;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.ai.addon.IHealer;
import com.elfocrash.roboto.helpers.FakeHelpers;
import com.elfocrash.roboto.model.HealingSpell;
import com.elfocrash.roboto.model.OffensiveSpell;
import com.elfocrash.roboto.model.SpellUsageCondition;
import com.elfocrash.roboto.model.SupportSpell;

import java.util.*;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.group.Party;

import javax.management.Query;

/**
 * @author Elfocrash
 *
 */
public class CardinalAI extends CombatAI implements IHealer {
	public CardinalAI(FakePlayer character) {
		super(character);
	}

	@Override
	public void thinkAndAct() {
		super.thinkAndAct();
		setBusyThinking(true);
		applyDefaultBuffs();
		handleShots();
		handelSpiritOre();
		restoreMP();
		tryToJoinParty();
		tryTargetingLowestHpTargetInRadius(_fakePlayer, FakePlayer.class, FakeHelpers.getTestTargetRange());
		//tryHealingTarget(_fakePlayer);
		if(_fakePlayer.getParty()!=null)
			tryPartyHealing(_fakePlayer);
	setBusyThinking(false);
}

	@Override
	protected ShotType getShotType() {
		return ShotType.BLESSED_SPIRITSHOT;
	}

	@Override
	protected List<OffensiveSpell> getOffensiveSpells() {
		return Collections.emptyList();
	}

	@Override
	protected List<HealingSpell> getHealingSpells() {
		List<HealingSpell> _healingSpells = new ArrayList<>();
		_healingSpells.add(new HealingSpell(1218, SkillTargetType.TARGET_ONE, 70, 1));
		_healingSpells.add(new HealingSpell(1217, SkillTargetType.TARGET_ONE, 80, 3));
		return _healingSpells;
	}

	@Override
	protected int[][] getBuffs() {
		return FakeHelpers.getMageBuffs();
	}

	@Override
	protected List<SupportSpell> getSelfSupportSpells() {
		return Collections.emptyList();
	}

	private void tryToJoinParty() {
		int partySize = Rnd.get(2, 9);
		if (_fakePlayer.getParty() == null || (_fakePlayer.getParty() != null && _fakePlayer.getParty().getMembers().size() <= 4)) {
			if (_fakePlayer.getClan() != null) {
				List<Player> clanMembersWithoutParty = Arrays.stream(_fakePlayer.getClan().getMembers()).filter(x -> x.getPlayerInstance() instanceof FakePlayer).map(x -> x.getPlayerInstance()).collect(Collectors.toList()).stream().filter(x -> x.getParty() == null && x.getObjectId() != _fakePlayer.getObjectId()).collect(Collectors.toList());

				Queue<Player>  playerQueue = new LinkedList<>();
				for(int i =0; i <= clanMembersWithoutParty.size()-1; i++){
					playerQueue.add(clanMembersWithoutParty.get(i));
				}
				if(playerQueue.size()>=1){
					Player p1 = playerQueue.poll();
					if(_fakePlayer.getParty() == null){
						_fakePlayer.setParty(new Party(_fakePlayer,p1, Party.LootRule.ITEM_RANDOM));
					}
					while (_fakePlayer.getParty().getMembers().size()<=4 && playerQueue.size()!=0){
						Player p2 = playerQueue.poll();
						if(p2!=null){
							_fakePlayer.getParty().addPartyMember(p2);
						}
					}
				}
			} else {
//				List<Player> playerWithoutClan = _fakePlayer.getKnownTypeInRadius(FakePlayer.class, 1000).stream().filter(x -> x.getParty() == null && x.getClan() == null).collect(Collectors.toList());
//                doPartyInvitationActions(playerWithoutClan,partySize);
			}
		}
	}

	protected void doPartyInvitationActions(List<Player> playersList, int partySize){
        while (playersList.iterator().hasNext()) {
            Player p = playersList.iterator().next();
            if (_fakePlayer.getParty() == null && p != null) {
                _fakePlayer.setParty(new Party(_fakePlayer, p, Party.LootRule.ITEM_RANDOM));
            } else if (_fakePlayer.getParty().getMembers().size() <= partySize && p != null) {
                _fakePlayer.getParty().addPartyMember(p);
            }
        }
    }
}

