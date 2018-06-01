package com.elfocrash.roboto.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.FakePlayerNameManager;
import com.elfocrash.roboto.ai.AdventurerAI;
import com.elfocrash.roboto.ai.ArchmageAI;
import com.elfocrash.roboto.ai.CardinalAI;
import com.elfocrash.roboto.ai.DominatorAI;
import com.elfocrash.roboto.ai.DreadnoughtAI;
import com.elfocrash.roboto.ai.DuelistAI;
import com.elfocrash.roboto.ai.FakePlayerAI;
import com.elfocrash.roboto.ai.FallbackAI;
import com.elfocrash.roboto.ai.GhostHunterAI;
import com.elfocrash.roboto.ai.GhostSentinelAI;
import com.elfocrash.roboto.ai.GrandKhavatariAI;
import com.elfocrash.roboto.ai.MoonlightSentinelAI;
import com.elfocrash.roboto.ai.MysticMuseAI;
import com.elfocrash.roboto.ai.SaggitariusAI;
import com.elfocrash.roboto.ai.SoultakerAI;
import com.elfocrash.roboto.ai.StormScreamerAI;
import com.elfocrash.roboto.ai.TitanAI;
import com.elfocrash.roboto.ai.WindRiderAI;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.PlayerNameTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.appearance.PcAppearance;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.ClassRace;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class FakeHelpers {
	
	public static int[][] getFighterBuffs() {
		return new int[][] { { 1204, 2 }, // wind walk	                
            { 1040, 3 }, // shield
			{ 1035, 4 }, // Mental Shield
			{ 1045, 6 }, // Bless the Body				
			{ 1068, 3 }, // might
			{ 1062, 2 }, // besekers
			{ 1086, 2 }, // haste
			{ 1077, 3 }, // focus
			{ 1388, 3 }, // Greater Might
			{ 1036, 2 }, // magic barrier
			{ 274, 1 }, // dance of fire
			{ 273, 1 }, // dance of fury
			{ 268, 1 }, // dance of wind
			{ 271, 1 }, // dance of warrior
			{ 267, 1 }, // Song of Warding
			{ 349, 1 }, // Song of Renewal
			{ 264, 1 }, // song of earth
			{ 269, 1 }, // song of hunter
			{ 364, 1 }, // song of champion
			{ 1363, 1 }, // chant of victory
			{ 4699, 5 } // Blessing of Queen
		};
	}

	public static int[][] getMageBuffs() {
		return new int[][] { { 1204, 2 }, // wind walk	
            { 1040, 3 }, // shield
			{ 1035, 4 }, // Mental Shield
			{ 4351, 6 }, // Concentration
			{ 1036, 2 }, // Magic Barrier
			{ 1045, 6 }, // Bless the Body
			{ 1303, 2 }, // Wild Magic
			{ 1085, 3 }, // acumen
			{ 1062, 2 }, // besekers
			{ 1059, 3 }, // empower
			{ 1389, 3 }, // Greater Shield
			{ 273, 1 }, // dance of the mystic
			{ 276, 1 }, // dance of concentration
			{ 365, 1 }, // Dance of Siren
			{ 264, 1 }, // song of earth
			{ 268, 1 }, // song of wind
			{ 267, 1 }, // Song of Warding
			{ 349, 1 }, // Song of Renewal
			{ 1413, 1 }, // Magnus\' Chant
			{ 4703, 4 } // Gift of Seraphim
		};
	}
	

	public static Class<? extends Creature> getTestTargetClass() {
		return Monster.class;
	}

	public static int getTestTargetRange() {
		return 2000;
	}
	
	public static FakePlayer createRandomFakePlayer(int level) {
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		String playerName = FakePlayerNameManager.INSTANCE.getRandomAvailableName();

		ClassId classId = getThirdClasses().get(Rnd.get(0, getThirdClasses().size() - 1));


		final PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId);
		PcAppearance app = getRandomAppearance(template.getRace());
		FakePlayer player = new FakePlayer(objectId, template, accountName, app);

		player.setName(playerName);

		player.setAccessLevel(/*Config.DEFAULT_ACCESS_LEVEL*/0);
		PlayerNameTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
		player.setBaseClass(player.getClassId());
		setLevel(player, level);
		player.rewardSkills();

		new ArmorHelper().giveArmorsByClass(player, level);
		new WeaponHelper().giveWeaponsByClass(player, false, level);
		player.heal();

		return player;
	}

	public static List<ClassId> getSecondClasses(){
		List<ClassId> classes = new ArrayList<>();

		//Human
		classes.add(ClassId.GLADIATOR);
		classes.add(ClassId.WARLORD);
		//classes.add(ClassId.PALADIN);
		//classes.add(ClassId.DARK_AVENGER);
		classes.add(ClassId.TREASURE_HUNTER);
		classes.add(ClassId.HAWKEYE);
		classes.add(ClassId.SORCERER);
		classes.add(ClassId.NECROMANCER);
		//classes.add(ClassId.WARLOCK);
		classes.add(ClassId.BISHOP);
		//classes.add(ClassId.PROPHET);

		//Elf
		//classes.add(ClassId.TEMPLE_KNIGHT);
		//classes.add(ClassId.SWORD_SINGER);
		classes.add(ClassId.PLAINS_WALKER);
		classes.add(ClassId.SILVER_RANGER);
		classes.add(ClassId.SPELLSINGER);
		//classes.add(ClassId.ELEMENTAL_SUMMONER);
		//classes.add(ClassId.ELVEN_ELDER);

		//DarkElf
		//classes.add(ClassId.SHILLIEN_KNIGHT);
		//classes.add(ClassId.BLADEDANCER);
		classes.add(ClassId.ABYSS_WALKER);
		classes.add(ClassId.PHANTOM_RANGER);
		classes.add(ClassId.SPELLHOWLER);
		//classes.add(ClassId.PHANTOM_SUMMONER);
		//classes.add(ClassId.SHILLIEN_ELDER);

		//Orc
		classes.add(ClassId.DESTROYER);
		classes.add(ClassId.TYRANT);
		classes.add(ClassId.OVERLORD);
		//classes.add(ClassId.WARCRYER);

		//Dwarf
		//classes.add(ClassId.BOUNTY_HUNTER);
		//classes.add(ClassId.WARSMITH);

		return classes;
	}

	public static List<ClassId> getThirdClasses() {
		// removed summoner classes because fuck those guys
		List<ClassId> classes = new ArrayList<>();

		/*
		 * classes.add(ClassId.EVAS_SAINT); classes.add(ClassId.SHILLIEN_TEMPLAR);
		 * classes.add(ClassId.SPECTRAL_DANCER); classes.add(ClassId.GHOST_HUNTER);
		 * 
		 * classes.add(ClassId.PHOENIX_KNIGHT);
		 * classes.add(ClassId.HELL_KNIGHT);
		 * 
		 * classes.add(ClassId.HIEROPHANT); classes.add(ClassId.EVAS_TEMPLAR);
		 * classes.add(ClassId.SWORD_MUSE);
		 * 
		 * classes.add(ClassId.DOOMCRYER); classes.add(ClassId.FORTUNE_SEEKER);
		 * classes.add(ClassId.MAESTRO);
		 */

		// classes.add(ClassId.ARCANA_LORD);
		// classes.add(ClassId.ELEMENTAL_MASTER);
		// classes.add(ClassId.SPECTRAL_MASTER);
		// classes.add(ClassId.SHILLIEN_SAINT);

		classes.add(ClassId.SAGGITARIUS);
		classes.add(ClassId.ARCHMAGE);
		classes.add(ClassId.SOULTAKER);
		classes.add(ClassId.MYSTIC_MUSE);
		classes.add(ClassId.STORM_SCREAMER);
		classes.add(ClassId.MOONLIGHT_SENTINEL);
		classes.add(ClassId.GHOST_SENTINEL);
		classes.add(ClassId.ADVENTURER);
		classes.add(ClassId.WIND_RIDER);
		classes.add(ClassId.DOMINATOR);
		classes.add(ClassId.TITAN);
		classes.add(ClassId.CARDINAL);
		classes.add(ClassId.DUELIST);
		classes.add(ClassId.GRAND_KHAVATARI);
		classes.add(ClassId.DREADNOUGHT);
		
		return classes;
	}

	public static Map<ClassId, Class<? extends FakePlayerAI>> getAllAIs() {
		Map<ClassId, Class<? extends FakePlayerAI>> ais = new HashMap<>();
		ais.put(ClassId.STORM_SCREAMER, StormScreamerAI.class);
		ais.put(ClassId.MYSTIC_MUSE, MysticMuseAI.class);
		ais.put(ClassId.ARCHMAGE, ArchmageAI.class);
		ais.put(ClassId.SOULTAKER, SoultakerAI.class);
		ais.put(ClassId.SAGGITARIUS, SaggitariusAI.class);
		ais.put(ClassId.MOONLIGHT_SENTINEL, MoonlightSentinelAI.class);
		ais.put(ClassId.GHOST_SENTINEL, GhostSentinelAI.class);
		ais.put(ClassId.ADVENTURER, AdventurerAI.class);
		ais.put(ClassId.WIND_RIDER, WindRiderAI.class);
		ais.put(ClassId.GHOST_HUNTER, GhostHunterAI.class);
		ais.put(ClassId.DOMINATOR, DominatorAI.class);
		ais.put(ClassId.TITAN, TitanAI.class);
		ais.put(ClassId.CARDINAL, CardinalAI.class);
		ais.put(ClassId.DUELIST, DuelistAI.class);
		ais.put(ClassId.GRAND_KHAVATARI, GrandKhavatariAI.class);
		ais.put(ClassId.DREADNOUGHT, DreadnoughtAI.class);

		//Second classes
		ais.put(ClassId.SPELLHOWLER, StormScreamerAI.class);
		ais.put(ClassId.SPELLSINGER, MysticMuseAI.class);
		ais.put(ClassId.SORCERER, ArchmageAI.class);
		ais.put(ClassId.NECROMANCER, SoultakerAI.class);
		ais.put(ClassId.HAWKEYE, SaggitariusAI.class);
		ais.put(ClassId.SILVER_RANGER, MoonlightSentinelAI.class);
		ais.put(ClassId.TREASURE_HUNTER, AdventurerAI.class);
		ais.put(ClassId.PLAINS_WALKER, WindRiderAI.class);
		ais.put(ClassId.ABYSS_WALKER, GhostHunterAI.class);
		ais.put(ClassId.OVERLORD, DominatorAI.class);
		ais.put(ClassId.DESTROYER, TitanAI.class);
		ais.put(ClassId.BISHOP, CardinalAI.class);
		ais.put(ClassId.GLADIATOR, DuelistAI.class);
		ais.put(ClassId.TYRANT, GrandKhavatariAI.class);
		ais.put(ClassId.WARLORD, DreadnoughtAI.class);

		return ais;
	}

	public static PcAppearance getRandomAppearance(ClassRace race) {

		Sex randomSex = Rnd.get(1, 2) == 1 ? Sex.MALE : Sex.FEMALE;
		int hairStyle = Rnd.get(0, randomSex == Sex.MALE ? 4 : 6);
		int hairColor = Rnd.get(0, 3);
		int faceId = Rnd.get(0, 2);

		return new PcAppearance((byte) faceId, (byte) hairColor, (byte) hairStyle, randomSex);
	}

	public static void setLevel(FakePlayer player, int level) {
		if (level >= 1 && level <= Experience.MAX_LEVEL) {
			long pXp = player.getExp();
			long tXp = Experience.LEVEL[level];

			if (pXp > tXp)
				player.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				player.addExpAndSp(tXp - pXp, 0);
		}
	}

	public static Class<? extends FakePlayerAI> getAIbyClassId(ClassId classId) {
		Class<? extends FakePlayerAI> ai = getAllAIs().get(classId);
		if (ai == null)
			return FallbackAI.class;

		return ai;
	}
}
