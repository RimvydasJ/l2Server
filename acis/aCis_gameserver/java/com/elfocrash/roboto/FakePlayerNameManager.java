package com.elfocrash.roboto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.elfocrash.roboto.helpers.FakeHelpers;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.datatables.PlayerNameTable;

public enum FakePlayerNameManager {
	INSTANCE;

	public static final Logger _log = Logger.getLogger(FakePlayerNameManager.class.getName());
	private List<String> _fakePlayerNames;

	public void initialise() {
		loadWordlist();
	}

	public String getRandomAvailableName() {
		String name = getRandomNameFromWordlist();

//		while(nameAlreadyExists(name)) {
//			name = getRandomNameFromWordlist();
//		}
		while(testNameAlreadyExists(name)){
			name= getRandomNameFromWordlist();
		}

		FakeHelpers._usedNames.add(name);
		return name;
	}
	
	private String getRandomNameFromWordlist() {
		return _fakePlayerNames.get(Rnd.get(0, _fakePlayerNames.size() - 1));
}

	public List<String> getFakePlayerNames() {
		return _fakePlayerNames;
	}
	
	private void loadWordlist()
    {
        try(LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("./data/fakenamewordlist.txt"))));)
        {
            String line;
            ArrayList<String> playersList = new ArrayList<String>();
            while((line = lnr.readLine()) != null)
            {
                if(line.trim().length() == 0 || line.startsWith("#"))
                    continue;
                playersList.add(line);
            }
            _fakePlayerNames = playersList;
            _log.log(Level.INFO, String.format("Loaded %s fake player names.", _fakePlayerNames.size()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean testNameAlreadyExists(String name){
		//String foundName = FakeHelpers._usedNames.stream().filter(x->x.equals(name)).findAny();
		return FakeHelpers._usedNames.contains(name);
	}
	
	private boolean nameAlreadyExists(String name) {
		return PlayerNameTable.getInstance().getPlayerObjectId(name) > 0;
	}
}
