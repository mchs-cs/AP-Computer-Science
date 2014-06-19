package battle.pack;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class WindowPrefs implements Serializable {
	static final long serialVersionUID = 1L;
	public int[] colWidths;
	public int winX, winY, winH, winW;
	public WindowPrefs(JFrame frame, JTable table) {
		if (table!=null) {
			TableColumnModel tcm = table.getColumnModel();
			colWidths=new int[tcm.getColumnCount()];
			for (int i = 0; i < (tcm.getColumnCount()); i++) {
				colWidths[i]=tcm.getColumn(i).getWidth();
			}
		}
		if (frame!=null) {
			winX = frame.getX();
			winY = frame.getY();
			winH=frame.getHeight();
			winW=frame.getWidth();
		}
	}
}
