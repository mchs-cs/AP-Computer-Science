package battle.pack;
import info.gridworld.world.World;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextArea;

import StaticLibrary.StaticLibrary;
import battle.pack.Bonus;
import battle.pack.BattleRunner;
/**
 * @author hyperion
 *
 */
public class BattleWorld extends World<Actor> {
	private 	List<BattleBug> plagiarizedBugs=BattleRunner.getPlagiarizers();

	private boolean checkedForDuplicates=false;
	private boolean gameOn;
	private boolean roundOn;
	private int steps;
	private int roundsPerGame;
	private int currentRound;
	private ArrayList<Bactor> eliminatedBactors;
	private ArrayList<BattleBug> badBugs=new ArrayList<BattleBug>();
	private GraphBox graphPane;
	private JTextArea gameText;
	private DoBattle doBattle;
	private FileWrite logFile;
	private int stepsWithNoDamage;
	private int stepsUntilPenalty;
	private int penaltyDamage;
	private boolean damageDealt;
	private String announcement;
	private int minimumTurnsBetweenNewBonus;
	private int turnsSinceLastNewBonus;
	private int initialBactorsInRound;
	private int freeBonusSpawns;
	private int mostDamage=0;
	private Bactor mostDamageBactor=null;
	private Bactor mostDamageVictim=null;
	private int mostPoints=0;
	private Bactor mostPointsBactor=null;
	private Bactor mostPointsVictim=null;
	private int battleNum=0;
	private int totalBattles=0;

	public void setBattleNum(int battleNum, int totalBattles) {
		this.battleNum=battleNum;
		this.totalBattles=totalBattles;
	}

	public void updateMostDamage(int damage, Bactor bactor, Bactor victim) {
		if (damage > this.mostDamage) {
			this.mostDamage=damage;
			this.mostDamageBactor=bactor;
			this.mostDamageVictim=victim;
		}
	}
	public void updateMostPoints(int points, Bactor bactor, Bactor victim) {
		if (points > this.mostPoints) {
			this.mostPoints=points;
			this.mostPointsBactor=bactor;
			this.mostPointsVictim=victim;
		}
	}
	public void setPenalties(int stepsUntilPenalty, int penaltyDamage) {
		this.stepsWithNoDamage=0;
		this.stepsUntilPenalty=stepsUntilPenalty;
		this.penaltyDamage=penaltyDamage;
	}

	// New Interface Methods and Instance Variables
	private Reporter reporter;
	public void setReporter(Reporter reporter) {
		this.reporter=reporter;
	}
	ArrayList<BattleRecord> battleRecords;




	//	private int once=0;
	ArrayList<Color> listOfColors = new ArrayList<Color>();
	ArrayList<int[]> listOfCoordinates = new ArrayList<int[]>();
	ArrayList<Bactor> bactors = new ArrayList<Bactor>();			// Added ------------------------

	private static final String DEFAULT_MESSAGE = "Welcome to BattleWorld";

	public void startCoordinate(Color col, int x1, int y1){
		int[] coordinateToAdd = {x1, y1};
		listOfColors.add(col);
		listOfCoordinates.add(coordinateToAdd);
	}

	private void setUp() {
		this.eliminatedBactors = new ArrayList<Bactor>();
		this.roundOn=true;
		this.graphPane=null;
		this.gameText=null;
		this.logFile=new FileWrite("BattleWorld.log.txt");
		this.logFile.write("BattleWorld(" + this.getGrid().getNumRows()+","+this.getGrid().getNumCols()+")\n", false);
		this.announcement=null;
		this.turnsSinceLastNewBonus=0;
		this.minimumTurnsBetweenNewBonus=1;
	}
	/**
	 * Constructs an actor world with a default grid.
	 */
	public BattleWorld()
	{
		this.setUp();
	}

	/**
	 * Constructs an actor world with a given grid.
	 * @param grid the grid for this world.
	 */
	public BattleWorld(Grid<Actor> grid)
	{
		super(grid);
		this.setUp();
	}

