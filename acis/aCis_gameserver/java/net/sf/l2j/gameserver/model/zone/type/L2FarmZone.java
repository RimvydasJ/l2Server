package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneForm;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;

public class L2FarmZone extends L2ZoneType {

    public L2FarmZone(int id){super(id);}

    @Override
    protected void onEnter(Creature character) {
        if(character instanceof Player){
            character.sendMessage("You entered FarmZone. There are: "+ this.getKnownTypeInside(Player.class).size() + " players inside.");
        }
    }

    @Override
    protected void onExit(Creature character) {
        if(character instanceof Player){
            character.sendMessage("You leaved FarmZone");
        }
    }

    @Override
    public void onDieInside(Creature character) {

    }

    @Override
    public void onReviveInside(Creature character) {

    }
}
