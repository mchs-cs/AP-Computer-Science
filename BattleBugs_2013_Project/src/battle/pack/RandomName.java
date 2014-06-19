package battle.pack;

import java.util.ArrayList;

public class RandomName {
	private int nameIndex;
	private ArrayList<String> names;
	public RandomName() {
		super();
		this.names = new ArrayList<String>();
		names.add("Odin");
		names.add("Loki");
		names.add("Thor");
		names.add("Balder");
		names.add("Bragi");
		names.add("Freyja");
		names.add("Frigga");
		names.add("Heimdall");
		names.add("Tyr");
		names.add("Aphrodite");
		names.add("Apollo");
		names.add("Ares");
		names.add("Artemis");
		names.add("Athena");
		names.add("Demeter");
		names.add("Dionysus");
		names.add("Hades");
		names.add("Hephaestus");
		names.add("Hera");
		names.add("Hermes");
		names.add("Hestia");
		names.add("Poseidon");
		names.add("Zeus");
		names.add("USS Enterprise");
		names.add("USS Vengeance");
		names.add("USS Leyte Gulf");
		names.add("USS Rabin");
		names.add("USS Dakota");
		names.add("USS Bradbury");
		names.add("USS Cheyenne");
		names.add("USS Constitution");
		names.add("USS Constellation");
		names.add("USS Farragut");
		names.add("USS Yorktown");
		
		this.nameIndex=(int)(Math.random()*names.size());
	}
	public String nextName() {
		this.nameIndex++;
		this.nameIndex=this.nameIndex % (this.names.size()-1);
		return this.names.get(this.nameIndex);
	}	

}
