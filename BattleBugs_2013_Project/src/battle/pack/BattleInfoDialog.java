package battle.pack;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import StaticLibrary.StaticLibrary;
import battle.pack.FileCopy.FileOperation;

public class BattleInfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JComboBox comboBox;
	private JButton btnImportExternalBugs;
	private JButton btnImportbattlers;
	private JButton btnDeletebattlers;
	private JButton btnDeleteExternalBugs;
	private JButton btnQuit;
	private JCheckBox chckbxFastModenoOutput;
	private List<FingerPrint> cheaters=new ArrayList<FingerPrint>();
	private boolean createCheaterLog=false;
	FileWrite cheaterLog=null;
	private JCheckBox chckbxPunishPlagiarizers;
	private JComboBox prefsComboBox;
	public BattleInfoDialog(JFrame owner, String filename) {
		super(owner, true);
		if (filename!=null) {
			createCheaterLog=true;
			cheaterLog=new FileWrite(filename);
			cheaterLog.write("Cheater Log Created: "+new Date()+"\n", false);			
		}
		setBounds(100, 100, 450, 329);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{220, 220, 0};
		gbl_contentPanel.rowHeights = new int[]{16, 16, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblBattles = new JLabel("Battles:");
			lblBattles.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblBattles = new GridBagConstraints();
			gbc_lblBattles.anchor = GridBagConstraints.EAST;
			gbc_lblBattles.insets = new Insets(0, 0, 5, 5);
			gbc_lblBattles.gridx = 0;
			gbc_lblBattles.gridy = 0;
			contentPanel.add(lblBattles, gbc_lblBattles);
		}
		{
			textField = new JTextField(5);
			textField.setText("1");
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.anchor = GridBagConstraints.NORTH;
			gbc_textField.insets = new Insets(0, 0, 5, 0);
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 0;
			contentPanel.add(textField, gbc_textField);
			textField.setColumns(10);
		}
		{
			JLabel lblOnlyRunBattles = new JLabel("Only Run Battles Including:");
			GridBagConstraints gbc_lblOnlyRunBattles = new GridBagConstraints();
			gbc_lblOnlyRunBattles.anchor = GridBagConstraints.EAST;
			gbc_lblOnlyRunBattles.insets = new Insets(0, 0, 5, 5);
			gbc_lblOnlyRunBattles.gridx = 0;
			gbc_lblOnlyRunBattles.gridy = 1;
			contentPanel.add(lblOnlyRunBattles, gbc_lblOnlyRunBattles);
		}
		{
			comboBox = new JComboBox();
			updateComboBox();
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.insets = new Insets(0, 0, 5, 0);
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 1;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			btnImportbattlers = new JButton("Import \"Battlers\"");
			btnImportbattlers.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					FileCopy.ExternalBugs("battlers.txt", FileOperation.COPY);
					updateComboBox();
				}
			});
			{
				btnImportExternalBugs = new JButton("Import External Bugs");
				btnImportExternalBugs.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						FileCopy.ExternalBugs("externalBugsDirectories.txt", FileOperation.COPY);
						updateComboBox();
					}
				});
				{
					chckbxPunishPlagiarizers = new JCheckBox("Punish Plagiarizers");
					GridBagConstraints gbc_chckbxPunishPlagiarizers = new GridBagConstraints();
					gbc_chckbxPunishPlagiarizers.insets = new Insets(0, 0, 5, 5);
					gbc_chckbxPunishPlagiarizers.gridx = 0;
					gbc_chckbxPunishPlagiarizers.gridy = 2;
					contentPanel.add(chckbxPunishPlagiarizers, gbc_chckbxPunishPlagiarizers);
				}
				{
					chckbxFastModenoOutput = new JCheckBox("Fast Mode(No Output)");
					GridBagConstraints gbc_chckbxFastModenoOutput = new GridBagConstraints();
					gbc_chckbxFastModenoOutput.insets = new Insets(0, 0, 5, 0);
					gbc_chckbxFastModenoOutput.gridx = 1;
					gbc_chckbxFastModenoOutput.gridy = 2;
					contentPanel.add(chckbxFastModenoOutput, gbc_chckbxFastModenoOutput);
				}
				GridBagConstraints gbc_btnImportExternalBugs = new GridBagConstraints();
				gbc_btnImportExternalBugs.insets = new Insets(0, 0, 5, 5);
				gbc_btnImportExternalBugs.gridx = 0;
				gbc_btnImportExternalBugs.gridy = 3;
				contentPanel.add(btnImportExternalBugs, gbc_btnImportExternalBugs);
			}
			{
				btnDeleteExternalBugs = new JButton("Delete External Bugs");
				btnDeleteExternalBugs.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FileCopy.ExternalBugs("externalBugsDirectories.txt", FileOperation.DELETE);
						updateComboBox();
					}
				});
				GridBagConstraints gbc_btnDeleteExternalBugs = new GridBagConstraints();
				gbc_btnDeleteExternalBugs.insets = new Insets(0, 0, 5, 0);
				gbc_btnDeleteExternalBugs.gridx = 1;
				gbc_btnDeleteExternalBugs.gridy = 3;
				contentPanel.add(btnDeleteExternalBugs, gbc_btnDeleteExternalBugs);
			}
			GridBagConstraints gbc_btnImportbattlers = new GridBagConstraints();
			gbc_btnImportbattlers.insets = new Insets(0, 0, 5, 5);
			gbc_btnImportbattlers.gridx = 0;
			gbc_btnImportbattlers.gridy = 4;
			contentPanel.add(btnImportbattlers, gbc_btnImportbattlers);
		}
		{
			btnDeletebattlers = new JButton("Delete \"Battlers\"");
			btnDeletebattlers.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileCopy.ExternalBugs("battlers.txt", FileOperation.DELETE);
					updateComboBox();
				}
			});
			GridBagConstraints gbc_btnDeletebattlers = new GridBagConstraints();
			gbc_btnDeletebattlers.insets = new Insets(0, 0, 5, 0);
			gbc_btnDeletebattlers.gridx = 1;
			gbc_btnDeletebattlers.gridy = 4;
			contentPanel.add(btnDeletebattlers, gbc_btnDeletebattlers);
		}
		{
			
			getLastUsedPrefsFilename();
			
			ArrayList<File> prefsFiles=FileCopy.getFiles(FileCopy.baseDir(), "*_bbprefs.ser");
			String[] prefChoices = new String[prefsFiles.size()];
			int selected=-1;
			for (int i=0; i<prefsFiles.size(); i++) {
				String fName=prefsFiles.get(i).getName();				
				prefChoices[i]=fName.substring(0, fName.indexOf("_bbprefs.ser"));
				if (fName.equals(currentPrefsFilename)) {
					selected=i;
				}
			}
			prefsComboBox = new JComboBox(prefChoices);
			if (selected!=-1) {
				prefsComboBox.setSelectedIndex(selected);
			}
			prefsComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
			prefsComboBox.setEditable(true);
			GridBagConstraints gbc_prefsComboBox = new GridBagConstraints();
			gbc_prefsComboBox.gridwidth = 2;
			gbc_prefsComboBox.insets = new Insets(0, 0, 0, 5);
			gbc_prefsComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_prefsComboBox.gridx = 0;
			gbc_prefsComboBox.gridy = 5;
			contentPanel.add(prefsComboBox, gbc_prefsComboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String prefName = (String)prefsComboBox.getSelectedItem();
						if (!prefName.endsWith("_bbprefs.ser")) {
							prefName=prefName+"_bbprefs.ser";
						}
						currentPrefsFilename=prefName;
						BattleInfoDialog.getPrefs();
						setLastUsedPrefsFilename();
						setVisible(false);
						dispose();
					}
				});
				{
					btnQuit = new JButton("Quit");
					btnQuit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.exit(0);
						}
					});
					buttonPane.add(btnQuit);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	public List<FingerPrint> getCheaters() {return cheaters;}
	private void updateComboBox() {
		ArrayList<BattleBug> battleBugs = StaticLibrary.getBattleBugs(StaticLibrary.VERBOSE.VERBOSE);
		ArrayList<FingerPrint> bugSigs=new ArrayList<FingerPrint>();
		ArrayList<String> ignoreMethodNames=new ArrayList<String>(Arrays.asList("getName","getVersion","getCreator","act","resetBug"));
		for (BattleBug battleBug: battleBugs) {
			Method[] meths=battleBug.getClass().getDeclaredMethods();
			Field[] fields=battleBug.getClass().getDeclaredFields();
			//			String[] methNames=new String[meths.length];
			ArrayList<String> temp=new ArrayList<String>();
			for (Method method:meths) {
				String methodName=method.getName();
				if (!ignoreMethodNames.contains(methodName)) temp.add(methodName);
				//				temp.add(methodName);
			}
			String[] methNames=new String[temp.size()];
			methNames=temp.toArray(methNames);
			//			for (int i=0; i<meths.length; i++) {
			//				methNames[i]=meths[i].getName();
			//			}
			String[] fieldNames=new String[fields.length];
			for (int i=0; i<fields.length; i++) {
				fieldNames[i]=fields[i].getName();
			}
			FingerPrint bugSig=new FingerPrint(battleBug,methNames,fieldNames);
			bugSigs.add(bugSig);
		}
		for (FingerPrint checkBug:bugSigs) {
			String output="";
			try {
				output="Checking: "+checkBug.getName()+" written by "+checkBug.getCreator()+" is unique.\n";
				boolean firstMatch=true;
				for (FingerPrint compareBug:bugSigs) {
					if (!checkBug.equals(compareBug)) {
						if (Arrays.deepEquals(checkBug.getMethods(), compareBug.getMethods()) && Arrays.deepEquals(checkBug.getFields(),compareBug.getFields())) {
							if (firstMatch) {
								firstMatch=false;
								output="Checking: "+checkBug;
							}
							if (!cheaters.contains(checkBug)) {
								cheaters.add(checkBug);
							}
							output+="\tis identical to "+compareBug.getName()+" written by "+compareBug.getCreator()+"\n";						
						}
					}
				}
			} catch(Error e) {
				output="Instantiation error on "+checkBug.getClass().getName();
				System.out.println(output);
			} finally {
				if (createCheaterLog) cheaterLog.write(output, true);
			}
		}
		ArrayList<String> battleBugNames = new ArrayList<String>();
		battleBugNames.add("All");
		for (BattleBug battleBug:battleBugs) {
			try {
				battleBugNames.add(battleBug.getName());
			} catch(Error e) {

			}
		}
		comboBox.setModel(new DefaultComboBoxModel(battleBugNames.toArray()));
	}
	public String getSelectedBugName() {
		String name = (String)comboBox.getSelectedItem();
		if (name.equals("All")) return null;
		return name;
	}
	public int getBattles() {
		int battles=1;
		try {
			battles=Integer.parseInt(textField.getText());
		} catch (Exception NumberFormatException) {
			battles=1;
		}
		return battles;
	}
	public boolean punishPlagiarizers() {return chckbxPunishPlagiarizers.isSelected();}
	public boolean fastMode() {return chckbxFastModenoOutput.isSelected();}
	private void getLastUsedPrefsFilename() {
		LastPrefs lastPrefs=new LastPrefs();
		try {
			FileInputStream fin = new FileInputStream("Preferences.ser");
			ObjectInputStream ois = new ObjectInputStream(fin);
			lastPrefs = (LastPrefs) ois.readObject();
			currentPrefsFilename=lastPrefs.getFilename();
			ois.close();
		} catch(Exception ex) {
			
		}
	}
	private void setLastUsedPrefsFilename() {
		try {
			LastPrefs lp=new LastPrefs();
			lp.setFilename(currentPrefsFilename);
			FileOutputStream fout = new FileOutputStream("Preferences.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(lp);	
			oos.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
		
	}
	public static void getPrefs() {
		BattleInfoDialog.battleBugsPrefs=new BattleBugsPrefs();
		try {
			FileInputStream fin = new FileInputStream(currentPrefsFilename);
			ObjectInputStream ois = new ObjectInputStream(fin);
			BattleInfoDialog.battleBugsPrefs = (BattleBugsPrefs) ois.readObject();
			ois.close();
		} catch(Exception ex) {
			
		}
	}
	public static String currentPrefsFilename="Default_bbprefs.ser";
	public static BattleBugsPrefs battleBugsPrefs;
	
	public static void storePrefs() {
		try {
			FileOutputStream fout = new FileOutputStream(currentPrefsFilename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(BattleInfoDialog.battleBugsPrefs);	
			oos.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
	}
}
