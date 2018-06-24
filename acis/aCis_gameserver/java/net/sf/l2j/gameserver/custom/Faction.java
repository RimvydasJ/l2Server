package net.sf.l2j.gameserver.custom;

public enum Faction {
    Default(0),
    Red(1),
    Blue(2);

    private final int _Id;

    Faction(int id){
        _Id = id;
    }

    public int getId(){
        return _Id;
    }
}
