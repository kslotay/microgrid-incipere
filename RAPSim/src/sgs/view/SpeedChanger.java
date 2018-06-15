package sgs.view;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sgs.controller.simulation.TimeThread;
import sgs.model.SgsGridModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
/**
 *
 * @author Tobi, Schweiger, Huber, Pöchacker
 */
public class SpeedChanger extends JDialog {

    private static final long serialVersionUID = 1L;
    private JSlider jSlider;
    
    public static boolean isRunning = false;
    private final File f = PropertiesDialog.getf();

//    private static File f = PropertiesDialog.getf();
    
    public static int MIN=10, MAX=40;
    
    private final Dictionary<Integer,JComponent> labels = new Hashtable<Integer,JComponent>();
//      private Dictionary labels = new Hashtable();
 
    /**
     * 
     */
    public SpeedChanger(final TimeThread timeThread) {
        this.setTitle("Idle Time");
        this.setLocationRelativeTo(SgsGridModel.mainView);
        this.setResizable(false); //hs new
        ImageIcon RAPSimIcon = new ImageIcon("./Data2/RAPSim_ICON.png");
        this.setIconImage((RAPSimIcon).getImage());
        isRunning = true;
        
        labels.put(10, new JLabel("0.01s"));
        labels.put(20, new JLabel("0.1s"));
        labels.put(30, new JLabel("1s"));
        labels.put(40, new JLabel("10s"));
        
        FormLayout thisLayout = new FormLayout(
        		"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)", 
        		"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");
        getContentPane().setLayout(thisLayout);
        
        {
        	//hs: here set Text for explanation
        	//JLabel IdTi = new JLabel ("Change waiting Time. Duration is in Milliseconds.");
//        	IdTi.setVerticalTextPosition(JLabel.BOTTOM);
//        	IdTi.setHorizontalTextPosition(JLabel.LEFT);
        	//getContentPane().add(IdTi);
        	setVisible(true);

        }
        
        {
        	int value = timeThread.interSimStepMiliSec;
        	double expValue = 10.0*Math.log10(value);
        	if(expValue < MIN)
        		expValue = MIN;
        	else if(expValue > MAX)
        		expValue = MAX;
        	value = (int) Math.round(expValue);
            jSlider = new JSlider(MIN, MAX, value);		
            //jSlider.setPreferredSize(new Dimension(300, 30));
            jSlider.setMajorTickSpacing((MAX-MIN)/6);
            //jSlider.setSnapToTicks(true); //automatisches Versetzen
            jSlider.setPaintTicks(true);
            jSlider.setPaintLabels(true);
//            var dict = labels.ToDicitionary<Interger,Double>
            jSlider.setLabelTable( labels);
            
            BoundedRangeModel sliderModel = jSlider.getModel();
            setAlwaysOnTop(true);
            
            sliderModel.addChangeListener(new ChangeListener() {
                @Override
    			public void stateChanged(ChangeEvent e) {
                    BoundedRangeModel m = (BoundedRangeModel) e.getSource();
                    int expValue = m.getValue();
                    timeThread.interSimStepMiliSec = mapedValue(expValue);
                    synchronized(timeThread){timeThread.interrupt();}
                }

				
            });
            getContentPane().add(jSlider, new CellConstraints("1, 1, 4, 1, default, default"));
        }
        
        {
            JButton stopButton = new JButton("Stop");
//            stopButton.setForeground(Color.RED);
            stopButton.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				timeThread.startOrStop(false);
    				setVisible(false);
    				f.delete();
    				//timeThread.endTime = timeThread.startTime;
    				//synchronized(timeThread){timeThread.notify();}
    			}
    		});
            getContentPane().add(stopButton, new CellConstraints("1, 2, 1, 3, default, default"));
        }
        {
            JButton stepButton = new JButton("NoDelay");
            stepButton.setPreferredSize(new Dimension(75, 25));
            stepButton.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
                    jSlider.setValue(MIN);//MaxSpeed ist 0 (so schnell wie möglich) also auch anzeigen
                	timeThread.interSimStepMiliSec = 0;
                    synchronized(timeThread){timeThread.interrupt();}
    			}
    		});
            getContentPane().add(stepButton, new CellConstraints("2, 3, 1, 1, default, default"));
        }
//        {
//            JButton stepButton = new JButton("Pause");
//            stepButton.addActionListener(new ActionListener() {
//    			@Override
//    			public void actionPerformed(ActionEvent arg0) {
//                	timeThread.interSimStepSec = Integer.MAX_VALUE;
//                    synchronized(timeThread){timeThread.interrupt();}
//    			}
//    		});
//            getContentPane().add(stepButton, new CellConstraints("3, 3, 1, 1, default, default"));
//        }
        {
        	final JToggleButton pauseGo = new JToggleButton("Pause");
        	pauseGo.setPreferredSize(new Dimension(75, 25));
        	pauseGo.addActionListener( new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {

					if ( pauseGo.isSelected() ){
						timeThread.interSimStepMiliSec = Integer.MAX_VALUE;
						synchronized(timeThread){timeThread.interrupt();}
						pauseGo.setText("  Go  ");
						
					} else {
						timeThread.interSimStepMiliSec = mapedValue( jSlider.getValue() );
						synchronized(timeThread){timeThread.interrupt();}
						pauseGo.setText("Pause");
					}
						
				}
        	});
            getContentPane().add(pauseGo, new CellConstraints("3, 3, 1, 1, default, default"));            
        }
       
        
        
        {
            JButton stepButton = new JButton("Step");
            stepButton.setPreferredSize(new Dimension(75, 25));
            stepButton.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				synchronized(timeThread){timeThread.notify();}
    			}
    		});
            getContentPane().add(stepButton, new CellConstraints("4, 3, 1, 1, default, default"));
        }



		

        this.pack();
    }

	protected int mapedValue(double expValue) {
		double value = Math.pow(10, expValue/10.0 );
        int retVal = (int) Math.round(value);
		return retVal;
	}
	
	
	
	
}


