package battle.pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JDialog;

public class BattleRunner {
	public static boolean FAST_RUN=true;
	public static boolean CATCH_CHEATERS=false;
	private static List<FingerPrint> plagiarizers=null;
	private static List<BattleBug> plagiarizedBugs=new ArrayList<BattleBug>();
	public static List<BattleBug> getPlagiarizers() {return plagiarizedBugs;}
	public static void main(String[] args) throws Exception {
		BattleBugsSecurityManager sm=new BattleBugsSecurityManager();
		System.setSecurityManager(sm);
		sm.setVerbose(false);
		sm.activeLog("LiveSecurityLog.txt");
		BattleInfoDialog battleInfoDialog=new BattleInfoDialog(null,"CheaterLog.txt");
		battleInfoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		battleInfoDialog.setVisible(true);
		int battles=battleInfoDialog.getBattles();
		FAST_RUN=battleInfoDialog.fastMode();
		plagiarizers=battleInfoDialog.getCheaters();
		CATCH_CHEATERS=battleInfoDialog.punishPlagiarizers();
		for (FingerPrint plagiarizer:plagiarizers) {
			plagiarizedBugs.add(plagiarizer.getBattleBug());
			System.out.println(plagiarizer);
		}
		String onlyBug=battleInfoDialog.getSelectedBugName();
		ArrayList<BattlerRecord> battlerRecords=new ArrayList<BattlerRecord>();
		ArrayList<BattlerRecord> results=null;
		DoBattle doBattle=new DoBattle(100,150);
		if (onlyBug!=null) doBattle.setOnlyBattlesWith(onlyBug);
		results=doBattle.beginBattle(battlerRecords,false,battles,onlyBug);			
		String label="Creator(BattleBug)";
		int maxNameLen=0;
		for (BattlerRecord br:battlerRecords) {
			int len=br.getBattleBug().getShortCreator().length()+br.getBattleBug().getShortName().length()+2;
			if (len>maxNameLen) {
				maxNameLen=len;
			}
		}
		while (label.length()<maxNameLen) {
			label+=" ";
		}
		label+="\tGRD";
		int rounds=results.get(0).getPositions().size();
		String line="\tBest"+"\tWorst\t  Avg\tMedian\tScore";
		for (int i=1; i<=rounds; i++) {
			String rnd="Rnd#"+i;
			while (rnd.length()<4) {
				rnd=" "+rnd;
			}
			line=line+"\t"+rnd;
		}
		FileWrite fw=new FileWrite("BattleResults.txt");
		fw.write(label+line+ "\n", false);
		System.out.println(label+line);
		for (BattlerRecord br:battlerRecords) {
			br.calculateMedian();			
		}
		Collections.sort(battlerRecords);
		System.out.println("Best Median Score: "+BattlerRecord.getBestMedian());
		int battlersCount=battlerRecords.size();
		int gradeGroup=(int)(battlersCount*.33);
		int brIndex=0;
		for (BattlerRecord br:battlerRecords) {
			brIndex++;
			if (brIndex<=gradeGroup) {
				br.setGrade("A");
			} else if (brIndex<=gradeGroup*2) {
				br.setGrade("B");
			} else {
				br.setGrade("C");
			}
		}
		Collections.sort(battlerRecords, new Comparator<BattlerRecord>() {
			public int compare(BattlerRecord br1, BattlerRecord br2) {
				return br1.getCreator().compareTo(br2.getCreator());	
			}
		});
		for (BattlerRecord br:battlerRecords) {
			brIndex++;
			double average=0;
			int best=battlerRecords.size()+1;
			int worst=0;
			int total=0;
			ArrayList<Integer> scores=br.getPositions();
			String creator=br.getCreator();
			String battleBug=br.getBattleBugName();
			label=creator+"("+battleBug+")";
			while (label.length()<maxNameLen) {
				label+=" ";
			}
			line="";
			for (int i=0; i<scores.size(); i++) {
				int score=scores.get(i);
				line+="\t"+String.format("%5d", score);
				total+=score;
				if (score<best) best=score;
				if (score>worst) worst=score;		
			}
			average=(double)total/scores.size();
			String avg=String.format("%5.2f", average);
			String myScore=String.format("%5d", br.getScore(battlerRecords.size()));
			String median=String.format("%5d", br.getMedian());
			String out=label+"\t "+br.getGrade()+"\t"+String.format("%4d", best)+"\t"+String.format("%5d", worst)+"\t"+avg+"\t"+median+"\t"+myScore+line+ "\n";
			fw.write(out, true);
			System.out.print(out);	
		}
		
		String report=sm.getSecurityLog(true, true);
		System.out.println(report);
		FileWrite securityReport=new FileWrite("SecurityReport.txt");
		securityReport.write(report, false);
	}
	
}	
