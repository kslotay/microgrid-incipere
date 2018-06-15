package sgs.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sgs.controller.fileManagement.csvConstruction.CSVController;
import sgs.controller.simulation.TimeThread;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;

import com.toedter.calendar.JCalendar;
/**
 * Gets Simulation options.
 * Writes SgsGridModel.saveSimResultsToFile
 *
 * @author Tobi, Kristofer Schweiger, Huber Sabrina
 */
public class SimOptionsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private boolean confirmed = false;
    private int resolution = 0;
    private static String startTimeString = "";
    private static String endTimeString = "";

    private GregorianCalendar startTime = new GregorianCalendar();
    private GregorianCalendar endTime = new GregorianCalendar();
    private GregorianCalendar tempCalendar;
    private final TimeThread timeThread;
    private final SgsGridModel gridModel;
    
    
    //GEN-LAST:event_endDateTextFieldMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    
    private JLabel endDateLabel;
    private JLabel startDateLabel;
    private JLabel resolutionLabel;
    private JTextField endDateTextField; 
    private JTextField resolutionTextField;
    private JTextField saveToTextField;
    private JTextField startDateTextfield;
    
    private JCheckBox gatherResultsCheckBox;
    private JCheckBox realTimeCheckBox;
//    private JRadioButton newFileButton = new JRadioButton();
//    private JRadioButton oldFileButton = new JRadioButton();
    private JTextField fileTextField = new JTextField();
   
   
    public JButton startSimButton;
    private JPanel timePanel;
    private JPanel resultsPanel;
    
  
	private final CSVController csvController;
	/**
     * 
     * @param timeThread
     * @param gridModel
	 * @param csvController 
     */
    public SimOptionsDialog(TimeThread timeThread, SgsGridModel gridModel) {
    	super(SgsGridModel.mainView, "Simulation Options", true);
    
    	this.timeThread = timeThread;
    	this.gridModel = gridModel;
    	this.setLocationRelativeTo(SgsGridModel.mainView);
    	this.csvController = timeThread.csvController;
    	
        JPanel optionsPanel = initComponents();
       
        this.add(optionsPanel);
     
        
        this.getRootPane().setDefaultButton(startSimButton);
        this.pack();
        this.setVisible(true); // blocks until panel is closed
    }
    
   
    /**
     * 
     * @return options panel, the view of the Simulation Options 
     */
    private JPanel initComponents() {

    	final JPanel optionsPanel = new JPanel();
    	
    	timePanel = new javax.swing.JPanel();
        startDateLabel = new javax.swing.JLabel();
        endDateLabel = new javax.swing.JLabel();
        startDateTextfield = new javax.swing.JTextField();
        endDateTextField = new javax.swing.JTextField();
        resolutionLabel = new javax.swing.JLabel();
        resolutionTextField = new javax.swing.JTextField();
        realTimeCheckBox = new javax.swing.JCheckBox();
        resultsPanel = new javax.swing.JPanel();
        gatherResultsCheckBox = new javax.swing.JCheckBox();
        saveToTextField = new javax.swing.JTextField();
        
        startSimButton = new javax.swing.JButton();

        timePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Time Settings"));

        startDateLabel.setText("Start Date:");

        endDateLabel.setText("End Date:");

        /**
         * @deprecated code for the Time Settings. choosenTime should be saved
         * static variable startTimeString and endTimeString will save the choosen Time settings.
         * if nothing was decided yet, if will take a default value
         * @code{
         * 
         * startDateTextfield.setText("2013.01.01 - 12:30");
	        startDateTextfield.addMouseListener(new java.awt.event.MouseAdapter() {
	            @Override
				public void mouseClicked(java.awt.event.MouseEvent evt) {
	                startDateTextfieldMouseClicked(evt);
	            }
	        });
	
	        endDateTextField.setText("2013.12.31 - 19:00");
	        endDateTextField.addMouseListener(new java.awt.event.MouseAdapter() {
	            @Override
				public void mouseClicked(java.awt.event.MouseEvent evt) {
	                endDateTextFieldMouseClicked(evt);
	            }
	        });
	         * }
         */
       
//        
        if(startTimeString.isEmpty() && endTimeString.isEmpty()){
        	startTimeString = "2014.01.01 - 12:30"; //also changed the year to 2014
        	endTimeString = "2014.12.31 - 19:00";
        } 
        
        startDateTextfield.setText(startTimeString); 
        startDateTextfield.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
                startDateTextfieldMouseClicked(evt);
//                startTimeString = start;
            }
        });

        endDateTextField.setText(endTimeString);
        endDateTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
                endDateTextFieldMouseClicked(evt);
