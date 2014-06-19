package battle.pack;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;

import java.awt.event.WindowAdapter;

public class Reporter extends JFrame {
	private static final long serialVersionUID = 1L;

	static class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		ArrayList<Color> rowColours;
		private String[] columnNames;
		private Object[][] data;
		@SuppressWarnings("unused")
		private static Color randomColor() {
			Random rnd = new Random();
			return new Color(rnd.nextInt(256),
					rnd.nextInt(256), rnd.nextInt(256));
		}
		public MyTableModel(String[] columnNames) {
			this.columnNames=columnNames;
			this.data=new Object[0][columnNames.length];
			this.rowColours=new ArrayList<Color>();
		}
		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class<? extends Object> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
		public void addRow(Object[] objs) {
			Object[][] newData = new Object[this.getRowCount()+1][this.getColumnCount()];
			for (int row=0; row<this.getRowCount(); row++) {
				for (int col=0; col<this.getColumnCount(); col++) {
					newData[row][col]=this.data[row][col];
				}
			}
			for (int col=0; col<this.getColumnCount(); col++) {
				newData[newData.length-1][col]=objs[col];
			}
			this.data=newData;
			this.rowColours.add(Color.blue);
			//			this.setRowColour(0, randomColor());
			fireTableDataChanged();
		}
		public void setRow(int rowIndex, Object[] objs) {
			for (int col=0; col<this.getColumnCount(); col++) {
				this.data[rowIndex][col]=objs[col];
			}
			if (!BattleRunner.FAST_RUN) fireTableRowsUpdated(rowIndex, rowIndex);
		}
		public  Object[] getRow(int rowIndex) {
			Object[] objs = new Object[this.getColumnCount()];

			for (int col=0; col<this.getColumnCount(); col++) {
				objs[col]=this.data[rowIndex][col];
			}
			return objs;
		}


		public void setRowColour(int row, Color c) {
			rowColours.set(row, c);
			fireTableRowsUpdated(row, row);
		}

