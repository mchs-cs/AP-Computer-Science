package battle.pack;

public enum Attribute
{
	POINTS(true,100), ATTACK(true,40), DEFENSE(true,40), HEALTH(true,80), MAXHEALTH(true,80), DIRECTION(false,0), DISTANCE(false,0);
	
	private final boolean INCLUDE_IN_CHART;
	private final int WIDTH;
	private Attribute(boolean INCLUDE_IN_CHART,int WIDTH) {
		this.INCLUDE_IN_CHART=INCLUDE_IN_CHART;
		this.WIDTH=WIDTH;
	}
	
	public final boolean INCLUDE_IN_CHART() {return this.INCLUDE_IN_CHART;}
	public final int WIDTH() {return this.WIDTH;}

	public Attribute get (int index)
	{
		Attribute[] attrib = Attribute.values();
		return attrib[index];
	}
	
	public Attribute[] chartValues() {
		Attribute[] allAttributes=Attribute.values();
		int chartAtributeCount=0;
		for (int i=0; i<allAttributes.length; i++) {
			if (allAttributes[i].INCLUDE_IN_CHART) {
				chartAtributeCount++;
			}
		}
		Attribute[] chartAttributes = new Attribute[chartAtributeCount];
		chartAtributeCount=0;
		for (int i=0; i<allAttributes.length; i++) {
			if (allAttributes[i].INCLUDE_IN_CHART) {
				chartAttributes[chartAtributeCount]=allAttributes[i];
				chartAtributeCount++;
			}
		}
		
		return chartAttributes;
	}
}