package battle.pack;


public class PlaceHolderBug extends AbstractBattleBug {
	private String name="PlaceHolderBug";
	public void setName(String name) {this.name=name;}
	public String getName() {return name;}
	public String getCreator() {return "Mr. Braskin";}
	public long getVersion() {return 1;}
	public void resetBug() {
	}
	public Act act() {
		return Act.TURN_CLOCKWISE;
	}
}