package net.sf.l2j.gameserver.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.Player;

/**
 * 
 * @author Povis
 *
 */

public class AutoBuffs 
{
	//mantasp111
	AutoBuffs _intstance = null;	
	static Map<Integer, Map<Integer, ArrayList<L2Skill>>> playersMap = new HashMap<>(); 
	private static final String SQL_LOAD_SCHEME = "SELECT * FROM auto_buffs WHERE objId=?";
	private static final String SQL_DELETE_SCHEME = "DELETE FROM auto_buffs WHERE objId=?";
	private static final String SQL_INSERT_SCHEME = "INSERT INTO auto_buffs (objId, id, level, section) VALUES (?,?,?,?)";
	
	public static AutoBuffs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void createMap(Player player)
	{
		playersMap.get(player.getObjectId()).put(getClass(player.getClassId().getId()), new ArrayList<L2Skill>());
	}
	static ArrayList<Integer> ids = new ArrayList<>();
	
	public AutoBuffs(){
		//playersMap.clear();		
	}
	
	public static ArrayList<Integer> setIds(){
		if(ids.isEmpty()){
		ids.add(1085); ids.add(1304); ids.add(1087); 
		ids.add(1045); ids.add(1303); ids.add(1048); 
		ids.add(1397); ids.add(1078); ids.add(1242); 
		ids.add(1059); ids.add(1077); ids.add(1388); 
		ids.add(1389); ids.add(1268); ids.add(1204); 
		ids.add(1062); ids.add(1240); ids.add(1086); 
		ids.add(1036); ids.add(1035); ids.add(1068); 
		ids.add(1259); ids.add(1040); ids.add(1356); 
		ids.add(1355); ids.add(1357); ids.add(1413); ids.add(1363);
		
		//dance
		ids.add(307);ids.add(276);ids.add(309);
		ids.add(274);ids.add(275);ids.add(272);
		ids.add(277);ids.add(273);ids.add(365);
		ids.add(310);ids.add(271);
		
		//song
		ids.add(364);ids.add(264);ids.add(306);
		ids.add(269);ids.add(270);ids.add(265);
		ids.add(363);ids.add(349);ids.add(308);
		ids.add(305);ids.add(304);ids.add(267);
		ids.add(266);ids.add(268);
		
		ids.add(4699);ids.add(4700);ids.add(4702);ids.add(4703);
		}
		return ids;
	}
	
	public void addBuffToList(L2Skill skill, Player player)
	{
		if(playersMap.get(player.getObjectId())!=null){
			if(playersMap.get(player.getObjectId()).get(player.getSection()) != null){
				playersMap.get(player.getObjectId()).get(player.getSection()).add(skill);
			}else{
				playersMap.get(player.getObjectId()).put(player.getSection(), new ArrayList<L2Skill>());
				playersMap.get(player.getObjectId()).get(player.getSection()).add(skill);
			}
		}else{
			playersMap.put(player.getObjectId(), new HashMap<Integer, ArrayList<L2Skill>>());
			playersMap.get(player.getObjectId()).put(player.getSection(), new ArrayList<L2Skill>());
			playersMap.get(player.getObjectId()).get(player.getSection()).add(skill);			
		}			
	}
	
