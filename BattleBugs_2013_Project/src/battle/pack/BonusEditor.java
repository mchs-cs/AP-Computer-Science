package battle.pack;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

import javax.swing.JComboBox;
import javax.swing.JPanel;


public class BonusEditor extends PropertyEditorSupport {
	private JComboBox combo;
    private JPanel panel = new JPanel();
	
	public BonusEditor() {

		Bonus bonuses[] = Bonus.values();
		combo = new JComboBox(bonuses);
		panel.add(combo);
	}
    public Object getValue()
    {
    	Bonus bonus = (Bonus)combo.getSelectedItem();
        return bonus;
    }
    public void setValue(Object newValue)
    {
    	if (newValue==null) {
    		newValue=Bonus.ATTACK.randomBonus();
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
