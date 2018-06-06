package com.elfocrash.roboto.helpers;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.helpers.Enums.TownIds;

public class ZoneChecker {
    public static boolean checkIfInRainboSprings(FakePlayer _fakePlayer){
        return _fakePlayer.getNearestTownId() == TownIds.Goddard
                && (_fakePlayer.getX() > 135000 && _fakePlayer.getX() < 150000)
                && (_fakePlayer.getY() < -110000 && _fakePlayer.getY() > -140000)
                && (_fakePlayer.getZ() < -1400 && _fakePlayer.getZ() > -3000);
    }

    public static boolean checkIfInLoa(FakePlayer _fakePlayer){
        return (_fakePlayer.getX() > 111584 && _fakePlayer.getX() < 135155)
                && (_fakePlayer.getY() < 122740 && _fakePlayer.getY() > 113837);
    }

    public static boolean checkIfInEvaGarden(FakePlayer _fakePlayer){
        return (_fakePlayer.getX() > 83432 && _fakePlayer.getX() < 87704)
                && (_fakePlayer.getY() < 259144 && _fakePlayer.getY() > 254856);
    }

    public static boolean checkIfInGiran(FakePlayer _fakePlayer){
        return (_fakePlayer.getX() > 76824 && _fakePlayer.getX() < 86776)
                && (_fakePlayer.getY() < 155800 && _fakePlayer.getY() > 143416);
    }

    public static boolean checkIfInSquare(FakePlayer _fakePlayer, StandingImitation.SquarePoints squarePoints){
        return (_fakePlayer.getX() > squarePoints.GetXmin() && _fakePlayer.getX() < squarePoints.GetXmax())
                && (_fakePlayer.getY() > squarePoints.GetYmin() && _fakePlayer.getY() < squarePoints.GetYmax());
    }
}
