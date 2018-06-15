package sgs.controller.fileManagement;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultDocument;

import sgs.controller.GridController;
import sgs.controller.ReflectionStuff;
import sgs.controller.simulation.NetworkAnalyser;
import sgs.model.ProgramParametersSaved;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridData.GridDataEnum.Type;
import sgs.model.gridObjects.PowerTransport;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.objectModels.AbstractModel;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;

/**
 * File I/O for SGS
 * 
 * @author tobi, Kristofer Schweiger
 */
public class FileManager {

	private final GridController gridController;
	private final NetworkAnalyser networkAnalyser;
	public final SgsGridModel gridModel;

	/**
	 * Controller
	 * @param gridController
	 * used {@code this.networkAnalyser = new NetworkAnalyser(gridController);
	 */
	public FileManager(GridController gridController){
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
		this.networkAnalyser = new NetworkAnalyser(gridController);
	}
	
//	/**
//	 * Save all important XML data
//	 * @param gridFile
//	 * @param parametersFile
//	 */
//	public void save(File gridFile, File parametersFile){
//		saveGridXML(gridFile);
//		saveProgramParameters(parametersFile);
//	}

	/**
	 * Load all important XML data
	 * @param gridFile
	 * @param parametersFile
	 */
	public void load(File gridFile, File parametersFile){
		loadGridXML(gridFile);
		loadProgramParameters(parametersFile);
	}


	/**
	 * Save the objects in the grid to an XML-File (in a zip-file)
	 * @param file
	 */
	public void saveGridXML(File file) {
		Document doc = new DefaultDocument();
		Element root = doc.addElement("root");

		for (SmartGridObject o : gridModel.gridObjectList) {
			
			Element e = root.addElement(o.getEnum().name());	// store with enum name!
			e.addElement("X").addText(""+o.getX());
			e.addElement("Y").addText(""+o.getY());
			for(SingleVariable v : o.variableSet){
				e.addElement(v.name()).addText(v.value().getValue());
			}
			if(o.getModel() != null){
				AbstractModel model = o.getModel();
				Element m = e.addElement("model");
//				System.out.println("juhu");
//				System.out.println(m.getName());
				m.addAttribute("name", model.getModelName());
				for(SingleVariable v: model.getVariables()){
					m.addElement(v.name()).addText(v.getValueNumeric().getValue());
				}
				//System.out.println(m.toString());
			}
			//else System.out.println("null");
		}
		//System.out.println(doc.getStringValue());
		SafeSAX.write(file, doc);
	}
	
