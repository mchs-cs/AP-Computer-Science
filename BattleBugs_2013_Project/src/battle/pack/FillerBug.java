package battle.pack;
import java.util.ArrayList;

import battle.pack.Informer.DistanceMeasure;
import StaticLibrary.StaticLibrary;

public class FillerBug extends AbstractBattleBug {

	private String name;
	private BonusPath bonusPath;
	private TargetPath targetPath;
	private Path thePath;
	private int turn=0;
	private boolean avoidanceTurned;
	public final double MERCY_PERCENT=0.25;

	public FillerBug() {
		super();
		this.name="BraskinBug";
	}

	public void resetBug() { // Before a round of combat starts, this method get's called, so reset appropriate variables here
		this.bonusPath=new BonusPath();
		this.targetPath=new TargetPath();
		this.thePath=new Path();
		this.turn=0;
		this.avoidanceTurned=false;
	}

	public void setName(String name) {
		this.name=name;
	}
	public void selectTarget() {
		print("Selecting a Target and getting a path");
		ArrayList<Target> targets = this.myInformer().getTargetsByPathDistance();
		for (int i=targets.size()-1; i>=0; i--) {
			Target target=targets.get(i);
			if (target.getTargetHealth()<(target.getTargetMaxHealth()*this.MERCY_PERCENT)) {
				targets.remove(i);
				print("Ignoring " + target.getName());
			}
		}
		if (targets.size()>0) {
			this.targetPath=this.myInformer().getTargetPath(targets.get(0));
			print("Selected: " + this.targetPath.pathAsShortString());
		}
	}
	public void selectBonus() {
		print("Selecting a Bonus and getting a path");
		ArrayList<BonusTarget> bonuses = this.myInformer().getBonusTargetList(DistanceMeasure.PATH);
		if (bonuses.size()>0) {
			this.bonusPath=this.myInformer().getBonusPath(bonuses.get(0));
			print("Moving to " + bonuses.get(0).getEffect().toString() + " using path:");
			print(this.bonusPath.pathAsShortString());
		} else {
			this.bonusPath=new BonusPath();
			print("No bonuses on the board!");
		}
	}
	private void summarizePaths() {
		print("Paths:");
		String s="\tBonus: ";
		if (this.bonusPath!=null && this.bonusPath.isValid()) {
			s+=this.bonusPath.pathAsShortString();
		} else if (this.bonusPath==null) {
			s+="Null";
		} else if (this.bonusPath.isValid()==false) {
			s+="Invalid";
		} else {
			s+="ERROR: Impossible \"else\" statement in summarizePaths()-BonusPath!";
		}
		print(s);
		s="\tTarget: ";
		if (this.targetPath!=null && this.targetPath.isValid()) {
			if (this.targetPath.isPathChanged()) {
				s+="[Target Moved]";
			}
			s+=this.targetPath.pathAsShortString();
		} else if (this.targetPath==null) {
			s+="Null";
		} else if (this.targetPath.isValid()==false) {
			s+="Invalid";
		} else {
			s+="ERROR: Impossible \"else\" statement in summarizePaths()-TargetPath!";
		}
		print(s);
		s="\tThe Path: ";
		if (this.thePath!=null && this.thePath.isValid()) {
			s+=this.thePath.pathAsShortString();
		} else if (this.thePath==null) {
			s+="Null";
		} else if (this.thePath.isValid()==false) {
			s+="Invalid";
		} else {
			s+="ERROR: Impossible \"else\" statement in summarizePaths()-ThePath!";
		}
		print(s);
	}
	public Act act() {
		this.turn++;
		print(StaticLibrary.padString("#", 20, "#"));
		print(this.getName() + "'s Turn #" + this.turn + " Begins");
		print("\nBegining \"Goal\" selection phase:");
		if (this.targetPath.isValid()) {
			Target target = this.targetPath.getTarget();
			if (target.getTargetHealth()<(target.getTargetMaxHealth()*this.MERCY_PERCENT)) {
				print("On my way to attack " + target.getName() + " but the MERCY RULE came into effect");
				this.targetPath=new TargetPath();
			}
		}
		this.summarizePaths();
		if (this.bonusPath==null || this.bonusPath.isValid()==false) {
			this.selectBonus();
		}
		if (this.targetPath==null || this.targetPath.isValid()==false) {
			this.selectTarget();
		}
		if (this.targetPath.isValid() && this.bonusPath.isValid()) {
			print("Both TargetPath & BonusPath are valid");
			if (this.targetPath.size()<=this.bonusPath.size()) {
				this.thePath=this.targetPath;
				print("TargetPath is at least as short as BonusPath, eliminating BonusPath");
				this.bonusPath=new BonusPath();
			} else {
				this.thePath=this.bonusPath;
				print("BonusPath is shorter than TargetPath, eliminating TargetPath");
				this.targetPath=new TargetPath();
			}
		} else if (this.targetPath.isValid()) {
			this.thePath=this.targetPath;
			print("Only TargetPath is valid");
		} else if (this.bonusPath.isValid()) {
			this.thePath=this.bonusPath;
			print("Only BonusPath is valid");
		} else {
			this.thePath=new Path();
			print("No Valid Path!");
		}
		print("Begin \"Action\" phase:");
		this.summarizePaths();
		Act step=null;
		if (this.thePath.isValid()) {
			step=this.thePath.nextStep();
		}
		if (step!=null) {
			print("Step=" + step.toString());
		} else {
			print("Step=NONE");
		}
		if (this.myInformer().haveBonus()) {
			print("Using " + this.myInformer().getEffect().toString() + " bonus");
			return Act.USE_BONUS;
		} else if (this.myInformer().bonusInFront()) {
			print("Getting a " + this.myInformer().targetBonusInFront().getEffect().toString() + " bonus");
			return Act.GET_BONUS;
		} else if (this.avoidanceTurned) {
			print("Completing Avoidance Turn");
			if (this.myInformer().canMove()) {
				print("Avoidance Turn complete");
				this.avoidanceTurned=false;
				return Act.MOVE;
			} else {
				print("Avoidance Completing Blocked, turn again!");
				return avoidanceTurn();
			}
		} else if (this.myInformer().foeInFront()==true) { // An opponent is in front of me, Attack!
			Target target=this.myInformer().targetFoeInFront();
			if (target.getTargetHealth()<(target.getTargetMaxHealth()*this.MERCY_PERCENT)) {
				print("Ignoring pitiful target before me!");
				return avoidanceTurn();
			} else {
				print("Attacking " + target.getName());
				return Act.ATTACK;
			}
		} else if (step!=null) {
			if (step.equals(Act.MOVE) && this.myInformer().canMove()) {
				this.thePath.completedStep();
				return Act.MOVE;
			} else if (step.equals(Act.MOVE) && !this.myInformer().canMove()) {
				print("Path Error: Path indicates MOVE but I can't move!");
				this.bonusPath=new BonusPath();				
				this.targetPath=new TargetPath();
				return Act.DEFEND;
			} else if (step.equals(Act.TURN_CLOCKWISE)) {
				this.thePath.completedStep();
				return Act.TURN_CLOCKWISE;
			} else if (step.equals(Act.TURN_COUNTER_CLOCKWISE)) {
				this.thePath.completedStep();
				return Act.TURN_COUNTER_CLOCKWISE;
			} else {
				print("Path Error: Step was not a MOVE or TURN!");
				this.bonusPath=new BonusPath();
				this.targetPath=new TargetPath();
				return Act.DEFEND;
			}
		}
		print("No path, and nothing else to do, I'll defend for this turn");
		return Act.DEFEND;
	}
	private Act avoidanceTurn() {
		int relDirection=this.myInformer().getOpenRelDirection();
		if (relDirection<-180 || relDirection>180) { // No open direction to turn!
			print("No where to turn, defending!");
			return Act.DEFEND;
		} else if (relDirection<0) { // I need to turn Counter-Clockwise to face my target
			this.bonusPath=new BonusPath();
			this.targetPath=new TargetPath();
			this.avoidanceTurned=true;
			return Act.TURN_COUNTER_CLOCKWISE;
		} else if (relDirection>0) { // I need to turn Counter-Clockwise to face my target
			this.bonusPath=new BonusPath();
			this.targetPath=new TargetPath();
			this.avoidanceTurned=true;
			return Act.TURN_CLOCKWISE;
		} else { // No other possible choice, I must need to turn Clockwise to face my target
			print("ERROR: Impossible \"else\" statement in Act() after getOpenRelDirection()!");
			return Act.DEFEND;
		}				
	}
	// Methods Not Yet Part of Main Project
	public void doTestMethod(){
	}	

	public String getName() {
		return this.name;
	}

	public String getCreator() {
		return "Braskin";
	}

	public long getVersion() {
		return 5;
	}

}
