package battle.pack;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.util.ArrayList;


public class Informer 
{
	public enum DistanceMeasure {STEP,PATH};
	public static final int SORT_ASCENDING=0;
	public static final int SORT_DESCENDING=1;

	private Bactor bactor;
	private BattleBug battleBug;

	public static final int CLOCKWISE=1;
	public static final int COUNTER_CLOCKWISE=-1;
	/** the <code>Informer</code> class can be used to gain information about the BattleWorld you are in.
	 * <br />
	 */
	public Informer(Bactor bactor, BattleBug battleBug) 
	{
		this.bactor=bactor;
		this.battleBug=battleBug;
	}
	/**
	 * Return the absolute direction to the target t.
	 * @param t is the target that you getting information about.
	 * @return returns an integer value between 0 and 360.
	 */
	public int turnTowards(Target t)
	{
		return Bactor.turnTowards(this.bactor.getDirection(), this.bactor.getLocation().getDirectionToward(t.getBactor().getLocation()));
	}
	boolean bactorFacingMe(Bactor b) 
	{
		int tD = b.getDirection();
		int mD = b.getLocation().getDirectionToward(this.bactor.getLocation());
		return tD==mD;
	}
	/**
	 * Returns the number of bugs that are adjacent to your bug and are facing towards your bug so they might choose to attack you this round.
	 * @return returns an integer value between 0 and 360.
	 */
	public int adjacentOpponentsCanAttack() 
	{
		int adjacentOpponentsCanAttack=0;
		ArrayList<Actor> actors = this.bactor.getGrid().getNeighbors(this.bactor.getLocation());
		for (Actor a:actors) 
		{
			if (a instanceof Bactor) 
			{
				Bactor b=(Bactor)a;
				if (this.bactorFacingMe(b)==true) 
				{
					adjacentOpponentsCanAttack++;
				}
			}
		}
		return adjacentOpponentsCanAttack;
	}
	/**
	 * Returns a complete list of bugs currently on the board in unsorted order. The list may have a size of 0 if you your bug is the sole survivor, but will never be null.
	 * @return returns a list of Targets
	 */
	public ArrayList<Target> getTargetList()
	{
		ArrayList<Target> targets = new ArrayList<Target>();
		ArrayList<Bactor> bactors = new ArrayList<Bactor>();
		ArrayList<Location> locs = new ArrayList<Location>();
		Grid<Actor> gr = this.bactor.getGrid();
		if (gr==null) return targets;
		locs = gr.getOccupiedLocations();
		for (Location loc : locs) 
		{
			if (gr.get(loc) instanceof Bactor) 
			{
				Bactor b=(Bactor)gr.get(loc);
				if (!this.bactor.equals(b)) 
				{
					bactors.add(b);
				}
			}
		}
		for (Bactor b : bactors) 
		{
			targets.add(new Target(this.bactor, b));
		}
		return targets;
	}
	/**
	 * Returns a list of bugs that are adjacent to your bug and are facing towards your bug so they might choose to attack you this round. The list may have a size of 0 if you your bug has no adjacent opponent bugs facing your bug, but the list will never be null.
	 * @return returns a list of Targets
	 */
	public ArrayList<Target> getAdjacentTargetsFacingMeList()
	{
		ArrayList<Target> getList = new ArrayList<Target>();
		ArrayList<Actor> actors = this.bactor.getGrid().getNeighbors(this.bactor.getLocation());
		for (Actor a:actors)
		{
			if (a instanceof Bactor) 
			{
				Bactor b=(Bactor)a;
				if (this.bactorFacingMe(b)==true) 
				{
					getList.add(new Target(this.bactor,b));
				}
			}
		}
		return getList;
	}
	/**
	 * Returns true only if there is an opponent adjacent one turn facing clockwise to your current direction.
	 * @return returns true or false
	 */
	public boolean isTargetAdjacentClockwise() {
		int checkDirection=this.bactor.getDirection()+45;
		Grid<Actor> gr=this.bactor.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.bactor.getLocation().getAdjacentLocation(checkDirection);
		if (!gr.isValid(nextLoc)) return false;
		Actor a = gr.get(nextLoc);
		if (a==null) return false;
		return (a instanceof Bactor);
	}
	/**
	 * Returns true only if there is an opponent adjacent one turn facing counter-clockwise to your current direction.
	 * @return returns true or false
	 */
	public boolean isTargetAdjacentCounterClockwise() {
		int checkDirection=this.bactor.getDirection()-45;
		Grid<Actor> gr=this.bactor.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.bactor.getLocation().getAdjacentLocation(checkDirection);
		if (!gr.isValid(nextLoc)) return false;
		Actor a = gr.get(nextLoc);
		if (a==null) return false;
		return (a instanceof Bactor);
	}
	/**
	 * Returns true only if there is an empty space one turn facing clockwise one step forward. In other words, if you turned clockwise, then moved, returns true if that space would be open right now.
	 * @return returns true or false
	 */	
	public boolean canMoveClockwise() {
		return this.getActorDirectionRel(45);
	}
	/**
	 * Returns true only if there is an empty space one turn facing counter-clockwise one step forward. In other words, if you turned counter-clockwise, then moved, returns true if that space would be open right now.
	 * @return returns true or false
	 */	
	public boolean canMoveCounterClockwise() {
		return this.getActorDirectionRel(-45);
	}
	private Boolean getActorDirectionRel(int degreesRelative) {
		int checkDirection=this.bactor.getDirection()+degreesRelative;
		Grid<Actor> gr=this.bactor.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.bactor.getLocation().getAdjacentLocation(checkDirection);
		if (!gr.isValid(nextLoc)) return false;
		Actor a = gr.get(nextLoc);
		return a==null;
	}
	/**
	 * Returns a list of bugs that are adjacent to your bug. The list may have a size of 0 if you your bug has no adjacent opponent bugs, but the list will never be null.
	 * @return returns a list of Target
	 */	
	public ArrayList<Target> getAdjacentTargetList()
	{
		ArrayList<Target> getList = new ArrayList<Target>();
		ArrayList<Actor> actors = this.bactor.getGrid().getNeighbors(this.bactor.getLocation());
		for (Actor a:actors)
		{
			if (a instanceof Bactor) 
			{
				Bactor b=(Bactor)a;
				getList.add(new Target(this.bactor,b));
			}
		}
		return getList;
	}
	/**
	 * Returns a non-null list of Targets sorted in either ascending or descending order based upon the specified attribute.
	 * The list might have a size of 0 if your BattleBug is the only one remaining on the board. <br />
	 * <em>example:</em> List&lt;Target&gt; foes=myInformer().getSortedTargetList(Informer.SORT_ASCENDING, Attribute.DISTANCE) <br />
	 * Returns a list of targets with the nearest target first in the list.
	 * @param order determines if the returned list is sorted in ascending or descending order and should be passed using the constants Informer.SORT_ASCENDING or Informer.SORT_DESCENDING
	 * @param Enumerated type <code>Attribute</code>: <code>ATTACK</code>, <code>DEFENSE</code>, <code>DIRECTION</code>, <code>DISTANCE</code>, <code>HEALTH</code>, <code>MAXHEALTH</code>, and <code>POINTS</code>.
	 * @return returns a list of Target
	 */	
	public ArrayList<Target> getSortedTargetList(int order, Attribute attribute)
	{
		ArrayList<Target> rawTargetList = this.getTargetList();
		if (rawTargetList.size() == 0)
		{
			return rawTargetList;
		}
		else 
		{
			if(order == Informer.SORT_ASCENDING)
			{
				for (int i = 0; i < rawTargetList.size(); i++)
				{
					for (int j = i+1; j < rawTargetList.size(); j++)
					{
						if (field(attribute, rawTargetList.get(i)) > field(attribute, rawTargetList.get(j)))
						{
							Target temp = rawTargetList.get(i);
							rawTargetList.set(i, rawTargetList.get(j));
							rawTargetList.set(j, temp);
						}
					}
				}
			}
			else
			{
				for (int i = 0; i < rawTargetList.size(); i++)
				{
					for (int j = i+1; j < rawTargetList.size(); j++)
					{
						if (field(attribute, rawTargetList.get(i)) < field(attribute, rawTargetList.get(j)))
						{
							Target temp = rawTargetList.get(i);
							rawTargetList.set(i, rawTargetList.get(j));
							rawTargetList.set(j, temp);
						}
					}
				}
			}
			return rawTargetList;
		}
	}
	private ArrayList<BonusTarget> gatherBonusTargetList() {
		ArrayList<BonusTarget> bonuses = new ArrayList<BonusTarget>();
		ArrayList<BonusActor> bonusActors = new ArrayList<BonusActor>();
		ArrayList<Location> locs = new ArrayList<Location>();
		Grid<Actor> gr = this.bactor.getGrid();
		locs = gr.getOccupiedLocations();
		for (Location loc : locs) 
		{
			if (gr.get(loc) instanceof BonusActor) 
			{
				bonusActors.add((BonusActor)gr.get(loc));
			}
		}
		for (BonusActor b : bonusActors ) 
		{
			bonuses.add(new BonusTarget(this.bactor, b));
		}
		return bonuses;		
	}
	/**
	 * Returns a complete list of bonuses currently on the board sorted by distance. The list may have a size of 0 if there are no bonuses on the board, but will never be null.
	 * @return returns a list of BonusTarget
	 */	
	public ArrayList<BonusTarget> getBonusTargetList()
	{
		ArrayList<BonusTarget> bonuses=this.gatherBonusTargetList();
		this.sortBonusTargetByStepDistance(bonuses);
		return bonuses;
	}
	private ArrayList<BonusTarget> gatherBonusTargetByType(Bonus type) {
		ArrayList<BonusTarget> rawList = this.getBonusTargetList();
		ArrayList<BonusTarget> sortedList = new ArrayList<BonusTarget>();
		for (BonusTarget b: rawList)
		{
			if (b.getEffect().equals(type))
			{
				sortedList.add(b);
			}
		}
		return sortedList;		
	}
	/**
	 * Returns a list of bonuses of the specified type currently on the board sorted by distance. The list may have a size of 0 if there are no bonuses on the board, but will never be null.<br />
	 * <em>example:</em> List&lt;BonusTarget&gt; bonuses=myInformer().getBonusTargetByType(Bonus.HEALTH) <br />
	 * Returns a list of only the Health bonuses with the nearest bonus first in the list.
	 * @param type determines the specific type of bonus to include in the list. It can have the values: <code>ATTACK</code>, <code>DEFENSE</code>, <code>HEALTH</code>, <code>MAX_HEALTH</code>, <code>MYSTERY</code>, or <code>POINTS</code>.
	 * @return returns a list of BonusTarget
	 */	
	public ArrayList<BonusTarget> getBonusTargetByType(Bonus type)
	{
		ArrayList<BonusTarget> bonuses = gatherBonusTargetByType(type);		
		this.sortBonusTargetByStepDistance(bonuses);
		return bonuses;
	}
	private void sortBonusTargetByStepDistance(ArrayList<BonusTarget> bonuses) {
		if (bonuses==null) return;
		if (bonuses.size()>1) {
			for (int j=0; j<bonuses.size(); j++) {
				for (int i=0; i<bonuses.size()-1; i++) {
					if (bonuses.get(i).getStepsDistance()>bonuses.get(i+1).getStepsDistance()) {
						BonusTarget temp=bonuses.get(i);
						bonuses.set(i,bonuses.get(i+1));
						bonuses.set(i+1, temp);
					}
				}
			}
		}
	}
	private void sortBonusTargetByPathDistance(ArrayList<BonusTarget> bonuses) {
		if (bonuses==null) return;
		if (bonuses.size()>1) {
			for (int j=0; j<bonuses.size(); j++) {
				for (int i=0; i<bonuses.size()-1; i++) {
					if (bonuses.get(i).getPathDistance()>bonuses.get(i+1).getPathDistance()) {
						BonusTarget temp=bonuses.get(i);
						bonuses.set(i,bonuses.get(i+1));
						bonuses.set(i+1, temp);
					}
				}
			}
		}
	}
	private void sortTargetByPathDistance(ArrayList<Target> targets) {
		if (targets==null) return;
		if (targets.size()>1) {
			for (int j=0; j<targets.size(); j++) {
				for (int i=0; i<targets.size()-1; i++) {
					if (targets.get(i).getPathDistance()>targets.get(i+1).getPathDistance()) {
						Target temp=targets.get(i);
						targets.set(i,targets.get(i+1));
						targets.set(i+1, temp);
					}
				}
			}
		}
	}
	/**
	 * Returns true if the space in front of your BattleBug is empty.
	 * @return returns true or false
	 */	
	public boolean canMove() 
	{
		return this.bactor.canMove();
	}
	/**
	 * @return Returns your BattleBug's current Attack value.
	 */	
	public int getAttack() 
	{
		return this.bactor.getAttack();
	}
	/**
	 * Returns an enumerated Bonus if your BattleBug has picked one up, and null if your bug doesn't currently have one that is can use.
	 * @return The enumerated type <code>Bonus</code> can have the values: <code>ATTACK</code>, <code>DEFENSE</code>, <code>HEALTH</code>, <code>MAX_HEALTH</code>, <code>MYSTERY</code>, <code>POINTS</code>, or <code>null</code> if you have no Bonus currently.
	 */	
	public Bonus getEffect()
	{
		return this.bactor.getStoredEffect();
	}
	/**
	 * Note: This value may be temporarily increased due to your last Act being Defense!
	 * @return Returns your BattleBug's current Defense value.
	 */	
	public int getDefense() 
	{
		return this.bactor.getDefense();
	}
	/**
	 * @return Returns your BattleBug's current Maximum Health value.
	 */	
	public int getMaxHealth() 
	{
		return this.bactor.getMaxHealth();
	}
	/**
	 * @return Returns your BattleBug's current Health value.
	 */	
	public int getHealth() 
	{
		return this.bactor.getHealth();
	}
	/**
	 * @return Returns your BattleBug's current Points.
	 */	
	public int getPoints() 
	{
		return this.bactor.getPoints();
	}
	/**
	 * Returns true if the space in front of your BattelBug has another BattleBug in it.
	 * @return returns true or false
	 */	
	public boolean foeInFront() 
	{
		return this.bactor.foeInFront();
	}
	/**
	 * Returns true if the space in front of your BattleBug has a bonus in it.
	 * @return returns true or false
	 */	
	public boolean bonusInFront() 
	{
		return this.bactor.bonusInFront();
	}
	/**
	 * Returns the absolute direction your bug is currently facing (0=North).
	 * @return returns an int between 0 and 360
	 */	
	public int getDirection() 
	{
		return this.bactor.getDirection();
	}
	/**
	 * Returns a reference to the BattleBug located directly in front of your BattleBug, or <code>null</code> if the space in front of your BattleBug has no BattleBug.
	 * @return returns a <code>Target</code> reference
	 */	
	public Target targetFoeInFront()
	{
		Target targetFoeInFront=null;
		if (this.bactor.foeInFront()==true) 
		{
			Grid<Actor> gr=this.bactor.getGrid();
			if (gr!=null)
			{
				Location myLoc=this.bactor.getLocation();
				if (myLoc!=null) 
				{
					Location nextLoc=myLoc.getAdjacentLocation(this.bactor.getDirection());
					if (gr.isValid(nextLoc))
					{
						if (gr.get(nextLoc) instanceof Bactor) 
						{
							Bactor opponent=(Bactor)gr.get(nextLoc);
							targetFoeInFront=new Target(this.bactor, opponent);
						}
					}
				}
			}
		}
		return targetFoeInFront;
	}
	/**
	 * Returns a reference to the Bonus located directly in front of your BattleBug, or <code>null</code> if the space in front of your BattleBug has no Bonus.
	 * @return returns a <code>BonusTarget</code> reference
	 */	
	public BonusTarget targetBonusInFront()
	{
		BonusTarget bonusInFront=null;
		if (this.bactor.bonusInFront()==true) 
		{
			Grid<Actor> gr=this.bactor.getGrid();
			if (gr!=null)
			{
				Location myLoc=this.bactor.getLocation();
				if (myLoc!=null) 
				{
					Location nextLoc=myLoc.getAdjacentLocation(this.bactor.getDirection());
					if (gr.isValid(nextLoc))
					{
						if (gr.get(nextLoc) instanceof BonusActor) 
						{
							BonusActor bonusActor=(BonusActor)gr.get(nextLoc);
							bonusInFront=new BonusTarget(this.bactor, bonusActor);
						}
					}
				}
			}
		}
		return bonusInFront;
	}
	/**
	 * Returns the string 'ascending' or 'descending' for use in sending output to your BattleBug's tab. (0=ascending, 1=descending).<br />
	 * <em>example:</em> myInformer().sortOrderString(Informer.SORT_ASCENDING) returns the String "ascending" <br />
	 * @param an int with a value of 0 or 1, or use the <code>Informer()</code> constants <code>SORT_ASCENDING</code> or <code> SORT_DESCENDING</code>.
	 * @return returns "ascending", "descending", or "".
	 */	
	public static final String sortOrderString(int sortOrder) 
	{
		String sortOrderString="";
		if (sortOrder==Informer.SORT_ASCENDING) 
		{
			sortOrderString="ascending";
		} 
		else if (sortOrder==Informer.SORT_DESCENDING) 
		{
			sortOrderString="descending";
		}
		return sortOrderString;
	}
	/**
	 * Returns an enumerated Bonus if the target BattleBug currently has an unused Bonus, or <code>null</code> the target doesn't have a Bonus.
	 * @param t is the <code>Target</code> about which you want information.
	 * @return The enumerated type <code>Bonus</code> can have the values: <code>ATTACK</code>, <code>DEFENSE</code>, <code>HEALTH</code>, <code>MAX_HEALTH</code>, <code>MYSTERY</code>, <code>POINTS</code>, or <code>null</code> if no bonus is carried.
	 */		
	public Bonus getTargetEffect(Target t)
	{
		return t.getCurrentEffect();
	}
	/**
	 * Returns the value of the Target's attribute selected by the enumerated Attribute type.<br />
	 * <em>example:</em> myInformer.field(Attribute.HEALTH , t) returns the current health of <code>Target</code> t.<br />
	 * @param attribute is the Enumerated type <code>Attribute</code>: <code>ATTACK</code>, <code>DEFENSE</code>, <code>DIRECTION</code>, <code>DISTANCE</code>, <code>HEALTH</code>, <code>MAXHEALTH</code>, and <code>POINTS</code>.
	 * @param t is the <code>Target</code> t to get the selected attribute from.
	 * @return returns int value of the selected Attribute.
	 */		
	public int field(Attribute attribute, Target t) 
	{
		int field=0;
		if (t!=null)
		{
			Bactor b = t.getBactor();
			switch(attribute)
			{
			case ATTACK:
				field=b.getAttack();
				break;
			case DEFENSE:
				field=b.getDefense();
				break;
			case DIRECTION:
				field=b.getDirection();
				break;
			case DISTANCE: 
				field=t.getStepsDistance();
				break;
			case HEALTH:
				field=b.getHealth();
				break;
			case POINTS:
				field=b.getPoints();
				break;
			case MAXHEALTH:
				field=b.getMaxHealth();
				break;
			default:
				field=0;
				break;
			}
		}
		return field;
	}
	/**
	 * Outputs a string to your bug's tab
	 * @param s String to output to your bug tab
	 */
	public void print(String s) {
		this.bactor.updateBugOut(s);
	}
	/**
	 * Finds out whether you have a bonus or not.
	 * @return <code>true</code> if you have a bonus you can use
	 */
	public boolean haveBonus() {
		return this.bactor.haveBonus();
		
	}
	/**
	 * Gets the type of bonus that the bug is carrying
	 * @return the enumerated <code>Bonus</code> your BattleBug is carrying or <code><b>null</b></code> if your BattleBug has no bonus
	 */
	public Bonus whatTypeOFBonus(){
		if (this.bactor.getStoredEffect() != null)
			return this.bactor.getStoredEffect();
		else return null;
	}	
	protected BattleBug getBattleBug() {
		return battleBug;
	}
	/**
	 * Gets the enumerated Act that your bug performed last turn, (<b>Defend</b> if it is the first round).
	 * @return the enumerated Act your bug performed last turn (<b>Defend</b> if it is the first round).
	 */
	public Act getLastAct() {
		return this.bactor.getLastAct();
	}
	/**
	 * Sends a taunt to the Announcer tab
	 * For Example: taunt(Taunt.NICE_TRY);
	 * If the taunt requires a target, using this method will result in no text being sent to the announcer tab.
	 * @param taunt the taunt to send to the Announcer tab.
	 */
	public void taunt(Taunt taunt) {
		this.bactor.taunt(taunt);
	}
	/**
	 * Use this method to send a Taunt to the Announcer tab when the taunt requires a target. For example myInformer.taunt (Taunt.GOING_TO_GET_YOU, target)."
	 * If the taunt selected doesn't require a target, using this method will still result in the taunt text being sent to the announcer tab. This version of the taunt method may also be used to trigger a taunt with no target using the form: Taunt.GOING_TO_GET_YOU, null)
	 * @param taunt The taunt to be sent.
	 * @param target The Target of the taunt. If <b>null</b> it triggers a taunt without a target.
	 */
	public void taunt(Taunt taunt, Target target) {
		if (target!=null) {
			String s=target.getName();
			this.bactor.taunt(taunt, s);
		}
	}
	public int checkDir(int relDir) {
		Grid<Actor> gr=this.bactor.getGrid();
		if (gr==null) return 360;
		int dir=0;
		boolean turn=true;
		int loopCount=0;
		while (turn==true) {
			loopCount++;
			Location nextLoc=this.bactor.getLocation().getAdjacentLocation(this.bactor.getDirection() + (dir * relDir));
			if (gr.isValid(nextLoc)) {
				Actor a = gr.get(nextLoc);
				if (a==null) {
					turn=false;
				} else {
					dir+=45;
				}
			} else {
				dir+=45;
			}
			if (dir>180) {
				turn=false;
			}
			if (loopCount>12) {
				System.out.println("loopCount=" + loopCount + ", dir=" + dir + ".getDirection()=" + this.bactor.getDirection() + ", relDir=" + relDir);
				break;
			}
		}
		return dir;
	}
	// Addendum 1 Methods
	//	
	/**
	 * "Use this method to examine the adjacent squares to your current location in both the clockwise and counter-clockwise direction from your current facing. The returned value will be a ± relative direction representing the closest open square. This method returns 0 if the square in front of your bug is open and will randomly select clockwise or counterclockwise if there are open squares in both directions an equal number of degrees. A returned value more or less than ±180 degrees indicates that there is NO open square surrounding your bug."
	 * @return the relative direction towards the closest open square adjacent to your bug. Returns a value with absolute value greater than 180 if totally surrounded.
	 */
	public int getOpenRelDirection() {
		int cwDir=this.checkDir(Informer.CLOCKWISE);
		int ccwDir=this.checkDir(Informer.COUNTER_CLOCKWISE);
		if (cwDir<=ccwDir) {
			return cwDir;
		} else {
			return -ccwDir;
		}
	}
	/**
	 * Gets the row your bug occupies.
	 * @return the row your bug occupies, ranging from 0 to getRows() - 1.
	 */
	public int getRow() {
		return this.bactor.getLocation().getRow();
	}
	/**
	 * Gets the current column your bug occupies
	 * @return the column your bug occupies, ranging from 0 to getCols() - 1.
	 */
	public int getCol() {
		return this.bactor.getLocation().getCol();
	}
	/**
	 * Gets the number of rows in the current grid
	 * @return the number of rows in the current grid
	 */
	public int getRows() {
		return this.bactor.getGrid().getNumRows();
	}
	/**
	 * Gets the number of columns in the current grid
	 * @return the number of columns in the current grid
	 */
	public int getCols() {
		return this.bactor.getGrid().getNumCols();
	}

