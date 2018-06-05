package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.helpers.ZoneChecker;
import com.elfocrash.roboto.model.WalkNode;
import com.elfocrash.roboto.model.WalkerType;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class CommonWalkerAi extends WalkerAI {
	private boolean isLinear = false;
	public CommonWalkerAi(FakePlayer character) {
		super(character);
	}

	@Override
	protected WalkerType getWalkerType() {
		if(!isLinear){
			return WalkerType.RANDOM;}
		else {
			return WalkerType.LINEAR;
		}
	}

	@Override
	protected void setWalkNodes(int townId) {
		switch (townId) {
			case 0:
				// "Talking Island Village";
			break;
			case 1:
				// "Elven Village";
				break;
			case 2:
				// "Dark Elven Village";
				break;
			case 3:
				// "Orc Village";
				break;
			case 4:
				// "Dwarven Village";
				break;
			case 5:
				// "Town of Gludio";
				break;
			case 6:
				// "Gludin Village";
				break;
			case 7:
				// "Town of Dion";
				break;
			case 8:
				//Town of Giran
				isLinear = false;
				if(_fakePlayer.getLevel() >= 78) {
					if (pretendWalkingInTown()) {
						giranWalkNodes();
					} else {
						giranTpNode();
					}
					if (ZoneChecker.checkIfInLoa(_fakePlayer)) {
						loaFarmZone();
					}
				}
				else {
					if (pretendWalkingInTown()) {
						giranWalkNodes();
					} else {
						giranTpNode();
					}
				}
				break;
			case 9:
				// "Town of Oren";
				break;
			case 10:
				// "Town of Aden";
				break;
			case 11:
				// "Hunters Village";
				break;
			case 12:
				// "Giran Harbor";
				break;
			case 13:
				// Goddard
				break;
			case 14:
				// "Rune Township";
				break;
			case 15://Goddard
				break;
			case 16:
				// "Town of Schuttgart";
				break;
			case 17:
				// "Floran Village";
				break;
			case 18:
				// "Primeval Isle";
				break;
			default:
				// "Town of Aden";
				break;
		}
	}

	private void giranWalkNodes() {
		_walkNodes.add(new WalkNode(82248 + Rnd.get(-35,35), 148600 + Rnd.get(-35,35), -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82072 + Rnd.get(-35,35), 147560 + Rnd.get(-35,35), -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82792 + Rnd.get(-35,35), 147832 + Rnd.get(-35,35), -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81352 + Rnd.get(-35,35), 149688 + Rnd.get(-35,35), -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81064 + Rnd.get(-35,35), 147784 + Rnd.get(-35,35), -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82792 + Rnd.get(-35,35), 149384 + Rnd.get(-35,35), -3464, Rnd.get(1, 40)));
	}

	private void giranTpNode() {
		_walkNodes.add(new WalkNode(83384 + Rnd.get(-20,20), 147928 + Rnd.get(-20,20), -3400, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(83656 + Rnd.get(-20,20), 149272 + Rnd.get(-20,20), -3400, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81304 + Rnd.get(-20,20), 147592 + Rnd.get(-20,20), -3463, Rnd.get(1, 20)));
	}

	private void loaFarmZone() {
		_walkNodes.add(new WalkNode(130981 + Rnd.get(-50,50), 114550+ Rnd.get(-50,50), -3730, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(129352+ Rnd.get(-50,50), 113480+ Rnd.get(-50,50), -3688, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(129832+ Rnd.get(-50,50), 115176+ Rnd.get(-50,50), -3816, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(127688+ Rnd.get(-50,50), 114344+ Rnd.get(-50,50), -3816, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(130392+ Rnd.get(-50,50), 114184+ Rnd.get(-50,50), -3768, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(129640+ Rnd.get(-50,50), 114200+ Rnd.get(-50,50), -3776, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(129169+ Rnd.get(-50,50), 115032+ Rnd.get(-50,50), -3784, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(130977+ Rnd.get(-50,50), 114268+ Rnd.get(-50,50), -3721, Rnd.get(1, 20)));
	}

	private void lvlUpZone() {
		_walkNodes.add(new WalkNode(140984 + Rnd.get(-100,100), -123432+ Rnd.get(-100,100), -1904, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(141848+ Rnd.get(-100,100), -123304+ Rnd.get(-100,100), -1896, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(140625+ Rnd.get(-100,100), -122971+ Rnd.get(-100,100), -1896, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(141176+ Rnd.get(-100,100), -123272+ Rnd.get(-100,100), -1912, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(141592+ Rnd.get(-100,100), -122840+ Rnd.get(-100,100), -1912, Rnd.get(1, 20)));
	}

	private void lvlUpZoneLeave() {
		_walkNodes.add(new WalkNode(140920 + Rnd.get(-20,20), -124008+ Rnd.get(-20,20), -1904, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(141784 + Rnd.get(-20,20), -121144+ Rnd.get(-20,20), -1912, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(140536 + Rnd.get(-20,20), -120024+ Rnd.get(-20,20), -1960, Rnd.get(1, 20)));

	}

	private void farmEvaGarden() {
		_walkNodes.add(new WalkNode(84280 + Rnd.get(-100,100), 258376+ Rnd.get(-100,100), -11664, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(85176+ Rnd.get(-100,100), 257576 + Rnd.get(-100,100), -11664, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(86520+ Rnd.get(-100,100), 255816+ Rnd.get(-100,100), -11664, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(86600+ Rnd.get(-100,100), 257752+ Rnd.get(-100,100), -11664, Rnd.get(1, 20)));
	}

	private boolean pretendWalkingInTown(){
		//80proc kad eis i farm zona
		return Rnd.nextDouble() < 0.2;
	}
}
