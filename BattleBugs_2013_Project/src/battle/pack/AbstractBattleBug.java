package battle.pack;

public abstract class AbstractBattleBug implements BattleBug {

	private Informer myInformer;

	public AbstractBattleBug() {
		this.myInformer=null;
		this.resetBug();
	}

	public final void setMyInformer(Informer myInformer) {
		if (myInformer.getBattleBug().equals(this)) {
			this.myInformer=myInformer;
		}
	}
	public void print(String s) {
		if (this.myInformer!=null) {
			this.myInformer.print(s);
		}
	}
	public final Informer myInformer() {
		return this.myInformer;
	}
	@Override
	abstract public String getName();
	public final String getShortName() {
		String s="";
		try {
			s=getName();
		} catch(Error e) {
			s=this.getClass().getSimpleName();
		}
		if (s.length()>22) s=s.substring(0, 22);
		return s;
	}

	@Override
	abstract public String getCreator();
	public final String getShortCreator() {
		String s="";
		try {
			s=getCreator();
		} catch(Error e) {
			s=this.getClass().getName();
		}
		if (s.length()>22) s=s.substring(0, 22);
		return s;
	}

	@Override
	abstract public long getVersion();

	@Override
	abstract public void resetBug();

	public final boolean equals(Object obj) {
		if (obj instanceof BattleBug) {
			try {
				BattleBug b=(BattleBug)obj;
				return getCreator().equals(b.getCreator()) && getName().equals(b.getName()) && getVersion()==b.getVersion();
			} catch(NullPointerException e) {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}
	@Override
	public final String getLongName() {
		return this.getClass().getSimpleName() + this.getName();
	}

	@Override
	abstract public Act act();

	@Override
	public void doTestMethod() {
		return;
	}
	public final void taunt(Taunt taunt) {

	}
	public final void taunt(Taunt taunt, Target target) {

	}
}
