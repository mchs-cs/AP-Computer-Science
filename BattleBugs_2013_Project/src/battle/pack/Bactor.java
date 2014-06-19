package battle.pack;


import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

import StaticLibrary.StaticLibrary;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import battle.pack.BattleRunner;

public class Bactor extends Actor {

	private BattleBug battlebug;
	private Informer bugInformer;

	private int attack;
	private int defense;
	private int maxHealth;
	private int health;
	private int points;
	private Color baseColor;
	private BattleWorld world;
	private JTextArea bugOut;
	private JTextArea announcerOut;
	private int startHealth;
	private BonusEffect storedEffect;
	private Act lastAct;
	private boolean hasActed;
	private boolean dealtDamage;

	public static int turnTowards(int myDirectiion, int targetDirectiion) {
		int aTurn=0;
		int myDirectiionMinustargetDirectiion= myDirectiion-targetDirectiion;
		if (myDirectiionMinustargetDirectiion<0) {
			if (Math.abs(myDirectiionMinustargetDirectiion)<180) {
				aTurn=-myDirectiionMinustargetDirectiion; // Turn CW
			} else {
				aTurn = - (360-Math.abs(myDirectiionMinustargetDirectiion)); // Turn CCW
			}
		} else {
			if (Math.abs(myDirectiionMinustargetDirectiion)<180) {
				aTurn=-myDirectiionMinustargetDirectiion; // Turn CCW
			} else {
				aTurn=360-myDirectiionMinustargetDirectiion; // Turn CW
			}
		}
		return aTurn;
	}
	public void updateColor() {
		float healthRatio=(float)this.health/(float)this.maxHealth;
		healthRatio=(float)Math.min(healthRatio, 1.0);
		healthRatio=(float)Math.max(healthRatio, 0.0);
		float[] hsbColor=Color.RGBtoHSB(this.getColor().getRed(), this.getColor().getGreen(), this.getColor().getBlue(),null);
		hsbColor[2]=healthRatio;
		int newColor = Color.HSBtoRGB(hsbColor[0], hsbColor[1], hsbColor[2]);
		this.setColor(new Color(newColor));		
	}
	public void resetBug() { // changed this so that bugs are reset every round to get rid of their bonuses
		this.battlebug.resetBug();
		this.attack=10;
		this.defense=5;
		this.maxHealth=100;
		this.health=100;
		this.bugOut.setText("");
		this.resetBonus();
		this.resetColor();
	}

