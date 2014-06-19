package battle.pack;

import java.io.Serializable;

public class LastPrefs implements Serializable {
	private String filename;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
