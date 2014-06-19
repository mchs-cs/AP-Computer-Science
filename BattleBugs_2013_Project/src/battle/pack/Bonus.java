package battle.pack;
// The enumerated type Bonus is now stored in it's own .java file called simply Bonus

/*
 	In addition to storing the basic types of HEALTH=0, POINTS=1, etc...
	There are two additional fields after each basic type(in order they are): 
	int DEFAULT_VALUE
	int RANDOM_CHANCE
	
	The type and value I've given them are for demonstration purposes only!
	The DEFAULT_VALUE field should be used to define what a default HEALTH bonus does when used. If it represents the amount of HEALTH gained, then a value of 100 might makes sense
	The RANDOM_CHANCE field is used to weight the % chance this type is chosen when picking a random bonus. Since new Bonus types may be added later, it does not make sense to use a percentage here,
	instead the number represents the chance of that Bonus type being randomly selected in relation to the others. If 5 types each have a value of 20, then they each have a 20% chance of being chosen.
	If someone adds a sixth type to the list and makes it's RANDOM_CHANCE = 100, then it represents a chance of being chosen of 100/200 or 50%, and each type with a RANDOM_CHANCE of 20 represents a 20/200
	or 10% chance of being chosen
 */
// Type is BONUSTYPE(DEFAULT_VALUE,RANDOM_CHANCE)
public enum Bonus {DEFENSE(3,10),ATTACK(4,10),HEALTH(200,20),MAX_HEALTH(50,10),MYSTERY(2,2),POINTS(500,5);

	private final int DEFAULT_VALUE;
	private final double RANDOM_CHANCE;
	
	private Bonus(int DEFAULT_VALUE, int RANDOM_CHANCE) {
		this.DEFAULT_VALUE=DEFAULT_VALUE;
		this.RANDOM_CHANCE=RANDOM_CHANCE;
	}
	
	/* 	These two methods allow you to say in code:
	 	Bonus.ATTACK.DEFAULT_VALUE
	 	Bonus.ATTACK.RANDOM_CHANCE */		
	public final int DEFAULT_VALUE() {return this.DEFAULT_VALUE;}
	final double RANDOM_CHANCE() {return this.RANDOM_CHANCE;}

	// This will probably eventually become private, it is used to figure out what the total is of all the RANDOM_CHANCE fields
	final int randomTotal() {
		// With an enumerated type, you get a method called values() that returns an array where each item in the array is a Bonus, and they array contains one of each type in the enumerated list:
		// bonuses[0] = HEALTH, bonuses[0] = POINTS, ... , bonuses[0] = MYSTERY
		Bonus bonuses[] = Bonus.values();
		int randomChanceTotal = 0;
		for (int i=0; i<bonuses.length; i++) {
			randomChanceTotal+=bonuses[i].RANDOM_CHANCE;
		}
		return randomChanceTotal; // The sum of each RANDOM_CHANCE is the randomChanceTotal
	}
	public final double randomPercent() {
		return (double)this.RANDOM_CHANCE()/this.randomTotal(); // In case you want to know what percentage chance there is of choosing a particular Bonus type
	}
	final Bonus randomBonus() { // This random bonus generator weights each type equally
		Bonus bonuses[] = Bonus.values();
		int bonusIndex=(int)(Math.random()*bonuses.length);
		return bonuses[bonusIndex];
	}
	final Bonus randomWeightedBonus() { // This random bonus generator use the RANDOM_CHANCE weights to pick a random choice
		Bonus bonuses[] = Bonus.values(); // Create a list of each type of Bonus
		int randomChanceTotal = this.randomTotal(); // Find out what the total of the individual RANDOM_CHANCE weights is
		int randomBonusVal=(int)(Math.random()*randomChanceTotal); // Pick a random number between 0 and the total from the line above
		boolean found=false; // Figure out which Bonus is represented by our random number, by starting with not having picked yet
		int index=0; // We will examine each Bonus.RANDOM_CHANCE until we get to our choice, start with the first one
		while (found==false && index<bonuses.length) { // If we haven't found one yet, and we haven't run out of choices, keep going
			if (randomBonusVal<bonuses[index].RANDOM_CHANCE) { // If there were two equally weight choices, say 50 and 50, my total is 100, and my random number is 38, then the first TYPE in the list is my choice
				found=true; // I'm done looking, whatever index I'm on now, is my random choice
			} else { // Otherwise I need to go on the the next Bonus to see if it is the right one
				randomBonusVal-=bonuses[index].RANDOM_CHANCE; /* On my next pass, I reduce my random choice # by my current choices RANDOM_CHANCE, so with my 50/50 example, if the random number is 58,
				 My first Bonus isn't the right one, so subtract 50, 58-50=8 , and not the next time through, the RANDOM_CHANCE is 50, 8 is less than 50, so that is the random choice */
				index++; // Look at the next bonus in the list.
			}
		}
		return bonuses[index]; // Return the bonus we ended up with.
	}
	final Bonus get(int bonusIndex) { // This is a legacy method to allow picking a Bonus by integer number
		Bonus bonuses[] = Bonus.values();
		return bonuses[bonusIndex];			
	}
	public final String toString() {
		return name();
	}
}
