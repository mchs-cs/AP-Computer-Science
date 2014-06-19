package battle.pack;

import java.awt.Color;
import java.util.ArrayList;

public class ColorIterator {
	private int colorIndex;
	private ArrayList<Color> colors;
	public ColorIterator() {
		super();
		this.colors = new ArrayList<Color>();
		colors.add(Color.blue);
		colors.add(Color.green);
		colors.add(Color.orange);
		colors.add(Color.pink);
		colors.add(Color.red);
		colors.add(Color.white);
		colors.add(Color.yellow);
		colors.add(new Color(250,235,215));
		colors.add(new Color(127,255,212));
		colors.add(new Color(138,43,226));
		colors.add(new Color(165,42,42));
		colors.add(new Color(95,158,160));
		colors.add(new Color(127,255,0));
		colors.add(new Color(0,100,0));
		colors.add(new Color(85,107,47));
		colors.add(new Color(0,191,255));
		colors.add(new Color(178,34,34));
		colors.add(new Color(255,215,0));
		colors.add(new Color(173,255,47));
		colors.add(new Color(230,230,250));
		colors.add(new Color(255,250,205));
		colors.add(new Color(255,160,122));
		colors.add(new Color(32,178,170));
		colors.add(new Color(25,25,112));
		this.colorIndex=(int)(Math.random()*colors.size());
	}
	public Color nextColor() {
		this.colorIndex++;
		this.colorIndex=this.colorIndex % (this.colors.size()-1);
		return this.colors.get(this.colorIndex);
	}	
}