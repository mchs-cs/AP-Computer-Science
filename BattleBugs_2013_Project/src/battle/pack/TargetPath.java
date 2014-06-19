package battle.pack;

import java.util.ArrayList;

import info.gridworld.grid.Location;

public class TargetPath extends Path {
	private Target target=null;
	private Bactor bactor;
	private Location lastLocation=null;

	public TargetPath() {
		super();
		this.target=null;
		this.lastLocation=null;
	}
	TargetPath(Bactor bactor, Target target) {
		super();
		this.bactor=bactor;
		if (target!=null && target.targetValid()) {
			Path path=new Path(bactor.getLocation(), bactor.getDirection(), target.getLocation());
			ArrayList<Act> acts=path.getPath();
			if (acts.size()>0) {
				Act act=acts.get(acts.size()-1);
				if (act.equals(Act.MOVE)) {
					acts.remove(acts.size()-1);
				}
			}
			path.setPath(acts);
			this.replacePath(path);
			this.target=target;
			this.lastLocation=target.getLocation();
		} else {
			this.setPath(new Path().getPath());
			this.target=null;
			this.lastLocation=null;
		}
	}
	/**
	 * @return <code>true</code> if the <code>Target</code> has moved from it's original location when the <code>TargetPath</code> was created.
	 */
	public boolean isPathChanged() {
		if (this.target!=null && this.target.targetValid()) {
			if (this.lastLocation.equals(getTarget().getLocation())) {
				return false;
			} else {
				Path path=new Path(bactor.getLocation(), bactor.getDirection(), target.getLocation());
				this.replacePath(path);
				this.lastLocation=target.getLocation();
				return true;
			}
		} 
		return true;
	}
	/**
	 * The isValid method can be used to determine if a path is still valid. A path is still valid if both it's current location
	 *  and destination location are not <code>null</code>, there is at least 1 step remaining in the path, and the <code>Target</code>
	 *  object is still on the board.
	 * @return <code>true</code> if the path is still valid.
	 */
	public boolean isValid() {
		if (this.target!=null && this.target.targetValid()) {
			return super.isValid();
		} else {
			return false;
		}
	}
	/**
	 * Describes the <code>Target</code> by name that this path leads to, the current position(current direction, row, and column), series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * The path will be labeled (Null) if the Bonus for this path is <code>null</code>.<br />
	 * The path will be labeled (Invalid) if the Bonus for this path is no longer valid.<br />
	 * A MOVE(forward) step is indicated by a ^<br />
	 * A CLOCKWISE step is indicated by a &gt;<br />
	 * A COUNTER_CLOCKWISE step is indicated by a &lt;<br />
	 * <em>example:</em> DemoBattleBug:(45/10,1)&lt;^^(7,1)
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsShortString() {
		if (this.target==null) {
			return "(Null)" + super.pathAsShortString();
		} else if (!this.target.targetValid()) {
			return this.target.getName() + "(Invalid):" + super.pathAsShortString();
		} else {
			return this.target.getName()+ ":" + super.pathAsShortString();
		}
	}
	/**
	 * Describes the <code>Target</code> by name that this path leads to, the current position(current direction, row, and column), series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * The path will be labeled (Null) if the Bonus for this path is <code>null</code>.<br />
	 * The path will be labeled (Invalid) if the Bonus for this path is no longer valid.<br />
	 * <em>example:</em> DemoBattleBug:From (315/16,4)-->Move-->Move-->To (13,1) in 2 steps
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsString() {
		if (this.target==null) {
			return "(Null)" + super.pathAsShortString();
		} else if (!this.target.targetValid()) {
			return this.target.getName() + "(Invalid):" + super.pathAsString();
		} else {
			return this.target.getName() + ":" + super.pathAsString();
		}
	}
	/**
	 * @return a reference to the <code>Target</code> of this path.
	 */
	public Target getTarget() {
		return this.target;
	}
	public Act nextStep() {
		if (this.isValid()) {
			return super.nextStep();
		} else {
			return Act.DEFEND;
		}
	}
	protected void setPathTo() {
		super.setPathTo();
		ArrayList<Act> acts=this.getPath();
		if (acts.size()>0) {
			Act act=acts.get(acts.size()-1);
			if (act.equals(Act.MOVE)) {
				acts.remove(acts.size()-1);
			}
		}
		super.setPath(acts);
	}
}