//                endTimeString = end;
            }
        });
        

       

        resolutionLabel.setText("Resolution (minutes):");

        resolutionTextField.setText("60");

        realTimeCheckBox.setSelected(true);
        realTimeCheckBox.setText("Change Speed:"); //change from Realtime to "Change Speed"
        realTimeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
  
        realTimeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                realTimeCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout timePanelLayout = new javax.swing.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timePanelLayout.createSequentialGroup()
                        .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(timePanelLayout.createSequentialGroup()
                                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startDateLabel)
                                    .addComponent(endDateLabel))
                                .addGap(18, 18, 18)
                                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(startDateTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(endDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(timePanelLayout.createSequentialGroup()
                                .addComponent(resolutionLabel)
                                .addGap(18, 18, 18)
                                .addComponent(resolutionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(timePanelLayout.createSequentialGroup()
                        .addComponent(realTimeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                        .addGap(18, 18, 18))))
        );

        timePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endDateLabel, resolutionLabel, startDateLabel});

        timePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endDateTextField, resolutionTextField, startDateTextfield});

        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateLabel)
                    .addComponent(startDateTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endDateLabel)
                    .addComponent(endDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resolutionLabel)
                    .addComponent(resolutionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addComponent(realTimeCheckBox))
        );
        

        fileTextField.setEnabled(false);
//        String s = csvController.csvModel.getFileName();
        fileTextField.setText(csvController.getCSVFile().toString());
            
        resultsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        gatherResultsCheckBox.setText("Gather Results");
        gatherResultsCheckBox.addActionListener(new java.awt.event.ActionListener(){
        	public void actionPerformed(java.awt.event.ActionEvent evt){
        		     		
        		if(gatherResultsCheckBox.isSelected()){

        			if(csvController.isHaveData() == false){
//        			if(csvController.dataColl.savedTree == null){	
        			
        				Object[] options = {"change results", "cancel"};
        				
        				String title = "Warning";
        				String message = "You haven't specified any data for the file!"; 
        				
        				int choice = JOptionPane.showOptionDialog(optionsPanel, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        				
        				if(choice == 0 ){
        					
//        					ResultsCSVView csvOptions = new ResultsCSVView(gridModel);
        					csvController.initRest();
        					closeSimOptionsDialog();
//        	    			csvOptions.setVisible(true);

        				}else{ 
        					//change state of checkbox
        					gatherResultsCheckBox.setSelected(false);
        					
        				}
        				
        			} else {
//        				setRadioButtonEnabled(true); 
        				//TODO display the file Name
        				fileTextField.setEnabled(true);
        				
        			}
        		} else {
//        			setRadioButtonEnabled(false);
        			fileTextField.setEnabled(false);
        		}
        		
        	}
        });

//        setRadioButtonEnabled(false);
//
//        newFileButton.setText("generate new File");
//        newFileButton.setSelected(true);
//        
//        newFileButton.addActionListener(new java.awt.event.ActionListener(){
//        	public void actionPerformed(java.awt.event.ActionEvent evt){
//        		csvController.csvModel.setNewFileName = true ; //the old file name with a version nr. 
//        	}
//        });
//        
//        
//        oldFileButton.setText("overwrite old File");
//        
//        oldFileButton.addActionListener(new java.awt.event.ActionListener(){
//        	public void actionPerformed(java.awt.event.ActionEvent evt){
//        		csvController.csvModel.setNewFileName = false;
//        	}
//        });
//        
//        ButtonGroup radioButtonGroup = new ButtonGroup();
//        radioButtonGroup.add(newFileButton);
//        radioButtonGroup.add(oldFileButton);


        javax.swing.GroupLayout resultsPanelLayout = new javax.swing.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(gatherResultsCheckBox)
                        .addComponent(fileTextField)
//                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER))
//                        .addComponent(newFileButton)
//                        .addComponent(oldFileButton)
//                        
                 		)
                .addContainerGap())
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addComponent(gatherResultsCheckBox)
                .addComponent(fileTextField)
//                .addGroup(resultsPanelLayout.createSequentialGroup()
//                		.addComponent(newFileButton)
//                		.addComponent(oldFileButton)                        
//                		)
      ));

        
        
        
        startSimButton.setText("Start Simulation");
        startSimButton.addActionListener(new java.awt.event.ActionListener() {
           
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
            	//csvController.checkEnumSelected();
            	//csvController.run();
//            	if(gatherResultsCheckBox.isSelected() && (csvController.csvModel.csv.exists() == true)){
//            		csvController.run();
//            	} else {
//            		
//            		String title = "Error";
//        			String message = "Missing Directory and Name for CSV-File!"; 
//        			
//        			JOptionPane.showMessageDialog( optionsPanel, message, title, JOptionPane.ERROR_MESSAGE);
//            	}
				if(gatherResultsCheckBox.isSelected()){
//					csvController.setCSVSpecifications_filename(fileTextField.getText());
					csvController.setCSVSpecifications(new File(fileTextField.getText()));
					csvController.run();
				}
				
				
				startTimeString = startDateTextfield.getText();
				endTimeString = endDateTextField.getText();
				
                startSimButtonActionPerformed(evt);
                
            }
        });

        
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(timePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(startSimButton)
                        .addGap(69, 69, 69))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(timePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startSimButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
		return optionsPanel;
    }// </editor-fold>//GEN-END:initComponents

  

