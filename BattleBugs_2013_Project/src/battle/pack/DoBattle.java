package battle.pack;

import battle.pack.Reporter.MyTableModel;
import battle.pack.Reporter.TabRecord;
import info.gridworld.actor.Actor;
import info.gridworld.grid.*;

import java.io.IOException;
import java.util.ArrayList;

import StaticLibrary.StaticLibrary;

public class DoBattle {
	private static final int MIN_BUGS = 9;
	private static final int MIN_BATTLE_DELAY=50;
	private static final int MAX_BATTLE_DELAY=2500;

	private int battle_delay = 750;
	private ArrayList<BattleRecord> battleRecords;	
	private ArrayList<BattleBug> battlebugs;
	private ArrayList<Bactor> allBactors;
	private BattleWorld world;
	private Reporter reporter;
	private int startHealth,startMaxHealth;
	private boolean battleInProgress=false;
	private boolean battleRunning=false;
	private int totalBattles=0;
	private int currentBattle=0;
	private Bactor onlyBattlesWith=null;
	public int getTotalBattles() {return totalBattles;}
	public int getCurrentBattle() {return currentBattle;}
	public void setOnlyBattlesWith(String onlyBug) {
		for (Bactor bactor:allBactors) {
			if(bactor.getBattleBug().getName().equals(onlyBug)) {
				onlyBattlesWith=bactor;
			}
		}
	}
	public DoBattle(int startHealth, int startMaxHealth) {
		this.startHealth=startHealth;
		this.startMaxHealth=startMaxHealth;
		this.battlebugs = new ArrayList<BattleBug>();
		try {
			Class<?>[] classList = StaticLibrary.getClasses("battle.game");
			for (int i=0; i<classList.length; i++) {
				Class<?> aClass = classList[i];
				//System.out.println("Class: " + aClass.getName() + " is extended from " + aClass.getSuperclass());
				if (aClass.getSuperclass().toString().equals("class battle.pack.AbstractBattleBug")) {
					try {
						BattleBug b = (BattleBug) aClass.newInstance();
						this.battlebugs.add(b);
					} catch (InstantiationException e) {
						//						System.out.println("Catch:InstantiationException"+err);
						//						e.printStackTrace();
					} catch (IllegalAccessException e) {
						//						System.out.println("Catch:IllegalAccessException"+err);
						//						e.printStackTrace();
					} catch (UnsupportedClassVersionError e) {
						//						System.out.println("Catch:UnsupportedClassVersionError"+err);
						//						e.printStackTrace();						
					} catch (NullPointerException e) {
						//						System.out.println("Catch:NullPointerError"+err);
						//						e.printStackTrace();		
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Catch:ClassNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Catch:IOException");
			e.printStackTrace();
		}
		this.allBactors = new ArrayList<Bactor>(); // Create a list to hold BattleBugs through Bactors
		ColorIterator ci = new ColorIterator();
		RandomName name = new RandomName();

		// Add Each Unique BattleBug
		for (BattleBug battlebug : this.battlebugs) {
			try {
				Bactor b = new Bactor(battlebug);
				b.setBaseColor(ci.nextColor());
				this.allBactors.add(b);
			} catch(Error e) {
				System.out.println("Cannot Instantiate "+battlebug.getClass().getName());
			}
		}
		// Then add BattleBugs until a 2^n power is reached
		int totalBugs=StaticLibrary.next2PowerN(Math.max(MIN_BUGS, this.allBactors.size()));
		while (this.allBactors.size()<totalBugs) {
			BasicBattleBug bug=new BasicBattleBug();
			bug.setName(name.nextName());
			Bactor b=new Bactor(bug,ci.nextColor());
			this.allBactors.add(b);			
		}

		// And finally, keep adding BasicBattleBugs until an even multiple of 4 is reached
		while (this.allBactors.size() % 4 !=0) {
			FillerBug bug=new FillerBug();
			bug.setName(name.nextName());
			Bactor b=new Bactor(bug,ci.nextColor());
			this.allBactors.add(b);			
		}

		//		this.battleRecords=new ArrayList<BattleRecord>();
		this.reporter = new Reporter(this.allBactors);
		this.reporter.setDoBattle(this);
		this.battleRecords=this.reporter.getBattleRecords();
	}
	public ArrayList<BattlerRecord> beginBattle(ArrayList<BattlerRecord> battlerRecords, boolean pauseBetween, int numBattles, String onlyBug) {
		if (battlerRecords.size()==0) {
			for (Bactor bactor:allBactors) {
				BattleBug battleBug=bactor.getBattleBug();
				BattlerRecord br=new BattlerRecord();
				br.setBattleBug(battleBug);
				battlerRecords.add(br);
			}
		}
		ArrayList<Bactor> allBactorsCopy=new ArrayList<Bactor>();
		for (Bactor bactor:allBactors) {
			allBactorsCopy.add(bactor);
		}
		totalBattles=numBattles;
		for (int battleNum=0; battleNum<numBattles; battleNum++) {
			currentBattle=battleNum+1;
			ArrayList<Object[]> results=recursiveBattle(allBactorsCopy);
			for (BattleRecord br:this.battleRecords) {
				allBactorsCopy.set(br.getTab().getRowIndex(), br.getBactor());
			}
			for (int i=0; i<results.size(); i++) {
				Object[] row=results.get(i);
				String creator=(String)row[1];
				String bugName=(String)row[0];
				bugName=bugName.substring(bugName.indexOf(".")+2);
				for (BattlerRecord br:battlerRecords) {
					BattleBug b=br.getBattleBug();
					String theCreator=b.getCreator();
					String theName=b.getName();
					if (theCreator.equals(creator)) {
						if (theName.equals(bugName)) {
							br.addPoints(i);
							break;
						}
					}
				}
			}
			//System.out.println("Battle #"+battleNum+" complete!");
		}
		return battlerRecords;
	}
	public ArrayList<Object[]> recursiveBattle(ArrayList<Bactor> currentCombatants) {
		ArrayList<Object[]> results=new ArrayList<Object[]>();
		if (onlyBattlesWith==null || currentCombatants.contains(onlyBattlesWith)) {
			currentCombatants=this.doBattle(currentCombatants);
			if (currentCombatants.size()>4) {
				ArrayList<Bactor> topHalf=new ArrayList<Bactor>();
				ArrayList<Bactor> bottomHalf=new ArrayList<Bactor>();
				int midIndex=currentCombatants.size()/2;
				for (int i=0; i<midIndex; i++) {
					topHalf.add(currentCombatants.get(i));
				}
				for (int i=midIndex; i<currentCombatants.size(); i++) {
					bottomHalf.add(currentCombatants.get(i));
				}
				this.recursiveBattle(bottomHalf);
				this.recursiveBattle(topHalf);			
			}
		}
		MyTableModel tableModel=(MyTableModel)reporter.getTableModel();
		for (int rowIndex=0; rowIndex<tableModel.getRowCount(); rowIndex++) {
			Object[] row=tableModel.getRow(rowIndex);
			results.add(row);
		}
		return results;
	}
	public void setUpRound(ArrayList<Bactor> bactorsThisRound) {
		for (BattleRecord battleRecord:this.battleRecords) {
			TabRecord tabRecord=battleRecord.getTab();
			BugStepRecord starter = new BugStepRecord(tabRecord.getBactor());
			Bactor bactor=tabRecord.getBactor();
			bactor.setPoints(0);
			this.reporter.updateTableRow(tabRecord.getRowIndex(),starter);			
		}
		ArrayList<Bactor> bactors = new ArrayList<Bactor>();
		for (Bactor b: bactorsThisRound) {
			bactors.add(b);
		}
		// ----------------------------- First reset all parameters for each bactor to start conditions --------------------------------------
		int bactorsPerSide =bactors.size()/4;
		int gridSideLen = (bactorsPerSide-1)*3+9;
		Grid<Actor> newGrid = new BoundedGrid<Actor>(gridSideLen,gridSideLen);
		if (this.world==null) this.world = new BattleWorld(newGrid);
		else this.world.setGrid(newGrid);
		this.world.setReporter(this.reporter);
		this.world.setDoBattle(this);
		this.world.setPenalties(gridSideLen*3, 1);
		this.world.setFreeBonusSpawns(bactorsThisRound.size());
		this.world.setInitialBactorsInRound(bactorsThisRound.size()); // Currently unused
		this.world.setBattleNum(getCurrentBattle(), getTotalBattles());
		ArrayList<BattleRecord> battleRecords = new ArrayList<BattleRecord>();
		for (int i=0; i<bactorsThisRound.size(); i++) {
			Bactor b = bactorsThisRound.get(i);
			b.setWorld(this.world);
			for (BattleRecord aBattleRecord:this.battleRecords) {
				if (aBattleRecord.getBactor().equals(b)) {
					battleRecords.add(aBattleRecord);
					break;
				}
			}
		}
		this.reporter.resetAnnouncer();
		int minRank=battleRecords.get(0).getTab().getRowIndex();
		int maxRank=battleRecords.get(battleRecords.size()-1).getTab().getRowIndex();
		this.world.preBattle(battleRecords);
		this.world.show();
		this.world.setMessage("Ranks " + (minRank+1) + " to " + (maxRank+1));
		this.reporter.showMe();
		if (BattleRunner.FAST_RUN) {
			this.world.fastAnnounce("Battle "+this.currentBattle+"/"+this.totalBattles+" with contestants "+(minRank+1) + "-" + (maxRank+1));
			this.world.announceNow();
		} else {
			this.world.announce("Now entering the arena contestants ranked " + (minRank+1) + " to " + (maxRank+1) + ":");
			String contestants="";
			for (int i=0; i<battleRecords.size(); i++) {
				BattleRecord battleRecord=battleRecords.get(i);
				contestants+=battleRecord.getBactor().getName();
				if (i<battleRecords.size()-2) {
					contestants+=", ";
				} else if (i<battleRecords.size()-1) {
					contestants+=", and ";
				}
			}
			this.world.announce(contestants);
			this.world.announceNow();
		}
		for (Bactor b: bactors) {
			b.resetColor();
			b.resetBug();
			b.setHealth(this.startHealth);
			b.setMaxHealth(startMaxHealth);
			b.setPoints(0);
		}
		Grid<Actor> gr = this.world.getGrid();
		for (Location loc : gr.getOccupiedLocations()) { // Clear the grid of all actors
			Actor a=gr.get(loc);
			a.removeSelfFromGrid();
		}
		if (bactors.size()>0) {
			int direction=90;
			bactorsPerSide = bactors.size()/4;
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
							col=this.world.getGrid().getNumCols()-1;
							break;
						case 2:
							row=this.world.getGrid().getNumRows()-1;
							col=i*3+4;
							break;
						case 3:
							row=i*3+4;
							col=0;
							break;
						}
						Location locBonus = new Location(row, col);
						boolean goodBonus=false;
						BonusActor bonus=new BonusActor(Bonus.ATTACK.randomWeightedBonus());
						do {
							bonus = new BonusActor(Bonus.ATTACK.randomWeightedBonus());
							if (bonus.getEffect().equals(Bonus.ATTACK) || bonus.getEffect().equals(Bonus.DEFENSE) || bonus.getEffect().equals(Bonus.HEALTH) || bonus.getEffect().equals(Bonus.MYSTERY)) {
								goodBonus=true;
							}
						} while (!goodBonus);
						this.world.add(locBonus, bonus);
						row = this.world.indentFromGrid(1, row, true, gr);
						col = this.world.indentFromGrid(1, col, false, gr);

						Location loc=new Location(row,col);
						int rB=(int)(Math.random()*bactors.size());
						bactors.get(rB).putSelfInGrid(newGrid, loc);
						bactors.get(rB).setDirection(direction);
						bactors.remove(rB);	        		
					}
				}	        	
			}
		}
	}
	public Reporter getReporter() {
		return reporter;
	}
	public ArrayList<Bactor> doBattle(ArrayList<Bactor> bactorsThisRound) {
		this.setUpRound(bactorsThisRound);
		this.battleInProgress=true;
		this.battleRunning=true;
		while (this.battleInProgress) {
			try {
				Thread.sleep(this.battle_delay);					
			} catch (InterruptedException e) {
				e.printStackTrace();
			}							
		}
		ArrayList<BattleRecord> myBattleRecords=this.world.getBattleRecords();
		ArrayList<Bactor> results = new ArrayList<Bactor>();
		for (BattleRecord battleRecord:myBattleRecords) {
			results.add(battleRecord.getBactor());
		}
		this.reporter.repaint();
		try {
			Thread.sleep(1000);					
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Grid<Actor> gr = this.world.getGrid();
		for (Location loc : gr.getOccupiedLocations()) { // Clear the grid of all actors
			Actor a=gr.get(loc);
			a.removeSelfFromGrid();
		}
		/*
		for (BattleRecord battleRecord:this.battleRecords) {
			TabRecord tabRecord=battleRecord.getTab();
			Bactor bactor=tabRecord.getBactor();
		}
		 */
		//System.out.println("");

		for (Bactor b: bactorsThisRound) {
			b.resetColor();
			b.resetBug();
			b.setHealth(this.startHealth);
			b.setMaxHealth(startMaxHealth);
			b.setPoints(0);
		}
		return results;
	}
	public void show() {
		this.reporter.setVisible(true);
	}
	public void updateBattleRecords(ArrayList<BattleRecord> battleRecords) {
		for (BattleRecord battleRecord:battleRecords) {
			for (BattleRecord myBattleRecord:this.battleRecords) {
				if (battleRecord.getBactor().equals(myBattleRecord.getBactor())) {
					myBattleRecord.getTab().setRowIndex(battleRecord.getTab().getRowIndex());
				}
			}
		}
	}
	public boolean isBattleInProgress() {
		return battleInProgress;
	}
	public boolean isBattleRunning() {
		return battleRunning;
	}
	public void setBattleRunning(boolean battleRunning) {
		this.battleRunning = battleRunning;
	}
	public void setBattleDelay(int percent) {
		if (percent<0) {percent=0;}
		if (percent>100) {percent=100;}
		double per=(double)percent/100;
		this.battle_delay=(int)((DoBattle.MAX_BATTLE_DELAY-DoBattle.MIN_BATTLE_DELAY)*per)+DoBattle.MIN_BATTLE_DELAY;
	}
	public void setBattleInProgress(boolean battleInProgress) {
		this.battleInProgress = battleInProgress;
	}
	public void announce(String announce) {
		this.reporter.announce(announce);
	}
}