package battle.game;

import info.gridworld.grid.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import battle.pack.*;

public class MrBBug extends AbstractBattleBug {
	private class NearRecord {
		public BonusTarget bonus;
		public Target target;
		public int distance;
	}
	private class BonusMap {
		private int[][] map;
		private int[] nearestIndex;
		private ArrayList<Target> targets;
		private ArrayList<BonusTarget> bonuses;
		public int getStepsCloserToBonus(BonusTarget bonus, int myDistance, boolean verbose) {
			int result=0;
			String log="";
			for (int bIndex=0; bIndex<bonuses.size(); bIndex++) {
				BonusTarget bonusTarget=bonuses.get(bIndex);
				if (bonusTarget.getLocation().equals(bonus.getLocation())) {
					for (int tIndex=0; tIndex<targets.size(); tIndex++) {
						Target target=targets.get(tIndex);
						if (nearestIndex[tIndex]==bIndex) {
							int stepsTo=map[tIndex][bIndex];
							if (stepsTo<myDistance) {
								int stepsCloser=myDistance-stepsTo;
								log+=target.getName()+" is "+stepsCloser+"\n";
								result+=stepsCloser;
							}
						}
					}					
				}
			}
			if (verbose) System.out.println(log);
			return result;
		}
		public void updateMap() {
			targets=myInformer().getTargetList();
			bonuses=myInformer().getBonusTargetList();
			map=new int[targets.size()][bonuses.size()];
			nearestIndex=new int[targets.size()];
			for (int tIndex=0; tIndex<targets.size(); tIndex++) {
				Target target=targets.get(tIndex);
				nearestIndex[tIndex]=0;
				for (int bIndex=0; bIndex<bonuses.size(); bIndex++) {
					BonusTarget bonusTarget=bonuses.get(bIndex);
					Path path=new Path(target.getLocation(), target.getTargetDirection(), bonusTarget.getLocation());
					int steps=path.size();
					map[tIndex][bIndex]=steps;
					if (steps<map[tIndex][nearestIndex[tIndex]]) {
						nearestIndex[tIndex]=bIndex;
					}
				}
			}
		}
		public String toString() {
			String result=String.format("%-12s","")+" ";
			for (int bIndex=0; bIndex<bonuses.size(); bIndex++) {
				result+=String.format("%-3s", bonuses.get(bIndex).getEffect()).substring(0,3)+" ";
			}
			result+="\n";
			for (int tIndex=0; tIndex<targets.size(); tIndex++) {
				Target target=targets.get(tIndex);			
				result+=String.format("%-12s", target.getName()).substring(0,12)+" ";
				for (int bIndex=0; bIndex<bonuses.size(); bIndex++) {
					String mark=" ";
					if (bIndex==nearestIndex[tIndex]) mark="*";
					result+=String.format("%3d", map[tIndex][bIndex]).substring(0,3)+mark;
				}
				result+="\n";
			}
			return result;
		}
	}
	public String getName() {return "Tron";}
	public String getCreator() {return "Braskin, Aaron";}
	public long getVersion() {return 1;}

	private BonusMap bonusMap=new BonusMap();
	private static boolean DEBUG=false;
	private int turnIndex=0; // Use to keep track of how many turns have occurred.
	private enum Mode {NORMAL,JINK,JINK2};
	private Mode mode=null;
	private int initialFoes=0;
	private int currentFoeNum=0;
	private int myDefense=0;
	private int myHealth=0;
	private int myMaxHealth=0;
	private int myAttack=0;
	private int possibleDamageToMe=0;
	private int possibleDamageToFoe=0;
	private int minSafeHealth=0;
	private Target foeInFront=null;
	private ArrayList<Target> allTargetsInBattle;
	private boolean endGame=false;
	private double ignoreFoePercent=0.75;
	ArrayList<Bonus> bonusTypesToGoFor;
	ArrayList<Distance> distances;
	private Distance distance;
	private boolean mustAdvance=false;
	private int myRank=0;
	private double foeRatio=0;
	private int foeCount=0;
	private boolean attackFoesICanDamage=false;

	private ArrayList<BonusTarget> bonuses=null;
	private ArrayList<Target> targets=null;

	private TargetSummary targetSummary=null;

