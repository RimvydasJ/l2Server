package net.sf.l2j.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.StringTokenizer;

import net.sf.l2j.gameserver.custom.AutoBuffs;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;

public class BufferInstance extends Folk
{
	public ArrayList<Integer> ids = AutoBuffs.setIds();
	AutoBuffs nah = AutoBuffs.getInstance();
	
	public BufferInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val)
	{
		try{
		if(nah.getSectionBuffs(player) == null)
		{
			nah.createMap(player);
		}
		
		if(player.getSection() == 119 && player.getActiveClass() != 111 && player.getActiveClass() != 104 && player.getActiveClass() != 96 && player.getActiveClass() != 91 && player.getActiveClass() != 118)			
				player.setSection(player.getActiveClass());
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());		
		
		html.setFile("data/html/buffer/test.htm");
		String buffsString = "";
		String whosBuffin = "";
		
		whosBuffin += "<table width=220><tr><td>"+ CharTemplateTable.getInstance().getClassNameById(player.getClassId().getId()) +":</td>";
		
		if(player.getSection() == player.getActiveClass())
		{
			buffsString += "<td><font color=\"00FF00\">Player</font></a></td>";
			if(player.getActiveClass() == 111  || player.getActiveClass() == 104 || player.getActiveClass() == 96 || player.getActiveClass() == 91 || player.getActiveClass() == 118)
				buffsString += "<td><a action=\"bypass buffer 73\"><font color=\"ffffff\">Pet</font></a></td></tr></table>";
			else
				buffsString += "<td></td></tr></table>";
		}
		else if(player.getSection() == 119)
		{
			buffsString += "<td><a action=\"bypass buffer 73\"><font color=\"ffffff\">Player</font></a></td>";
			buffsString += "<td><a action=\"bypass buffer 73\"><font color=\"00FF00\">Pet</font></a></td></tr></table>";		
		}
		else
		{
			buffsString += "<td><font color=\"00FF00\">Player</font></a></td>";
			player.setSection(player.getActiveClass());
			if(player.getActiveClass() == 111  || player.getActiveClass() == 104 || player.getActiveClass() == 96 || player.getActiveClass() == 91 || player.getActiveClass() == 118)
				buffsString += "<td><a action=\"bypass buffer 73\"><font color=\"ffffff\">Pet</font></a></td></tr></table>";
			else
				buffsString += "<td></td></tr></table>";
		}
		String seraphimb;
		String seraphimg;
		String queenb;
		String queeng;
		L2Skill sk = SkillTable.getInstance().getInfo(ids.get(ids.size()-2), SkillTable.getInstance().getMaxLevel(ids.get(ids.size()-2)));	
		if(nah.getSectionBuffs(player).contains(sk))
			seraphimb = "<font color=\"LEVEL\"><a action=\"bypass buffer "+(ids.size()-2)+"\">"+"Uni Bless"+"</a></font>";
		else
			seraphimb = "<a action=\"bypass buffer "+(ids.size()-2)+"\">"+"Uni Bless"+"</a>";	
		
		sk = SkillTable.getInstance().getInfo(ids.get(ids.size()-4), SkillTable.getInstance().getMaxLevel(ids.get(ids.size()-4)));	
		if(nah.getSectionBuffs(player).contains(sk))
			queenb = "<font color=\"LEVEL\"><a action=\"bypass buffer "+(ids.size()-4)+"\">"+"Cat Bless"+"</a></font>";
		else
			queenb = "<a action=\"bypass buffer "+(ids.size()-4)+"\">"+"Cat Bless"+"</a>";	
		
		sk = SkillTable.getInstance().getInfo(ids.get(ids.size()-1), SkillTable.getInstance().getMaxLevel(ids.get(ids.size()-1)));			
		if(nah.getSectionBuffs(player).contains(sk))
			seraphimg = "<font color=\"LEVEL\"><a action=\"bypass buffer "+(ids.size()-1)+"\">"+"Uni Gift"+"</a></font>";
		else
			seraphimg = "<a action=\"bypass buffer "+(ids.size()-1)+"\">"+"Uni Gift"+"</a>";
		
		sk = SkillTable.getInstance().getInfo(ids.get(ids.size()-3), SkillTable.getInstance().getMaxLevel(ids.get(ids.size()-3)));			
		if(nah.getSectionBuffs(player).contains(sk))
			queeng = "<font color=\"LEVEL\"><a action=\"bypass buffer "+(ids.size()-3)+"\">"+"Cat Gift"+"</a></font>";
		else
			queeng = "<a action=\"bypass buffer "+(ids.size()-3)+"\">"+"Cat Gift"+"</a>";
		
		buffsString += "<table width=300><tr><td>" + seraphimb.replace("ing", "").replace("of ", "").replace("im", "")+ "</td><td>" + queenb.replace("ing", "").replace("of ", "") + "</td><td>" + seraphimg.replace("ing", "").replace("of ", "").replace("im", "") + "</td><td>" + queeng.replace("ing", "").replace("of ", "") + "</td></tr><tr>";
		
		int i = 0;
		int g = 14;
		int d = 28;
		int s = 39;

		for(int z=0;z<ids.size();z++)
		{				
			//System.out.println("veikia "+ z + "? "+i+"-"+d+"-"+s);
			// 1 - 28 buffs
			// 29 - 39 dances
			// 40 - 53 songs		
			
			L2Skill dance = null;
			L2Skill song = null;
			L2Skill debil = null;
			L2Skill buff = null;
			
			if(i >= 0 && i <= 13)//28
				debil = SkillTable.getInstance().getInfo(ids.get(i), SkillTable.getInstance().getMaxLevel(ids.get(i)));	
			if(g >= 14 && g <= 27)//28
				buff = SkillTable.getInstance().getInfo(ids.get(g), SkillTable.getInstance().getMaxLevel(ids.get(g)));						
			if(d >= 28 && d <= 38)
				dance = SkillTable.getInstance().getInfo(ids.get(d), SkillTable.getInstance().getMaxLevel(ids.get(d)));
			if(s >= 39 && s <= 52)
				song = SkillTable.getInstance().getInfo(ids.get(s), SkillTable.getInstance().getMaxLevel(ids.get(s)));
			
				if(debil != null)
				{					
					if(nah.getSectionBuffs(player).contains(debil))
						buffsString += "<td><font color=\"LEVEL\"><a action=\"bypass buffer "+i+"\">"+debil.getName()+"</a></font></td>";
					else
						buffsString += "<td><a action=\"bypass buffer "+i+"\">"+debil.getName()+"</a></td>";		
				}else
				{					
					buffsString += "<td></td>";
					break;
				}
				
				if(buff != null)
				{					
					if(nah.getSectionBuffs(player).contains(buff))
						buffsString += "<td><font color=\"LEVEL\"><a action=\"bypass buffer "+g+"\">"+buff.getName().replace("Spirit", "")+"</a></font></td>";
					else
						buffsString += "<td><a action=\"bypass buffer "+g+"\">"+buff.getName().replace("Spirit", "")+"</a></td>";		
				}else
				{					
					buffsString += "<td></td>";
				}
				
				if(dance != null)
				{
					if(nah.getSectionBuffs(player).contains(dance))
						buffsString += "<td><font color=\"LEVEL\"><a action=\"bypass buffer "+d+"\">"+dance.getName().replace("Dance of ","").replace("ration", "").replace("Guard", "").replace("Dance", "").replace("the ", "").replace("of ", "")+"</a></font></td>";//dance of
					else
						buffsString += "<td><a action=\"bypass buffer "+d+"\">"+dance.getName().replace("Dance of ","").replace("ration", "").replace("Guard", "").replace("Dance", "").replace("the ", "").replace("of ", "")+"</a></td>";		
				}else
					buffsString += "<td></td>";
				
				if(song != null)
				{
					if(nah.getSectionBuffs(player).contains(song))
						buffsString += "<td><font color=\"LEVEL\"><a action=\"bypass buffer "+s+"\">"+song.getName().replace("Song of ","").replace("Guard", "")+"</a></font></td></tr> <tr>";//song of 
					else
						buffsString += "<td><a action=\"bypass buffer "+s+"\">"+song.getName().replace("Song of ","").replace("Guard", "")+"</a></td></tr> <tr>";
				}else
					buffsString += "</tr>";			
			i++;
			s++;
			d++;
			g++;
		}
		buffsString += "</table>";
		html.replace("%kas%", whosBuffin);
		html.replace("%bufai%", buffsString);
		player.sendPacket(html);
		
		}catch(Exception e){e.printStackTrace();}
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		/*int[] ids = { 
				1085, 1304, 1087, 
				1045, 1243, 1048, 1397, 1078, 
				1242, 1059, 1077, 1388, 1389, 1268, 1204, 1062,
				1240, 1086, 1036, 1035, 1068, 1259, 1040,
				1356, 1355, 1357, 1413, 1363				
				};//isidesto netaip*/
		
		//showBuffer(player);	
		
		/*	
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.add(1085); ids.add(1304); ids.add(1087); 
		ids.add(1045); ids.add(1243); ids.add(1048); 
		ids.add(1397); ids.add(1078); ids.add(1242); 
		ids.add(1059); ids.add(1077); ids.add(1388); 
		ids.add(1389); ids.add(1268); ids.add(1204); 
		ids.add(1062); ids.add(1240); ids.add(1086); 
		ids.add(1036); ids.add(1035); ids.add(1068); 
		ids.add(1259); ids.add(1040); ids.add(1356); 
		ids.add(1355); ids.add(1357); ids.add(1413); ids.add(1363);*/
		
		if(AttackStanceTaskManager.getInstance().isInAttackStance(player) && !player.isInsideZone(ZoneId.PEACE))
		{
			player.sendMessage("Can not do that while fighting.");
			return;
		}
		
		if(player.isDead())
		{
			player.sendMessage("Can not do that while dead.");
			return;
		}
		
		if(player.getKarma() > 0)
		{
			player.sendMessage("Can not do that with karma.");
			return;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		String actualCommand = st.nextToken(); // Get actual command
		
		try{
		if(Integer.parseInt(actualCommand) <= ids.size())
		{
			L2Skill skill;
			int id = ids.get(Integer.parseInt(actualCommand));//-1);		
			skill = SkillTable.getInstance().getInfo(id, SkillTable.getInstance().getMaxLevel(id));
			
				if(!nah.getSectionBuffs(player).contains(skill))
				{
					nah.addBuffToSect(skill, player);
					if(player.getSection()<=118)
					{
						skill.getEffects(player, player);
					}
					else
					{
						if(player.getPet()!=null)
							skill.getEffects(player.getPet(), player.getPet());
						else
							player.sendMessage("You don't have any pet.");
					}
					/*if(player.getSection()<=118)
						player.broadcastPacket(new MagicSkillUser(player, player, skill.getId(), skill.getLevel(), 800));  
						else
						{
							if(player.getPet()!=null)
							player.broadcastPacket(new MagicSkillUser(player.getPet(), player.getPet(), skill.getId(), skill.getLevel(), 800));  
							//else
								//player.sendMessage("You don't have any pet.");
						}*/
				}
				else
				{
					nah.removeBuffFromSection(skill, player);
					if(player.getSection() <= 118)
					{
						for(L2Effect e : player.getAllEffects())
							if(e.getSkill().getId()==skill.getId())
								e.exit();
					}
					else
						for(L2Effect e : player.getPet().getAllEffects())
							if(e.getSkill().getId()==skill.getId())
								e.exit();					
				}
		}
		}catch(Exception d){System.out.println(d + " buffer, ");d.printStackTrace();}
		switch(Integer.parseInt(actualCommand)){
		case 71: 
			if(player.getSection()<=118)
			{
				 player.getStatus().setCurrentCp(player.getMaxCp()); 
				 player.getStatus().setCurrentHp(player.getMaxHp()); 
				 player.getStatus().setCurrentMp(player.getMaxMp());	
				 player.broadcastPacket(new MagicSkillUse(player, player, 4380, 1, 0,0, true));
			}else
				if(player.getPet()!=null)
				{
					 player.getPet().getStatus().setCurrentCp(player.getPet().getMaxCp()); 
					 player.getPet().getStatus().setCurrentHp(player.getPet().getMaxHp()); 
					 player.getPet().getStatus().setCurrentMp(player.getPet().getMaxMp());	
					 StatusUpdate su = new StatusUpdate(player.getPet());
					 su.addAttribute(StatusUpdate.CUR_HP, (int) player.getPet().getCurrentHp());
					 su.addAttribute(StatusUpdate.CUR_MP, (int) player.getPet().getCurrentMp());
					 player.getPet().sendPacket(su);
					 player.broadcastPacket(new MagicSkillUse(player.getPet(), player.getPet(), 4380, 1, 0, 0, true));
				}
				 break;
		case 72: 
			
			if(player.getSection() <= 118)
				for(L2Skill skill : nah.getSectionBuffs(player))
				{
					for(L2Effect e : player.getAllEffects())
						if(e.getSkill().getId()==skill.getId())
							e.exit();
				}
			else
				if(player.getPet() != null)
					for(L2Skill skill : nah.getSectionBuffs(player))
					{
						for(L2Effect e : player.getPet().getAllEffects())
							if(e.getSkill().getId()==skill.getId())
								e.exit();
					}
			break;	
		case 73: 
			if(player.getSection() <= 118)
				player.setSection(119);
			else
				player.setSection(AutoBuffs.getClass(player.getClassId().getId()));					
			break;
		case 74:						
			    if(player.getSection()<=118)
				{
					//player.stopAllEffects();
					 for(L2Skill skill : nah.getSectionBuffs(player))
						skill.getEffects(player, player);
				}
				else
					{
						if(player.getPet() == null)
						{
							player.sendMessage("You don't have any pet.");
							return;						
						}
					
						for(L2Skill skill : nah.getSectionBuffs(player))
						{
							for(L2Effect e : player.getPet().getAllEffects())
								if(e.getSkill().getId()==skill.getId())
									e.exit();
							
							skill.getEffects(player.getPet(), player.getPet());
						}
					}
			break;
		}	
		showChatWindow(player, 0);
	}
}
