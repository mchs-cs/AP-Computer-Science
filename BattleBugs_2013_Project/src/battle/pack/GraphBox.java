package battle.pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GraphBox extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private BattleWorld world;
	
	private static int calcY(int health, int hMax, int graphHeight) { // This static method converts a health, MaxHealth, and the height of the graph in pixels to a pixel height, and reverses the origin so, 0 is the bottom of the graph
		double hRatio=(double)health/hMax;
		return graphHeight-(int)(hRatio*graphHeight);
	}
	public GraphBox(){
		world = null;
	}
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		if(world==null) return;
		if(!world.isGameOn()) return;
		
		Graphics2D g2=(Graphics2D) g;
		int turnIndex=world.getSteps(); // All our graphing is based on the turnIndex in steps
		int x2= turnIndex*10; // We scale everything by 10, so that graph isn't all bunched up
		if (turnIndex==1) { // First we erase the background, if it the start of a new round
			Shape p = new Rectangle(g.getClipBounds());
			g2.setColor(Color.white);
			g2.draw(p);
			g2.fill(p);
		} else { // Otherwise, we only want to erase the name labels, which are included in this rectangle:
			Shape whiteRect = new Rectangle(x2-9,g.getClipBounds().y,g.getClipBounds().width,g.getClipBounds().height);
			g2.setColor(Color.white);
			g2.draw(whiteRect);
			g2.fill(whiteRect);			
		}
		ArrayList<Bactor> bactors=this.world.getBactors(); // Get a list of all the Bactors still on the field
		bactors.addAll(this.world.getEliminatedBactors()); // Add to that list all the Bactors that have been eliminated so far
		for (int i=0; i<bactors.size(); i++) { // Update the graph for each Bactor
			Bactor b = bactors.get(i);
			Color c = b.getBaseColor(); // The graph color should match the BattleBug's icon color
			int graphHeight = g2.getClipBounds(getVisibleRect()).height-bactors.size()-1; // To be able to see all the line stacked on top of each other, we have to adjust for the number of Bactors on the field when figuring out how big the graph is
			Shape r = new Rectangle(g2.getClipBounds().x,g2.getClipBounds().y,g2.getClipBounds().width-1, g2.getClipBounds().height-1);
			g2.setColor(Color.black);
			g2.draw(r);	// This just outlines our graphing area with a black box		
			int hMax=b.getMaxHealth(); // All values on the graph are scaled by the MaxHealth of the Bactor, so they are % health
			int y2=GraphBox.calcY(b.getHealth(), hMax, graphHeight); // Get our y value for the graph, a health of 0 will translate the bottom of the graph
			y2=Math.min(graphHeight, y2); // If we somehow have a y value outside our graph bounds, then use the graph bound(in case of negative health values)
			int x1 = turnIndex*10-10; // Same as x2, but ten pixels less
			int y1= GraphBox.calcY(b.getStartHealth(), hMax, graphHeight); // The line we draw goes from the health at the start of the step, to the current health
			g2.setColor(c);
			g2.drawString(bactors.get(i).getName(), x2+1, y2); // Put the Bactor's BattleBug name on the graph
			Shape p = new Line2D.Double(x1,y1+i,x2,y2+i); // Define the line, we add i to the y values so they stack
			g2.draw(p);
		}		
	}
	public BattleWorld getWorld() {
		return world;
	}
	public void setWorld(BattleWorld world) {
		this.world = world;
	}
}
