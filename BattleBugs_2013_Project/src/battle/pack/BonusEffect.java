package battle.pack;

public class BonusEffect 
{

	private Bonus bonus;

	public BonusEffect() {
		this.bonus=Bonus.HEALTH.randomWeightedBonus();
	}

	public BonusEffect(Bonus bonus) {
		this.bonus = bonus;			
	}
	public Bonus getBonus() {
		return this.bonus;
	}
	public String toString() {
		return this.bonus.toString();
	}
	public void execute(Bactor bactor, int value) {
		switch (this.bonus) {
		case ATTACK:
		{
			bactor.setAttack((int) (bactor.getAttack() + value));
			break;
		}
		case DEFENSE:
		{
			bactor.setDefense((int) (bactor.getDefense() + value));
			break;
		}
		case HEALTH:
		{
			int health = bactor.getHealth() + value ;
			if (health>bactor.getMaxHealth()) {
				health=bactor.getMaxHealth();
			}
			bactor.setHealth(health);
			bactor.updateColor();
			break;
		}
		case POINTS:
		{
			bactor.setPoints((int) (bactor.getPoints() + value));
			break;
		}
		case MAX_HEALTH:
		{
			bactor.setHealth(getMaxHealthHealthBonus(bactor));
			bactor.setMaxHealth(bactor.getMaxHealth() + value);
//			bactor.setHealth((int) (bactor.getHealth() + value));
			bactor.updateColor();
			break;
		}
		case MYSTERY:
		{
			int bonusMultiplier = this.bonus.DEFAULT_VALUE();
			bonus = Bonus.MYSTERY.randomBonus();
			this.execute(bactor, value * bonusMultiplier);
			break;
		}
		}
	}
	int getMaxHealthHealthBonus(Bactor bactor) {
		if (bactor!=null && bonus!=null) {
			int health=bactor.getHealth();
			int maxHealth=bactor.getMaxHealth();
			int newMaxHealth=maxHealth+this.bonus.DEFAULT_VALUE();
			int healthBonus=(int)((health*newMaxHealth)/maxHealth);
			return healthBonus;
		} else return 0;
	}
	public void execute(Bactor bactor)
	{
		if (this.bonus.equals(Bonus.MYSTERY)) {
			int bonusMultiplier = this.bonus.DEFAULT_VALUE();
			bonus = Bonus.MYSTERY.randomBonus();
			this.execute(bactor, this.bonus.DEFAULT_VALUE()* bonusMultiplier);			
		} else {
			this.execute(bactor, this.bonus.DEFAULT_VALUE());
		}
		bactor.resetBonus();
	}
}
