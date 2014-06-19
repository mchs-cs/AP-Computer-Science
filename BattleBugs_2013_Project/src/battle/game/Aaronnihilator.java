// Version 9
// Added code so the Aaronnihilator always goes after a bonus
// if it calculates that it can reach that bonus before everyone
// else on the board
//
// Version 8
// In the final battle, restored these settings:
//				ignoreFoePercent=0.75;
//				minSafeHealth=50;
//
// Version 7
// Adding Point Bonuses to normal and ignoreHealth bonus lists
//
// Version 6
// ignoreFoePercent changed from .75 to .6
// minSafeHealth increased from 50 to 60
//
package battle.game;
import info.gridworld.grid.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import battle.pack.AbstractBattleBug;
import battle.pack.Act;
import battle.pack.Attribute;
import battle.pack.Bonus;
import battle.pack.BonusTarget;
import battle.pack.Informer;
import battle.pack.Informer.DistanceMeasure;
import battle.pack.Path;
import battle.pack.Target;
public class Aaronnihilator extends AbstractBattleBug {
	// Reporting methods	
	public String getName() {return "Aaronnihilator";}
	public String getCreator() {return "Mr. B.";}
	public long getVersion() {return 6;}

	// My Target Wrapper Stuff
	private List<Opponent> aTargets;
	// Useful Collections
	private ArrayList<Bonus> ignoreHealthBonusTypes;
	private ArrayList<Bonus> normalBonusTypes;
	private ArrayList<Bonus> rescueBonusTypes;

	// Used to keep track of Normal vs. Final Four operation
	private enum Mode {NORMAL,RESCUE};
	private Mode mode;

	// Used to keep track of movement modes
	private enum MoveMode {NORMAL,JINK,JINK2};
	private MoveMode moveMode=null;
	private Target jinkCause;

	// Used to determine which goals to pursue
	private double ignoreFoePercent=0.6;
	boolean mustAdvance;
	boolean willAdvance;
	private int foesRemaining;
	private ArrayList<Bonus> goalBonusTypes;
	private int minSafeHealth=60;
	// Used to keep track of which opponent I am moving towards
	private Target oTarget=null;
	private BonusTarget bTarget=null;

	// Used to keep track of my current stats:
	int myAttack;
	int myDefense;
	int myHealth;
	int myMaxHealth;
	int possibleDamageToMe;
	private ArrayList<Target> allTargetsInBattle;
	private int initialFoes;

	// Use to keep track of how many times I've moved
	private int steps=0;