		public Color getRowColour(int row) {
			return rowColours.get(row);
		}

	}
	class MyTableCellRender extends DefaultTableCellRenderer {  
		private static final long serialVersionUID = 1L;
		public MyTableCellRender()   
		{  
			super();  
			setOpaque(true);  
		}
		public void setForeground(Color color) {
			this.setForeground(color);

		}
		public void setBackground(Color color) {
			this.setForeground(color);
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			MyTableModel model = (MyTableModel) table.getModel();
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(model.getRowColour(row));
			return c;
		}
	}  
	public class TabRecord {
		private JTabbedPane bugTabPane;
		private JTextArea bugTextArea;
		private Bactor bactor;
		private int rowIndex;
		public TabRecord() {
			this.bactor=null;
			this.bugTabPane=null;
			this.bugTextArea=null;
			this.rowIndex=-1;
		}
		public TabRecord(Bactor bactor, JTabbedPane bugTabPane, JTextArea bugTextArea, int rowIndex) {
			this.bactor=bactor;
			this.bugTabPane=bugTabPane;
			this.bugTextArea=bugTextArea;
			this.rowIndex=rowIndex;
		}
		public int getRowIndex() {
			return rowIndex;
		}
		public void setRowIndex(int rowIndex) {
			this.rowIndex = rowIndex;
		}
		public JTabbedPane getBugTabPane() {
			return bugTabPane;
		}
		public JTextArea getBugTextArea() {
			return bugTextArea;
		}
		public Bactor getBactor() {
			return this.bactor;
		}
		public void setBugTabPane(JTabbedPane bugTabPane) {
			this.bugTabPane = bugTabPane;
		}
		public void setBugTextArea(JTextArea bugTextArea) {
			this.bugTextArea = bugTextArea;
		}
		public void setBactor(Bactor bactor) {
			this.bactor = bactor;
		}
		public int compareTo(TabRecord otherTabRecord) {
			int res=0;
			if (this.getBactor().getPoints() < otherTabRecord.getBactor().getPoints()) {
				res=-1;
			}
			if (this.getBactor().getPoints() > otherTabRecord.getBactor().getPoints()){
				res=1;
			}
			return res;
		}	

	}
	private ArrayList<JTextArea> bugOuts;
	private DoBattle doBattle;
	private JTextArea announcerTextArea;
	private JScrollPane announcerScrollPane;
	private JTabbedPane bugTabbedPane;
	private JTable table;
	private JScrollPane tableScrollPane;
	private ArrayList<BattleRecord> battleRecords;
	private JSplitPane splitPane;
	private WindowPrefs reporterPrefs;

	public Reporter(ArrayList<Bactor> bactors) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				storePrefs();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				storePrefs();
			}
		});
		setBounds(555, 0, 458, 581);
		Attribute[] attributeNames=Attribute.ATTACK.chartValues();
		String[] otherColumnNames={"Name","Creator","Ver"};
		int[] otherColumnWidths={250,150,25};
		String[] columnNames = new String[attributeNames.length+otherColumnNames.length];
		int[] columnWidths=new int[attributeNames.length+otherColumnNames.length];
		for (int i=0; i<otherColumnNames.length; i++) {
			columnNames[i]=otherColumnNames[i];
			columnWidths[i]=otherColumnWidths[i];
		}
		for (int i=0; i<attributeNames.length; i++) {
			columnNames[otherColumnNames.length+i]=attributeNames[i].toString();
			columnWidths[otherColumnNames.length+i]=attributeNames[i].WIDTH();
		}
		this.bugOuts = new ArrayList<JTextArea>();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{458, 0};
		gridBagLayout.rowHeights = new int[]{485, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		splitPane = new JSplitPane();
		splitPane.setMinimumSize(new Dimension(244, 500));
		splitPane.setPreferredSize(new Dimension(244, 600));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		getContentPane().add(splitPane, gbc_splitPane);

		this.tableScrollPane = new JScrollPane();
		tableScrollPane.setMinimumSize(new Dimension(0, 0));
		tableScrollPane.setPreferredSize(new Dimension(4, 300));
		splitPane.setLeftComponent(tableScrollPane);
		this.tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		this.table = new JTable(new MyTableModel(columnNames));
		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < (tcm.getColumnCount()); i++) {
			tcm.getColumn(i).setPreferredWidth(columnWidths[i]);
		}
		table.setMinimumSize(new Dimension(0, 0));
		table.setPreferredSize(new Dimension(675, 800));
		this.table.setFillsViewportHeight(true);
		this.tableScrollPane.setViewportView(this.table);

		this.bugTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		bugTabbedPane.setPreferredSize(new Dimension(21, 300));
		bugTabbedPane.setMinimumSize(new Dimension(0, 0));
		splitPane.setRightComponent(bugTabbedPane);
		this.bugTabbedPane.setMaximumSize(new Dimension(400, 400));
		this.bugTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		this.announcerScrollPane = new JScrollPane();
		this.announcerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.bugTabbedPane.addTab("Announcer", null, this.announcerScrollPane, null);
		this.announcerTextArea = new JTextArea();
		this.announcerTextArea.setLineWrap(true);
		this.announcerTextArea.setWrapStyleWord(true);
		this.announcerScrollPane.setViewportView(this.announcerTextArea);
		DefaultCaret caret = (DefaultCaret)this.announcerTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.battleRecords=new ArrayList<BattleRecord>();
		for (Bactor bactor:bactors) {
			Icon icon=null;
			if (!BattleRunner.FAST_RUN) icon=this.createImageIcon("Bactor" + bactor.getImageSuffix() + ".gif");
			TabRecord myTab=this.addBugTab(bactor.getBattleBug().getName(), icon);
			bactor.setBugOut(myTab.getBugTextArea());
			bactor.setAnnouncerOut(this.getAnnouncer());
			BugStepRecord starter = new BugStepRecord(bactor);
			myTab.setBactor(bactor);
			myTab.setRowIndex(this.addTableRow(starter));
			BattleRecord myBattleRecord=new BattleRecord(bactor, myTab, starter);
			this.battleRecords.add(myBattleRecord);			
		}
		this.pack();
	}
	public ArrayList<BattleRecord> getBattleRecords() {
		return this.battleRecords;
	}
	public TableModel getTableModel() {
		return this.table.getModel();
	}
	public int addTableRow(BugStepRecord bugStepRecord) {
		Object[] rowObjs = new Object[this.table.getColumnCount()];
		rowObjs[0] = (this.table.getRowCount()) + ". " + bugStepRecord.getName();
		rowObjs[1] = bugStepRecord.getCreator();
		rowObjs[2] = bugStepRecord.getVersion();
		for (int i=0; i<bugStepRecord.getAttributeVals().length; i++) {
			rowObjs[3+i] = bugStepRecord.getAttributeVals()[i];
		}
		MyTableModel myTableModel = (MyTableModel)this.table.getModel();
		myTableModel.addRow(rowObjs);
		return myTableModel.getRowCount()-1;
	}
	public void updateTableRow(int rowIndex, BugStepRecord bugStepRecord) {
		Object[] rowObjs = new Object[this.table.getColumnCount()];
		rowObjs[0] = (rowIndex+1) + ". " + bugStepRecord.getName();
		rowObjs[1] = bugStepRecord.getCreator();
		rowObjs[2] = bugStepRecord.getVersion();
		for (int i=0; i<bugStepRecord.getAttributeVals().length; i++) {
			rowObjs[3+i] = bugStepRecord.getAttributeVals()[i];
		}
		MyTableModel myTableModel = (MyTableModel)this.table.getModel();
		myTableModel.setRow(rowIndex, rowObjs);
	}
	public void swapTableRows(int rowIndexA, int rowIndexB) {
		MyTableModel myTableModel = (MyTableModel)this.table.getModel();
		Object[] rowA=myTableModel.getRow(rowIndexA);
		Object[] rowB=myTableModel.getRow(rowIndexB);
		myTableModel.setRow(rowIndexA, rowB);
		myTableModel.setRow(rowIndexB, rowA);			
	}
	public TabRecord addBugTab(String name, Icon icon) {
		JScrollPane bugScrollPane = new JScrollPane();

		bugScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		TabRecord tabRecord=new TabRecord();
		final JTextArea textArea = new JTextArea();
		textArea.setFont( new Font("monospaced", Font.PLAIN, 12) );
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		bugScrollPane.setViewportView(textArea);
		tabRecord.bugTextArea=textArea;
		textArea.setEditable(false);
		this.bugOuts.add(textArea);
		tabRecord.setBugTabPane(this.bugTabbedPane);
		this.bugTabbedPane.addTab(name, icon, bugScrollPane);
		return tabRecord;
	}
	public void setRowColor(int rowIndex, Color color) {
		MyTableModel myTableModel = (MyTableModel)this.table.getModel();
		myTableModel.setRowColour(rowIndex, color);
	}
	public void showMeOld() {
		//Gets the screen size and positions the frame left bottom of the screen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int x = (int)rect.getMaxX()- this.getWidth();
		int y = (int)rect.getMinY();
		this.setLocation(x ,y);
		this.setSize(this.getWidth(), ((int)rect.getMaxY()-(int)rect.getMinY())-40);
		this.setVisible(true);
	}
	public void showMe() {
		getPrefs();
		this.setLocation(reporterPrefs.winX,reporterPrefs.winY);
		this.setSize(reporterPrefs.winW,reporterPrefs.winH);
		if (table!=null) {
			TableColumnModel tcm = table.getColumnModel();
			if (reporterPrefs.colWidths!=null) {
				for (int i = 0; i < reporterPrefs.colWidths.length; i++) {
					if (i<tcm.getColumnCount()) tcm.getColumn(i).setPreferredWidth(reporterPrefs.colWidths[i]);
				}
			}
		}
		this.setVisible(true);
	}
	public DoBattle getDoBattle() {
		return doBattle;
	}
	public void setDoBattle(DoBattle doBattle) {
		this.doBattle = doBattle;
	}
	public JTextArea getAnnouncer() {
		return this.announcerTextArea;
	}
	public void resetAnnouncer() {
		this.announcerTextArea.setText("");
	}
	public void announce(String announce) {
		this.announcerTextArea.setText(this.announcerTextArea.getText()+ announce);
	}
	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = DoBattle.class.getResource(path);
		if (imgURL != null) {
			Image img = new ImageIcon(imgURL).getImage();  
			Image newimg = img.getScaledInstance(32, 32,  Image.SCALE_DEFAULT);  
			return new ImageIcon(newimg);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	private void getPrefs() {
		reporterPrefs=BattleInfoDialog.battleBugsPrefs.getReporterWindowPrefs();
		if (reporterPrefs==null) reporterPrefs=new WindowPrefs(this, table);
//		try {
//			FileInputStream fin = new FileInputStream("repWin_"+bbPrefs.getPrefsName());
//			ObjectInputStream ois = new ObjectInputStream(fin);
//			reporterPrefs = (WindowPrefs) ois.readObject();
//			ois.close();
//		} catch(Exception ex) {
//			reporterPrefs=new WindowPrefs(this, table);
//		}
	}
	private void storePrefs() {
		reporterPrefs=new WindowPrefs(this, table);
		BattleInfoDialog.battleBugsPrefs.setReporterWindowPrefs(reporterPrefs);
//		try {
//			FileOutputStream fout = new FileOutputStream("repWin_"+bbPrefs.getPrefsName());
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(reporterPrefs);	
//			oos.close();
//		} catch(Exception ex) {
//			ex.printStackTrace();
//		} 
	}
}