	/**
	 * Sets the color of this actor.
	 * @param newColor the new color
	 */
	public Color getBaseColor() {
		return baseColor;
	}
	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
	}

	public void resetColor() {
		super.setColor(this.baseColor);
	}
	public BattleWorld getWorld() {
		return world;
	}
	public void setWorld(BattleWorld world) {
		this.world = world;
	}


	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	public boolean canMove() {
		Grid<Actor> gr=this.getGrid();
		if (gr==null) return false;
		Location nextLoc=this.getLocation().getAdjacentLocation(this.getDirection());
		if (!gr.isValid(nextLoc)) return false;
		return gr.get(nextLoc)==null;
	}
	private void initBactor(BattleBug battlebug) {
		this.attack=10;
		this.defense=0;
		this.maxHealth=100;
		this.health=100;
		this.points=0;
		this.bugInformer=new Informer(this,battlebug);
		this.battlebug=battlebug;
		this.battlebug.setMyInformer(this.bugInformer);
		this.bugOut=null;
		//		this.outText=null;
		this.announcerOut=null;
		this.storedEffect = null;
		this.lastAct=null;

	}
	public Bactor(BattleBug battlebug) {
		super();
		this.initBactor(battlebug);
	}
	public Bactor(BattleBug battlebug, Color color) {
		super();
		this.initBactor(battlebug);
		this.setColor(color);
		this.setBaseColor(color);
	}
	public String getName() {
		return this.battlebug.getName();
	}
	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		if (this.lastAct.equals(Act.DEFEND)) {
			return (int)(this.defense*1.5);
		} else {
			return defense;
		}
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		if (this.health<=0) {
			this.removeSelfFromGrid();
		} else {
			this.updateColor();
		}
	}

	public BattleBug getBattleBug() {
		return this.battlebug;
	}
	public void act() {
		this.dealtDamage=false;
		ActToken actToken = new ActToken(this);
		PrintStream originalStream = System.out;
		try {
			PrintStream dummyStream    = new PrintStream(new OutputStream(){
				public void write(int arg0) throws IOException {
				}
			});
			System.setOut(dummyStream);
			Act act=this.getBattleBug().act();
			setHasActed(true);
			switch (act) {
			case ATTACK:
				attack();
				break;
			case DEFEND:
				defend();
				break;
			case GET_BONUS:
				getBonus();
				break;
			case MOVE:
				move();
				break;
			case TURN_CLOCKWISE:
				turnClockwise();
				break;
			case TURN_COUNTER_CLOCKWISE:
				turnCounterClockwise();
				break;
			case USE_BONUS:
				useBonus();
				break;
			default:
				break;
			}
		} catch (NullPointerException error) {
			this.bugInformer.print("Caught: NullPointerException\n" + StaticLibrary.stackTraceToString(error));
		} catch (ArrayIndexOutOfBoundsException error) {
			this.bugInformer.print("Caught: ArrayIndexOutOfBoundsException\n" + StaticLibrary.stackTraceToString(error));	
		} catch (ArithmeticException error) {
			this.bugInformer.print("Caught: ArithmeticException\n" + StaticLibrary.stackTraceToString(error));	
		} catch (ClassCastException error) {
			this.bugInformer.print("Caught: ClassCastException\n" + StaticLibrary.stackTraceToString(error));	
		} catch (IndexOutOfBoundsException error) {
			this.bugInformer.print("Caught: IndexOutOfBoundsException\n" + StaticLibrary.stackTraceToString(error));	
		} catch (StackOverflowError error) {
			this.bugInformer.print("Caught: StackOverflowError\n" + StaticLibrary.stackTraceToString(error));	
		} catch (SecurityException error) {
			this.world.announce("Ooops, "+this.getBattleBug().getCreator()+" tried to cheat!");
		} catch (RuntimeException error) {
			this.bugInformer.print("Caught: RuntimeException\n" + StaticLibrary.stackTraceToString(error));
		} catch (java.lang.Throwable error) {
			this.bugInformer.print("Caught: Unknown Error\n" + StaticLibrary.stackTraceToString(error));
		} finally {
			System.setOut(originalStream);
		}
	}
	public int pointValue() {
		int pointValue=this.getHealth()/10;
		pointValue+=this.getAttack();
		pointValue+=this.getDefense();
		pointValue+=this.getPoints()/200;
		return pointValue;
	}
	public void attack() {
		this.lastAct=Act.ATTACK;
		Grid<Actor> gr=this.getGrid();
		if (gr!=null) {
			Location myLoc=this.getLocation();
			if (myLoc!=null) {
				Location nextLoc=myLoc.getAdjacentLocation(this.getDirection());
				if (gr.isValid(nextLoc)) {
					if (gr.get(nextLoc) instanceof Bactor) {
						String announce=null;
						Bactor opponent=(Bactor)gr.get(nextLoc);
						int damage=Math.max(0, this.attack-opponent.getDefense());
						int points=Math.min(damage, opponent.getHealth());
						opponent.setHealth(opponent.getHealth()-points);
						if (damage>0) {
							this.dealtDamage=true;
							if (opponent.getHealth()==0) {
								points+=opponent.pointValue();
								announce=this.getName() + " kills " + opponent.getName() + " earning " + points + " points";
							} else {
								points+=opponent.pointValue()/10;
							}
							this.world.updateMostDamage(damage, this, opponent);
							this.world.updateMostPoints(points, this, opponent);
							this.points+=points;
						}						
						if(announce!=null) {
							this.world.announce(announce);
						}
					}
				}
			}
		}	
	}
	public void removeSelfFromGrid()
	{
		if (this.world!=null) {
			this.world.eliminateBactor(this);
		}
		super.removeSelfFromGrid();
	}

	public void move() {
		this.lastAct=Act.MOVE;
		Grid<Actor> gr=this.getGrid();
		if (gr!=null) {
			Location myLoc=this.getLocation();
			if (myLoc!=null) {
				Location nextLoc=myLoc.getAdjacentLocation(this.getDirection());
				if (gr.isValid(nextLoc)) {
					if (gr.get(nextLoc)==null) {
						this.moveTo(nextLoc);						
					}
				}
			}
		}		
	}
	public void defend() {
		this.lastAct=Act.DEFEND;
	}

	public boolean foeInFront() {
		boolean foeInFront=false;
		Grid<Actor> gr=this.getGrid();
		if (gr!=null) {
			Location myLoc=this.getLocation();
			if (myLoc!=null) {
				Location nextLoc=myLoc.getAdjacentLocation(this.getDirection());
				if (gr.isValid(nextLoc)) {
					if (gr.get(nextLoc) instanceof Bactor) {
						foeInFront=true;
					}
				}
			}
		}
		return foeInFront;
	}

	//new thing
	public boolean bonusInFront() {
		boolean bonusInFront=false;
		Grid<Actor> gr=this.getGrid();
		if (gr!=null) {
			Location myLoc=this.getLocation();
			if (myLoc!=null) {
				Location nextLoc=myLoc.getAdjacentLocation(this.getDirection());
				if (gr.isValid(nextLoc)) {
					if (gr.get(nextLoc) instanceof BonusActor) {
						bonusInFront=true;
					}
				}
			}
		}
		return bonusInFront;
	}

	public void turnClockwise() {
		this.lastAct=Act.TURN_CLOCKWISE;
		this.setDirection(this.getDirection()+Location.HALF_RIGHT);		
	}
	public void turnCounterClockwise() {
		this.lastAct=Act.TURN_COUNTER_CLOCKWISE;
		this.setDirection(this.getDirection()+Location.HALF_LEFT);				
	}
	public String toString()
	{
		return super.toString() + this.getName();
	}
	// Custom Methods Not Part of Main Project!	
	public void doTestMethod() {
		this.battlebug.doTestMethod();
	}
	public String getImageSuffix() {
		return "_" + this.battlebug.getClass().getSimpleName();
	}
	public JTextArea getBugOut() {
		return bugOut;
	}
	public void setBugOut(JTextArea textArea) {
		this.bugOut = textArea;
	}
	public void updateBugOut(String bugText) {
		if (BattleRunner.FAST_RUN) return;
		if (this.bugOut!=null) {
			this.bugOut.append("\n" + bugText);
		}
	}
	public int getStartHealth() {
		return startHealth;
	}
	public void setStartHealth(int startHealth) {
		this.startHealth = startHealth;
	}	
	///////////////////////////////////////////////////////////////////////////////////////////	
	public boolean haveBonus() {
		if (this.storedEffect!=null) return true;
		else return false;
	}


	public void getBonus() {//takes the bonus in front of the actor
		this.lastAct=Act.GET_BONUS;
		Grid<Actor> gr=this.getGrid();
		if (gr!=null) {
			Location myLoc=this.getLocation();
			if (myLoc!=null) {
				Location nextLoc=myLoc.getAdjacentLocation(this.getDirection());
				if (gr.isValid(nextLoc)) {
					if (gr.get(nextLoc) instanceof BonusActor) {
						String announce="";
						if (this.storedEffect!=null) {
							announce+=this.getName() + " ditches a " + this.storedEffect.toString() + " in order to pick up a ";
						} else {
							announce+=this.getName() + " picks up a ";
						}
						this.storedEffect = ((BonusActor) gr.get(nextLoc)).take();
						announce+=this.storedEffect.toString() + " bonus";
						this.getWorld().announce(announce);
						this.getWorld().announceNow();
					}
				}
			}
		}
	}

	public void useBonus() {//uses the currently stored bonus
		this.lastAct=Act.USE_BONUS;
		if (this.storedEffect!=null) {
			//			this.getWorld().announce(this.getName() + " use a " + this.storedEffect.toString() + " bonus!");
			//			this.getWorld().announceNow();
			storedEffect.execute(this);			
		}
	}

	public Bonus getStoredEffect() {//returns the bonus representing the bonus type
		if (storedEffect != null){
			return storedEffect.getBonus();
		}
		else return null;
	}

	public void resetBonus() {//used to reset stored bonus to 0 after bonus is executed
		this.storedEffect = null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////

	public int[] getAttributeVals() {
		int[] attributeVals=new int[Attribute.ATTACK.chartValues().length];
		attributeVals[Attribute.ATTACK.ordinal()]=this.getAttack();
		attributeVals[Attribute.DEFENSE.ordinal()]=this.getDefense();
		attributeVals[Attribute.HEALTH.ordinal()]=this.getHealth();
		attributeVals[Attribute.MAXHEALTH.ordinal()]=this.getMaxHealth();
		attributeVals[Attribute.POINTS.ordinal()]=this.getPoints();
		return attributeVals;
	}
	public Act getLastAct() {
		if (this.lastAct==null) {
			this.lastAct=Act.DEFEND;
		}
		return this.lastAct;
	}
	public void taunt(Taunt taunt) {
		if (this.announcerOut!=null) {
			this.announcerOut.append("\n" + this.getBattleBug().getName() + " says \""+ taunt + "\"");		
		}
	}
	public void taunt(Taunt taunt, String s) {
		if (this.announcerOut!=null) {
			this.announcerOut.append("\n" + this.getBattleBug().getName() + " says \"" + taunt + " " + s);		
		}
	}
	public JTextArea getAnnouncerOut() {
		return announcerOut;
	}
	public void setAnnouncerOut(JTextArea announcerOut) {
		this.announcerOut = announcerOut;
	}
	public boolean isHasActed() {
		return hasActed;
	}
	public void setHasActed(boolean hasActed) {
		this.hasActed = hasActed;
	}
	public boolean isDealtDamage() {
		return dealtDamage;
	}
}

