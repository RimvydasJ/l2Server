package com.elfocrash.roboto.helpers;

import net.sf.l2j.commons.random.Rnd;

import java.util.ArrayList;
import java.util.List;

public class StandingImitation {

    private static List<SquarePoints> _GiranStandingSquares = new ArrayList<SquarePoints>(){{
        add(new SquarePoints(82280,82728,149368,147832));
        add(new SquarePoints(83288,83624,149192,147960));
    }};

     public static SquarePoints GetGiranSquareForStandingImitation(){
        return _GiranStandingSquares.get(Rnd.get(0,_GiranStandingSquares.size()-1));
    }

    public static class SquarePoints{
        private long _xMin;
        private long _xMax;
        private long _yMin;
        private long _yMax;

        public SquarePoints(long xMin, long xMax, long yMax, long yMin){
            _xMax = xMax;
            _xMin = xMin;
            _yMax = yMax;
            _yMin = yMin;
        }

        public long GetXmin(){
            return _xMin;
        }
        public long GetXmax(){
            return _xMax;
        }
        public long GetYmin(){
            return _yMin;
        }
        public long GetYmax(){
            return _yMax;
        }
    }
}
