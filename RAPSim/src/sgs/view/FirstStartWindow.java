package sgs.view;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sgs.model.ProgramParametersSaved;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FirstStartWindow extends JDialog {

    private static final long serialVersionUID = 1L;
    private JLabel jLabel_IL;
    private JPanel jPanel1;
    
    public static void start(final JFrame parent, final ProgramParametersSaved params){
		new Thread(){
			@Override
			public void run(){
				new FirstStartWindow(parent, params).setVisible(true);
			}
		}.start();
    }
    
    /**
     * 
     * @param parent
     */
    public FirstStartWindow(JFrame parent, final ProgramParametersSaved params) {
        super(parent, "About SmartGridSim", true);

        JPanel p2 = new JPanel();
        JButton ok = new JButton(" OK ");
        p2.add(ok);
        getContentPane().add(p2, "South");
        //p2.setPreferredSize(new java.awt.Dimension(298, 38));
        {
        	jPanel1 = new JPanel();
        	BoxLayout jPanel1Layout = new BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS);
        	jPanel1.setLayout(jPanel1Layout);
        	getContentPane().add(jPanel1, BorderLayout.CENTER);
        	jPanel1.setPreferredSize(new java.awt.Dimension(439, 203));
        	{
        		jLabel_IL = new JLabel(" Smart Grid Simulator FEATURES:  ");
        		jPanel1.add(jLabel_IL);
        		jLabel_IL.setText(" Smart Grid Simulator FEATURES:  ");
        		jLabel_IL.setFont(new java.awt.Font("Segoe UI",1,16));
        	}
        	{
        		jPanel1.add(new JLabel(" (First start information)"));
        		jPanel1.add(new JLabel("    "));
        		jPanel1.add(new JLabel("  - Zoom with scroll"));
        		jPanel1.add(new JLabel("  - Move objects, delete and add objects (lower panel)"));
        		jPanel1.add(new JLabel("  - Change object properties in the specific properties panel"));
        		jPanel1.add(new JLabel("  - Change simulation algorithm with menu"));
        		jPanel1.add(new JLabel("  - Start simulation and save results"));
        		jPanel1.add(new JLabel("    "));
        		jPanel1.add(new JLabel("  - Click OK for not showing this dialog again"));
        	}
        }

        ok.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
            	params.setFirstStart(false);
            	setVisible(false);
            }
        });

        //setSize(250, 150);
        pack();
        this.setSize(457, 338);
        setLocationRelativeTo(parent);
    }
}
