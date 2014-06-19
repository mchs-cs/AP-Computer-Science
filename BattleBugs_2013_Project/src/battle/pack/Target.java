package battle.pack;
import StaticLibrary.StaticLibrary;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Location;

public class Target {

	public boolean targetFacingMe() {
		int TargetFacing=this.getTargetDirection();
		info.gridworld.grid.Grid<Actor> gr=this.toB.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.toB.getLocation().getAdjacentLocation(TargetFacing);
		if (!gr.isValid(nextLoc)) return false;
		Location myLoc=this.fromB.getLocation();
		return (myLoc.getCol()==nextLoc.getCol()) && (myLoc.getRow()==nextLoc.getRow());
	}
	private Bactor fromB;
	private Bactor toB;
	/**
	 * Is not always valid. If the target was removed from the grid, Then errors will occur. Always test if it is valid before.
	 * @param fromBactor
	 * @param toBactor
	 */
	public Target(Bactor fromBactor, Bactor toBactor) {
		this.fromB=fromBactor;
		this.toB=toBactor;
	}
	/**
	 * Gets the name of the target for sending to your bug tag.
	 * @return the name of the target
	 */
	public String getTargetName() {
		return this.toB.getBattleBug().getName();
	}
	/**
	 * Gets the string name of the target's creator for sending to your bugs tab
	 * @return the name of the target's creator.
	 */
	public String getTargetCreator() {
		return this.toB.getBattleBug().getCreator();
	}
	/**
	 * Gets the version of the target's bug
	 * @return the version of the target's bug
	 */
	public long getTargetVersion() {
		return this.toB.getBattleBug().getVersion();
	}
	/**
	 * Gets the target's current health
	 * @return the target's current health
	 */
	public int getTargetHealth() {
		return this.toB.getHealth();
	}
	/**
	 * Gets the target's current attack value
	 * @return the targets attack value
	 */
	public int getTargetAttack() {
		return this.toB.getAttack();
	}
	/**
	 * @return the target's current point value if killed
	 */
	public int getTargetPointValue() {
		return this.toB.pointValue();
	}

	/**
	 * Gets the target's current defense value
	 * @return the target's current defense value
	 */
	public int getTargetDefense() {
		return this.toB.getDefense();
	}
	/**
	 * Gets the target's current maximum health value
	 * @return the target's current maximum health value
	 */
	public int getTargetMaxHealth() {
		return this.toB.getMaxHealth();
	}
	/**
	 * Gets the target's points
	 * @return the target's number of points
	 */
	public int getTargetPoints() {
		return this.toB.getPoints();
	}
	/**
	 * Gets the target's current absolute direction
	 * @return the target's current absolute direction
	 */
	public int getTargetDirection() {
		return this.toB.getDirection();
	}
	/**
	 * Gets the target's most recent act
	 * @return The last act performed by the target
	 */
	public Act getTargetsLastAct() {
		return this.toB.getLastAct();
	}
	/**
	 * <em>When a target is being stored between turns, it is important to make sure it is still valid before trying to use the Target methods</em>
	 * @return true if this is still a valid target. A target becomes invalid upon being removed from the board.
	 */
	public boolean targetValid() {
		boolean targetValid=true;
		if (this.toB.getGrid()==null) {
			targetValid=false;
		} else if (this.fromB.getGrid()==null) {
			targetValid=false;
		} else {
			targetValid = this.toB.getGrid().equals(this.fromB.getGrid());
		}
		return targetValid;
	}
	/**
	 * Gets the absolute direction towards the target, with basically the same North, South, East, West values as gridworld
	 * @return The absolute direction to the target
	 */
	public int getDirectionTo() {
		int directionTo = 0;
		if (this.toB==null) return 0;
		else {
			Location tLoc = this.toB.getLocation();
			Location fLoc = this.fromB.getLocation();
			if (tLoc != null && fLoc != null){
				directionTo = fLoc.getDirectionToward(tLoc);
			}
		return directionTo;
		}
	}
	/**
	 * Gets the target bugs name, same as getTargetName()
	 * @return the target bug's name
	 */
	public String getName() {
		if (this.toB==null) return "";
		else return this.toB.getName();
	}
	/**
	 * Gets the relative direction to the target. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the target.
	 * @return the relative direction towards the target, 0 if the target is invalid.
	 */
	public int getrelDirectionTo() {
		return this.getRelDirectionTo();
	}
	/**
	 * Gets the relative direction to the target. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the target.
	 * @return the relative direction towards the target, 0 if the target is invalid.
	 */
	public int getRelDirectionTo() {
		if (this.toB==null) return 0;
		else {
			Location tLoc = this.toB.getLocation();
			Location fLoc = this.fromB.getLocation();
			if (tLoc!=null && fLoc!=null) {
				int directionTo = fLoc.getDirectionToward(tLoc);
				int turnTowards=StaticLibrary.degreesRel(this.fromB.getDirection(), directionTo);
				return turnTowards;
			} else {
				return 0;
			}
		} 
	}
	private int stepsTo(Location t) {
		int steps=0;
		if (this.fromB.getGrid()!=null && this.toB.getGrid()!=null) {
			Location myLoc=this.fromB.getLocation();
			if (myLoc!=null) {
				int directionToT=myLoc.getDirectionToward(t);
				Location nextLoc=myLoc.getAdjacentLocation(directionToT);
				while (!nextLoc.equals(this.toB.getLocation())) {
					steps++;
					directionToT=nextLoc.getDirectionToward(t);
					nextLoc=nextLoc.getAdjacentLocation(directionToT);
				}
			}
		}
		return steps;
	}
	/**
	 * Gets the number of steps it would take to end up adjacent to and facing this target.
	 * Example: A target located one square behind a bug would have a step distance of 4 because it would require four turns to face that target.
	 * @return the number of steps it would take to end up adjacent to, and facing this target.
	 */
	public int getStepsDistance() {
		Location tLoc = this.toB.getLocation();
		int directionTo = this.getDirectionTo();
		int turnTowards=StaticLibrary.degreesRel(this.fromB.getDirection(), directionTo);
		int turnDistance=Math.abs(turnTowards / 45);
		
		int gridDistance=this.stepsTo(tLoc);
		int totalStepsDistance = turnDistance + gridDistance;
		return totalStepsDistance;
	}
	Bactor getBactor() {
		return this.toB;
	}
	Bonus getCurrentEffect() {
		return this.fromB.getStoredEffect();
	}
	// Addendum 1 Methods
	/**
	 * Gets the current row the target bug is in.
	 * @return the current row of the target, a value between 0 and the maximum number of rows - 1, inclusive.
	 */
	public int getRow() {
		return this.toB.getLocation().getRow();
	}
	/**
	 * Gets the current column the target bug is in.
	 * @return the current column of the target, a value between 0 and the maximum number of columns - 1, inclusive.
	 */
	public int getCol() {
		return this.toB.getLocation().getCol();
	}
	/**
	 * Find out whether the target has acted or not.
	 * @return true if the target has already taken it's turn this round.
	 */
	public boolean hasActed() {
		return this.toB.isHasActed();
	}
	// Addendum 2 Methods
	public Location getLocation() {
		return this.toB.getLocation();
	}
	
	// Addendum 3 Methods
	public int getPathDistance() {
		return new Path(this.fromB.getLocation(), this.fromB.getDirection(), this.getLocation()).size();		
	}

	// 2014 Addendum 1 Methods
	public String toString() {
		return this.getTargetName();
	}
}
