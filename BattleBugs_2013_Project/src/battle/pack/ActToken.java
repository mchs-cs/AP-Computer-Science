package battle.pack;

public class ActToken {
	
	private Bactor bactor;
	private boolean tokenSpent;
	ActToken(Bactor bactor) {
		this.bactor=bactor;
		this.tokenSpent=false;
	}
	
	public void move() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.move();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to MOVE\n");
		}
	}
	public void turnClockwise() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.turnClockwise();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to TURN CLOCKWISE\n");
		}
	}
	public void turnCounterClockwise() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.turnCounterClockwise();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to TURN COUNTERCLOCKWISE\n");
		}
	}
	public void attack() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.attack();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to ATTACK\n");
		}
	}
	public void getBonus() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.getBonus();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to GET BONUS\n");
		}
	}
	
	public void useBonus() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.useBonus();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to USE BONUS\n");
		}
	}
	public void defend() {
		if (this.tokenSpent==false) {
			this.tokenSpent=true;
			this.bactor.setHasActed(true);
			this.bactor.defend();
		} else {
			bactor.updateBugOut("<-- ActToken ERROR: Token already Spent -->\n\tWhile trying to DEFEND\n");
		}
	}
}
