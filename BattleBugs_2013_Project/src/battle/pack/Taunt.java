package battle.pack;

public enum Taunt {
	NICE_TRY(false),GOING_TO_GET_YOU(true),COMING_FOR_YOU(true),I_AM_KING_KONG(false),I_AM_KING_OF_THE_WORLD(false),RUN_FOR_IT(false);
	
	private final boolean REQUIRES_TARGET;
	private Taunt(boolean REQUIRES_TARGET) {
		this.REQUIRES_TARGET=REQUIRES_TARGET;
	}
	public final boolean REQUIRES_TARGET() {return this.REQUIRES_TARGET;}
	Taunt get(int index)
	{
		Taunt[] taunt = Taunt.values();
		return taunt[index];
	}
	public String toString() {
		String s=super.toString();
		s=s.substring(0, 1)+s.substring(1).toLowerCase();
	    s = s.replace('_', ' ');
		return s;
	}

}