	public int getRoundsPerGame() {
		return roundsPerGame;
	}
	public void setRoundsPerGame(int roundsPerGame) {
		this.roundsPerGame = roundsPerGame;
		this.currentRound=0;
	}

	/**Indents a location a number of spaces
	 * @param int Margin is the number of spaces to be left between the wall and the loc
	 * @param boolean row dictates whether or not the value is a row
	 * @return a new int which is half of a new location
	 */
	public int indentFromGrid(int Margin, int toBeIndented, boolean row, Grid<Actor> grid){

		if (toBeIndented < 1 && row)toBeIndented += Margin;
		if (toBeIndented == grid.getNumRows()-1 && row)toBeIndented -= Margin;

		if (toBeIndented < 1 && row == false)toBeIndented += Margin;
		if (toBeIndented == grid.getNumCols()-1 && row == false)toBeIndented -= Margin;
		return toBeIndented;
	}

	public void setUpRound(ArrayList<Bactor> bactors) {
		int once = 0;
		if(once == 0) {
			once++;
		}
		// ----------------------------- First reset all parameters for each bactor to start conditions --------------------------------------
		for (Bactor b: bactors) {
			b.resetColor();
			b.setHealth(b.getMaxHealth());
			b.resetBug();
		}
		@SuppressWarnings("unused")
		ArrayList<Actor> actors = new ArrayList<Actor>();
		Grid<Actor> gr = getGrid();
		for (Location loc : gr.getOccupiedLocations()) { // Clear the grid of all actors
			// Collects all actors by location, so put them into the appropriate list
			Actor a=gr.get(loc);
			a.removeSelfFromGrid();
		}
		if (bactors.size()>0) {
			int direction=90;
			int bactorsPerSide = bactors.size()/4;
			for (int j=0; j<4; j++) {
				direction+=90;
				for (int i=0; i<bactorsPerSide; i++) {
					if (bactors.size()>0) {
						int row=0, col=0;

						switch (j) {
						case 0:
							row=0;
							col=i*3+4;
							break;
						case 1:
							row=i*3+4;
							col=this.getGrid().getNumCols()-1;
							break;
						case 2:
							row=this.getGrid().getNumRows()-1;
							col=i*3+4;
							break;
						case 3:
							row=i*3+4;
							col=0;
							break;
						}
						Location locBonus = new Location(row, col);
						BonusActor bonus = new BonusActor(Bonus.ATTACK.randomBonus());
						this.add(locBonus, bonus);
						row = indentFromGrid(1, row, true, this.getGrid());
						col = indentFromGrid(1, col, false, this.getGrid());

						Location loc=new Location(row,col);
						int rB=(int)(Math.random()*bactors.size());
						this.add(loc,bactors.get(rB));
						bactors.get(rB).setDirection(direction);
						bactors.remove(rB);	        		
					}
				}	        	
			}
		}
		this.currentRound++;

		if (this.currentRound<=this.roundsPerGame) {
			this.setMessage("Round : " + this.currentRound + " of " + this.roundsPerGame);
			this.roundOn=true;
			this.gameOn=true;
			this.steps=0;
		} else {
			this.gameOn=false;
			this.roundOn=false;
			this.setMessage("Game Over!");
		}
	}
	public void eliminateBactor(Bactor bactor) {
		this.eliminatedBactors.add(bactor);
	}
	public void show()
	{

		if (getMessage() == null)
			setMessage(DEFAULT_MESSAGE);
		super.show();
	}
	public void bonusSpawner(){
		int baseBonuses = bactors.size()/4;
		for(int i = 0;i < baseBonuses; i++){
			//World.add(new BonusActor());
		}
	}
	public void sortBactorsByHealth(ArrayList<Bactor> bactors) {		
		if (bactors.size()>1) { // Only sort the list if there are at least two bactors to sort
			for (int i=0; i<bactors.size()-1; i++) { // Sort bactors by health, weakest bactor acts first
				for (int j=i+1; j<bactors.size(); j++) {
					Boolean swapBactors=false;
					if (bactors.get(i).getHealth()>bactors.get(j).getHealth()) { // Compare health, the lower health bactor goes first
						swapBactors=true;
					} else if (bactors.get(i).getHealth()==bactors.get(j).getHealth()) { // If they have the same health then compare their points
						if (bactors.get(i).getPoints()>bactors.get(j).getPoints()) { // Same Health, lower points goes first
							swapBactors=true;	
						} else if (bactors.get(i).getPoints()==bactors.get(j).getPoints()) { // If they have the same health & points then randomly select one
							if ((int)(Math.random()*2)==0) { // Flip a coin
								swapBactors=true;
							}
						}
					}
					if (swapBactors==true) {
						Bactor b=bactors.get(i);
						bactors.set(i, bactors.get(j));
						bactors.set(j,b);	    				
					}
				}
			}
		}
	}

