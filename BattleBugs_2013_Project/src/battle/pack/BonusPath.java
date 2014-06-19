package battle.pack;

import java.util.ArrayList;

import info.gridworld.grid.Location;

public class BonusPath extends Path {
	private BonusTarget bonus=null;
	public BonusPath() {
		super();
		this.bonus=null;
	}
	public BonusPath(Location from, int fromDirection, BonusTarget bonus) {
		super();
		if (bonus!=null && bonus.isValid()) {
			Path path=new Path(from, fromDirection, bonus.getLocation());
			ArrayList<Act> acts=path.getPath();
			if (acts.size()>0) {
				Act act=acts.get(acts.size()-1);
				if (act.equals(Act.MOVE)) {
					acts.remove(acts.size()-1);
				}
			}
			path.setPath(acts);
			this.replacePath(path);
			this.bonus=bonus;
		} else {
			this.setPath(new Path().getPath());
			this.bonus=null;
		}
	}
	/**
	 * The isValid method can be used to determine if a path is still valid. A path is still valid if both it's current location
	 *  and destination location are not <code>null</code>, there is at least 1 step remaining in the path, and the Bonus object
	 *  is still on the board.
	 * @return <code>true</code> if the path is still valid.
	 */
	public boolean isValid() {
		if (this.bonus!=null && this.bonus.isValid()) {
			return super.isValid();
		} else {
			return false;
		}
	}
	/**
	 * Describes the Bonus that this path leads to, the current position(current direction, row, and column), series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * The path will be labeled (Null) if the Bonus for this path is <code>null</code>.<br />
	 * The path will be labeled (Invalid) if the Bonus for this path is no longer valid.<br />
	 * A MOVE(forward) step is indicated by a ^<br />
	 * A CLOCKWISE step is indicated by a &gt;<br />
	 * A COUNTER_CLOCKWISE step is indicated by a &lt;<br />
	 * <em>example:</em> DEFENSE:(180/1,4)^^^&gt;(4,0)
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsShortString() {
		if (this.bonus==null) {
			return "(Null)" + super.pathAsShortString();
		} else if (!this.bonus.isValid()) {
			return this.bonus.getEffect().toString() + "(Invalid):" + super.pathAsShortString();
		} else {
			return this.bonus.getEffect().toString() + ":" + super.pathAsShortString();
		}
	}
	/**
	 * Describes the Bonus that this path leads to, the current position(current direction, row, and column), series of steps necessary to reach the set destination location, and the number of steps.<br />
	 * The path will be labeled (Null) if the Bonus for this path is <code>null</code>.<br />
	 * The path will be labeled (Invalid) if the Bonus for this path is no longer valid.<br />
	 * <em>example:</em> MYSTERY:From (0/16,10)--&gtTurn_counter_clockwise--&gtMove-->Move--&gtTurn_counter_clockwise--&gtTo (17,7) in 4 steps
	 * @return Current path as a String. The String will be empty if the path is not valid.
	 */
	public String pathAsString() {
		if (this.bonus==null) {
			return "(Null)" + super.pathAsShortString();
		} else if (!this.bonus.isValid()) {
			return this.bonus.getEffect().toString() + "(Invalid):" + super.pathAsString();
		} else {
			return this.bonus.getEffect().toString() + ":" + super.pathAsString();
		}
	}
	/**
	 * @return Returns a reference to the <code>BonusTarget</code> that this BonusPath leads to.
	 */	
	public Bonus getBonus() {
		return this.bonus.getEffect();
	}
	/**
	 * Returns the enumerated <code>Bonus</code> that this BonusPath leads to.
	 * @return The enumerated type <code>Bonus</code> can have the values: <code>ATTACK</code>, <code>DEFENSE</code>, <code>HEALTH</code>, <code>MAX_HEALTH</code>, <code>MYSTERY</code>, <code>POINTS</code>, or <code>null</code> if you have no Bonus currently.
	 */	
	public BonusTarget getBonusTarget() {
		return this.bonus;
	}
}
