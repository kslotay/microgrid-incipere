package sgs.controller;

import static sgs.model.gridData.GridDataEnum.EMPTY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import jGridMap.JGridMap;
import jGridMap.view.interfaces.PrimaryViewI;
import sgs.model.SgsGridModel;
import sgs.model.SgsGridModel.OverlayMode;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.variables.EnumPV;
import sgs.view.ColorLegendPanel;

public class ColorOverlayController {
	
	public ColorLegendPanel legendPanel;
	private final GridController gridController;
	private final SgsGridModel gridModel;
	private final PrimaryViewI view;
	private final ColorLegendPanel eastColorPanel;		// color panel on the right
	private BufferedImage img;
	
//	private int overlayNumber; 
	/*
	 * static definition of 3 colorOverlays, with start init false. More than three are not needed.
	 * so its ok to decl. and init it static
	 */
	private final boolean [] overlayNumberArray = {false, false, false};

	
	/**
	 * constructor
	 * @param gridController2
	 * @param view
	 */
	 public ColorOverlayController(GridController gridController2, PrimaryViewI view) {
		
		 this.gridController = gridController2;
		 this.gridModel = gridController.getModel();
		 this.view = view;
		 this.eastColorPanel = new ColorLegendPanel(); //call view
		 view.setJComponent(eastColorPanel, BorderLayout.EAST); //set the panel to the east of the primary view 
		 eastColorPanel.setVisible(false);
		 
		 try {
				this.img = gridModel.jGridMap.createBufferedImageFromFile("Data2/mark.png");
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

	 /**
	  * called from MenuController.java
	  * Initialize the colorOvelay, the legend and the overlay on the grid
	  * @param legendNumber
	  */
	 public void init(int legendNumber){
	 	
		 //set legend the opposite of current value
		 for(int i = 0; i<overlayNumberArray.length; i++){
			 if(i == (legendNumber-1)){ //search for the selected legend
				 //legendNumber-1 because number 0 is reserved for the "all Legends" button, so the method will never send a zero
				 overlayNumberArray[i] = !(overlayNumberArray[i]); //set status opposite to current status 
			 }
		 }
		 
		 //repaint legends
		 view.setJComponent(eastColorPanel, BorderLayout.EAST);
		 eastColorPanel.repaintLegend(overlayNumberArray); //repaint the Legend
		
		 
		 repaint();//callculates new values for the overlay and repaint it with the new given value
		 
	 }
	 

	 
	 /**
	  * check if a legend is activated
	  * if yes, set the overlayUse to booelan value true,
	  * else to false
	  */
	 public boolean checkOverlayUse(){
		 
		 boolean isArrayEmpty = true; //check if the array is empty, an totaly full with false - values 
		 
		 for(int i = 0; i<overlayNumberArray.length; i++){
			 if(overlayNumberArray[i] == true){
//				 gridModel.overlayUse = true;
				 isArrayEmpty = false;		
				 eastColorPanel.setVisible(true); //the Legends
				 eastColorPanel.repaint();	//the legends
			 }
		 }		 
		
		 if(isArrayEmpty == true){
//			 gridModel.overlayUse = false; 
		 	eastColorPanel.setVisible(false); //the Legends
		 	eastColorPanel.repaint(); //the Legends
		 	
		 	return false;
		 }
		 
		 return true;
	 }

	 //TODO zu langsam
	 public void mark(int orgX, int orgY, int [][][] gridData3D){
		
		 //es darf nicht den gleichen Index haben, sonst überzeichnet er das markieren
	     int overlayIndex = GridDataEnum.getGridDataSize()/20;		// get start index for pictures/colors, index/20 (just a random number)
	     
	     if(gridData3D[4][orgY][orgX] == overlayIndex)	 gridData3D[4][orgY][orgX] = EMPTY.getID();
	    	
	     else {
	    	 gridModel.jGridMap.addBufferedImageToScale(overlayIndex, img ,false); //handle the img over to the meth. to improve the speed
	    	 gridData3D[4][orgY][orgX] = overlayIndex;
	     }
	     
	     gridModel.jGridMap.setDataDirect(gridData3D);
		
		 
	 }

	/**
	 * method marks every single grid tile
	 */
	public void markAll() {
		
		
		
		int[][][] gridData3D = gridModel.gridData3D;
		 for (int y = 0; y < gridData3D[4].length; y++) {
		 	for (int x = 0; x < gridData3D[4][y].length; x++) {
		 		gridData3D[4][y][x] = EMPTY.getID();
		 		mark(x,y,gridData3D);
		 	}
		 }
		
		 view.repaint();
	}

	/**
	  * we mark the clicked spot with a transparent overlay
	  * @param markMore - boolean to check if more objects should be marked. Method still mark only one object at the time, but dont delete the other marks
	  * @param x - x coordination of object
	  * @param y - y coordination of object
	  */
	 public void markOne(int orgX, int orgY, boolean markMore){
		 
		 int[][][] gridData3D = gridModel.gridData3D;
		
		 if(!markMore){
			 for (int y = 0; y < gridData3D[4].length; y++) {
				 	for (int x = 0; x < gridData3D[4][y].length; x++) {
				 		gridData3D[4][y][x] = EMPTY.getID(); //andere aktive Overlays sollen vom Löschen nicht betroffen sein
				    }
				 
			 }
		 
		 }
		 
		 
	     
		mark(orgX, orgY, gridData3D);
		
		view.repaint();
	 }
	 
	 
	 /**
	  * Add Overlay color to jGridMap and ID to gridData
	  * @param x x coordinate of object
	  * @param y y coordinate of object
	  * @param z size of tile
	  * @param color calculated color
	  * @param overlayIndex unused
	  * @param gridData3D 
	  */
	private void addOverlay(int x, int y, int z, Color color, int overlayIndex, int[][][] gridData3D){
		if(color != null){
			gridModel.jGridMap.addColorToScale(overlayIndex, color, true);
			
			gridData3D[z][y][x] = overlayIndex;		// An Exception here might indicate 
			overlayIndex++;
		} 
	}
	
	
	/**
	* Classical consumption and production in red/green
	 * @param overlayData
	 */
	private Color colorOverlay1(SmartGridObject sgo){
			
				double powerDemand, powerProductionOptimal;
				double overlayColor;
				
				if((powerDemand=sgo.getPowerDemand().getReal()) > 0){
					double currentPowerConsumption = sgo.getCurrentPowerConsumption().getReal();
					overlayColor = currentPowerConsumption / powerDemand; //das ist die math. formel?
				}
				else if((powerProductionOptimal = sgo.getPeakPower().getReal()) > 0){
					double powerProduction = sgo.getPowerProduction().getReal();
					overlayColor = powerProduction / powerProductionOptimal;
				}
				else{
					return null;
				}
				
				return Color.getHSBColor(((float)overlayColor * 0.3f - 0.01f), 1.0f, 1.0f);
				//gridModel.jGridMap.addColorToScale(overlayIndex, Color.getHSBColor(((float)overlayColor * 0.3f - 0.01f), 1.0f, 1.0f), true);
			}


	/**
		 * calculate new value for result. if result is greater than 1, retun 1, or if result is smaller than zero, return zero.
		 * if we are not doing that, the formula will calculate us another color, which is not black, white, or gray shaded. 
		 * @param sgo - which object is it
		 * @return the voltageAngle value for the given sgo object
		 */
		private Color colorOverlay2(SmartGridObject sgo){
			
			double result = sgo.variableSet.get(EnumPV.voltageAngle).getValueDouble();
			
			double angleVariation = sgs.model.ProgramConstants.varCOVoltageAngle;
			
	//		System.out.println(result);
			
//			result = result/(Math.PI)*180;
			
			if(result > angleVariation){
				result = angleVariation;
			} else if(result < -angleVariation){
				result = -angleVariation;
			}
	//		System.out.println("new " + result);
			result = 1 - Math.abs(result)/angleVariation;
			result = Math.round( result*255) ;

			Color colorresult = new Color( (int)result ,  (int)result, (int)result  );
			
			return colorresult;
		}

		//auch für Nennspannung sollten alle drei berechnungen gleich sein
		private Color colorOverlay3(SmartGridObject sgo){
			
			Color colorResult = new Color(255,255,255);
			int value = 0; 		
			
			double voltageVariation = sgs.model.ProgramConstants.varCONominalVoltage;
			
//			double result = sgo.getCurrentVoltage().getValueDouble()/sgo.getCurrentVoltage().getValueDouble();
			double voltage = sgo.getCurrentVoltage().getValueDouble(); // keine nominal Voltage gefunden. current voltage genommen
			double nomVoltage = sgo.getVariableSet().get(EnumPV.nominalVoltage).getValueDouble();
			
			if (nomVoltage == 0.0){
				nomVoltage= 1.0;
			}	
			double result = voltage/nomVoltage;  
							
			if(result > 1.0)//1.0 is zero
				{
				if(result > 1+voltageVariation){
					result = 1+voltageVariation;
				}
				value = (int) ((result-1)*200/voltageVariation); 
				colorResult = new Color(255, (255-value), (255-value) );
				return colorResult;
				}
			else if(result == 1.0){
				colorResult = Color.WHITE;
				return colorResult;
			}
			else if(result < 1.0){
				if(result < 1-voltageVariation) {
					result = 1-voltageVariation;
				} 
				value = (int) ((1-result)*200/voltageVariation);
				colorResult = new Color((255-value),(255-value),255);
				return colorResult;
			}
			else 
				return null;
		}

		/**
	     * Reset overlay data in grid. (z>1)
	     * @param gridData3D
	     */
	    private void resetOverlayData(int[][][] gridData3D){ 
	    	for(int z=1; z<gridData3D.length-1; z++){
//	    		if(z!=4){ //4 is the mark, which we never want to erase 
	    			for (int y = 0; y < gridData3D[z].length; y++) {
		    			for (int x = 0; x < gridData3D[z][y].length; x++) {
		    				gridData3D[z][y][x] = EMPTY.getID();
		    			}
		    		}
//	    		}
	    		
	    	}
	    	
	    	
	    }
	    
	    /**
	     * 
	     * @param gridData3D - data for objects and overlays (z>0)
	     * @param sgo - object for overlays
	     * @param overlayIndex - current overlayIndex (unused)
	     * @return new overlayIndex (unused)
	     */
	    private int setOverlay(int[][][] gridData3D, SmartGridObject sgo, OverlayMode overlayMode, int overlayIndex){
	    	
	    	int x = sgo.getX();
	    	int y = sgo.getY();
	
	    	for(int i = 0; i<overlayNumberArray.length; i++){
	    		if(overlayNumberArray[i] == true){
	    			
	    			if(i == 0){	
	    				addOverlay(x,y,1,colorOverlay1(sgo), overlayIndex++, gridData3D);		
	    			} else if(i == 1){
	    				addOverlay(x,y,2, colorOverlay2(sgo), overlayIndex++, gridData3D);  			    		
	    			} else if(i == 2){
	    				addOverlay(x,y,3, colorOverlay3(sgo), overlayIndex++, gridData3D);
	    			}
	    		}
	    	}
	    		    	    	
	    	return overlayIndex;
	    }
	    


	    /**
	     * repaints the grid, and the legends
	     */
		public void repaint(){
				
		JGridMap jGridMap = gridModel.jGridMap;
		
        if (checkOverlayUse()) {		// if overlay should be used: gridModel.overlayUse
        	
        	int[][][] gridData3D = gridModel.gridData3D;
        	
        	resetOverlayData(gridData3D);	// reset overlay drawing data in grid
        	
        	int overlayIndex = GridDataEnum.getGridDataSize();		// get start index for pictures/colors
        	
        	for(SmartGridObject sgo : gridModel.gridObjectList){	// set Overlay for every object
        		overlayIndex = setOverlay(gridData3D, sgo, gridModel.overlayMode, overlayIndex);
        	}
        	
            jGridMap.setDataDirect(gridData3D);		// set data reference in JGM (should be already set in most cases)

            
        } else {
        	resetOverlayData(gridModel.gridData3D);
        }
        
        view.repaint();		// for Color overlay bar.
        //repaint also repaints data from pirmary controller
		
	 }
	
	


	/**
	 * this methode was called from other classes, to repaint the grid.
	 * they dont know which overlay is actually in use, so they also dont know the number which is needed in the repaint() methode.
	 * 
	 */
	public void repaintData() {
		
		for(int i = 0; i<overlayNumberArray.length; i++){
			repaint();
		}
	
	}


	/**
	 * we save all checked overlay
	 * @return an array of the overlays by number
	 */
	public boolean[] getOverlayNumberArray() {
		
		return overlayNumberArray;
	}

	
	/**
	 * this method inis all legends and their overlays.
	 */
	public void initAll() {
		for(int i = 0; i<overlayNumberArray.length; i++){
			init(i+1);
		}
		
		 eastColorPanel.repaintLegend(overlayNumberArray); //repaint the Legend
			
	}
	
}
