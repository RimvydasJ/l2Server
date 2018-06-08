package com.elfocrash.roboto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


// SINGLETON
public class ClanManager {
    private static ClanManager _Instance;

    private static List<String> ClanNames = new ArrayList<>();

    public static final Logger _log = Logger.getLogger(ClanManager.class.getName());

    public static ClanManager Initialize(){
        if(_Instance == null){
            _Instance = new ClanManager();
        }
        return _Instance;
    }

    public void LoadClanNames(){
        try(LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("./data/clannamewordlist.txt"))));)
        {
            String line;
            ArrayList<String> clanList = new ArrayList<String>();
            while((line = lnr.readLine()) != null)
            {
                if(line.trim().length() == 0 || line.startsWith("#"))
                    continue;
                clanList.add(line);
            }
            ClanNames = clanList;
            _log.log(Level.INFO, String.format("Loaded %s clan names.", ClanNames.size()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static List<String> getClanNames(){
        return ClanNames;
    }

    public ClanManager(){

    }
}
