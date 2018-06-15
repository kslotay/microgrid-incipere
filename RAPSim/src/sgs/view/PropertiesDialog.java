package sgs.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import sgs.controller.ReflectionStuff;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.gridObjects.Connector;
import sgs.model.objectModels.AbstractModel;
import sgs.model.simulation.Path;
import sgs.model.variables.VariableSet;

/**
 *
 * @author 
 */
public class PropertiesDialog extends JDialog implements KeyListener {

    private static final long serialVersionUID = 156465465L;
    private JPanel panel;
    private JPanel tablePanel;
    private JTable table;
    private JTable modelTable;
    private JComboBox<String> modelComboBox;
    private JLabel modelHeader;
    private JTextArea modelDescription;
    private final int x;
    private final int y;
    private Container container;
    //private int numberOfLines; // Amount of lines the dialog has
    private VariableSet variableSet;
    private VariableSet modelVars;
	private final JFrame MainWindow;
    private final SmartGridObject gObj;
	private final SgsGridModel gridModel;
	private ArrayList<Class<?>> modelClasses;
	private Class<?> abstractModelClass;
	private Class<?> prosumerClass;
	
	private final JButton bClose = new JButton("Close"); //button hier, leichter events auszulösen
	private final JButton bRefresh = new JButton("refresh");
	
	public static File getf(){
		return new File ("PropD.lock");
	}
	
	/**
	 * 
	 * @param gridModel
	 * @param _x
	 * @param _y
	 */
	public PropertiesDialog(SgsGridModel gridModel, int x, int y) {
		this(gridModel, gridModel.gridObjects[y][x]);
        assert x == gObj.getX();
        assert y == gObj.getY();
	}
    /**
     * Open a PropertiesDialog for the specified object.
     * @param gridModel
     * @param _x
     * @param _y
     */
    public PropertiesDialog(SgsGridModel gridModel, SmartGridObject gObject) {
        //Locale.setDefault(Locale.US);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.MainWindow = SgsGridModel.mainView;
        this.gObj = gObject;
        this.x = gObj.getX();
        this.y = gObj.getY();
        this.gridModel = gridModel;
        
        if(!(gObj instanceof Connector)){
        	 this.container = this.getContentPane();
             this.setResizable(true);
             this.setTitle(getDialogTitle());
             
            
         	ImageIcon RAPSimIcon = new ImageIcon("./Data2/RAPSim_ICON.png");
     		this.setIconImage( (RAPSimIcon).getImage() ); 
             
             // window close:
             addWindowListener(new WindowAdapter() {
                 @Override
     			public void windowClosing(WindowEvent e) {
                     exit();
                 }
             });
             	
             	gObj.closeAllWindows();
             	
             		makePanel();
                     
                     // --- Table ---
                     variableSet = gObj.getVariableSet().getVisibles();
//                     variableSet = gObj.getData();

                     // Table with select all:
                     
                     // if a model exists for this object, load both: modelvariables and description and object variables, otherwise only the object is relevant
                     if(gObj.getEnum().getMappedModel() != null){
                     	prosumerClass = gObj.getEnum().getMappedClass();
                     	abstractModelClass = gObj.getEnum().getMappedModel();
                     	modelVars = gObj.getModel().getVariables().getVisibles();
                     	tablePanel = new JPanel();
                     	tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
                     	addObjectTable();
                     	addModelChooser();
                     	addModelTable();
                     	addModelDescription();
                     	container.add(tablePanel);
                     }
                     else{
                     	table = new JTableSGS(variableSet);
                     	table.addKeyListener(this);
                     	container.add(table, BorderLayout.CENTER);
                     }
                     //table = new JTableSGS(variableSet);
                 	//table.addKeyListener(this);
                     container.add(table.getTableHeader(), BorderLayout.NORTH);
                     //container.add(table, BorderLayout.CENTER);
                     container.add(panel, BorderLayout.SOUTH);
                     
                     this.setSize(400, 350);
                     this.setLocationRelativeTo(MainWindow);

                     this.addKeyListener(this);
                     this.setFocusable(true);
                     //this.setAlwaysOnTop(true);
                     this.requestFocus();
                     gObj.addWindow(this);
                     setVisible(true);             
        }      
    }
    
    
    
