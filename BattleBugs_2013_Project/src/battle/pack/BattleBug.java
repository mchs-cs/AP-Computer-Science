package battle.pack;

public interface BattleBug {
	void setMyInformer(Informer myInformer);
	public String getName();
	public String getCreator();
	public long getVersion();
	public void resetBug();
	public String getLongName();
	public void print(String s);
	public String getShortName();
	public String getShortCreator();
	
	public Act act();
	public void taunt(Taunt taunt);
	public void taunt(Taunt taunt, Target target);
	public void doTestMethod();
}