	public int battleStep() {
		Grid<Actor> gr = getGrid();
		ArrayList<Actor> actors = new ArrayList<Actor>();
		ArrayList<Bactor> bactors = new ArrayList<Bactor>();
		ArrayList<BonusActor> bonusActors = new ArrayList<BonusActor>();
		for (Location loc : gr.getOccupiedLocations()) {
			// Collects all actors by location, so put them into the appropriate list
			Actor a=gr.get(loc);
			if (a instanceof Bactor) { // Put Bactors in list
				Bactor b=(Bactor)a; // Cast the actor into a Bactor
				b.setHasActed(false); // Reset the HasActed flag
				bactors.add(b); // Add it to the list
			} else if (a instanceof BonusActor) { // Put Bactors in list
				BonusActor b=(BonusActor)a; // Cast the actor into a Bactor
				bonusActors.add(b); // Add it to the list
			} else { // if it isn't an instance of something else that goes in it's own list, then it must be an actor
				actors.add(a);
			}
		}
		this.turnsSinceLastNewBonus++;
		if (bonusActors.size()<(bactors.size()/2)) { // If there are less than half as many Bonus Objects on the board as there are Bactors, add a Bonus Object
			if (this.turnsSinceLastNewBonus>=this.minimumTurnsBetweenNewBonus) {
				BonusActor bonus = new BonusActor(Bonus.ATTACK.randomBonus());
				this.add(bonus);
				this.turnsSinceLastNewBonus=0;
				if (this.freeBonusSpawns>0) {
					this.freeBonusSpawns--;
				}
				if (this.freeBonusSpawns==0) {
					this.minimumTurnsBetweenNewBonus++;
				}
			}
			this.setMessage(this.getMessage() + ", New bonus in " + (this.minimumTurnsBetweenNewBonus-this.turnsSinceLastNewBonus) + " turns");
		}
		// Sort bactors into the order desired for the act() methods to be called
		// First make three lists:
		ArrayList<Bactor> moveFirst=new ArrayList<Bactor>();
		ArrayList<Bactor> moveMiddle=new ArrayList<Bactor>();
		ArrayList<Bactor> moveLast=new ArrayList<Bactor>();
		for (Bactor bactor:bactors) {
			if (bactor.getLastAct().equals(Act.ATTACK)) {
				moveLast.add(bactor);
			} else if (bactor.getLastAct().equals(Act.DEFEND)) {
				moveFirst.add(bactor);		
			} else {
				moveMiddle.add(bactor);						
			}
		}
		this.sortBactorsByHealth(moveFirst);
		this.sortBactorsByHealth(moveMiddle);
		this.sortBactorsByHealth(moveLast);
		bactors=moveFirst;
		bactors.addAll(moveMiddle);
		bactors.addAll(moveLast);
		for (Bactor b : bactors)
		{
			// only act if another actor hasn't removed a
			if (b.getGrid() == gr)
				this.logFile.write("\""+ b.getBattleBug().getName() + "\" created by \"" + b.getBattleBug().getCreator() + "\"\n", false);
			/*
			 */ 
			if (BattleRunner.CATCH_CHEATERS) {
				BattleBug bb=b.getBattleBug();
				if (plagiarizedBugs.contains(bb)) {
					b.turnCounterClockwise();
					bb.print("I should not copy other people's code!");
				} else if (badBugs.contains(bb)) {
					b.turnClockwise();
					bb.print("I should not copy Mr. Braskin's code!");
				} else {
					if (!(bb.getCreator().equals("Mr. B.") && bb.getName().equals("Aaronilator"))) {
						try {
							Class[] paramTypes = new Class[1];
							paramTypes[0]=Boolean.TYPE;
							Method x = bb.getClass().getDeclaredMethod("willAdvance",paramTypes);
							Method s = bb.getClass().getMethod("myAct",null);
							b.turnClockwise();
							if (!badBugs.contains(bb)) {
								badBugs.add(bb);
								System.out.println(b.getBattleBug().getCreator()+" uses myAct() & willAdvance(boolean verbose) Methods!");
							}
						} catch (NoSuchMethodException e) {
							b.act();
						}
					} else {
						b.act();
					}
				}
			} else {
				b.act();
			}
			/* 
			 */
			this.setMessage(this.getMessage());
		}
		actors = new ArrayList<Actor>();
		bactors = new ArrayList<Bactor>();
		bonusActors = new ArrayList<BonusActor>();
		for (Location loc : gr.getOccupiedLocations()) {
			// Collects all actors by location, so put them into the appropriate list
			Actor a=gr.get(loc);
			if (a instanceof Bactor) { // Put Bactors in list
				Bactor b=(Bactor)a; // Cast the actor into a Bactor
				bactors.add(b); // Add it to the list
			} else if (a instanceof BonusActor) { // Put Bactors in list
				BonusActor b=(BonusActor)a; // Cast the actor into a Bactor
				bonusActors.add(b); // Add it to the list
			} else { // if it isn't an instance of something else that goes in it's own list, then it must be an actor
				actors.add(a);
			}
		}
		return bactors.size();
	}
	public void preBattle(ArrayList<BattleRecord> initialBattleRecords) {
		this.battleRecords = initialBattleRecords;
	}
	public void step()
	{		
		if (this.reporter==null) {
			this.setMessage("Error: No Valid Reporter Class Found, ignoring STEP");
			return;
		}
		if (this.doBattle==null) {
			this.setMessage("Error: No Valid DoBattle Class Found, ignoring STEP");	
			return;
		}
		if (!this.doBattle.isBattleInProgress()) {
			this.setMessage("Error: Battle Not In Progress, ignoring STEP");	
			return;			
		}
		this.damageDealt=false;
		this.steps++;
		this.announce("\n---------Turn  #" + this.steps);
		ArrayList<Bactor> bactors = new ArrayList<Bactor>();
		Grid<Actor> gr = getGrid();
		this.mostDamage=0;
		this.mostPoints=0;
		this.mostDamageBactor=null;
		this.mostPointsBactor=null;
		for (Location loc : gr.getOccupiedLocations()) {
			Actor a=gr.get(loc);
			if (a instanceof Bactor) { 
				Bactor b=(Bactor)a;
				bactors.add(b);
			}
		}
		if (this.stepsWithNoDamage>=this.stepsUntilPenalty) {
			for (Bactor b:bactors) {
				b.setHealth(b.getHealth()-this.penaltyDamage);
			}
			this.stepsWithNoDamage=0;
			if (this.stepsUntilPenalty==1) {
				this.penaltyDamage*=2;
			} else {
				this.stepsUntilPenalty/=2;
				if (this.stepsUntilPenalty<1) this.stepsUntilPenalty=1;
			}
		}
		String battleOf="Battle #"+battleNum+"/"+totalBattles;
		String newMessage=battleOf+", Turn #" + this.steps + ", peaceful turns=" + this.stepsWithNoDamage + " Peace dividend in=" +
				this.stepsUntilPenalty + ",\nPeace dividend=" + this.penaltyDamage;
		this.setMessage(newMessage);
		this.logFile.write(this.getMessage(), false);

		int battlers=this.battleStep();
		for (int i=0; i<this.battleRecords.size(); i++) {
			BattleRecord battleRecord=battleRecords.get(i);
			Bactor bactor=battleRecord.getBactor();
			BugStepRecord bugStepRecord = new BugStepRecord(bactor);
			battleRecord.setLastStep(bugStepRecord);
			this.reporter.updateTableRow(battleRecord.getTab().getRowIndex(), bugStepRecord);
		}
		// Sort Current Battle Participants by Points
		for (int x=0; x<battleRecords.size(); x++) { // bubble sort outer loop
			for (int i=0; i < battleRecords.size() - x - 1; i++) {
				if (battleRecords.get(i).compareTo(battleRecords.get(i+1)) < 0) {
					int tempIndex = battleRecords.get(i).getTab().getRowIndex();
					battleRecords.get(i).getTab().setRowIndex(battleRecords.get(i+1).getTab().getRowIndex());
					battleRecords.get(i+1).getTab().setRowIndex(tempIndex);
					BattleRecord temp = battleRecords.get(i);
					battleRecords.set(i,battleRecords.get(i+1) );
					battleRecords.set(i+1, temp);
				}
			}
		}
		for (BattleRecord battleRecord:battleRecords) {
			this.reporter.updateTableRow(battleRecord.getTab().getRowIndex(), battleRecord.getLastStep());
		}
		if (battlers<2) {
			this.doBattle.updateBattleRecords(this.battleRecords);
			this.doBattle.setBattleInProgress(false);
			this.reporter=null;
			this.doBattle=null;
			this.gameOn=false;
		}
		for (Bactor b:bactors) {
			if (b.isDealtDamage()) {
				this.damageDealt=true;
			}
		}

		if (this.damageDealt==false) {
			this.stepsWithNoDamage++;
			this.announcement=null;
		} else {
			if (this.mostDamageBactor!=null && this.mostPointsBactor!=null) {
				if (this.mostDamageBactor==this.mostPointsBactor) {
					this.announce(this.mostDamageBactor.getBattleBug().getName() + " blasts " + this.mostDamageVictim.getBattleBug().getName() + " doing " + this.mostDamage + " damage, and earning " + this.mostPoints + " points");
				} else {
					this.announce(this.mostDamageBactor.getBattleBug().getName() + " hits " + this.mostDamageVictim.getBattleBug().getName() + " doing " + this.mostDamage + " damage");
					this.announce(this.mostPointsBactor.getBattleBug().getName() + " earns " + this.mostPoints + " points from " + this.mostPointsVictim.getBattleBug().getName());					
				}
			} else if (this.mostDamageBactor!=null) {
				this.announce(this.mostDamageBactor.getBattleBug().getName() + " attacks " + this.mostDamageVictim.getBattleBug().getName() + " doing " + this.mostDamage + " damage");				
			} else if (this.mostPointsBactor!=null) {
				this.announce(this.mostPointsBactor.getBattleBug().getName() + " gets " + this.mostPoints + " points from " + this.mostPointsVictim.getBattleBug().getName());									
			}
			this.announceNow();
			this.stepsWithNoDamage=0;
		}
	}
	public ArrayList<BattleRecord> getBattleRecords() {
		return this.battleRecords;
	}

