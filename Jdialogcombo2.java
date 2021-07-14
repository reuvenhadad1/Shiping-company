package program;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import components.Branch;
import components.Hub;
import components.MainOffice;

public class Jdialogcombo2 extends JDialog  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private JFrame fr;
	public JFrame getFr() {
		return fr;
	}

	private JPanel selectPanel;
	JComboBox<String> Branches;
	
	public Jdialogcombo2(int numOfBranches) {
		String [] Phases=new String[numOfBranches];
		
		for(int i=0;i<numOfBranches;i++) {
			Phases[i]="Branch"+(i+1);
			
		}
		selectPanel= new JPanel();
	    selectPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Select Branch"), 
			BorderFactory.createEmptyBorder(40,40,40,40)));
	    
	    Branches=new JComboBox <String>(Phases);
		selectPanel.add(Branches);
		Branches.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
            	
            	
            		String p=""+(Branches.getSelectedIndex()+1);
            		MainOffice.CloneBranch(p);
            		fr.setVisible(false);
          		   MainOffice.getPanel().repaint();
            			
            		}
            		
            		
            		
            		
            		
            	
            
        });
		
		
		fr=new JFrame();
		fr.add(selectPanel);
		fr.pack();
		fr.setLocationRelativeTo(null);
		fr.setVisible(true);
		
    	
		
 }
}