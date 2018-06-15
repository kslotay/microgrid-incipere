package sgs.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import sgs.controller.simulation.Weather;
import sgs.model.ProgramConstants;

/**
 * Panel with all elements at the top of the GUI.
 * 
 * @author Kristofer Schweiger
 */
public class TopPanel extends JToolBar{
	
	private static final long serialVersionUID = 1L;
	public JPanel buttonPanel = new JPanel(new FlowLayout(CENTER));
	public DateAndTimePanel dateAndTimePanel = new DateAndTimePanel();
	public LogoPanel logoPanel = new LogoPanel(1);

	public TopPanel(){
		this.add(dateAndTimePanel);
		this.add(buttonPanel);
//		this.add(Box.createHorizontalGlue());
		this.add(logoPanel);
	
	}
	
	/**
	 * Make JButton, add Action listener, add the button to the view.
	 * @param name		- button name
	 * @param l 		- ActionListener to add, can be null.
	 * @return JButton
	 */
    public JButton addControl_TopButton(String name, ActionListener l){
    	
    	JButton button = new JButton(name);
    	
    	
    	buttonPanel.add(button);
    	if(l != null)
    		button.addActionListener(l);
    	
    	return button;
    }
    
	/**
	 * Make JToggleButton, add Action listener, add the button to the view.
	 * @param name		- button name
	 * @param l 		- ActionListener to add, can be null.
	 * @return JButton
	 */
    public JToggleButton addControl_TopToggle(String name, ActionListener l){
    	
    	JToggleButton button = new JToggleButton(name);
    	
    	buttonPanel.add(button);
    	if(l != null)
    		button.addActionListener(l);
    	
    	return button;
    }
	
    
	/** 
	 * Update time and weather label.
	 */
	public void updateDateAndTime(Weather currentWeather, GregorianCalendar currentTime) // Updates Time + Weather - Labels
	{
		dateAndTimePanel.timeLabel.setText(ProgramConstants.df.format(currentTime.getTime()));
		dateAndTimePanel.weatherLabel1.setText(String.format("Cloudfactor: "+currentWeather.getCloudFactor() ));
		dateAndTimePanel.weatherLabel2.setText(String.format("Windspeed: "+currentWeather.getWindSpeed() ));
//		dateAndTimePanel.weatherLabel3.setText(String.format("Temperature: "+currentWeather.temperature));
	}
}

class DateAndTimePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	protected JLabel timeLabel;
	protected JLabel weatherLabel1;
	protected JLabel weatherLabel2;
//	protected JLabel weatherLabel3;

	public DateAndTimePanel(){
    	
    	super(new GridLayout(4, 1));
    	this.setPreferredSize(new Dimension(120,60)); //stop tremble from buttons
    	initDateAndTime();
    }
    
	/**
	 * Initialize time and weather label
	 * 
	 */
	private void initDateAndTime() {
		
		this.timeLabel = new JLabel();
		this.weatherLabel1 = new JLabel();
		this.weatherLabel2 = new JLabel();
//		this.weatherLabel3 = new JLabel();
		

		this.add(timeLabel);
		this.add(new JLabel() );
		this.add(weatherLabel1);
		this.add(weatherLabel2);
//		this.add(weatherLabel3);
	}
	
}


class LogoPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	ImageIcon RAPSimIcon;
	
	public LogoPanel(){
		int size=1;
		new LogoPanel( size );
	}
		
	public LogoPanel(int size){
	    	
//    	super( new FlowLayout(RIGHT) );
		this.setLayout( new FlowLayout(FlowLayout.CENTER) );
		switch(size) { 
			case 1:
				this.setPreferredSize(new Dimension(200,60)); //stop tremble from buttons
				RAPSimIcon = new ImageIcon("./Data2/RAPSim_1.png");  	
				break;
			case 2:
				this.setPreferredSize(new Dimension(400,120)); //stop tremble from buttons
				RAPSimIcon = new ImageIcon("./Data2/RAPSim_2.png");
				break;
		}
    }

	// Override
	@Override
	protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    g.drawImage( RAPSimIcon.getImage() , 0, 0, null);
	}
}
    