//    private void setRadioButtonEnabled(boolean visible) {
//			
//    	newFileButton.setEnabled(visible);
//    	oldFileButton.setEnabled(visible);
// 
//    }


    /**
     * it should close the Simulations Options Dialog Window
     */
    private void closeSimOptionsDialog() {
    	this.setVisible(false);
    }



	public void setGatherResults(){
		
		if(gatherResultsCheckBox.isSelected()){
		}
	}



	private void realTimeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realTimeCheckBoxActionPerformed
    }//GEN-LAST:event_realTimeCheckBoxActionPerformed

    private void startSimButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSimButtonActionPerformed
    	
        confirmed = true;
//        startTime = new GregorianCalendar();
//        endTime = new GregorianCalendar();
        try {
            startTime.setTime(ProgramConstants.df.parse(startDateTextfield.getText()));
            endTime.setTime(ProgramConstants.df.parse(endDateTextField.getText()));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        timeThread.startTime = startTime;
        timeThread.currentTime = startTime;
        timeThread.endTime = endTime;
        
        timeThread.realTime = realTimeCheckBox.isSelected();
        resolution = Integer.parseInt(resolutionTextField.getText());
        gridModel.saveSimResultsToFile = gatherResultsCheckBox.isSelected();
        gridModel.programParameters.setSimResultFile(saveToTextField.getText());
        
        this.setVisible(false);
    }//GEN-LAST:event_startSimButtonActionPerformed

    private void startDateTextfieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startDateTextfieldMouseClicked
        changeDateDialog();
        startTime = tempCalendar;
        startDateTextfield.setText(ProgramConstants.df.format(startTime.getTime()));
    }//GEN-LAST:event_startDateTextfieldMouseClicked

    private void endDateTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_endDateTextFieldMouseClicked
        changeDateDialog();
        endTime = tempCalendar;
        endDateTextField.setText(ProgramConstants.df.format(endTime.getTime()));
    }
    
 

    /**
     * @return the confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * @return the startTime
     */
    public GregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public GregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * this panel modifiers the changes of the Date. 
     */
    private void changeDateDialog() {
        final JDialog jDialog = new JDialog(SgsGridModel.mainView, "Choose Day", true);
        BorderLayout bl = new BorderLayout();
        bl.setVgap(0);
        jDialog.setLayout(bl);
        final JCalendar jc = new JCalendar(timeThread.currentTime);
        jc.setWeekOfYearVisible(false);
        jc.setPreferredSize(new Dimension(200, 130));
        jc.setVisible(true);
        JPanel bottom = new JPanel();
        GridLayout bottomLayout = new GridLayout(3, 1);
        bottomLayout.setVgap(1);
        bottom.setLayout(bottomLayout);
        JPanel hourContainer = new JPanel();
        JPanel minuteContainer = new JPanel();
        GridLayout gl = new GridLayout(1, 2);
        gl.setHgap(5);
        hourContainer.setLayout(gl);
        minuteContainer.setLayout(gl);
        bottom.setPreferredSize(new Dimension(200, 70));

        JLabel hour = new JLabel(" Hours    (0-24):");
        final TextField hourOfDay = new TextField("12");
        JLabel minute = new JLabel(" Minutes (0-59):");
        final TextField minuteOfDay = new TextField("30");
        hourContainer.add(hour);
        hourContainer.add(hourOfDay);
        minuteContainer.add(minute);
        minuteContainer.add(minuteOfDay);

        bottom.add(hourContainer);
        bottom.add(minuteContainer);
        JButton save = new JButton("Save");
        bottom.add(save);
        save.setPreferredSize(new Dimension(200, 20));
        jDialog.add(jc, BorderLayout.CENTER);
        jDialog.add(bottom, BorderLayout.PAGE_END);
        jDialog.setSize(200, 250);
        jDialog.setLocationRelativeTo(SgsGridModel.mainView);

        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int hours = Integer.parseInt(hourOfDay.getText());
                int minutes = Integer.parseInt(minuteOfDay.getText());
                GregorianCalendar gc;
                gc = (GregorianCalendar) jc.getCalendar();
                if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
                    gc.set(GregorianCalendar.HOUR_OF_DAY, hours);
                    gc.set(GregorianCalendar.MINUTE, minutes);
                    tempCalendar = gc;
                    jDialog.setVisible(false);
                    jDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(jDialog, "Time input not correct", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        jDialog.setVisible(true);
    }

    /**
     * @return the resolution
     */
    public int getResolution() {
        return resolution;
    }
}
