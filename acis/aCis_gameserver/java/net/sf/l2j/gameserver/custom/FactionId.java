package net.sf.l2j.gameserver.custom;

public enum FactionId {
    NON(0),
    BLUE(1),
    RED(2);

    private final int _id;
    FactionId(int id){
        _id = id;
    }
    public int getId(){return _id;}
}
