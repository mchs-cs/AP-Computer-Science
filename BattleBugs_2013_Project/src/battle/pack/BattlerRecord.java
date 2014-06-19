package battle.pack;

import java.util.ArrayList;
import java.util.Arrays;

public class BattlerRecord implements Comparable<BattlerRecord> {
	private static int bestMedian=Integer.MAX_VALUE;
	private ArrayList<Integer> positions;
	private BattleBug battleBug;
	private String grade="F";
	private int median=Integer.MAX_VALUE;
	public BattlerRecord() {
		positions=new ArrayList<Integer>();
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public static void setBestMedian(int setBestMedian) {
		bestMedian=setBestMedian;
	}
	public static int getBestMedian() {
		return bestMedian;
	}
	public String getCreator() {
		return battleBug.getShortCreator();
	}
	public String getBattleBugName() {
		return battleBug.getShortName();
	}
	public void setBattleBug(BattleBug battleBug) {
		this.battleBug = battleBug;
	}
	public void addPoints(int points) {
		positions.add(points+1);
	}
	public ArrayList<Integer> getPositions() {
		return positions;
	}
	public BattleBug getBattleBug() {return battleBug;}
	public double getAverage() {
		if (positions.size()==0) return 0;
		double average=0;
		for(int pos:positions) {
			average+=pos;
		}
		average/=positions.size();
		return average;
	}
	public int calculateMedian() {
		if (positions.size()==0) return 0;
		Object[] arr=positions.toArray();
		Arrays.sort(arr);
		median=(Integer)arr[arr.length/2];
		if (median<bestMedian) bestMedian=median;
		return median;
	}
	public int getMedian() {return median;}
	public int getScore(int numBattleBugs) {
		if (median==Integer.MAX_VALUE) calculateMedian();
		
		int score=numBattleBugs-(getMedian()-bestMedian);
		return score;
	}
	public String getGrade() {
		return grade;
	}
	public int getBest() {
		int best=positions.size();
		for(int pos:positions) {
			if (pos<best) best=pos;
		}
		return best;
	}
	public int getWorst() {
		int worst=1;
		for(int pos:positions) {
			if (pos>worst) worst=pos;
		}
		return worst;
	}
	@Override
	public int compareTo(BattlerRecord o) {
		calculateMedian();
		o.calculateMedian();
		return getMedian()-o.getMedian();
	}
}