	// Addendum 2 Methods
	/**
	 * Gets the number of turns remaining until the next penalty damage will be applied if no BattleBug damages deals damage to an opponent
	 * @return the number of turns remaining until the next penalty damage will be applied if no BattleBug damages deals damage to an opponent
	 */
	public int getTurnsUntilPenalty() {
		return this.bactor.getWorld().getStepsUntilPenalty()-this.bactor.getWorld().getStepsWithNoDamage();
	}
	/**
	 * Gets the amount of damage penalty that will get applied to every BattleBug if no damage is being done (a.k.a. the 'peace dividend') when getTurnsUntilPenalty() reaches 0.
	 * @return the amount of damage that will be done when the peace dividend reaches 0.
	 */
	public int getPenaltyDamage() {
		return this.bactor.getWorld().getPenaltyDamage();
	}
	/**
	 * Gets a <code>Path</code> from your BattleBug's current location to the specified <code>Location</code> in the BattleWorld.
	 * @param location the location the path should lead to.
	 * @return A new <code>Path</code> object.
	 */
	public Path getPathToLocation(Location location) {
		return new Path(this.bactor.getLocation(), this.bactor.getDirection(), location);
	}
	/**
	 * Gets a <code>Path</code> from your BattleBug's current location to the specified <code>Target</code>'s current location in the BattleWorld.
	 * @param target the <code>Target</code> whose location the path should lead to.
	 * @return A new <code>Path</code> object.
	 */
	public Path getPathToTarget(Target target) {
		if (target!=null && target.targetValid()) {
			return new Path(this.bactor.getLocation(), this.bactor.getDirection(), target.getBactor().getLocation());
		} else {
			return new Path();
		}
	}
	/**
	 * Gets a <code>Path</code> from your BattleBug's current location to the specified <code>Bonus</code>'s current location in the BattleWorld.<br />
	 * <em>Note:</em> This returns a path to a location, not to the specified <code>Bonus</code> so the <code>isValid()</code> method for the returned
	 * path will <em>not</em> return <code>false</code> if the <code>Bonus</code> is removed from the grid!
	 * @param bonusTarget the <code>BonusTarget</code> whose location the path should lead to.
	 * @return A new <code>Path</code> object.
	 */
	public Path getPathToBonus(BonusTarget bonusTarget) {
		if (bonusTarget!=null && bonusTarget.isValid()) {
			return new Path(this.bactor.getLocation(),this.bactor.getDirection(), new Location(bonusTarget.getRow(),bonusTarget.getCol()));
		} else {
			return new Path();
		}
	}
	/**
	 * Gets a <code>Path</code> from <code>Location from</code> to the specified <code>Location</code> in the BattleWorld.
	 * @param from the location the should begin with.
	 * @param fromDirection the absolute direction(0-360) that the path should lead from.
	 * @param location the location the path should lead to.
	 * @return A new <code>Path</code> object.
	 */
	public Path fromLocationToLocation(Location from, int fromDirection, Location to) {
		return new Path(from, fromDirection, to);
	}
	/**
	 * Gets a <code>BonusPath</code> from your BattleBug's current location to the specified <code>Bonus</code>.<br />
	 * <em>Note:</em> This returns a <code>BonusPath</code> to the specified <code>Bonus</code> so the <code>isValid()</code> method
	 * only returns <code>true</code> if the <code>Bonus</code> is still on the board!
	 * @param bonusTarget is the Bonus the path should lead to.
	 * @return A new <code>BonusPath</code> object.
	 */
	public BonusPath getBonusPath(BonusTarget bonusTarget) {
		return new BonusPath(this.bactor.getLocation(),this.bactor.getDirection(), bonusTarget);
	}
	/**
	 * Returns a complete list of bonuses currently on the board sorted by distance in terms of distanceMeasure. The list may have a size of 0 if there are no bonuses on the board, but will never be null.
	 * @param distanceMeasure the way to sort the list.
	 * @return returns a list of BonusTarget
	 */	
	public ArrayList<BonusTarget> getBonusTargetList(DistanceMeasure distanceMeasure) {
		ArrayList<BonusTarget> bonuses=this.gatherBonusTargetList();
		if (distanceMeasure.equals(DistanceMeasure.STEP)) {
			this.sortBonusTargetByStepDistance(bonuses);
		} else if (distanceMeasure.equals(DistanceMeasure.PATH)) {
			this.sortBonusTargetByPathDistance(bonuses);
		}
		return bonuses;
	}
	/**
	 * This method returns a list of all <code>Bonus</code> objects on the grid of the types included in the list <code>bonusTypes</code> sorted by
	 * distance using either traditional step-by-step method of determines distance or by using the distance measured using a <code>Path</code>.<br />
	 * <em>example:</em> List<code>&lt;BonusTarget&gt; healthBonuses=myInformer.getBonusTargetByType(Bonus.HEALTH, DistanceMeasure.STEP);</code>
	 *  returns a list of all the Health bonuses on the board with the closest one first in the list.<br />
	 * @param bonusTypes is a list of the enumerated type <code>Bonus</code>.
	 * @param distancemeasure is the enumerated type <code>DistanceMeasure</code> and can be either <code>DistanceMeasure.STEP</code> or <code>DistanceMeasure.PATH</code>.
	 * @return A new list of BonusTarget. The list may have 0 elements but will never be <code>null</code>.
	 */
	public ArrayList<BonusTarget> getBonusTargetByType(ArrayList<Bonus> bonusTypes, DistanceMeasure distanceMeasure) {
		ArrayList<BonusTarget> bonuses=new ArrayList<BonusTarget>();
		for (Bonus type:bonusTypes) {
			bonuses.addAll(this.gatherBonusTargetByType(type));
		}
		if (distanceMeasure.equals(DistanceMeasure.STEP)) {
			this.sortBonusTargetByStepDistance(bonuses);
		} else if (distanceMeasure.equals(DistanceMeasure.PATH)) {
			this.sortBonusTargetByPathDistance(bonuses);
		}
		return bonuses;
	}
	/**
	 * This method returns a list of all <code>Bonus</code> objects on the grid of the type <code>Bonus</code> sorted by
	 * distance using either traditional step-by-step method of determines distance or by using the distance measured using a <code>Path</code>.<br />
	 * <em>example:</em> List<code>&lt;BonusTarget&gt; healthBonuses=myInformer.getBonusTargetByType(Bonus.HEALTH, DistanceMeasure.STEP);</code>
	 *  returns a list of all the Health bonuses on the board with the closest one first in the list.<br />
	 * @param type is the enumerated type <code>Bonus</code>.
	 * @param distancemeasure is the enumerated type <code>DistanceMeasure</code> and can be either <code>DistanceMeasure.STEP</code> or <code>DistanceMeasure.PATH</code>.
	 * @return A new list of BonusTarget. The list may have 0 elements but will never be <code>null</code>.
	 */
	public ArrayList<BonusTarget> getBonusTargetByType(Bonus type, DistanceMeasure distanceMeasure) {
		ArrayList<BonusTarget> bonuses=this.gatherBonusTargetByType(type);
		if (distanceMeasure.equals(DistanceMeasure.STEP)) {
			this.sortBonusTargetByStepDistance(bonuses);
		} else if (distanceMeasure.equals(DistanceMeasure.PATH)) {
			this.sortBonusTargetByPathDistance(bonuses);
		}
		return bonuses;
	}

