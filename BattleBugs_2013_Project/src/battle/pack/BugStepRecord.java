package battle.pack;

public class BugStepRecord {
	private Bactor bactor;
	private Act act;
	private String bonus;
	private long version;
	private int[] attributeVals;
	
	public BugStepRecord(Bactor bactor) {
		this.bactor=bactor;		
		this.act=bactor.getLastAct();
		this.version=bactor.getBattleBug().getVersion();
		this.attributeVals=bactor.getAttributeVals();
		if (this.bactor.haveBonus()) {
			this.bonus=this.bactor.getStoredEffect().toString();
		} else {
			this.bonus="none";
		}
	}
	public String getName() {
		return this.bactor.getBattleBug().getName();
	}
	public String getCreator() {
		return this.bactor.getBattleBug().getCreator();
	}
	public String getAct() {
		return this.act.toString();
	}
	public long getVersion() {return version;}
	public int[] getAttributeVals() {
		return this.attributeVals;
	}
	public String getBonus() {
		return this.bonus;
	}
}
