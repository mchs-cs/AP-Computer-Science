package battle.pack;

import battle.pack.Reporter.TabRecord;

public class BattleRecord {
	private TabRecord tab;
	private BugStepRecord lastStep;
	private Bactor bactor;
	public BattleRecord(Bactor bactor, TabRecord tab, BugStepRecord lastStep) {
		this.tab=tab;
		this.lastStep=lastStep;
		this.bactor=bactor;
	}
	public TabRecord getTab() {
		return tab;
	}
	public void setTab(TabRecord tab) {
		this.tab = tab;
	}
	public BugStepRecord getLastStep() {
		return lastStep;
	}
	public void setLastStep(BugStepRecord lastStep) {
		this.lastStep = lastStep;
	}
	public Bactor getBactor() {
		return bactor;
	}
	public void setBactor(Bactor bactor) {
		this.bactor = bactor;
	}
	public int compareTo(BattleRecord otherBattleRecord) {
		int res=0;
		if (this.getBactor().getPoints() < otherBattleRecord.getBactor().getPoints()) {
			res=-1;
		}
		if (this.getBactor().getPoints() > otherBattleRecord.getBactor().getPoints()){
			res=1;
		}
		return res;
	}	
}
