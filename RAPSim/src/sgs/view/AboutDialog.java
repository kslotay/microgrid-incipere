package sgs.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;
//	private static final Component LogoPanel = new LogoPanel();

    /**
     * 
     * @param parent
     */
    public AboutDialog(JFrame parent) {
        super(parent, "About RAPSim", true);

        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        
        JPanel logoPanel = new JPanel();
//        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.getContentPane().add(logoPanel);
        
        JPanel textPanel = new JPanel();
        //textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        this.getContentPane().add(textPanel);
        
        LogoPanel logo = new LogoPanel(2);
        logoPanel.add(logo);
              
        
        textPanel.add(new JLabel("RAPSim - Renewable Alternative Powersystems Simulation, Version 0.95"));
        textPanel.add(new JLabel(" ") );
        textPanel.add(new JLabel("Created by Manfred Pöchacker, Kristofer Schweiger and Sabrina Huber"  )); 
        textPanel.add(new JLabel("at the Institute of Networked and Embedded Systems of AAU Klagenfurt. " )) ; 
        textPanel.add(new JLabel("An extension of 'Smart Grid Sim' from Christoph Granzer & Tobias Ibounig. "  )); 
        textPanel.add(new JLabel(" ") );
        textPanel.add(new JLabel("Published under GNU General Public License."));
        textPanel.add(new JLabel("Available at https://sourceforge.net/projects/rapsim "));
        textPanel.add(new JLabel(" ") );
        textPanel.add(new JLabel("Further contributors (in chronological order):"));
        textPanel.add(new JLabel("Anita Sobe, Benjamin Breiling "));        
        
        
        /*
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        
//        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        LogoPanel logo = new LogoPanel();
//        logo.	, "Center");
        
        b.add(logo );
        
        b.add(new JLabel("RAPSim - Renewable Alternative Powersystems Simulation:"));
        b.add(new JLabel(" 	Created by Manfred Pöchacker & Kristofer Schweiger "  )); 
        b.add(new JLabel(" 	at the Institute of Networked and Embedded Systems of AAU Klagenfurt. " )) ; 
        b.add(new JLabel(" ") );
        b.add(new JLabel("Published under general puplic license."));
        b.add(new JLabel("Available at https://sourceforge.net/projects/rapsim "));
        b.add(new JLabel(" ") );
        b.add(new JLabel("Further contributors (in chronological order):"));
        b.add(new JLabel("Benjamin Breiling, Sabrina Huber "));

        b.add(Box.createGlue());
        getContentPane().add(b, "Center");
        */
        
        JPanel p2 = new JPanel();
        JButton close = new JButton(" Close ");
        p2.add(close);
        this.getContentPane().add(p2, "South");
        

        close.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });

        //setSize(250, 150);
        pack();
        setLocationRelativeTo(parent);
    }
    
}
    


