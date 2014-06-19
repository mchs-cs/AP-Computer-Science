package battle.pack;

import battle.pack.AbstractBattleBug;

public class BasicBattleBug extends AbstractBattleBug {
	private String name="BasicBattleBug";
	public void setName(String name) {this.name=name;}
	public String getName() {return name;}
	public String getCreator() {return "Mr. Braskin";}
	public long getVersion() {return 0;}
	public void resetBug() {
	}
	public Act act() {
		if (myInformer().haveBonus()) {
			return Act.USE_BONUS;
		} else if (myInformer().bonusInFront()) {
			return Act.GET_BONUS;
		} else if (myInformer().foeInFront()) {
				return Act.ATTACK;
		}
		int dirToTarget=myInformer().getTargetsByPathDistance().get(0).getrelDirectionTo();
		if (dirToTarget>0) {
			return Act.TURN_CLOCKWISE;
		} else if (dirToTarget<0) {
			return Act.TURN_COUNTER_CLOCKWISE;
		}
		return Act.MOVE;
	}
}