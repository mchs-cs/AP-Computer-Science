package battle.pack;

public enum Act {
	MOVE,TURN_CLOCKWISE,TURN_COUNTER_CLOCKWISE,ATTACK,GET_BONUS,USE_BONUS,DEFEND;
	public Act get(int index) {
		Act[] acts = Act.values();
		return acts[index];
	}
	public String toString() {
		/* This method should convert the String s returned from the line below into "normalized" text, by replacing the
		 * any underscore characters with spaces and leaving only the first letter of each word capitalized:
		 * 	i.e. TURN_CLOCKWISE is turned into Turn Clockwise,
		 * SPECIAL CASE: COUNTER_CLOCKWISE should be converted to Counter-clockwise
		 */ 
		String s=super.toString();
		return s;
	}
	public static Act get(String actName) {
		/* This method should take the String value returned by the toString() method for an Act and return the corresponding
		 * Act enumerated type, or null if there is no matching Act.
		 */
		Act[] acts = Act.values();
		for (Act act:acts) {
			if (act.equals(actName)) {
				return act;
			}
		}
		return null;
	}
}