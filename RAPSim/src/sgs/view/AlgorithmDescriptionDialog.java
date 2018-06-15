package sgs.view;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import sgs.controller.simulation.AbstractDistributionAlgorithm;

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
public class AlgorithmDescriptionDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel algorithmNameL;
	private JTextArea algorithmDescriptionTF;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				AlgorithmDescriptionDialog inst = new AlgorithmDescriptionDialog(frame);
				inst.setVisible(true);
			}
		});
	}
	
	/**
	 * Constructor for editor and testing.
	 * @param owner
	 */
	private AlgorithmDescriptionDialog(JFrame owner) {
		this(owner, null);
	}
	
	/**
	 * 
	 * @param owner
	 * @param algorithm
	 */
	public AlgorithmDescriptionDialog(JFrame owner,
			AbstractDistributionAlgorithm algorithm) {
		
		super(owner);
		this.setTitle("Algorithm information");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initGUI();
		
		if(algorithm!=null){
			algorithmNameL.setText(algorithm.getName());
			algorithmDescriptionTF.setText(algorithm.getDescription());
		}
		else{
			algorithmNameL.setText("null");
			algorithmDescriptionTF.setText("No algorithm selected. No algorithm selected. No algorithm selected." +
					"No algorithm selected. No algorithm selected. No algorithm selected. No algorithm selected.");
		}
		
		setVisible(true);
	}

	private void initGUI() {
		try {
			{
				algorithmNameL = new JLabel("Name");
				getContentPane().add(algorithmNameL, BorderLayout.NORTH);
				algorithmNameL.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				algorithmNameL.setFont(new java.awt.Font("Segoe UI",1,15));
				algorithmNameL.setSize(382, 35);
			}
			{
				algorithmDescriptionTF = new JTextArea("Description");
				getContentPane().add(algorithmDescriptionTF, BorderLayout.CENTER);
				algorithmDescriptionTF.setEditable(false);
				algorithmDescriptionTF.setWrapStyleWord(true);
				algorithmDescriptionTF.setLineWrap(true);
				//algorithmDescriptionTF.setAlignmentX(0.0f);
				algorithmDescriptionTF.setFont(new java.awt.Font("Segoe UI",0,12));
			}
			setSize(500, 400);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
