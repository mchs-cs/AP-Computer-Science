package battle.pack;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Location;
import StaticLibrary.StaticLibrary;

public class Objective {
	public boolean targetFacingMe() {
		int TargetFacing=this.toB.getDirection();
		info.gridworld.grid.Grid<Actor> gr=this.toB.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.toB.getLocation().getAdjacentLocation(TargetFacing);
		if (!gr.isValid(nextLoc)) return false;
		Location myLoc=this.fromB.getLocation();
		return (myLoc.getCol()==nextLoc.getCol()) && (myLoc.getRow()==nextLoc.getRow());
	}
	private Bactor fromB;
	private Bactor toB=null;
	private BonusActor bonus=null;
	/**
	 * Is not always valid. If the objective was removed from the grid, Then errors will occur. Always test if it is valid before.
	 * @param fromBactor
	 * @param toBactor
	 */
	public Objective(Bactor fromBactor, Bactor toBactor) {
		this.fromB=fromBactor;
		this.toB=toBactor;
	}
	/**
	 * Is not always valid. If the objective was removed from the grid, Then errors will occur. Always test if it is valid before.
	 * @param fromBactor
	 * @param b
	 */
	public Objective(Bactor fromBactor, BonusActor b) {
		this.fromB=fromBactor;
		this.bonus=b;
	}
	/**
	 * Gets the name of the objective for sending to your bug tag, if bonus returns null.
	 * @return the name of the objective
	 */
	public String getTargetName() {
		if(toB==null){
			return null;
		}
		return this.toB.getBattleBug().getName();
	}
	public Bonus getEffect()
	{ if(bonus==null){
		return null;
	}
		return this.bonus.getEffect();
	}
	/**
	 * Gets the string name of the objective's creator for sending to your bugs tab, returns null if bonus.
	 * @return the name of the objective's creator.
	 */
	public String getTargetCreator() {
		if(toB==null){
			return null;
		}
		return this.toB.getBattleBug().getCreator();
	}
	/**
	 * Gets the version of the objective's bug, if bonus returns 0
	 * @return the version of the objective's bug
	 */
	public long getTargetVersion() {
		if(toB==null){
			return 0;
		}
		return this.toB.getBattleBug().getVersion();
	}
	/**
	 * Gets the objective's current health, if objective is a bonus returns 0
	 * @return the objective's current health
	 */
	public int getTargetHealth() {
		if(toB==null){
			return 0;
		}
		return this.toB.getHealth();
	}
	/**
	 * Gets the objective's current attack value, returns 0 if it is a bonus.
	 * @return the objective's attack value
	 */
	public int getTargetAttack() {
		if(toB==null){
			return 0;
		}
		return this.toB.getAttack();
	}
	/**
	 * @return the objective's current point value if killed, if it is a bonus returns 0
	 */
	public int getTargetPointValue() {
		if(toB==null){
			return 0;
		}
		return this.toB.pointValue();
	}

	/**
	 * Gets the objective's current defense value
	 * @return the target's current defense value
	 */
	public int getTargetDefense() {
		if(toB==null){
			return 0;
		}
		return this.toB.getDefense();
	}
	/**
	 * Gets the objective's current maximum health value, if it is a bonus returns 0.
	 * @return the objective's current maximum health value
	 */
	public int getTargetMaxHealth() {
		if(toB==null){
			return 0;
		}
		return this.toB.getMaxHealth();
	}
	/**
	 * Gets the objective's points, if it is a bonus returns 0.
	 * @return the objective's number of points
	 */
	public int getTargetPoints() {
		if(toB==null){
			return 0;
		}
		return this.toB.getPoints();
	}
	/**
	 * Gets the objective's current absolute direction, returns 0 if it is a bonus.
	 * @return the objective's current absolute direction
	 */
	public int getTargetDirection() {
		if(toB==null){
			return 0;
		}
		return this.toB.getDirection();
	}
	/**
	 * Gets the objective's most recent act, returns null if it is a bonus.
	 * @return The last act performed by the objective.
	 */
	public Act getTargetsLastAct() {
		if(toB==null){
			return null;
		}
		return this.toB.getLastAct();
	}
	/**
	 * <em>When a objective is being stored between turns, it is important to make sure it is still valid before trying to use the Objective methods</em>
	 * @return true if this is still a valid objective. A objective becomes invalid upon being removed from the board.
	 */
	public boolean targetValid() {
		if(toB==null){
			if (bonus.getGrid() == null) return false;
			else if (fromB.getGrid() == null) return false;
			else if (!fromB.getGrid().equals(bonus.getGrid())) return false;
			else return true;
		}
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
	 * Gets the absolute direction towards the objective, with basically the same North, South, East, West values as gridworld
	 * @return The absolute direction to the objective
	 */
	public int getDirectionTo() {
		int directionTo = 0;
		if(toB==null){
			directionTo = 0;
				Location tLoc = this.bonus.getLocation();
				Location fLoc = this.fromB.getLocation();
				if (tLoc != null && fLoc != null){
					directionTo = fLoc.getDirectionToward(tLoc);
				}
				return directionTo;
			}
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
	 * Gets the objective bugs name, same as getObjectiveName(), if it is a bonus returns null.
	 * @return the objective bug's name
	 */
	public String getName() {
		if(toB==null){
			return null;
		}
		if (this.toB==null) return "";
		else return this.toB.getName();
	}
	/**
	 * Gets the relative direction to the objective. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the objective.
	 * @return the relative direction towards the objective, 0 if the objective is invalid.
	 */
	public int getrelDirectionTo() {
		return this.getRelDirectionTo();
	}
	/**
	 * Gets the relative direction to the objective. Relative direction is a value within ±180 degrees of your bug's current direction and is the shortest amount one would have to turn to be facing the objective.
	 * @return the relative direction towards the target, 0 if the target is invalid.
	 */
	public int getRelDirectionTo() {
		if (this.toB==null&&this.bonus==null) return 0;
		if(toB==null){
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
		if(toB==null){
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
		if(toB==null){
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
		if(toB==null){
			return this.bonus.getLocation().getRow();
		}
		return this.toB.getLocation().getRow();
	}
	/**
	 * Gets the current column the target bug is in.
	 * @return the current column of the target, a value between 0 and the maximum number of columns - 1, inclusive.
	 */
	public int getCol() {
		if(toB==null){
			return this.bonus.getLocation().getCol();
		}
		return this.toB.getLocation().getCol();
	}
	/**
	 * Find out whether the target has acted or not.
	 * @return true if the target has already taken it's turn this round.
	 */
	public boolean hasActed() {
		if(toB==null){
			return false;
		}
		return this.toB.isHasActed();
	}
	// Addendum 2 Methods
	public Location getLocation() {
		if(toB==null){
			return this.bonus.getLocation();
		}
		return this.toB.getLocation();
	}
	
	// Addendum 3 Methods
	public int getPathDistance() {
		if(toB==null){
			return new Path(this.fromB.getLocation(), this.fromB.getDirection(), this.getLocation()).size();		
		}
		return new Path(this.fromB.getLocation(), this.fromB.getDirection(), this.getLocation()).size();		
	}

	// 2014 Addendum 1 Methods
	public String toString() {
		if(toB==null){
			return null;
		}
		return this.getTargetName();
	}

	public boolean isValid()
	{
		if(bonus==null){
			return false;
		}
		if (bonus.getGrid() == null) return false;
		else if (fromB.getGrid() == null) return false;
		else if (!fromB.getGrid().equals(bonus.getGrid())) return false;
		else return true;
	}
}
