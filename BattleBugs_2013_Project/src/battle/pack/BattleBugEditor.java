package battle.pack;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JPanel;


import StaticLibrary.StaticLibrary;

public class BattleBugEditor extends PropertyEditorSupport {
	private JComboBox combo;
    private JPanel panel = new JPanel();
    @SuppressWarnings("rawtypes")
	private Class defaultClass;
	
	@SuppressWarnings("rawtypes")
	public BattleBugEditor() {
		try {
			Class[] classList = StaticLibrary.getClasses("battle.game");
/*
			ArrayList<Class> battleBugs = new ArrayList<Class>();
			for (int i=0; i<classList.length; i++) {
				Class aClass = classList[i];
				if (aClass.getSuperclass().toString().equals("class battle.pack.BattleBug")) {
					battleBugs.add(aClass);
				}
			}
			for (Class aClass: battleBugs) {
				System.out.println(aClass.toString());
			}
			Class[] bugsArr = (Class[])battleBugs.toArray();
			for (int i=0; i<bugsArr.length; i++) {
				System.out.println(bugsArr[i].getName());
				System.out.println(bugsArr[i].toString());
			}
			*/
			if (classList.length>0) {
				this.defaultClass=classList[0];
			}
/*			for (int i=0; i<classList.length; i++) {
				System.out.println(classList[i].getSimpleName());
			}
*/
			combo = new JComboBox(classList);
			panel.add(combo);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public Object getValue()
    {
    	@SuppressWarnings("rawtypes")
		Class bug = (Class)combo.getSelectedItem();
        return bug;
    }
    public void setValue(Object newValue)
    {
    	if (newValue==null) {
    		newValue=this.defaultClass;
    	}
		combo.setSelectedItem(newValue);
    }

    public boolean supportsCustomEditor()
    {
        return true;
    }

    public Component getCustomEditor()
    {
        return panel;
    }
}
