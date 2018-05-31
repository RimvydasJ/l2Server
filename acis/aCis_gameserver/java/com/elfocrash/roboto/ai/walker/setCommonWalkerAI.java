package com.elfocrash.roboto.ai.walker;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.model.WalkNode;
import com.elfocrash.roboto.model.WalkerType;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class setCommonWalkerAI extends WalkerAI {
	private boolean isLinear = false;
	public setCommonWalkerAI(FakePlayer character) {
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

			case 1:
				// "Elven Village";

			case 2:
				// "Dark Elven Village";

			case 3:
				// "Orc Village";

			case 4:
				// "Dwarven Village";

			case 5:
				// "Town of Gludio";

			case 6:
				// "Gludin Village";

			case 7:
				// "Town of Dion";

			case 8:
				//Town of Giran
				isLinear = false;
				if(_fakePlayer.getLevel() >= 78) {
					if (pretendWalkingInTown()) {
						giranWalkNodes();
					} else {
						giranTpNode();
					}
					if (!_fakePlayer.isInsideZone(ZoneId.TOWN)) {
						giranZoneFarm();
					}
				}
				else {
					if (pretendWalkingInTown()) {
						giranWalkNodes();
					} else {
						giranTpNode();
					}
				}

			case 9:
				// "Town of Oren";

			case 10:
				// "Town of Aden";

			case 11:
				// "Hunters Village";

			case 12:
				// "Giran Harbor";

			case 13:
				isLinear = false;
				farmEvaGarden();

			case 14:
				// "Rune Township";

			case 15://Goddard
				if(_fakePlayer.getLevel() <78){
				isLinear = false;
				lvlUpZone();
				}
				else {
					isLinear = false;
					lvlUpZoneLeave();
				}

			case 16:
				// "Town of Schuttgart";

			case 17:
				// "Floran Village";

			case 18:
				// "Primeval Isle";

			default:
				// "Town of Aden";
		}
	}

	private void giranWalkNodes() {
		_walkNodes.add(new WalkNode(82248, 148600, -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82072, 147560, -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82792, 147832, -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81352, 149688, -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81064, 147784, -3464, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(82792, 149384, -3464, Rnd.get(1, 40)));
		//_walkNodes.add(new WalkNode(87016, 148632, -3400, Rnd.get(1, 40)));
	}

	private void giranTpNode() {
		_walkNodes.add(new WalkNode(83384, 147928, -3400, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(83656, 149272, -3400, Rnd.get(1, 20)));
		_walkNodes.add(new WalkNode(81304, 147592, -3463, Rnd.get(1, 20)));
	}

	private void giranZoneFarm() {
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
		return Rnd.nextDouble() < 0.5;
}
}