	public void resetBug() {
		ignoreHealthBonusTypes=new ArrayList<Bonus>();
		ignoreHealthBonusTypes.add(Bonus.ATTACK);
		ignoreHealthBonusTypes.add(Bonus.DEFENSE);
		ignoreHealthBonusTypes.add(Bonus.MYSTERY);
		ignoreHealthBonusTypes.add(Bonus.MAX_HEALTH);
		ignoreHealthBonusTypes.add(Bonus.POINTS);
		normalBonusTypes=new ArrayList<Bonus>();
		normalBonusTypes.add(Bonus.ATTACK);
		normalBonusTypes.add(Bonus.DEFENSE);
		normalBonusTypes.add(Bonus.MYSTERY);
		normalBonusTypes.add(Bonus.HEALTH);
		normalBonusTypes.add(Bonus.MAX_HEALTH);
		normalBonusTypes.add(Bonus.POINTS);
		rescueBonusTypes=new ArrayList<Bonus>();
		rescueBonusTypes.add(Bonus.HEALTH);
		rescueBonusTypes.add(Bonus.MAX_HEALTH);
		rescueBonusTypes.add(Bonus.DEFENSE);
		goalBonusTypes=normalBonusTypes;
		mode=Mode.NORMAL;
		oTarget=null;
		bTarget=null;
		steps=0;
		initialFoes=0;
		moveMode=MoveMode.NORMAL;
		jinkCause=null;
	}
	private Location getLocation() {
		return new Location(myInformer().getRow(),myInformer().getCol());
	}
	private double absDistance(Location loc1, Location loc2) {
		int x1=loc1.getCol();
		int y1=loc1.getRow();
		int x2=loc2.getCol();
		int y2=loc2.getRow();
		return Math.sqrt(Math.pow((x1-x2), 2)+Math.pow((y1-y2),2));
	}
	private int getDirectionToward(Location from, Location to) {
		int dx = to.getCol() - from.getCol();
		int dy = to.getRow() - from.getRow();
		// y axis points opposite to mathematical orientation
		int angle = (int) Math.toDegrees(Math.atan2(-dy, dx));

		// mathematical angle is counterclockwise from x-axis,
		// compass angle is clockwise from y-axis
		int compassAngle = 90 - angle;
		// wrap negative angles
		if (compassAngle < 0)
			compassAngle += 360;
		return compassAngle;
	}
	private Act nextMove(Location myLoc, Location toLoc) {
		int myX=myLoc.getCol();
		int myY=myLoc.getRow();
		int myDir=myInformer().getDirection();
		int toX=toLoc.getCol();
		int toY=toLoc.getRow();

		int xD=Math.abs(myX-toX);
		int yD=Math.abs(myY-toY);
		boolean turnAllowed=(xD==0 || yD==0 || (xD==yD));

		Location withMove=myLoc.getAdjacentLocation(myDir);
		double distWithMove=absDistance(withMove,toLoc);
		int absDirTo=getDirectionToward(myLoc,toLoc);
		int dirTo=StaticLibrary.StaticLibrary.degreesRel(myDir, absDirTo);
		if (dirTo>45) return Act.TURN_CLOCKWISE;
		if (dirTo<-45) return Act.TURN_COUNTER_CLOCKWISE;
		int newDir=myDir+dirTo;
		Location withTurn=myLoc.getAdjacentLocation(newDir);
		double distWithTurn=absDistance(withTurn,toLoc);
		if (turnAllowed && distWithTurn<distWithMove) {
			if (dirTo>=0) {
				return Act.TURN_CLOCKWISE;
			} else {
				return Act.TURN_COUNTER_CLOCKWISE;
			}
		} else {
			return Act.MOVE;
		}		
	}

	private boolean haveOTarget() {
		if (oTarget==null) return false;
		return oTarget.targetValid();
	}
	private boolean haveBTarget() {
		if (bTarget==null) return false;
		return bTarget.isValid();
	}
	private boolean haveTarget() {
		if (haveBTarget()) {
			oTarget=null;
			return true;
		} else if (haveOTarget()) {
			return true;
		}
		return false;
	}
	private int getRelDir() {
		if (haveBTarget()) {
			return bTarget.getRelDirectionTo();
		} else if (haveOTarget()) {
			return oTarget.getRelDirectionTo();
		} else return 0;
	}
	private Location getMoveLoc() {
		if (haveBTarget()) {
			return bTarget.getLocation();
		} else if (haveOTarget()) {
			return oTarget.getLocation();
		} else return null;
	}

