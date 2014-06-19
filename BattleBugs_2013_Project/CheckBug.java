package battle.game;

import java.io.IOException;
import java.net.URL;

import battle.pack.AbstractBattleBug;
import battle.pack.ActToken;

public class CheckBug extends AbstractBattleBug{

	@Override
	public String getName() {
		return "Check Bug";
	}

	@Override
	public String getCreator() {
		return "Anthony, Tommy";
	}

	@Override
	public long getVersion() {
		return 1;
	}

	@Override
	public void resetBug() {
		
		
	}

	@Override
	public void act(ActToken myToken) {
		URL url;
		try {
			url = new URL("http://www.google.com/");
			url.openConnection().getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
