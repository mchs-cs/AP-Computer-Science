package battle.pack;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea textArea;
	ArrayList<ArrayList<String>> speechTerms = new ArrayList<ArrayList<String>>();		// Literally a list of lists.
	Random randomNumberGenerator = new Random();

	
	
	/*
	 * 
	 * 1 = HappyThoughts (2)
	 * 2 = SadThoughts	(2)
	 * 3 = NeutralThoughts	(1)
	 * 4 = FightingWords	(1)
	 * 5 = FlightingWords	(0)
	 * 6 = ForTheWinWords	(5)
	 * 7 = ForTheLossWords	(0)
	 * 8 = RandomWords	(2)
	 * 9 = HiddenWords	(2)
	 * 
	 */
	
	
	ArrayList<String> HappyThoughts = new ArrayList<String>();
	ArrayList<String> SadThoughts = new ArrayList<String>();
	ArrayList<String> NeutralThoughts = new ArrayList<String>();
	ArrayList<String> FightingWords = new ArrayList<String>();
	ArrayList<String> FlightingWords = new ArrayList<String>();
	ArrayList<String> ForTheWinWords = new ArrayList<String>();
	ArrayList<String> ForTheLossWords = new ArrayList<String>();
	ArrayList<String> RandomWords = new ArrayList<String>();
	ArrayList<String> HiddenWords = new ArrayList<String>();
	
	
	private void fillSpeechTerms() {
		speechTerms.add(HappyThoughts);
		speechTerms.add(SadThoughts);
		speechTerms.add(NeutralThoughts);
		speechTerms.add(FightingWords);
		speechTerms.add(FlightingWords);
		speechTerms.add(ForTheWinWords);
		speechTerms.add(ForTheLossWords);
		speechTerms.add(RandomWords);
		speechTerms.add(HiddenWords);
		fillHappyThoughts();
		fillSadThoughts();
		fillNeutralThoughts();
		fillFightingWords();
		fillFlightingWords();
		fillForTheWinWords();
		fillForTheLossWords();
		fillRandomWords();
		fillHiddenWords();
	}
	
	private void fillHappyThoughts() {
		HappyThoughts.add("Doing good.");
	}
	
	private void fillSadThoughts() {
		SadThoughts.add("Oh this could be bad…");
		SadThoughts.add("I think I’m gonna need help");
		SadThoughts.add("…");
		
	}
	
	private void fillNeutralThoughts() {
		NeutralThoughts.add("Not bad…");
		NeutralThoughts.add("No comment.");
	}

	private void fillFightingWords() {
		FightingWords.add("It’s ME!");
		FightingWords.add("I’m attacking him.");
		
	}

	private void fillFlightingWords() {
		FlightingWords.add("I need to run away.");
	}

	private void fillForTheWinWords() {
		ForTheWinWords.add("I’m gonna win.");
		ForTheWinWords.add("I’ve got this in the bag.");
		ForTheWinWords.add("I’m going to kill him.");
		ForTheWinWords.add("GG");
		ForTheWinWords.add("FINISH HIM!");
		ForTheWinWords.add("FATALITY!! ");
	}

	private void fillForTheLossWords() {
		ForTheLossWords.add("-_-");
	}

	private void fillRandomWords() {
		RandomWords.add("IT’S THE FINAL COUNTDOWN!!!");
		RandomWords.add("Big girls don’t cry.");
		RandomWords.add("GLHF");
	}

	private void fillHiddenWords() {
		HiddenWords.add("Its OVER 9000!!!");
		HiddenWords.add("Pika Pika!");
		HiddenWords.add('\u00B0' + "o" + '\u00B0');
	}
	
	
	/**
	 * Create the panel.
	 */
	public GamePanel() {
		fillSpeechTerms();
		setLayout(null);
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(10, 11, 430, 278);
		add(textArea);
		
		
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}
	
	public void say(Bactor bact, int number) {
		number = number - 1;
		String pretext = textArea.getText() + "\n";
		if(number < 10) {
			System.out.println("" + speechTerms.get(number).size());
			int index = randomNumberGenerator.nextInt(speechTerms.get(number).size());
			textArea.setText(pretext + bact.getName() + ": " + speechTerms.get(number).get(index));
		}
		if(number < 99 && number >= 10) {
			try {
				textArea.setText(pretext + bact.getName() + ": " + speechTerms.get(number/10-1).get(number%10-1));
			}
			catch(IndexOutOfBoundsException e) {
				
			}
		}
		
		if(number > 100 || number < 0) return;
		
	}
	
	
}