	/**
	 * Adds an actor to this world at a given location.
	 * @param loc the location at which to add the actor
	 * @param occupant the actor to add
	 */
	public void add(Location loc, Actor occupant)
	{
		if (occupant instanceof Bactor) {
			Bactor b = (Bactor)occupant;
			b.setWorld(this);
		}
		occupant.putSelfInGrid(getGrid(), loc);
	}

	/**
	 * Adds an occupant at a random empty location.
	 * @param occupant the occupant to add
	 */
	public void add(Actor occupant)
	{
		Location loc = getRandomEmptyLocation();
		if (loc != null)
			add(loc, occupant);
	}

	/**
	 * Removes an actor from this world.
	 * @param loc the location from which to remove an actor
	 * @return the removed actor, or null if there was no actor at the given
	 * location.
	 */
	public Actor remove(Location loc)
	{
		Actor occupant = getGrid().get(loc);
		if (occupant == null)
			return null;
		occupant.removeSelfFromGrid();
		return occupant;
	}
	// Custom Methods Not Yet Part of Main Project
	public GraphBox getGraphPane() {
		return graphPane;
	}
	public void setGraphPane(GraphBox graphPane) {
		this.graphPane = graphPane;
	}
	public ArrayList<Bactor> getBactors() {
		Grid<Actor> gr = getGrid();
		@SuppressWarnings("unused")
		ArrayList<Actor> actors = new ArrayList<Actor>();
		ArrayList<Bactor> bactors = new ArrayList<Bactor>();
		for (Location loc : gr.getOccupiedLocations()) {
			// Collects all actors by location, so put them into the appropriate list
			Actor a=gr.get(loc);
			if (a instanceof Bactor) { // Put Bactors in list
				Bactor b=(Bactor)a; // Cast the actor into a Bactor
				bactors.add(b); // Add it to the list
			} else { // if it isn't an instance of something else that goes in it's own list, then it must be an actor
				//        		actors.add(a);
			}
		}
		return bactors;
	}
	public boolean isGameOn() {
		return gameOn;
	}
	public void setGameOn(boolean gameOn) {
		this.gameOn = gameOn;
	}
	public boolean isRoundOn() {
		return roundOn;
	}
	public void setRoundOn(boolean roundOn) {
		this.roundOn = roundOn;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public int getCurrentRound() {
		return currentRound;
	}
	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}
	public ArrayList<Bactor> getEliminatedBactors() {
		return eliminatedBactors;
	}
	public void setEliminatedBactors(ArrayList<Bactor> eliminatedBactors) {
		this.eliminatedBactors = eliminatedBactors;
	}
	public JTextArea getGameText() {
		return gameText;
	}
	public void setGameText(JTextArea gameText) {
		this.gameText = gameText;
	}

