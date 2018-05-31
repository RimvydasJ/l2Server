package com.elfocrash.roboto.ai;

        import com.elfocrash.roboto.FakePlayer;
        import com.elfocrash.roboto.ai.addon.IConsumableSpender;
        import com.elfocrash.roboto.ai.walker.setCommonWalkerAI;
        import com.elfocrash.roboto.helpers.FakeHelpers;
        import com.elfocrash.roboto.model.HealingSpell;
        import com.elfocrash.roboto.model.OffensiveSpell;
        import com.elfocrash.roboto.model.SupportSpell;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import java.util.stream.Collectors;

        import net.sf.l2j.commons.random.Rnd;
        import net.sf.l2j.gameserver.model.L2Skill;
        import net.sf.l2j.gameserver.model.ShotType;
        import net.sf.l2j.gameserver.model.item.type.CrystalType;

/**
 * @author Elfocrash
 *
 */
public class LvlUpAI extends CombatAI implements IConsumableSpender
{
    private boolean isJustCreatedAndNeedsToWait = true;
    private int iteration = 0;
    private int iterationsToWait = Rnd.get(8,20);
    public LvlUpAI(FakePlayer character)
    {
        super(character);
    }

    @Override
    public void thinkAndAct()
    {
        super.thinkAndAct();
        setBusyThinking(true);
//        if(isJustCreatedAndNeedsToWait){
//            if (iteration <= iterationsToWait) {
//                iteration++;
//            } else {
//                isJustCreatedAndNeedsToWait = false;
//            }
//        }
        //if(!isJustCreatedAndNeedsToWait) {
            GiveItems(_fakePlayer);
            handleConsumable(_fakePlayer, getArrowId());
            if (_fakePlayer.getLevel() < 78) {
                applyDefaultBuffs();
                handleShots();
                tryTargetMobByLevel();
                if (!_fakePlayer.isMageClass()) {
                    tryAttackingUsingFighterOffensiveSkill();
                } else {
                    tryAttackingUsingMageOffensiveSkill();
                }
            }
        //}

        setBusyThinking(false);
    }

    private void GiveItems(FakePlayer fakePlayer){
        CrystalType grade = _fakePlayer.getActiveWeaponItem().getCrystalType();
        int level = _fakePlayer.getLevel();
        if(grade != CrystalType.B && level >=52 && level < 61 ){
            FakeHelpers.giveArmorsByClass(fakePlayer,level);
            FakeHelpers.giveWeaponsByClass(fakePlayer,false);
            _fakePlayer.broadcastUserInfo();
        }

        if(grade != CrystalType.A && level >=61 && level < 80 ){
            FakeHelpers.giveArmorsByClass(fakePlayer,level);
            FakeHelpers.giveWeaponsByClass(fakePlayer,false);
            _fakePlayer.broadcastUserInfo();
        }

        if(grade != CrystalType.S && level ==80 ){
            FakeHelpers.giveArmorsByClass(fakePlayer,level);
            FakeHelpers.giveWeaponsByClass(fakePlayer,true);
            _fakePlayer.broadcastUserInfo();
        }


    }

    @Override
    protected ShotType getShotType()
    {
        if(_fakePlayer.isMageClass()){
            return ShotType.BLESSED_SPIRITSHOT;
        }
        return ShotType.SOULSHOT;
    }

    @Override
    protected List<OffensiveSpell> getOffensiveSpells()
    {
        List<OffensiveSpell> _offensiveSpells = new ArrayList<>();
        List<L2Skill> skills;
        if(_fakePlayer.isMageClass()){

            skills = _fakePlayer.getSkills().values().stream().filter(x->x.isDamage() && x.getElement() != -1).collect(Collectors.toList());
        }
        else{
        skills = _fakePlayer.getSkills().values().stream().filter(x->x.isDamage()).collect(Collectors.toList());
        }
        Collections.sort(skills,(L2Skill x1, L2Skill x2) -> Double.compare(x1.getPower(),x2.getPower()));
        Collections.reverse(skills);
        int i = 1;
        for(L2Skill skill : skills){
            _offensiveSpells.add(new OffensiveSpell(skill.getId(),i++));
        }
        return  _offensiveSpells;
    }

    @Override
    protected int[][] getBuffs()
    {
        return FakeHelpers.getFighterBuffs();
    }

    @Override
    protected List<HealingSpell> getHealingSpells()
    {
        return Collections.emptyList();
    }

    @Override
    protected List<SupportSpell> getSelfSupportSpells() {
        return Collections.emptyList();
    }

}