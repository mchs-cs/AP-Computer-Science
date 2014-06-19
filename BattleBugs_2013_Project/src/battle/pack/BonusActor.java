package battle.pack;

import info.gridworld.actor.*;

public class BonusActor extends Actor

//base class, do not use. Use Extensions.

{
	private int life;
	private BonusEffect effect;
	private final int defaultLife = 10;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Constructors
	
	public BonusActor()
	{
		life = defaultLife;
		this.effect = new BonusEffect();
	}
	public BonusActor(Bonus bonus)
	{
		life = defaultLife;
		this.effect = new BonusEffect(bonus);
	}
	
	public BonusActor(int life)
	{
		this.life = life;
		this.effect = new BonusEffect();
	}
	
	public BonusActor(Bonus bonus, int life)
	{
		this.life = life;
		this.effect = new BonusEffect(bonus);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void act()
	{
		if(getGrid() != null)
		{
			if(life == 0)
			{
				removeSelfFromGrid();
			}
			else life--;
		}
	}
	
	public int getTimeLeft()
	{
		return life;
	}
	
	public BonusEffect take()
	{
		this.removeSelfFromGrid();
		return effect;
	}
	
	
	public void setBonus(Bonus bonus)
	{
		this.effect = new BonusEffect(bonus);
	}
	
	public Bonus getEffect() {
		return this.effect.getBonus();
	}
	
	public String getImageSuffix() {
		return "_" + this.getEffect();
	}
}