	public void resetBug() {
		mode=Mode.NORMAL;
		turnIndex=0;
		initialFoes=0;
		currentFoeNum=0;
		myDefense=0;
		myHealth=0;
		myMaxHealth=0;
		minSafeHealth=0;
		myAttack=0;
		possibleDamageToMe=0;
		possibleDamageToFoe=0;
		foeInFront=null;
		endGame=false;
		distances=new ArrayList<Distance>();
		distance=null;
		allTargetsInBattle=new ArrayList<Target>();
		myRank=0;
		mustAdvance=false;
		foeRatio=0;
		foeCount=0;
		attackFoesICanDamage=false;
		bonuses=null;
		targets=null;
		targetSummary=new TargetSummary();
	}
	private boolean willAdvance(boolean verbose) {
		if (initialFoes==3) {
			if (verbose) print("Final Round!");
			return false;
		}
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
	private void updateTurnSettings() {
		if (turnIndex==0) {
			allTargetsInBattle=myInformer().getTargetList();
			initialFoes=allTargetsInBattle.size();
		}
		turnIndex++;
		List<Target> targetsCanHitMe=myInformer().getAdjacentTargetsFacingMeList();
		bonuses=myInformer().getBonusTargetList();
		targets=myInformer().getTargetList();
		foeCount=targets.size();
		myDefense=myInformer().getDefense();
		myHealth=myInformer().getHealth();
		myMaxHealth=myInformer().getMaxHealth();
		myAttack=myInformer().getAttack();
		possibleDamageToMe=0;
		possibleDamageToFoe=0;
		foeInFront=myInformer().targetFoeInFront();
		foeRatio=foeCount/(double)initialFoes;
		myRank=myRank();
		for (Target target:targetsCanHitMe) {
			int foeAttack=target.getTargetAttack();
			if (foeAttack>myDefense) {
				possibleDamageToMe+=foeAttack-myDefense;
			}
		}
		targetSummary.update();
		distances=getBonusDistances();
		distances.addAll(getTargetDistances());
		Collections.sort(distances);
		print("\n************************> Turn#"+turnIndex+" <************************");
		print("My Attack/Defense:" + myAttack+"/"+myDefense+"\t(DtM=Damage to Me, DtF=Damage to Foe)");
		if (targetsCanHitMe.size()>0) {
			print("Foes that can attack me:");
			print("Foe    \tAtt/Def\tDtM\tDtF\tP-Val\tRemoved");
			for (Target target:targetsCanHitMe) {
				print(targetSummary(target));
			}
			print("Total possible damage to me: "+possibleDamageToMe);
		} else {
			print("No Foes can attack me");
		}
		if (foeInFront!=null) {
			int foeDefense=foeInFront.getTargetDefense();
			if (myAttack>foeDefense) {
				possibleDamageToFoe=myAttack-foeDefense;
			}
		}
		for (Distance d:distances) {
			print(""+d);
		}
	}	
	private int damageDone(int attack, int defense) {
		int damageDone=attack-defense;
		if (damageDone<0) {
			damageDone=0;
		}
		return damageDone;
	}

	private String targetSummary(Target target) {
		int targetDefense=target.getTargetDefense();
		int targetAttack=target.getTargetAttack();
		int damageToMe=damageDone(targetAttack,myDefense);
		int damageToFoe=damageDone(myAttack,targetDefense);
		int end=target.getName().length();
		if (end>8) {end=8;}
		return target.getName().substring(0, end)+"\t" +targetAttack+"/"+targetDefense+"\t"+damageToMe+"\t"+damageToFoe+"\t"+target.getTargetPointValue();
	}
	private Act nextAct() {
		if (myInformer().haveBonus()) return Act.USE_BONUS;
		if (myInformer().bonusInFront()) return Act.GET_BONUS;
		if (myInformer().foeInFront()) return Act.ATTACK;
		if (distances.size()>0) {
			distance=distances.get(0);
		} else distance=null;
		if (distance!=null && distance.getDistance()>0) {
			return distance.getSteps().get(0);
		}
		if (myInformer().canMove()) {
			return Act.MOVE;
		} else {
			return Act.TURN_CLOCKWISE;
		}
	}
	public void act(ActToken myToken) {
		updateTurnSettings();
		Act act=nextAct();
		print("Selected Act: "+act);
		if (act==Act.ATTACK) myToken.attack();
		else if (act==Act.DEFEND) myToken.defend();
		else if (act==Act.GET_BONUS) myToken.getBonus();
		else if (act==Act.MOVE) myToken.move();
		else if (act==Act.TURN_CLOCKWISE) myToken.turnClockwise();
		else if (act==Act.TURN_COUNTER_CLOCKWISE) myToken.turnCounterClockwise();
		else if (act==Act.USE_BONUS) myToken.useBonus();
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
	private Act nextMove(Position from, Position to) {
		Location myLoc=from.getLoc();
		Location toLoc=to.getLoc();
		int myX=myLoc.getCol();
		int myY=myLoc.getRow();
		int myDir=from.getDirection();
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
	private ArrayList<Act> steps(Position from, Position to) {
		Position current=new Position(new Location(from.getLoc().getRow(),from.getLoc().getCol()),from.getDirection());
		ArrayList<Act> acts=new ArrayList<Act>();
		while (!current.equals(to)) {
			Act act=nextMove(current,to);
			acts.add(act);
			if (act.equals(Act.MOVE)) {
				current.setLoc(current.getLoc().getAdjacentLocation(current.getDirection()));
			} else if (act.equals(Act.TURN_CLOCKWISE)) {
				int dir=current.getDirection()+45;
				if (dir>=360) dir-=360;
				current.setDirection(dir);
			} else if (act.equals(Act.TURN_COUNTER_CLOCKWISE)) {
				int dir=current.getDirection()-45;
				if (dir<0) dir+=360;
				current.setDirection(dir);
			}
		}
		to.setDirection(current.getDirection());
		return acts;
	}
	private String commaList(ArrayList<Bonus> bonusTypesToGoFor) {
		String result="";
		for (int i=0; i<bonusTypesToGoFor.size(); i++) {
			if (i==bonusTypesToGoFor.size()-1) {
				result+=", and ";
			} else if (i>0) {
				result+=", ";
			}
			result+=bonusTypesToGoFor.get(i);
		}
		return result;
	}
	private ArrayList<Distance> getTargetDistances() {
		ArrayList<Distance> distances=new ArrayList<Distance>();
		ArrayList<Target> foes=myInformer().getTargetList();
		for (Target foe:foes) {
			Position myPos=new Position(new Location(myInformer().getRow(),myInformer().getCol()),myInformer().getDirection());
			Position foePos=new Position(foe.getLocation(),0);
			ArrayList<Act> steps=steps(myPos,foePos);
			Distance targetWDistance=new Distance(myPos,foe,steps);
			distances.add(targetWDistance);
		}
		return distances;
	}
	private ArrayList<Distance> getBonusDistances() {
		ArrayList<Distance> distances=new ArrayList<Distance>();
		ArrayList<BonusTarget> bonuses=myInformer().getBonusTargetList();
		for (BonusTarget bonus:bonuses) {
			Position myPos=new Position(new Location(myInformer().getRow(),myInformer().getCol()),myInformer().getDirection());
			Position bonusPos=new Position(bonus.getLocation(),0);
			ArrayList<Act> steps=steps(myPos,bonusPos);
			Distance targetWDistance=new Distance(myPos,bonus,steps);
			distances.add(targetWDistance);
		}
		return distances;
	}
	public void print(String s) {
		super.print(s);
		if (DEBUG) {
			System.out.println(s);
		}
	}
	public void doTestMethod() {
		bonusMap.updateMap();
		System.out.println(bonusMap);
	}
	private enum TargetType {Target,BonusTarget,Location;}
	private class TargetSummary {
		public int maxAttack=0,maxMaxHealth=0,maxDefense=0,maxPoints=0,maxPointValue=0;
		public double avgAttack=0,avgMaxHealth=0,avgDefense=0,avgPoints=0,avgPointValue=0,avgHealth=0;
		public double maxDamageRatio=0, minDamageRatio=Double.MAX_VALUE;

		public void update() {
			for (Target t:targets) {
				int attack=t.getTargetAttack();
				int defense=t.getTargetDefense();
				int points=t.getTargetPoints();
				int pointValue=t.getTargetPointValue();
				int health=t.getTargetHealth();
				int maxHealth=t.getTargetMaxHealth();
				if (attack>maxAttack) maxAttack=attack;
				avgAttack+=attack;
				if (defense>maxDefense) maxDefense=defense;
				avgDefense+=defense;
				if (points>maxPoints) maxPoints=points;
				avgPoints+=points;
				if (pointValue>maxPointValue) maxPointValue=pointValue;
				avgPointValue+=pointValue;
				if (health>maxHealth) maxHealth=health;
				avgHealth+=health;
				if (maxHealth>maxMaxHealth) maxMaxHealth=maxHealth;
				avgMaxHealth+=maxHealth;
				int dTm=damageDone(attack,myDefense);
				int dTf=damageDone(myAttack,defense);
				double damageRatio=0;
				if (dTm==0) {
					damageRatio=dTf;
				} else {
					damageRatio=dTf/dTm;
				}
				damageRatio-=1;
				if (damageRatio<minDamageRatio) minDamageRatio=damageRatio;
				if (damageRatio>maxDamageRatio) maxDamageRatio=damageRatio;
			}
			avgAttack/=foeCount;
			avgDefense/=foeCount;
			avgPoints/=foeCount;
			avgPointValue/=foeCount;
			avgHealth/=foeCount;
			avgMaxHealth/=foeCount;
		}
	}
	private class BonusDistance implements Comparable<BonusDistance> {
		private BonusMap bonusMap;
		private BonusTarget bonusTarget;
		private ArrayList<Act> steps;
		private Position from;
		private Position to;
		public BonusDistance(Position from, BonusTarget bonusTarget, ArrayList<Act> steps, BonusMap bonusMap) {
			this.from=from;
			this.bonusTarget=bonusTarget;
			this.to=new Position(new Location(bonusTarget.getRow(),bonusTarget.getCol()),0);
			this.steps=steps;
			this.bonusMap=bonusMap;
		}
		public BonusTarget getBonusTarget() {return bonusTarget;}
		public int getDistance() {return steps.size();}
		public int getRelativeValue() {
			int mySteps=getDistance();
			int otherStepsCloser=bonusMap.getStepsCloserToBonus(bonusTarget, mySteps, true);
			return mySteps+otherStepsCloser;
		}
		public int compareTo(BonusDistance other) {
			return getRelativeValue()-other.getRelativeValue();
		}
		public ArrayList<Act> getSteps() {return steps;}
		public Position getFrom() {return from;}
		public Position getTo() {return to;}
		public String toString() {
			String s="";
				s+=bonusTarget.getEffect();
			s+="["+from+"]";
			for (Act act:steps) {
				if (act.equals(Act.MOVE)) s+="^";
				else if (act.equals(Act.TURN_CLOCKWISE)) s+=">";
				else if (act.equals(Act.TURN_COUNTER_CLOCKWISE)) s+="<";
			}
			s+="["+to+"]("+steps.size()+")";
			s+=", Relative Value="+getRelativeValue();
			return s;
		}
	}
	private class Distance implements Comparable<Distance> {
		private Object target;
		private ArrayList<Act> steps;
		private Position from;
		private Position to;
		public Distance(Position from, Object target, ArrayList<Act> steps) {
			this.from=from;
			this.target=target;
			if (target instanceof BonusTarget) {
				BonusTarget bonus=(BonusTarget)target;
				this.to=new Position(new Location(bonus.getRow(),bonus.getCol()),0);
			} else if (target instanceof Target) {
				Target theTarget=(Target)target;
				this.to=new Position(new Location(theTarget.getRow(),theTarget.getCol()),theTarget.getTargetDirection());
			} else if (target instanceof Position) {
				Position position=(Position)target;
				this.to=new Position(new Location(position.getLoc().getRow(),position.getLoc().getCol()),0);
			} else {
				this.to=null;
			}
			this.steps=steps;
		}
		public TargetType getTargetType() {
			if (target instanceof BonusTarget) return TargetType.BonusTarget;
			else if (target instanceof Target) return TargetType.Target;
			return TargetType.Location;
		}
		private double valHealth() {return 1.0-((double)myHealth/myMaxHealth);}
		private double valMaxHealth() {return 1.0-((double)myMaxHealth/targetSummary.avgMaxHealth);}
		private double valAttack() {
			double value=1.0-((double)myAttack/targetSummary.maxAttack);
			if (myAttack<=targetSummary.maxDefense || myAttack<=targetSummary.avgAttack) value=1.0;
			return value;
		}
		private double valPoints() {
			if (Bonus.POINTS.DEFAULT_VALUE()>targetSummary.maxPoints) {
				return 1.0;
			} else {
				return (double)Bonus.POINTS.DEFAULT_VALUE()/targetSummary.maxPoints;
			}
		}
		private double valDefense() {
			double value=1.0-((double)myDefense/targetSummary.maxDefense);
			if (myDefense<=targetSummary.maxAttack || myDefense<=targetSummary.avgDefense) value=1.0;
			return value;			
		}
		public double getValue() {
			double value=0;
			if (getTargetType()==TargetType.BonusTarget) {
				if (bonuses.size()==1) { // If there is only one bonus, then it has a maximum value
					value=1.0;
				} else {
					BonusTarget bonusTarget=(BonusTarget)target;
					Bonus bonus=bonusTarget.getEffect();
					if (bonus.equals(Bonus.HEALTH)) {
						value=valHealth();
					} else if (bonus.equals(Bonus.MAX_HEALTH)) {
						value=valMaxHealth();
					} else if (bonus.equals(Bonus.ATTACK)) {
						value=valAttack();						
					} else if (bonus.equals(Bonus.POINTS)) {
						value=valPoints();
					} else if (bonus.equals(Bonus.DEFENSE)) {
						value=valDefense();
					} else if (bonus.equals(Bonus.MYSTERY)) {
						value+=valHealth()*Bonus.HEALTH.randomPercent();
						value+=valMaxHealth()*Bonus.MAX_HEALTH.randomPercent();
						value+=valAttack()*Bonus.ATTACK.randomPercent();
						value+=valDefense()*Bonus.DEFENSE.randomPercent();
						value+=valPoints()*Bonus.POINTS.randomPercent();
					} 
				}
			} else if (getTargetType()==TargetType.Target) {
				Target t=(Target)target;
				value=t.getTargetPointValue()/targetSummary.maxPointValue;
				int dTm=damageDone(t.getTargetAttack(),myDefense);
				int dTf=damageDone(myAttack,t.getTargetDefense());
				double damageRatio=0;
				if (dTm==0) {
					damageRatio=dTf;
				} else {
					damageRatio=dTf/dTm;
				}
				damageRatio-=1;
				double normalizedDamageRatio=0;
				if (targetSummary.maxDamageRatio!=0) normalizedDamageRatio=damageRatio/targetSummary.maxDamageRatio;
				//value=(normalizedDamageRatio+value)/2;
				value=normalizedDamageRatio;
			}
			return value;
		}
		public Object getTarget() {return target;}
		public int getDistance() {return steps.size();}
		public double getRelativeValue() {
			double value=getValue();
			int sumFoeStepsCloser=0;
			if (getTargetType()==TargetType.BonusTarget) {
				int mySteps=getDistance();
				BonusTarget bonusTarget=(BonusTarget)target;
				for (Target t:targets) {
					Position from=new Position(t.getLocation(),t.getDirectionTo());
					Position to=new Position(bonusTarget.getLocation(),0);
					int targetSteps=steps(from,to).size();
					if (targetSteps<mySteps) {
						sumFoeStepsCloser+=(mySteps-targetSteps);
					}
				}
			}
			if (value!=0) return (getDistance()+(sumFoeStepsCloser*sumFoeStepsCloser))/getValue();
			return Double.MAX_VALUE;
		}
		public int compareTo(Distance other) {
			double valDif=getRelativeValue()-other.getRelativeValue();
			if (valDif<0) return -1;
			else if (valDif>0) return 1;
			return 0;
		}
		public ArrayList<Act> getSteps() {return steps;}
		public Position getFrom() {return from;}
		public Position getTo() {return to;}
		public String toString() {
			String s="";
			if (target instanceof BonusTarget) {
				s+=((BonusTarget)target).getEffect();
			} else if (target instanceof Target) {
				s+=((Target)target).getName();
			}
			s+="["+from+"]";
			for (Act act:steps) {
				if (act.equals(Act.MOVE)) s+="^";
				else if (act.equals(Act.TURN_CLOCKWISE)) s+=">";
				else if (act.equals(Act.TURN_COUNTER_CLOCKWISE)) s+="<";
			}
			s+="["+to+"]("+steps.size()+")";
			s+=" Value="+getValue();
			s+=", Relative Value="+getRelativeValue();
			return s;
		}
	}
	private class Position {
		Location loc;
		int direction;
		Position(Location loc, int direction) {
			this.loc=loc;
			this.direction=direction;
		}
		public void setLoc(Location loc) {this.loc = loc;}
		public void setDirection(int direction) {this.direction = direction;}
		public Location getLoc() {return loc;}
		public int getDirection() {return direction;}
		public String toString() {
			return loc+"/"+direction;
		}
		public boolean equals(Object other) {
			if (other instanceof Position) {
				return loc.equals(((Position) other).loc);
			} else {
				return super.equals(other);
			}
		}
	}
}
