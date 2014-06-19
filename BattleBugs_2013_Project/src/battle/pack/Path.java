package battle.pack;

import info.gridworld.grid.Location;

import java.util.ArrayList;

//import StaticLibrary.StaticLibrary;

public class Path {
	private ArrayList<Act> path;
	private Location from;
	private int fromDirection;
	private Location to;
	private int finalDirection;
	public Path() {
		this.from=null;
		this.fromDirection=0;
		this.to=null;
		this.path=new ArrayList<Act>();				
	}
	/** The <code>Path</code> class is used to return an object which contains a series of steps to follow. A path is made up of a
	 * list of enumerated constant <code>Act</code>. Each element will be a <code>MOVE</code>, <code>TURN_CLOCKWISE</code>, or <code>TURN_COUNTER_CLOCKWISE</code>.<br />
	 * In each successive BattleBug turn, <code>nextStep()</code> method will return the next step to be taken along the path. Afterwards, the <code>completedStep()</code>
	 * method should be called so the next step along path will be returned.
	 */
	public Path(Location from, int fromDirection, Location to) {
		if (from!=null && to!=null) {
			this.from=from;
			this.fromDirection=fromDirection;
			this.to=to;
			this.path=new ArrayList<Act>();
			this.setPathTo();
		} else {
			this.from=null;
			this.fromDirection=0;
			this.to=null;
			this.path=new ArrayList<Act>();							
		}
	}
	/**
	 * @return Returns a list of enumerated <code>Act</code> containing <code>MOVE</code>, <code>TURN_CLOCKWISE</code>, or <code>TURN_COUNTER_CLOCKWISE</code> steps to get to the designated location.
	 */		
	public ArrayList<Act> getPath() {
		return path;
	}
	protected void setPath(ArrayList<Act> path) {
		this.path = path;
	}
	protected Location getFrom() {
		return from;
	}
	protected int getFromDirection() {
		return fromDirection;
	}
	protected Location getTo() {
		return to;
	}
	/**
	 * @return Returns the number of steps remaining in the path to reach the destination.
	 */
	public int size() {
		return this.path.size();
	}
	protected void setPathTo() {
		this.path=new ArrayList<Act>();
		Location myLoc=new Location(this.from.getRow(),this.from.getCol());
		boolean keepTurning=true;
		int absDir2=this.fromDirection;
		int intendedDirs=getDirectionToward(myLoc,to);
		int loops=0;
		while(keepTurning){//THIS LOOP ORIENTS IT IN The correct starting direction.
			loops++;
			int degreesRel=getRotation(absDir2,intendedDirs);
			if(degreesRel<=-45){
				path.add(Act.TURN_CLOCKWISE);
				absDir2=(absDir2+45)%360;
			}else if(degreesRel>=45){
				path.add(Act.TURN_COUNTER_CLOCKWISE);
				absDir2=(absDir2-45)%360;
			}else{//IS IN A CORRECT STARTING POSITION.
				keepTurning=false;
			}
			if(loops>100){
				System.out.println("PATH GENERATION ERROR! Probable infinite loop\n" + this.pathAsString());
				keepTurning=false;
			}
		}
		Location nextLoc=myLoc.getAdjacentLocation(absDir2);
		int loopsa=0;
		while(!this.to.equals(nextLoc)){
			intendedDirs=getDirectionToward(myLoc,to);
			if(intendedDirs%90==0){
				if(Math.abs(getRotation(absDir2,intendedDirs))<45){
					nextLoc=myLoc.getAdjacentLocation(absDir2);
					if(nextLoc==to)break;
					path.add(Act.MOVE);
					myLoc=nextLoc;
				}
				else{
					loops=0;
					keepTurning=true;
					while(keepTurning){
						loops++;
						int degreesRel=getRotation(absDir2,intendedDirs);
						if(degreesRel<=-45){
							path.add(Act.TURN_CLOCKWISE);
							absDir2=(absDir2+45)%360;
						}else if(degreesRel>=45){
							path.add(Act.TURN_COUNTER_CLOCKWISE);
							absDir2=(absDir2-45)%360;
						}else{//IS in a correct rotation
							keepTurning=false;
						}
						if(loops>100){
							System.out.println("PATH GENERATION ERROR! Probable infinite loop\n" + this.pathAsString());
							keepTurning=false;
							this.path.clear();
							break;
						}
					}
				}
			}
			else{
				int xDistA=Math.abs(myLoc.getCol()-to.getCol());
				int yDistA=Math.abs(myLoc.getRow()-to.getRow());
				if(Math.abs(xDistA-yDistA)<=1){
					if(Math.abs(getRotation(absDir2,intendedDirs))<45){
						nextLoc=myLoc.getAdjacentLocation(absDir2);
						if(nextLoc.equals(to))break;
						path.add(Act.MOVE);
						myLoc=nextLoc;
					}
					else{
						loops=0;
						keepTurning=true;
						while(keepTurning){//THIS LOOP ORIENTS IT IN The correct direction.
							loops++;
							int degreesRel=getRotation(absDir2,intendedDirs);
							if(degreesRel<=-45){
								path.add(Act.TURN_CLOCKWISE);
								absDir2=(absDir2+45)%360;
								
							}else if(degreesRel>=45){
								path.add(Act.TURN_COUNTER_CLOCKWISE);
								absDir2=(absDir2-45)%360;
							}else{//IS IN a CORRECT Rotation.
								keepTurning=false;
							}
							if(loops>100){
								System.out.println("PATH GENERATION ERROR! Probable infinite loop\n" + this.pathAsString());
								keepTurning=false;
								this.path.clear();
								break;
							}
						}
					}
				}
				else
				{
					nextLoc=myLoc.getAdjacentLocation(absDir2);
					if(nextLoc==to)break;
					path.add(Act.MOVE);
					myLoc=nextLoc;
				}
			}
			if(loopsa>100){
				System.out.println("PATH GENERATION ERROR! Probable infinite loop\n" + this.pathAsString());
				break;
			}
			loopsa++;
		}
		finalDirection=absDir2;
	}
	/**
	 * Gets the final direction the path ends in.
	 * @return the absolute direction the path ends up facing.
	 */
	public int getFinalDirection(){
		return ((finalDirection+360)%360);
	}
	/**
	 * Describes the current position(current direction, row, and column),series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * <em>example:</em> From (0/16,7)--&gt;Turn_counter_clockwise--&gt;Turn_counter_clockwise--&gt;Turn_counter_clockwise--&gt;To (17,7) in 3 steps
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsString() {
		if (this.isValid()) {
			String path="From ("+ this.fromDirection + "/" + from.getRow() + "," + from.getCol() + ")-->";
			for (Act act:this.path) {
				path+=act.toString() + "-->";
			}
			path+="To (" + this.to.getRow() + "," + this.to.getCol() + ") in " + this.path.size() + " steps";
			return path;
		} else {
			return "";
		}
	}
	/**
	 * Describes the current position(current direction, row, and column),series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * A MOVE(forward) step is indicated by a ^<br />
	 * A CLOCKWISE step is indicated by a &gt;<br />
	 * A COUNTER_CLOCKWISE step is indicated by a &lt;<br />
	 * <em>example:</em> (90/10,1)&lt;^^&lt;(7,0)
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsShortString() {
		if (this.isValid()) {
			String path="("+ this.fromDirection + "/" + from.getRow() + "," + from.getCol() + ")";
			for (Act act:this.path) {
				if (act.equals(Act.MOVE)) {
					path+="^";
				} else if (act.equals(Act.TURN_CLOCKWISE)) {
					path+=">";
				} else if (act.equals(Act.TURN_COUNTER_CLOCKWISE)) {
					path+="<";
				} else {
					path+="*";
				}
			}
			path+="(" + this.to.getRow() + "," + this.to.getCol() + ")";
			return path;
		} else {
			return "";
		}
	}
	/**
	 *<em>usage:</em> <code>Act step=myPath.nextStep();</code>
	 * @return The next step on the path to reach the destination as an enumerated type <code>Act</code>. It will be <code>MOVE</code>, <code>TURN_CLOCKWISE</code>, or <code>TURN_COUNTER_CLOCKWISE</code>, or <code>DEFEND</code> if the path is no longer valid.<br />
	 */
	public Act nextStep() {
		if (this.isValid()) {
			return this.path.get(0);
		} else {
			return Act.DEFEND;
		}
	}
	/**
	 * This method should be called when a step along the path has been completed so the next step will be returned by the next call to <code>nextStep()</code><br />
	 * <em>usage:</em> <code>myPath.completedStep();</code>
	 */
	public void completedStep() {
		if (this.isValid()) {
			this.path.remove(0);
		}
	}
	/**
	 * The isValid method can be used to determine if a path is still valid. A path is still valid if both it's current location and destination location are not <code>null</code> and there is at least 1 step remaining in the path.
	 * @return <code>true</code> if the path is still valid.
	 */
	public boolean isValid() {
		if (this.to!=null && this.from!=null && this.path!=null && this.path.size()>0) {
			return true;
		} else {
			return false;
		}
	}
	private int getDirectionToward(Location from, Location to) {
		int dx = to.getCol() - from.getCol();
		int dy = to.getRow() - from.getRow();
		// y axis points opposite to mathematical orientation
		int angle = (int) Math.toDegrees(Math.atan2(-dy, dx));

		// mathematical angle is counterclockwise from x-axis,
		// compass angle is clockwise from y-axis
		int compassAngle = 90-angle;
		// wrap negative angles
		if (compassAngle < 0)
			compassAngle += 360;
		return compassAngle;
	}
	private int getRotation(int from, int to){
		int amount=(from-to)%360;
		if(amount>180)amount=amount-360;
		if(amount<-180)amount=amount+360;
		return amount;
	}
	/**
	 * Returns the path as the <code>pathAsShortString()</code> method.
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String toString() {
		return pathAsShortString();
	}
	protected void replacePath(Path path) {
		this.from=path.from;
		this.fromDirection=path.fromDirection;
		this.path=path.getPath();
		this.to=path.to;
	}
}