	private String getTargetName() {		
		if (haveBTarget()) {
			return bTarget.toString();
		} else if (haveOTarget()) {
			return oTarget.toString();
		} else return "No Target!";
	}
	private int max(int a, int b) {
		if (a>=b) return a;
		return b;
	}
	/** 
	 * @param t The Target to compare to
	 * @return The difference in damage dealt vs. received
	 * positive values represent more damage to the opponent
	 * than damage received.
	 */
	private int damageDiff(Target t) {
		if (steps>1) {
			t.getTargetAttack();
		}
		int dToFoe=damageToFoe(t);
		int dToMe=damageToMe(t);
		int diff=dToFoe-dToMe;
		return diff;
	}
	private int damageToFoe(Target t) {
		int oDefense=t.getTargetDefense();
		int damToO=max(0,myAttack-oDefense);
		return damToO;

	}
	private int damageToMe(Target t) {
		int oAttack=t.getTargetAttack();
		int damToMe=max(0,oAttack-myDefense);
		return damToMe;		
	}
	private Act jink() {
		Boolean canMoveClockwise=myInformer().canMoveClockwise();
		Boolean canMoveCounterClockwise=myInformer().canMoveCounterClockwise();
		if (moveMode.equals(MoveMode.NORMAL)) moveMode=MoveMode.JINK;
		if (moveMode.equals(MoveMode.JINK)) { // Begin Jink Manuever
			if (canMoveClockwise || canMoveCounterClockwise) {
				moveMode=MoveMode.JINK2;
				if (canMoveClockwise) {
					print("Turning clockwise to JINK");
					return Act.TURN_CLOCKWISE;
				} else {
					print("Turning counter-clockwise to JINK");
					return Act.TURN_COUNTER_CLOCKWISE;
				}
			} else { // No immediate escape vector
				moveMode=MoveMode.JINK;
				int turnDir=myInformer().getOpenRelDirection();
				print("I'll have to turn "+turnDir+" to escape!");
				if (turnDir>180 || turnDir<-180)  { // Surrounded, fight or defend!
					print("I appear to be surrounded! Defend!");
					return Act.DEFEND;
				} else {
					if (turnDir>0) {
						print("Turning clockwise");
						return Act.TURN_CLOCKWISE;
					} else if (turnDir<0) {
						print("Turning counter-clockwise");
						return Act.TURN_COUNTER_CLOCKWISE;
					} else {
						print("Oops, you have LOGIC ERROR while calculating JINK! You should not reach this point if you can move!");
						return Act.DEFEND;
					}
				}
			}
		}
		if (myInformer().canMove()) {
			print("Completing JINK manuever");
			moveMode=MoveMode.NORMAL;
			return Act.MOVE;
		} else {
			print("Cannot complete JINK manuever, starting JINK again!");
			moveMode=MoveMode.JINK;
			return jink();
		}
	}
	/**
	 * outTorB(): OutputTargetorBonus
	 * @return String summary of the selected Bonus Target if it is not null,<br>
	 * the Opponent Target if it is not null, or<br>
	 * "I have no Target!" if both are null.
	 */
	private String outTorB() {
		if (bTarget!=null) return outBonus(bTarget);
		if (oTarget!=null) return outTarget(oTarget);
		return "I have no Target!";
	}