	/**
	 * Load the objects in the grid from an XML-File (in a zip-file)
	 * @param file
	 */
	public void loadGridXML(File file) {
		Document doc = SafeSAX.read(file, true); //set true for Zip
		Element root = doc.getRootElement();
		Iterator<?> objectIterator = root.elementIterator();

		while (objectIterator.hasNext()) {
			
			// --- Make object ---
			Element objectElement = (Element) objectIterator.next();
			String  objectName    = objectElement.getName();
			GridDataEnum objectEnum = GridDataEnum.getEnum(objectName);
			if(objectEnum==null){
				System.err.println("Error at loading object: " + objectName);
				continue;
			}
			SmartGridObject sgo = SmartGridObject.factory(objectEnum);
			//System.out.println("<"+ objectName +">");
			
			// --- Get/set object data ---
			
			for(Object o : objectElement.elements()){
				
				Element dElement = (Element)o; 
				String dataName = dElement.getName();
				String dataValue = dElement.getText();
				
				//System.out.println("\t<"+dataName+">");
				
				if(dataName.equalsIgnoreCase( "X" )){
					sgo.setX(Integer.parseInt( dataValue ));
					//System.out.println("\t\t"+dataValue);
				}
				else if(dataName.equalsIgnoreCase( "Y" )){
					sgo.setY(Integer.parseInt( dataValue ));
					//System.out.println("\t\t"+dataValue);
				}
				else if(dataName.equals("model")){
			    	if(sgo.getEnum().getMappedModel() != null){
			    		ArrayList<Class<?>> modelClasses = ReflectionStuff.getClassesForPackage(sgo.getEnum().getMappedModel());
			    		modelClasses = ReflectionStuff.getSubClasses(sgo.getEnum().getMappedModel(), modelClasses, false);
			    		for(Class<?> c: modelClasses){
			    			if(c.getSimpleName().equalsIgnoreCase(dElement.attributeValue("name"))){
								try {
									Class<?> mClass = sgo.getEnum().getMappedClass();
//									Constructor<?> constructor = c.getConstructor(sgo.getEnum().getMappedClass());
									Constructor<?> constructor = c.getConstructor(mClass);
									AbstractModel newModel = (AbstractModel)constructor.newInstance(sgo.getEnum().getMappedClass().cast(sgo));
									sgo.setModel(newModel);
									break;
								} catch (Exception e) {
									e.printStackTrace();
								}
			    			}
			    		}
			    	}
					VariableSet modelVars = sgo.getModel().getVariables();
					for(Object o1: dElement.elements()){
						Element mElement = (Element) o1;
						if( modelVars.contains(mElement.getName())  ){
//						if(modelVars.contains(mElement.getName())){
							//System.out.println("\t\t<"+mElement.getName()+">");
							//System.out.println("\t\t\t"+mElement.getText());
							//System.out.println("\t\t</"+mElement.getName()+">");
							modelVars.set(mElement.getName(), mElement.getText());
						}
					}
				}
				else {
					try{
						sgo.variableSet.set(dataName, dataValue);
						//System.out.println("\t\t"+dataValue);
					}
					catch(NullPointerException e){
						System.err.println("Error at load: " + dataName + " not contained in variableSet.");
						e.printStackTrace();
					}
				}
				//System.out.println("\t</"+dataName+">");
			}
			//System.out.println("</"+ objectName +">");
			
			gridModel.gridData[sgo.getY()][sgo.getX()]    = objectEnum.getID(Type.MAP_LRUD);
			gridModel.gridObjects[sgo.getY()][sgo.getX()] = sgo;
			gridModel.gridObjectList.add(sgo);
			int mode=2;
			if(sgo instanceof PowerTransport)
				mode=1;
			gridController.calculatePowerLines(sgo.getX(), sgo.getY(), mode);
		}
		
//		gridController.repaintData();
		gridController.colorOverlayController.repaintData();
		networkAnalyser.createBusesAndFindPaths();
	}

	
	/** TODO: in Controller? Model?
	 * 
	 * @param file
	 */
	public void saveProgramParameters(File file) {
		
		Document doc = new DefaultDocument();
		Element root = doc.addElement("root");
		Element e = root.addElement("Parameters");
		ProgramParametersSaved params = gridModel.programParameters;
			
		e.addElement("FirstStart").addText(""+params.isFirstStart());
		e.addElement("SimResultFile").addText(""+params.getSimResultFile());
			
		SafeSAX.write(file, doc);
	}
	
	/** TODO: in Controller? Model? 
	 * 
	 * @param file
	 */
	public void loadProgramParameters(File file) {
		
		ProgramParametersSaved params = new ProgramParametersSaved();
		gridModel.programParameters = params;
		Document doc;
		try{
			doc = SafeSAX.read(file, true); //set true for Zip
			Element root = doc.getRootElement();
			Iterator<?> objectIterator = root.elementIterator();
			
			// --- 
			Element element = (Element) objectIterator.next();
			if(!element.getName().equals("Parameters")) throw new RuntimeException();
			
			// --- 
			Iterator<?> valueIterator = element.elementIterator();
			
			while(valueIterator.hasNext()){		//TODO: better system for parameters
				element = (Element) valueIterator.next();
						
				if(element.getName().equals("FirstStart")){
					params.setFirstStart( Boolean.valueOf(element.getText()) );
				}
				else if(element.getName().equals("SimResultFile")){
					params.setSimResultFile( element.getText() );
				}
				else{
					throw new RuntimeException("Correct error in program parameter file. Name='"+element.getName()+"'");
				}
			}
			
			params.parametersChanged = false;
		}
		catch(Exception e1){
			System.err.println("loadProgramParameters failed, using default: "+e1.getLocalizedMessage());
			// use default parameters
			params.parametersChanged = true;
		}
		
	}
	
		

}