    /**
     * Make additional Panel with buttons.
     */
    private void makePanel() {
    	
    	this.panel = new JPanel();
        this.panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // --- Buttons ---
        //JButton bAddOffer = new JButton("Add Offer");
        //bAddOffer.setFocusable(false);
        //panel.add(bAddOffer);
        // add offer button:
//        bAddOffer.addActionListener(new ActionListener() {
//            @Override
//			public void actionPerformed(ActionEvent e) {
//                String price = JOptionPane.showInputDialog(null, "Enter Price for new Offer",
//                        "New Offer",
//                        JOptionPane.PLAIN_MESSAGE);
//                String amount = JOptionPane.showInputDialog(null, "Enter Amount for new Offer",
//                        "New Offer",
//                        JOptionPane.PLAIN_MESSAGE);
//                
//                Offer offer = null;
//                try{				// try to parse numbers (get offer)
//                	offer = new Offer(Double.parseDouble(price), Double.parseDouble(amount), gObj, gridModel.buses.get(gObj.getBusNumber()));
//                }
//                catch(NullPointerException | NumberFormatException ex){
//                	JOptionPane.showMessageDialog(null, "Input values are not numbers!", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//                
//                if(offer!=null){	// add offer
//                	ProSumer pros = (ProSumer) gObj;
//                	pros.getOffers().add(offer);
//                	
//                	exit();			// "reboot" properties dialog
//                	new PropertiesDialog(PropertiesDialog.this.gridModel, MainWindow, x, y);
//                }
//            }
//        });
        
//        JButton bClose = new JButton("Close");
        bClose.setFocusable(false);
        panel.add(bClose);
        bClose.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent event) {
                exit();
            }
        });
        
        
        //JButton bRefresh = new JButton("refresh");	//refresh-Button to repaint the frame
        bRefresh.setFocusable(false);
        panel.add(bRefresh);
        bRefresh.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent event){
//        		gObj.getModel().updateModel(currentTime, weather, resolution)
        		container.revalidate();
        		container.repaint();


        		
        	}
        });

	}

	/**
     * Exit dialog
     */
    private void exit() {
    	table.editCellAt(-1, -1);				// use value which is edited at the moment
        //gridObject.setData(variableSet);		// not necessary because of the (same) value objects.
        gObj.removeWindow(this);				// remove from window list and dispose of it.
        //this.dispose();						// "throw away" this window
        		//delete file
    }
    /**
     * @author bbreilin
     * create table with object-variables
     */
    private void addObjectTable(){
    	table = new JTableSGS(variableSet);
    	table.addKeyListener(this);
    	JLabel objectHeader = new JLabel(gObj.getObjectName()+"-Object");
    	tablePanel.add(objectHeader);
    	tablePanel.add(table);
    }
    
    /**
     * @author bbreilin
     * create an add JCombobox to choose a model for the object
     */
	private void addModelChooser(){
    	JPanel changeModelPanel = new JPanel();
    	changeModelPanel.setLayout(new BoxLayout(changeModelPanel,BoxLayout.LINE_AXIS));
    	changeModelPanel.setBackground(Color.WHITE);
    	changeModelPanel.add(new JLabel("actuall used model:"));
    	changeModelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,20));
    	modelHeader = new JLabel(gObj.getModel().getModelName(),gObj.getModel().getIcon(),SwingConstants.LEFT);
    	tablePanel.add(modelHeader);
    	this.modelClasses = ReflectionStuff.getClassesForPackage(abstractModelClass);
    	modelClasses = ReflectionStuff.getSubClasses(abstractModelClass, modelClasses, false);
    	ArrayList<String> modelNames = new ArrayList<String>();
    	for(Class<?> c: modelClasses){
    		modelNames.add(c.getSimpleName());
    	}
    	String[] modelNamesA = new String[modelNames.size()];
        modelComboBox = new JComboBox<String>(modelNames.toArray(modelNamesA));
    	modelComboBox.setSelectedIndex(modelClasses.indexOf(gObj.getModel().getClass()));
    	modelComboBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("unchecked")
				JComboBox<String> tmp = (JComboBox<String>)arg0.getSource();
				int classIndex = tmp.getSelectedIndex();
				Class<?> c = modelClasses.get(classIndex);
				try {
					Constructor<?> constructor = c.getConstructor(prosumerClass);
					AbstractModel newModel = (AbstractModel)constructor.newInstance((prosumerClass.cast(gObj)));
// handover of model and object values is done in instantiatin of model
//					if(gObj instanceof PowerPlant){
//						NumericValue currentPeakPower = ((PowerPlant)gObj).getModel().getRatedPower();
//						((PowerPlant)gObj).getModel().setRatedPower(currentPeakPower);
//					}
//					else if(gObj instanceof Consumer){
//						NumericValue currentPowerDemand = ((Consumer)gObj).getModel().getPowerDemand();
//						((Consumer)gObj).getModel().setPowerDemand(currentPowerDemand);
//					}
					gObj.setModel(newModel);
					refreshModel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
    		
    	});
    	changeModelPanel.add(modelComboBox);
    	tablePanel.add(changeModelPanel);
    	
    }
    /**
     * @author bbreilin
     * create table with model-variables
     */
    private void addModelTable(){
    	this.modelTable = new JTableSGS(modelVars);
     	modelTable.addKeyListener(this);
     	tablePanel.add(modelTable);
    }
    
    /**
     * @author bbreilin
     * adds a JTextArea which contains the description of the objects model 
     */
	private void addModelDescription(){
    	modelDescription = new JTextArea();
    	modelDescription.setLineWrap(true);
    	modelDescription.setText(gObj.getModel().getDescription());
    	modelDescription.setEditable(false);
    	JScrollPane descPane = new JScrollPane(modelDescription);
    	descPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    	tablePanel.add(descPane);
    }
    
    /**
     * @author bbreilin
     * refresh view after changing the model
     */
	private void refreshModel(){
    	modelVars = gObj.getModel().getVariables().getVisibles();
    	modelHeader.setText(gObj.getModel().getModelName());
    	modelHeader.setIcon(gObj.getModel().getIcon());
    	modelComboBox.setSelectedIndex(modelClasses.indexOf(gObj.getModel().getClass()));
    	((JTableSGS)modelTable).setTableModelVariables(modelVars);
    	modelDescription.setText(gObj.getModel().getDescription());
    	// TODO: update also the Object-parameters
    }
    /**
     * @return Title for dialog
     */
    private String getDialogTitle(){

    	String title = String.format("%s @(%d|%d)", gObj.getName(), x, y);

    	int busNr = gObj.getBusNumber();
    	if(busNr >= 0) {				// add busNr:
    		title += ", Bus="+busNr;
    	}
    	else {							// add paths:
    		String paths = "";
    		for(Path p : gridModel.paths){
    			if(p.pathObjects.contains(gObj)){
    				paths += String.format(" %s<>%s", p.getBus1().getBusNumber(), p.getBus2().getBusNumber());
    			}
    		}
    		if(!paths.isEmpty())
    			title += ", Paths: "+paths;
    	}

   		return title;
    }

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			exit();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	
}