	public DoBattle getDoBattle() {
		return doBattle;
	}

	public void setDoBattle(DoBattle doBattle) {
		this.doBattle = doBattle;
	}
	/**
	 * @param announce
	 */
	public void announce(String announce) {
		if (BattleRunner.FAST_RUN) return;
		if (this.announcement==null) {
			this.announcement=announce + "\n";
		} else {
			this.announcement+=announce+"\n";
		}
	}
	public void fastAnnounce(String announce) {
		this.announcement=announce;
	}
	public void announceNow() {
		if (this.reporter!=null) {
			if (this.announcement!=null) {
				this.reporter.announce(this.announcement);
				this.announcement=null;
			}
		}
	}

	public int getStepsWithNoDamage() {
		return stepsWithNoDamage;
	}

	public int getStepsUntilPenalty() {
		return stepsUntilPenalty;
	}

	public int getPenaltyDamage() {
		return penaltyDamage;
	}

	public int getMinimumTurnsBetweenNewBonus() {
		return minimumTurnsBetweenNewBonus;
	}

	public int getTurnsSinceLastNewBonus() {
		return turnsSinceLastNewBonus;
	}

	int getInitialBactorsInRound() {
		return initialBactorsInRound;
	}

	void setInitialBactorsInRound(int initialBactorsInRound) {
		this.initialBactorsInRound = initialBactorsInRound;
	}

	int getFreeBonusSpawns() {
		return freeBonusSpawns;
	}

	void setFreeBonusSpawns(int freeBonusSpawns) {
		this.freeBonusSpawns = freeBonusSpawns;
	}
	public int getTurnsToPossibleBonusSpawn() {
		return this.minimumTurnsBetweenNewBonus-this.turnsSinceLastNewBonus;
	}
}
