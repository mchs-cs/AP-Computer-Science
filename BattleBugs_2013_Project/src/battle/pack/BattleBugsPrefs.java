package battle.pack;

import java.io.Serializable;

public class BattleBugsPrefs implements Serializable {
	WindowPrefs battleBugsWindowPrefs;
	WindowPrefs reporterWindowPrefs;
	
	public WindowPrefs getBattleBugsWindowPrefs() {
		return battleBugsWindowPrefs;
	}
	public void setBattleBugsWindowPrefs(WindowPrefs battleBugsWindowPrefs) {
		this.battleBugsWindowPrefs = battleBugsWindowPrefs;
	}
	public WindowPrefs getReporterWindowPrefs() {
		return reporterWindowPrefs;
	}
	public void setReporterWindowPrefs(WindowPrefs reporterWindowPrefs) {
		this.reporterWindowPrefs = reporterWindowPrefs;
	}
}