	// Addendum 3 Methods
	/**
	 * Gets a <code>TargetPath</code> from your BattleBug's current location to the specified <code>Target</code>.<br />
	 * <em>Note:</em> This returns a <code>TargetPath</code> to the specified <code>Target</code> so the <code>isValid()</code> method
	 * only returns <code>true</code> if the <code>Target</code> is still on the board!
	 * @param target is the <code>Target</code> the path should lead to.
	 * @return A new <code>BonusPath</code> object.
	 */
	public TargetPath getTargetPath(Target target) {
		return new TargetPath(this.bactor, target);
	}
	/**
	 * Gets a list of all <code>Target</code> objects in the BattleGrid sorted so the nearest <code>Target</code> is first in the list
	 * using the distance measured by creating a <code>Path</code> to each <code>Target</code>.
	 * @return A list of <code>Target</code>.
	 */
	public ArrayList<Target> getTargetsByPathDistance() {
		ArrayList<Target> targets=new ArrayList<Target>();
		targets.addAll(this.getTargetList());
		this.sortTargetByPathDistance(targets);
		return targets;
	}
	/**
	 * @return The number of turns before the next random bonus will be added to the BattleGrid.
	 */
	public int getTurnsToNextPossibleBonus() {
		return this.bactor.getWorld().getTurnsToPossibleBonusSpawn();
	}
	// 2013 Addendum 1 Methods
	/**
	 * @return The number of points that will be added for the type of bonus you are currently carrying.
	 */
	public int getBonusValue() {
		if (haveBonus()) {
			return this.bactor.getStoredEffect().DEFAULT_VALUE();
		}
		return 0;
	}
	/**
	 * @return The number of points that will be added if you were to use a MAX_HEALTH bonus.
	 */
	public int getHealthBonusUsingMaxHealth() {
		BonusEffect bonusEffect=new BonusEffect(Bonus.MAX_HEALTH);
		int health=bonusEffect.getMaxHealthHealthBonus(bactor)-bactor.getHealth();
		return health;
	}
}
