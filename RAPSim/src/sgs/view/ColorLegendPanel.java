package sgs.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JToolBar;




public class ColorLegendPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final greenYellowRed_Panel gyrPanel = new greenYellowRed_Panel();
	private final blackWhiteBlack_Panel bwbPanel = new blackWhiteBlack_Panel();
	private final firebrickMidnightblue_Panel fmPanel = new firebrickMidnightblue_Panel();
	
	
	public ColorLegendPanel(){
		
		this.setPreferredSize(new Dimension(200,220));
		
		
		JToolBar colorLegendToolBar = new JToolBar();
		colorLegendToolBar.setFloatable(true);
		colorLegendToolBar.setRollover(true);
		
		
		colorLegendToolBar.add(gyrPanel);
		gyrPanel.setVisible(false);
//		colorLegendToolBar.addSeparator();
		colorLegendToolBar.add(bwbPanel);
		bwbPanel.setVisible(false);
//		colorLegendToolBar.addSeparator();
		colorLegendToolBar.add(fmPanel);
		fmPanel.setVisible(false);
		
		
		this.add(colorLegendToolBar);
		
		this.repaint();
		
		
	}
	

	
	public void repaintLegend(boolean[] overlayNumberArray) {
		
		for(int i = 0; i<overlayNumberArray.length; i++){

			if(overlayNumberArray[i] == true){
				if(i == 0){
					gyrPanel.setVisible(true);
					this.repaint();
				}
				else if(i == 1){
					bwbPanel.setVisible(true);
					this.repaint();
				}
				else if(i == 2){
					fmPanel.setVisible(true);
					this.repaint();
				}
			} else { //false
				if(i == 0){
					gyrPanel.setVisible(false);
					this.repaint();
				}
				else if(i == 1){
					bwbPanel.setVisible(false);
					this.repaint();
				}
				else if(i == 2){
					fmPanel.setVisible(false);
					this.repaint();
				}
			}
		}
		
//		this.add(colorLegendToolBar); //TODO immer ein neues legendpanel. nicht erwünscht
		
		
	}


	
}




class greenYellowRed_Panel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public greenYellowRed_Panel(){
		super();
		this.setPreferredSize(new Dimension(50, 220));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g); //http://stackoverflow.com/questions/15544549/how-does-paintcomponent-work
		
		
		if (true) {
			int h = getHeight();
			Graphics2D g2d = (Graphics2D)g;
			
			for (int i = -100; i < 100; i++) { //zeichnet die tatsächliche Legende
				g2d.setColor(Color.getHSBColor((0.3f * (i + 100)) / 200 + 0.01f, 1f, 1f)); //RED-YELLOW-GREEN
				g2d.fillRect(5, (h / 2) - i, 20, 1);
				
			}
		    
		
			//setzt Bezeichnung auf schwarz
			g2d.setColor(Color.BLACK);
			
			//Rahmen + Bezeichnung
			g2d.drawLine(5, (h / 2) - 100, 25, (h / 2) - 100);
			g2d.drawString("100%", 5, (h / 2) - 100);
			g2d.drawLine(5, (h / 2), 25, (h / 2));
			g2d.drawString("50%", 5, (h / 2));
			g2d.drawLine(5, (h / 2) + 100, 25, (h / 2) + 100);
			g2d.drawString(" 0%", 5, (h / 2) + 100);
//	
		}
	}
}

class blackWhiteBlack_Panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public blackWhiteBlack_Panel(){
		super();
		this.setPreferredSize(new Dimension(50, 220));
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g); //http://stackoverflow.com/questions/15544549/how-does-paintcomponent-work
		
		
		if (true) {
			int h = getHeight();
			Graphics2D g2d = (Graphics2D)g;
			
			  for ( int i = -100; i <100; i++){ 
	//		    	float print = 0.1f*(100 + i) / 100; 
			    	
			    	float print =  ((1f*i)/100);
			    	if( i > 0){
			    		print = 1f - print;
			    	} else print = print + 1f;
	
			    	g2d.setColor(Color.getHSBColor(0f, 0f, print));
			    	
			    	g2d.fillRect(5, (h/2) - i , 20, 1);
			    }
			  
				String uperMark = "  "+ String.valueOf( sgs.model.ProgramConstants.varCOVoltageAngle )+"°";
				String lowMark = " -"+ String.valueOf( sgs.model.ProgramConstants.varCOVoltageAngle )+"°";

			  //setzt Bezeichnung auf schwarz
				g2d.setColor(Color.BLACK);
				//Rahmen + Bezeichnung
				g2d.drawLine(5, (h / 2) - 100, 25, (h / 2) - 100);
				g2d.drawString(uperMark, 5, (h / 2) - 100);
				g2d.drawLine(5, (h / 2), 25, (h / 2));
				g2d.drawString("  0°", 5, (h / 2));
				g2d.drawLine(5, (h / 2) + 100, 25, (h / 2) + 100);
				g2d.setColor(Color.WHITE);
				g2d.drawString(lowMark, 5, (h / 2) + 100);
			}
		}
	
}

class firebrickMidnightblue_Panel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public firebrickMidnightblue_Panel(){
		super();
		this.setPreferredSize(new Dimension(50,220));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g); //http://stackoverflow.com/questions/15544549/how-does-paintcomponent-work
		
		
		if (true) {
			int h = getHeight();
			Graphics2D g2d = (Graphics2D)g;    
			
			for (int i = -100; i < 100; i++) { //zeichnet die tatsächliche Legende
				if(i==0){
					g2d.setColor(Color.WHITE);
				} else if(i<0){ //blue
					g2d.setColor(new Color((55+(2*(i+100))),(55+(2*(i+100))),255));
				} else {
					g2d.setColor(new Color(255, (255-(2*i)), (255-(2*i))));
				}
		
				g2d.fillRect(5, (h / 2) - i, 20, 1);
			}
			
			String uperMark = " "+ String.valueOf( 1d + sgs.model.ProgramConstants.varCONominalVoltage );
			String lowMark = " "+ String.valueOf( 1d - sgs.model.ProgramConstants.varCONominalVoltage );

			//setzt Bezeichnung auf schwarz
			g2d.setColor(Color.BLACK);
			//Rahmen + Bezeichnung
			g2d.drawLine(5, (h / 2) - 100, 25, (h / 2) - 100);
//			g2d.drawString(" 1.6", 5, (h / 2) - 100);
			g2d.drawString(uperMark, 5, (h / 2) - 100);
			g2d.drawLine(5, (h / 2), 25, (h / 2));
			g2d.drawString("  1", 5, (h / 2));
			g2d.drawLine(5, (h / 2) + 100, 25, (h / 2) + 100);
			g2d.setColor(Color.WHITE);
			g2d.drawString(lowMark, 5, (h / 2) + 100);
			
		}	
	}
	
}
