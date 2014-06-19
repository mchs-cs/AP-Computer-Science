package battle.pack;

import StaticLibrary.StaticLibrary;
import info.gridworld.grid.*;

public class BonusTarget
{
	private BonusActor bonus;
	private Bactor fromB;

	public BonusTarget(Bactor fromB, BonusActor bonus)
	{
		this.bonus = bonus;
		this.fromB = fromB;
	}
	/**
	 * Gets the location of the target 
	 * @return the location of the target
	 */
	public Location getLocation()
	{
		return this.bonus.getLocation();
	}
	/**
	 * Gets the target's enumerated Bonus type. Use the Constants within the Bonus class to process the data.
	 * @return the target's enumerated Bonus type
	 */
	public Bonus getEffect()
	{
		return this.bonus.getEffect();
	}
	public String toString() {
		return this.bonus.toString();
	}
	/**
	 * Gets the absolute direction toward this target. Absolute direction is the direction that is used within setDirection() in GridWorld.
	 * @return the absolute direction towards this target
	 */
	public int getDirectionTo() {
		int directionTo = 0;
		if (this.bonus==null) return 0;
		else {
			Location tLoc = this.bonus.getLocation();
			Location fLoc = this.fromB.getLocation();
			if (tLoc != null && fLoc != null){
				directionTo = fLoc.getDirectionToward(tLoc);
			}
			return directionTo;
		}
	}
	/**
	 * Gets the relative direction to the target. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the target.
	 * @return the relative direction towards the target, 0 if the target is invalid.
	 */
	public int getRelDirectionTo() {
		if (this.bonus==null) return 0;
		else {
			Location tLoc = this.bonus.getLocation();
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
	/**
	 * Gets the relative direction to the target. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the target.
	 * @return the relative direction towards the target, 0 if the target is invalid.
	 */
	public int getrelDirectionTo() {
		return getRelDirectionTo();
	}
	
	private int stepsTo(Location t) {
		int steps=0;
		if (this.fromB.getGrid()!=null && this.bonus.getGrid()!=null) {
			Location myLoc=this.fromB.getLocation();
			if (myLoc!=null) {
				int directionToT=myLoc.getDirectionToward(t);
				Location nextLoc=myLoc.getAdjacentLocation(directionToT);
				while (!nextLoc.equals(this.bonus.getLocation())) {
					steps++;
					directionToT=nextLoc.getDirectionToward(t);
					nextLoc=nextLoc.getAdjacentLocation(directionToT);
				}
			}
		}
		return steps;
	}
	/**
	 * <em>When a target is being stored between turns, it is important to make sure it is still valid before trying to use the Target methods</em>
	 * @return true if this is still a valid target. A target becomes invalid upon being removed from the board.
	 */
	public boolean isValid()
	{
		if (bonus.getGrid() == null) return false;
		else if (fromB.getGrid() == null) return false;
		else if (!fromB.getGrid().equals(bonus.getGrid())) return false;
		else return true;
	}
	/**
	 * Gets the number of steps it would take to end up adjacent to and facing this target.
	 * Example: A target located one square behind a bug would have a step distance of 4 because it would require four turns to face that target.
	 * @return the number of steps it would take to end up adjacent to and facing this target.
	 */
	public int getStepsDistance() {
		@SuppressWarnings("unused")
		Location sLoc = this.fromB.getLocation();
		Location tLoc = this.bonus.getLocation();
		int directionTo = this.getDirectionTo();
		int turnTowards=StaticLibrary.degreesRel(this.fromB.getDirection(), directionTo);
		int turnDistance=Math.abs(turnTowards / 45);

		int gridDistance=this.stepsTo(tLoc);
		int totalStepsDistance = turnDistance + gridDistance;
		return totalStepsDistance;
	}
	@SuppressWarnings("unused")
	private int doStep(Location loc, Location tLoc, int stepsSoFar, int curDirection) {
		int directionToTarget=StaticLibrary.degreesRel(curDirection, loc.getDirectionToward(tLoc));
		System.out.println("Current Location=" + loc.toString() + ", stepsSoFar=" + stepsSoFar + ", currentDirection=" + curDirection + ", relative Direction to target=" + directionToTarget);
		if (directionToTarget==0) { // Facing the correct direction, move forward
			Location nextLoc=loc.getAdjacentLocation(curDirection);
			if (nextLoc.equals(tLoc)) { // If the next step would put us at the Target, then we have reached our destination/base case
				return stepsSoFar;
			} else { // Otherwise we move forward
				stepsSoFar=doStep(nextLoc, tLoc, stepsSoFar+1, curDirection);
			}
		} else {// Need to turn to face our target
			if (directionToTarget<0) { // Relative direction is negative, turn counter-clockwise
				stepsSoFar=doStep(loc, tLoc, stepsSoFar+1, curDirection-45);
			} else { // Relative direction is positive, turn clockwise
				stepsSoFar=doStep(loc, tLoc, stepsSoFar+1, curDirection+45);				
			}
		}
		return stepsSoFar;
	}
	// Addendum 1 Methods
	/**
	 * Gets the current row the target bonus is in.
	 * @return the current row of the target, a value between 0 and the maximum number of rows - 1, inclusive.
	 */
	public int getRow() {
		return this.bonus.getLocation().getRow();
	}
	/**
	 * Gets the current column the target bonus is in.
	 * @return the current column of the target, a value between 0 and the maximum number of columns - 1, inclusive.
	 */
	public int getCol() {
		return this.bonus.getLocation().getCol();
	}
	// Addendum 2 Methods
	public int getPathDistance() {
		return new Path(this.fromB.getLocation(), this.fromB.getDirection(), this.getLocation()).size();		
	}


}