	/**
	 * outBonus(): OutputBonusTarget b
	 * @param b the BonusTarget to summarize
	 * @return String summary including the Bonus type and location<br>
	 * of the Bonus Target b if it is not null,<br>
	 * "The bonus is no longer valid," or<br>
	 * or "The value passed to outBonus() was null"
	 */
	private String outBonus(BonusTarget b) {
		if (b!=null && b.isValid()) return b.getEffect().toString()+b.getLocation().toString();
		if (b!=null) return "The bonus is no longer valid";
		return "The value passed to outBonus() was null";
	}
	/**
	 * outTarget(): OutputTarget t
	 * @param t the Target to summarize
	 * @return String summary including the name, location,<br>
	 * attack, defense, and damage differential in the form<br>
	 * Name(row,col)[attack/defense/DamageDifferential]<br>
	 * of the Target t if it is not null,<br>
	 * or "The value passed to outTarget() was null"
	 */
	private String outTarget(Target t) {
		if (t!=null && t.targetValid()) return t.getName()+t.getLocation()+"["+t.getTargetAttack()+"/"+t.getTargetDefense()+"/"+damageDiff(t)+"]";
		if (t!=null) return "The target is no longer valid";
		return "The value passed to outTarget() was null";
	}
	private void summarizeMe(boolean endSummary) {
		print("Current Stats:");
		print("Attack: "+myAttack);
		print("Defense: "+myDefense);
		print("Health: "+myHealth+"/"+myMaxHealth);
		print("Points: "+myInformer().getPoints());
		print("Mode: "+mode);
		if (mustAdvance) {
			print("I must advance!");
		}
		print("I am in "+moveMode+" movement mode.");
		if (haveTarget()) {
			print("I'm pursuing "+outTorB());
		}
		if (!endSummary) {
			if (possibleDamageToMe>0) {
				print("I could take up to "+possibleDamageToMe+" damage this round from "+myInformer().getAdjacentTargetsFacingMeList().size()+" foes");
			}
			String s="I am considering bonuses: ";
			for (int i=0; i<goalBonusTypes.size(); i++) {
				s+=goalBonusTypes.get(i);
				if (i<goalBonusTypes.size()-1) s+=", ";
			}
			print(s);
		}
	}
	private Act endAct(Act act) {
		summarizeMe(true);
		print("Finally, I have decided that my act will be:");
		print("||-->"+act.toString()+"<--||");
		print("<---- End Step #"+steps+" ---->");
		return act;
	}
	private int calcPossibleDamageToMe() {
		int possibleDamageToMe=0;
		List<Target> targetsCanHitMe=myInformer().getAdjacentTargetsFacingMeList();
		for (Target target:targetsCanHitMe) {
			int foeAttack=target.getTargetAttack();
			if (foeAttack>myDefense) {
				possibleDamageToMe+=foeAttack-myDefense;
			}
		}
		return possibleDamageToMe;
	}
	private boolean willAdvance(boolean verbose) {
		int myRank=myRank();
		int rankToAdvance=(initialFoes/2);
		if (verbose) print("My Rank="+myRank+", Rank needed to advance="+rankToAdvance);
		return myRank<rankToAdvance;
	}
	private int myRank() {
		Collections.sort(allTargetsInBattle, new Comparator<Target>() {
			public int compare(Target target1, Target target2) {
				return -(target1.getTargetPoints() - target2.getTargetPoints());
			}
		});
		int myRank=0;
		int myPoints=myInformer().getPoints();
		while (myRank<allTargetsInBattle.size() && myPoints<=allTargetsInBattle.get(myRank).getTargetPoints()) {
			myRank++;
		}
		return myRank;
	}
	private boolean mustAdvance(boolean verbose) {
		if (verbose) {
			print("There are "+foesRemaining+" foes remaining, and I am ranked "+myRank());
			if (willAdvance) {
				print("I will Advance");
				return false;
			} else {
				print("I Will not currently Advance!");
			}			
		}
		double foeRatio=foesRemaining/(double)initialFoes;
		if (verbose) {
			print("The current foeRatio="+foeRatio+" and I MUST ADVANCE when it falls below "+ignoreFoePercent);
		}
		if (foeRatio<=ignoreFoePercent || initialFoes==3) {
			return true;
		}
		return false;
	}
	private int bestDamageDiff() {
		List<Target> targets=myInformer().getSortedTargetList(Informer.SORT_ASCENDING, Attribute.DEFENSE);
		int bestDamageDiff;
		if (targets.size()>1) {
			// First find the best Damage Differential
			bestDamageDiff=damageDiff(targets.get(0));
			for (int i=1; i<targets.size(); i++) {
				Target t=targets.get(i);
				int curDamageDiff=damageDiff(t);
				if (curDamageDiff>=bestDamageDiff) {
					bestDamageDiff=curDamageDiff;
				}
			}
		} else {
			bestDamageDiff=0;
		}
		return bestDamageDiff;
	}
	private List<Target> getMySortedTargets() {
		List<Target> targets=myInformer().getSortedTargetList(Informer.SORT_ASCENDING, Attribute.DEFENSE);
		if (targets.size()>1) {
			// First find the best Damage Differential
			int bestDamageDiff=bestDamageDiff();
			// Remove all targets that have a worse damage differential
			for (int i=targets.size()-1; i>=0; i--) {
				Target t=targets.get(i);
				if (damageDiff(t)<bestDamageDiff) {
					targets.remove(i);
				}
			}
			// Sort remaining targets by distance
			if (targets.size()>1) {
				for (int i = 1; i < targets.size(); i++) {
					Target t=targets.get(i);
					int valueToSort =myInformer().getTargetPath(t).size();
					int j = i;
					while (j > 0 && myInformer().getTargetPath(targets.get(j-1)).size() > valueToSort) {
						targets.set(j, targets.get(j-1));
						j--;
					}
					targets.set(j, t);
				}
			}
		}
		return targets;
	}
	public static String rJust(String s, int maxLen) {
		s=trim(s,maxLen);
		while (s.length()<maxLen) s=" "+s;
		return s;
	}
	public static String lJust(String s, int maxLen) {
		s=trim(s,maxLen);
		while (s.length()<maxLen) s+=" ";
		return s;
	}
	public static String trim(String s, int maxLen) {
		if (s.length()>maxLen) s=s.substring(0, maxLen);
		return s;
	}
	private void printTargets(List<Target> targets) {
		int maxNameLen=14;
		String s="Name";
		while(s.length()<maxNameLen) s+=" ";		
		print(s+" Grid Loc Atk Def Dif Dis");
		s="----";
		while(s.length()<maxNameLen) s+="-";		
		print(s+" -------- --- --- --- ---");
		for (Target t:targets) {
			String tName=rJust(t.getName(),maxNameLen);
			String tLoc=rJust(""+t.getLocation(),8);
			String tAttack=rJust(""+t.getTargetAttack(),3);
			String tDefense=rJust(""+t.getTargetDefense(),3);
			String tDamageDiff=rJust(""+damageDiff(t),3);
			String tDistance=rJust(""+myInformer().getTargetPath(t).size(),3);
			print(tName+" "+tLoc+" "+tAttack+" "+tDefense+" "+tDamageDiff+" "+tDistance);
		}
	}
	private Act actNew() {
		if (!moveMode.equals(MoveMode.NORMAL)) {
			print("Continuing jink manuever!");
			return jink();
		}
		oTarget=null;
		Opponent aTarget=null;
		for (int i=0; i<aTargets.size(); i++) {
			Opponent at=aTargets.get(i);
			if (mustAdvance) {
				if (at.t.getTargetDefense()<myAttack) {
					aTarget=at;
					break;
				}
			} else {
				if (at.getDamageDiff()>0) {
					aTarget=at;
					break;
				}
			}
		}
		BonusWrapper sureThingBonus=getBonusFirst();
		if (sureThingBonus!=null) {
			aTarget=null;
			bTarget=sureThingBonus.b;
		} else {
			if (myHealth+50>myInformer().getMaxHealth() && goalBonusTypes.contains(Bonus.HEALTH)) {
				print("A health bonus wouldn't help, removing it from the list of goal bonuses!");
				goalBonusTypes.remove(Bonus.HEALTH);
			}
			List<BonusTarget> bonuses=myInformer().getBonusTargetByType(goalBonusTypes, DistanceMeasure.PATH);
			List<BonusWrapper> wBonuses=new ArrayList<BonusWrapper>();
			for (BonusTarget b:bonuses) {
				wBonuses.add(new BonusWrapper(b));
			}
			Collections.sort(wBonuses);
			print("Current Bonuses:");
			for (int i=0; i<wBonuses.size(); i++) {
				BonusWrapper bw=wBonuses.get(i);
				if (i==0) print(bw.getSummaryHeading());
				print(bw.getSummary());
			}
			if (!wBonuses.isEmpty()) {
				bTarget=wBonuses.get(0).b;
			} else {
				print("No bonuses in list matching my goals");
				if (aTarget==null) {
					print("There are no opponents I should fight, getting list of ALL bonuses!");
					bonuses=myInformer().getBonusTargetList(DistanceMeasure.PATH);
					if (!bonuses.isEmpty()) {
						bTarget=bonuses.get(0);
					} else {
						print("There are NO bonuses on the board!");
						bTarget=null;
					}
				} else {
					print("Since I have a target, I will not pursue other bonus types");
					bTarget=null;
				}
			}
		}
		if (bTarget!=null && aTarget!=null) {
			int btDist=new BonusWrapper(bTarget).getModDistance();
			int atDist=aTarget.getModDistance();
			print("Choosing between "+outBonus(bTarget)+" using distance: "+btDist+" & "+outTarget(aTarget.t)+" using distance: "+atDist);
			if (btDist<=atDist) {
				print("Choosing bonus");
				aTarget=null;
			} else {
				print("Choosing opponent");
				oTarget=aTarget.t;
				bTarget=null;
			}
		} else if (aTarget!=null) {
			oTarget=aTarget.t;
		}
		if (myInformer().foeInFront()) {
			Target foe=myInformer().targetFoeInFront();
			if (damageToFoe(foe)<1) {
				print("Jinking because I cannot do any damage to the foe in front of me...");
				return jink();						
			} else if (mode.equals(Mode.RESCUE) && targetEquals(foe,oTarget)) {
				print("I'm in RESCUE mode, but attack anyway!");
			}  else if (mode.equals(Mode.RESCUE) && !targetEquals(foe,oTarget)) {
				print("Jinking because the foe before me isn't my chosen foe and I am in Rescue mode...");
				return jink();		
			} 
			print("Attacking: "+outTarget(myInformer().targetFoeInFront()));
			return Act.ATTACK;
		}

		if (haveTarget()) {
			// Find the relative direction to my target
			Act moveAct=this.nextMove(new Location(myInformer().getRow(),myInformer().getCol()), getMoveLoc());
			int dirToTarget=getRelDir();
			print("Moving towards "+outTorB()+" by "+moveAct.toString());
			return moveAct;
		}
		if (possibleDamageToMe>0) {
			print("I don't have the chops to attack anyone and there are no bonuses, but I can be hurt...");
			if (myInformer().canMove()) {
				print("Running away!!!!!");
				return Act.MOVE;
			} else {
				if (myInformer().foeInFront()) {
					print("Jinking because there is a foe in the way and I want to RUN!");
					return jink();		
				} else {
					int relDir=myInformer().getOpenRelDirection();
					if (Math.abs(relDir)==180) {
						print("I'm surrounded! Defensive crouch while I wait to die!");
						return Act.DEFEND;
					} else if (relDir<0) {
						return Act.TURN_COUNTER_CLOCKWISE;
					} else {
						return Act.TURN_CLOCKWISE;
					}
				}
			}
		}
		// If I couldn't find anything else to do, defend myself by default!
		print("Oh no! I don't know what to do!");
		return Act.DEFEND;

	}
	private boolean targetEquals(Target t1, Target t2) {
		if ((t1==null && t2!=null) || (t1!=null && t2==null)) return false;
		if (t1==null && t2==null) return true;
		return t1.getLocation().equals(t2.getLocation());
	}
	public Act act() {
		myAttack=myInformer().getAttack();
		myDefense=myInformer().getDefense();
		myHealth=myInformer().getHealth();
		myMaxHealth=myInformer().getMaxHealth();
		if (myInformer().getLastAct().equals(Act.DEFEND)) {
			int reduce=(int)(myDefense*.5);
			myDefense-=reduce;
		}
		if (steps==0) {
			allTargetsInBattle=myInformer().getTargetList();
			aTargets=new ArrayList<Opponent>();
			for (Target t:allTargetsInBattle) {
				Opponent at=new Opponent(t);
				at.update();
				aTargets.add(at);
			}
			initialFoes=myInformer().getTargetList().size();
			if (initialFoes==63) {
				print("Fifth to Last Battle!");
			} else if (initialFoes==31) {
				print("Fourth to Last Battle!");				
			} else if (initialFoes==15) {
				print("Second to Last Battle!");
			} else if (initialFoes==7) {
				print("Second to Last Battle!");
			} else if (initialFoes==3) {
				ignoreHealthBonusTypes=new ArrayList<Bonus>();
				ignoreHealthBonusTypes.add(Bonus.ATTACK);
				ignoreHealthBonusTypes.add(Bonus.DEFENSE);
				normalBonusTypes=new ArrayList<Bonus>();
				normalBonusTypes.add(Bonus.ATTACK);
				normalBonusTypes.add(Bonus.DEFENSE);
				normalBonusTypes.add(Bonus.MAX_HEALTH);
				rescueBonusTypes=new ArrayList<Bonus>();
				rescueBonusTypes.add(Bonus.HEALTH);
				rescueBonusTypes.add(Bonus.MAX_HEALTH);
				rescueBonusTypes.add(Bonus.DEFENSE);

				print("Final Battle!");
			}
		} else {
			for (int i=aTargets.size()-1; i>=0; i--) {
				Opponent at=aTargets.get(i);
				if (!at.update()) {
					aTargets.remove(i);
				}
			}
		}
		Collections.sort(aTargets);
		steps++;
		possibleDamageToMe=calcPossibleDamageToMe();
		foesRemaining=myInformer().getTargetList().size();
		if (myHealth<50) {
			goalBonusTypes=rescueBonusTypes;
			mode=Mode.RESCUE;
		} else if (myHealth+50>myInformer().getMaxHealth()) {
			goalBonusTypes=ignoreHealthBonusTypes;
			mode=Mode.NORMAL;
		} else {
			goalBonusTypes=normalBonusTypes;
			mode=Mode.NORMAL;
		}
		print("\n----> Step #"+steps+" <----");
		willAdvance=willAdvance(true);
		mustAdvance=mustAdvance(true);
		summarizeMe(false);
		print("Current Target List:");
		for (int i=0; i<aTargets.size(); i++) {
			if (i==0) print(aTargets.get(i).getSummaryHeading());
			print(aTargets.get(i).getSummary());
		}
		if (myInformer().haveBonus()) {
			print("Using Bonus: "+myInformer().getEffect());
			return endAct(Act.USE_BONUS);
		} else if (myInformer().bonusInFront()) {
			print("Getting Bonus: "+outBonus(myInformer().getBonusTargetList(DistanceMeasure.PATH).get(0)));
			return endAct(Act.GET_BONUS);
		}
		return endAct(actNew());
	}
	private BonusWrapper getBonusFirst() {
		List<BonusTarget> bonuses=myInformer().getBonusTargetList(DistanceMeasure.PATH);
		for (int i=bonuses.size()-1; i>=0; i--) {
			BonusWrapper bw=new BonusWrapper(bonuses.get(i));
			if (!bw.reachFirst()) {
				bonuses.remove(i);
			}
		}
		if (bonuses.isEmpty()) {
			print("There are no bonuses I can reach first");
			return null;
		}
		print("I can reach "+outBonus(bonuses.get(0))+" before anyone else.");
		return new BonusWrapper(bonuses.get(0));
	}

