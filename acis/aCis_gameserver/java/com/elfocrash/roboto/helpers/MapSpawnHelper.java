package com.elfocrash.roboto.helpers;

import java.util.HashMap;
import java.util.Map;

public class MapSpawnHelper {

    public static class Coordinates {

        public Coordinates(int x, int y, int z){
            this.X = x;
            this.Y = y;
            this.Z = z;
        }

        public int X;
        public int Y;
        public int Z;
    }

    public static Map<Integer, Coordinates> RainbowSprings = new HashMap<Integer, Coordinates>() {
        {
            put(1, new Coordinates(140856,-124056,-1904));
            put(2, new Coordinates(140888,-124040,-1904));
            put(3, new Coordinates(140984,-123912,-1904));
            put(4, new Coordinates(140952,-123752,-1904));
            put(5, new Coordinates(141128,-123784,-1904));
            put(6, new Coordinates(141400,-123768,-1904));
            put(7, new Coordinates(141560,-123992,-1904));
            put(8, new Coordinates(141752,-124184,-1904));
            put(9, new Coordinates(141720,-124392,-1904));
            put(10, new Coordinates(141752,-124520,-1904));
        }
    };

}
