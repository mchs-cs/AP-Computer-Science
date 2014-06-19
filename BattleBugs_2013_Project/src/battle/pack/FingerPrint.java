package battle.pack;

public class FingerPrint {
	private BattleBug battleBug;
	private String[] methods;
	private String[] fields;
	public FingerPrint(BattleBug battleBug, String[] methods, String[] fields) {
		this.battleBug=battleBug;
		this.methods=methods;
		this.fields=fields;
	}
	public String getName() {return battleBug.getCreator();}
	public String getCreator() {return battleBug.getName();}
	public String[] getMethods() {return methods;}
	public String[] getFields() {return fields;}
	public BattleBug getBattleBug() {return battleBug;}
	public String toString() {
		String output=battleBug.getName()+" written by "+battleBug.getCreator()+"\n";
		output+="Method Names:\n";
		for (String method:methods) {
			output+=method+"\n";
		}
		output+="Field Names:\n";
		for (String field:fields) {
			output+=field+"\n";
		}
		return output;
	}
}