	public void removeBuffFromSection(L2Skill skill, Player playa){
		try{
			playersMap.get(playa.getObjectId()).get(playa.getSection()).remove(skill);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void addBuffToSect(L2Skill skill, Player playa)
	{
		getSectionBuffs(playa);
		int obj = playa.getObjectId(); 
		int section = playa.getSection();
		if(skill.getId()==1356||skill.getId()==1357||skill.getId()==1355||skill.getId()==1413||
				skill.getId()==1363||skill.getId()==1388||skill.getId()==1389 || skill.getId()==4699 ||
				skill.getId()==4700 || skill.getId()==4702 || skill.getId()==4703
				){
		switch(skill.getId()){
		case 1356:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363)), playa);
			
			addBuffToList(skill, playa);
			break;
		case 1355:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363)), playa);
			
			addBuffToList(skill, playa);
			break;	
		case 1357:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363)), playa);	
			
			addBuffToList(skill, playa);
			break;
		case 1413:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1363,  SkillTable.getInstance().getMaxLevel(1363)), playa);	
			
			addBuffToList(skill, playa);
			break;
		case 1363:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1356,  SkillTable.getInstance().getMaxLevel(1356)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1357,  SkillTable.getInstance().getMaxLevel(1357)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1413,  SkillTable.getInstance().getMaxLevel(1413)), playa);
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1355,  SkillTable.getInstance().getMaxLevel(1355)), playa);
			
			addBuffToList(skill, playa);
			break;
		case 1388:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1389,  SkillTable.getInstance().getMaxLevel(1389))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1389,  SkillTable.getInstance().getMaxLevel(1389)), playa);
			addBuffToList(skill, playa);
			break;
		case 1389:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(1388,  SkillTable.getInstance().getMaxLevel(1388))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(1388,  SkillTable.getInstance().getMaxLevel(1388)), playa);
			addBuffToList(skill, playa);
			break;	
		case 4699:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(4700,  SkillTable.getInstance().getMaxLevel(4700))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(4700,  SkillTable.getInstance().getMaxLevel(4700)), playa);
			addBuffToList(skill, playa);
			break;
		case 4700:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(4699,  SkillTable.getInstance().getMaxLevel(4699))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(4699,  SkillTable.getInstance().getMaxLevel(4699)), playa);
			addBuffToList(skill, playa);
			break;
		case 4702:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(4703,  SkillTable.getInstance().getMaxLevel(4703))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(4703,  SkillTable.getInstance().getMaxLevel(4703)), playa);
			addBuffToList(skill, playa);
			break;
		case 4703:
			if(playersMap.get(obj).get(section).contains(SkillTable.getInstance().getInfo(4702,  SkillTable.getInstance().getMaxLevel(4702))))
				removeBuffFromSection(SkillTable.getInstance().getInfo(4702,  SkillTable.getInstance().getMaxLevel(4702)), playa);
			addBuffToList(skill, playa);
			break;
		}

		}else addBuffToList(skill, playa);
	}
	
	public ArrayList<L2Skill> getSectionBuffs(Player playa, int sect)
	{
		if(playersMap.get(playa.getObjectId()) == null)
		{
			playersMap.put(playa.getObjectId(), new HashMap<Integer, ArrayList<L2Skill>>());
			playersMap.get(playa.getObjectId()).put(playa.getSection(), new ArrayList<L2Skill>());
			return playersMap.get(playa.getObjectId()).get(playa.getSection());
		}
		if(playersMap.get(playa.getObjectId()).get(playa.getSection()) == null)
		{
			playersMap.get(playa.getObjectId()).put(playa.getSection(), new ArrayList<L2Skill>());
			return playersMap.get(playa.getObjectId()).get(playa.getSection());
		}
		
		return playersMap.get(playa.getObjectId()).get(sect);
	}
	
	public ArrayList<L2Skill> getSectionBuffs(Player player)
	{
		if(playersMap.get(player.getObjectId()) == null)
		{
			Map<Integer, ArrayList<L2Skill>> list = new HashMap<>();
			list.put(player.getActiveClass(), new ArrayList<L2Skill>());
			playersMap.put(player.getObjectId(), list);
			return new ArrayList<>();
		}
		try{
		if(playersMap.get(player.getObjectId()).get(player.getSection()) == null)
		{
			//for(int prev = CharTemplateTable.getInstance().getTemplate(player.getSection()).getClassId().getParent().getId();
			//	prev > -1;
			//	prev = CharTemplateTable.getInstance().getTemplate(prev).getClassId().getParent().getId())				
			//{
			int prev = CharTemplateTable.getInstance().getTemplate(player.getSection()).getClassId().getParent().getId();
				if(playersMap.get(player.getObjectId()).get(prev) != null)
				{
					playersMap.get(player.getObjectId()).put(player.getSection(), playersMap.get(player.getObjectId()).get(prev));
					playersMap.get(player.getObjectId()).remove(prev);				
				}
			//}				
			return playersMap.get(player.getObjectId()).get(player.getSection());
		}
		}catch(Exception e){e.printStackTrace();}//this catch catches some crap that i dont understand but it works so its fine
		return playersMap.get(player.getObjectId()).get(player.getSection());
	}
	
	public void onShutDown(){
		clearDB();
		saveDataToDB();
	}
	
	public static void onPlayerLogin(Player player)
	{
		if (playersMap.get(player.getObjectId()) == null)
			  loadPlayerBuffs(player);
	}
	
	public void onPlayerLogin(int playerId)
	{
		if (playersMap.get(playerId) == null)
			loadPlayerBuffs(playerId);
	}
	
	public void saveDataToDB()
	{
		if (playersMap.isEmpty())
			return;		
		int count = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection()){
			PreparedStatement statement = con.prepareStatement(SQL_INSERT_SCHEME);
			for (Map.Entry<Integer, Map<Integer, ArrayList<L2Skill>>> e : playersMap.entrySet())
			{
				if (e.getValue() == null || e.getValue().isEmpty())
					continue;
				
				for(Map.Entry<Integer, ArrayList<L2Skill>> a : e.getValue().entrySet())
					for(L2Skill s : a.getValue())
					{						
						statement.setInt(1, e.getKey());
						statement.setInt(2, s.getId());
						statement.setInt(3, s.getLevel());
						statement.setInt(4, a.getKey());
						statement.execute();
					}				
				count++;
			}
			statement.close();

		}
		catch (Exception e)
		{
			System.out.println("AutoBuffs: Error while trying to save schemes");
		}
		finally
		{
			System.out.println("AutoBuffs: Saved " + String.valueOf(count + " scheme(s)"));
		}
	}
	
	public void clearDB()
	{
		if (playersMap.isEmpty())
			return;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection()){
			PreparedStatement statement = con.prepareStatement(SQL_DELETE_SCHEME);
			for (Map.Entry<Integer, Map<Integer, ArrayList<L2Skill>>> e : playersMap.entrySet())
			{				
				statement.setInt(1, e.getKey());
				statement.execute();
			}
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("AutoBuffs: Error while trying to delete schemes");
		}
	}
	
	private static int Prof(int prof)
	{
		int rd = prof;
		CharTemplateTable t = CharTemplateTable.getInstance();
		
		for(int i=88;i<118;i++)
		{
			if(t.getTemplate(i).getClassId().getParent().getId() == rd)
				rd = i;
		}		
		t = null;
		return rd;
	}
	
	public static int getClass(int c)
	{
		int cl = c;
		
		if(CharTemplateTable.getInstance().getTemplate(c).getClassBaseLevel() == 40)
		{
			cl = Prof(c);
		}		
		
		return cl;
	}
	
	public void loadPlayerBuffs(int objectId){
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection()){
			PreparedStatement statement = con.prepareStatement(SQL_LOAD_SCHEME);
			statement.setInt(1, objectId);
			ResultSet rs = statement.executeQuery();
			ArrayList<L2Skill> map = new ArrayList<>();
			ArrayList<L2Skill> map1 = new ArrayList<>();
			while (rs.next())
			{
				int id = rs.getInt("id");
				int lv = rs.getInt("level");
				int sect = rs.getInt("section");
				L2Skill s = SkillTable.getInstance().getInfo(id, lv);
				if(sect == 0)
					map.add(s);
				else
					map1.add(s);
			}
			Map<Integer, ArrayList<L2Skill>> m = new HashMap<>();
			m.put(0, map);
			m.put(1, map1);
			if (!m.isEmpty())
				playersMap.put(objectId, m);
			statement.close();
			rs.close();
		}
		catch (Exception e)
		{		
			System.out.println("Error trying to load buff scheme from object id: " + objectId);
		}
	}
	
	public static void loadPlayerBuffs(Player player){
		int objectId = player.getObjectId();
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SQL_LOAD_SCHEME);
			statement.setInt(1, objectId);
			ResultSet rs = statement.executeQuery();			
			
			Map<Integer, ArrayList<L2Skill>> list = new HashMap<>();
			
			list.put(119, new ArrayList<L2Skill>());
			
			while (rs.next())
			{
				int id = rs.getInt("id");
				int lv = rs.getInt("level");
				int sect = rs.getInt("section");
				L2Skill s = SkillTable.getInstance().getInfo(id, lv);
				
				if(sect<=118)
					if(list.get(sect)==null)
					{
						list.put(sect, new ArrayList<L2Skill>());
						list.get(sect).add(s);
					}else
						list.get(sect).add(s);
					//else
					// list.get(sect).add(s);								
				if(sect==119)
					list.get(sect).add(s);
				/*}else
					if(list.get(sect)==null)
					{
						list.put(sect, new ArrayList<L2Skill>());
						list.get(sect).add(s);
					}
					else
					 list.get(sect).add(s);
					map1.add(s);
			*/}
			
			/*Map<Integer, ArrayList<L2Skill>> m = new HashMap<Integer, ArrayList<L2Skill>>();
			
			for (Map.Entry<Integer, ArrayList<L2Skill>> e : list.entrySet())
			{
				if (e.getValue() == null || e.getValue().isEmpty())
					continue;
				
				m.put(e.getKey(), e.getValue());				
			}
			
			//m.put(getClass(player.getClassId().getId()), map);
			m.put(119, map1);
			*/
			if (!list.isEmpty())
				playersMap.put(objectId, list);
			
			statement.close();
			rs.close();
		}
		catch (Exception e)
		{		
			System.out.println("Error trying to load buff scheme from object id: " + objectId);
		}
		objectId = 0;
	}

	
	private static class SingletonHolder
	{
		protected static final AutoBuffs _instance = new AutoBuffs();
	}
}