	final class BonusWrapper implements Comparable<BonusWrapper> {
		private BonusTarget b;
		private int pathDistance;
		public BonusWrapper(BonusTarget b) {
			this.b=b;
			pathDistance=myInformer().getPathToBonus(b).size();
		}
		public boolean isValid() {
			return b.isValid();
		}
		public int getPathDistance() {
			return pathDistance;
		}
		public boolean reachFirst() {
			List<Target> targets=myInformer().getTargetList();
			for (Target t:targets) {
				int distFromT=new Path(t.getLocation(),t.getTargetDirection(),b.getLocation()).size();
				if (distFromT<=getPathDistance()) return false;
			}
			return true;
		}
		public int compareTo(BonusWrapper o) {
			if (!isValid()) return Integer.MAX_VALUE;
			if (!o.isValid()) return Integer.MIN_VALUE;
			return getModDistance()-o.getModDistance();
		}
		public int getModDistance() {
			int pd=getPathDistance();
			Bonus be=b.getEffect();
			if (be.equals(Bonus.HEALTH) || be.equals(Bonus.MAX_HEALTH)) {
				int hDiff=(myHealth-minSafeHealth);
				pd+=hDiff;
			}
			return pd;
		}
		private String getSummary() {
			String tName=rJust(""+b.getEffect(),10);
			String tLoc=rJust(""+b.getLocation(),8);
			String tMDistance=rJust(""+getModDistance(),3);
			String tPDistance=rJust(""+getPathDistance(),3);
			String s=tName+" "+tLoc+" "+tMDistance+" "+tPDistance;
			if (reachFirst()) {
				s+="  Yes";
			} else {
				s+="  No";
			}
			return s;
		}
		public String getSummaryHeading() {
			String s=lJust("Type",10);
			s+=" Grid Loc mD  pD  R F";
			s+="\n";
			String nameUnder="";
			while(nameUnder.length()<10) nameUnder+="-";			
			s+=nameUnder+" -------- --- --- ---";
			return s;

		}
		public String toString() {
			String s="";
			if (isValid()) {
				String tName=b.getEffect().toString();
				String tLoc="("+b.getRow()+","+b.getCol()+"->pD="+getPathDistance()+",mD="+getModDistance()+")";
				s+=tName+tLoc;
				return s;
			} else {
				return "Invalid";
			}
		}

	}
	final class Opponent implements Comparable<Opponent> {
		public static final int MAX_NAME_LEN=14;
		private Target t;
		private int damageDiff;
		private int pathDistance;
		public Opponent(Target t) {
			this.t=t;
			update();
		}
		private boolean update() {
			if (!t.targetValid()) return false;
			damageDiff=damageDiff(t);
			pathDistance=myInformer().getPathToTarget(t).size();
			return true;
		}
		private int getDamageDiff() {return damageDiff;}
		private int getPathDistance() {return pathDistance;}
		private int getModDistance() {return pathDistance-damageDiff;}
		public int compareTo(Opponent otherOpponent) {
			if (!t.targetValid()) return Integer.MAX_VALUE;
			Location loc=getLocation();
			int myDir=myInformer().getDirection();
			if (otherOpponent.update()) {
				return getModDistance()-otherOpponent.getModDistance();
			} else {
				return Integer.MIN_VALUE;
			}
		}
		private String getSummary() {
			String tName=rJust(t.getName(),MAX_NAME_LEN);
			String tLoc=rJust(""+t.getLocation(),8);
			String tAttack=rJust(""+t.getTargetAttack(),3);
			String tDefense=rJust(""+t.getTargetDefense(),3);
			String tDamageDiff=rJust(""+damageDiff(t),3);
			String tMDistance=rJust(""+getModDistance(),3);
			String tPDistance=rJust(""+pathDistance,3);
			String s=tName+" "+tLoc+" "+tAttack+" "+tDefense+" "+tDamageDiff+" "+tMDistance+" "+tPDistance;
			return s;
		}
		public String getSummaryHeading() {
			String s=lJust("Name",MAX_NAME_LEN);
			s+=" Grid Loc Atk Def Dif mD  pD";
			s+="\n";
			String nameUnder="";
			while(nameUnder.length()<MAX_NAME_LEN) nameUnder+="-";			
			s+=nameUnder+" -------- --- --- --- --- ---";
			return s;

		}
		public String toString() {
			String s="";
			if (update()) {
				String tName=trim(t.getName(),MAX_NAME_LEN);
				String tLoc="("+t.getRow()+","+t.getCol()+"->pD="+pathDistance+",mD="+getModDistance()+")";
				s+=tName+tLoc+"["+t.getTargetAttack()+"/"+t.getTargetDefense()+"/"+damageDiff+"]";
				return s;
			} else {
				return "Invalid";
			}
		}
	}
}
