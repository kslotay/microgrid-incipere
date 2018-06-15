package sgs.model.gridData;

import static sgs.model.ProgramConstants.dataPath;

import java.io.File;
import java.util.ArrayList;

import sgs.model.gridObjects.Connector;
import sgs.model.gridObjects.CustomConsumer;
import sgs.model.gridObjects.CustomPowerPlant;
import sgs.model.gridObjects.FossilFuelPowerPlant;
import sgs.model.gridObjects.GridPower;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.gridObjects.SolarPowerPlant;
import sgs.model.gridObjects.WindTurbinePowerPlant;
import sgs.model.objectModels.AbstractCustomConsumerModel;
import sgs.model.objectModels.AbstractCustomPowerPlantModel;
import sgs.model.objectModels.AbstractFossilFuelPowerPlantModel;
import sgs.model.objectModels.AbstractModel;
import sgs.model.objectModels.AbstractSolarPowerPlantModel;
import sgs.model.objectModels.AbstractWindPowerPlantModel;

/**
 * Holds:
 * 	> all button images
 *  	-> Control buttons
 *  	-> Buttons for GridObjects
 * 
 * 	> all images for JGM
 * 
 * Ideas for future: 
 * 	- Buffer/Store images or image information in Enum
 * 	  for faster start up.
 * 	- Better automated system for handling different
 *    images for a single grid object. (encapsulate in a new class)
 * 
 * @author Kristofer Schweiger
 */
public enum GridDataEnum {
	
	/*
	 * WARNING: Order of Enums can be important for algorithm.
	 * Define additional tools first, add new objects last.
	 * 
	 * Grid Data Objects:
	 *   Syntax: ENUM_NAME ("BasicPictureName", "ToolTip")
	 */
	// --- Special: ---
	EMPTY (null,null),								// EMPTY defines an empty field with no object.
	// --- Tools: ---
	MOVE_GEAR ("Move", "Use this to Navigate"),		// Tool for moving
	DELETE_GEAR ("Delete", "Delete Objects"),		// Tool for deleting
	//COPY_GEAR("Copy", "copy one single Object"),
	// --- Objects: ---
	PLINE ("PLine", "Resistance of a powerline", PowerLine.class),
	SPLINE ("sPline", "Lossless Connectors", Connector.class),
	// -
	CUSTOM_CONSUMER ("House", "Residential consumption", CustomConsumer.class, AbstractCustomConsumerModel.class), 
	// -
	//POWER_PLANT("PowerPlant", "produces Power using a PowerPlantModel", PowerPlant.class),
	SOLAR_POWER_PLANT ("SolarPowerPlant", "PV Generation", SolarPowerPlant.class, AbstractSolarPowerPlantModel.class),
	WIND_TURBINE_POWER_PLANT ("WindTurbinePowerPlant", "Windturbine", WindTurbinePowerPlant.class, AbstractWindPowerPlantModel.class),
	FOSSIL_FUEL_POWER_PLANT ("FossilFuelPowerPlant", "Fuels Generator", FossilFuelPowerPlant.class , AbstractFossilFuelPowerPlantModel.class),
	CUSTOM_POWER_PLANT ("CustomPowerPlant", "Any other Powerplant", CustomPowerPlant.class, AbstractCustomPowerPlantModel.class),
	// -
	GRID_POWER ("GridPower", "Grid connection: Marks Reference-Bus", GridPower.class);
	;
	
	/** 
	 * Stores name data and tooltip. 
	 */
	private String imgName, imgExtension, toolTip;
	/**  
	 * Class mapped to this enum instance.
	 */
	private Class<? extends SmartGridObject> mappedClass = null;
	private Class<? extends AbstractModel> mappedModel = null;
	
	
	/**
	 * ID_START ... ID of the first GridDataEnum.
	 * ID_RANGE ... number of possible Types/Pictures for every GridDataEnum.
	 */
	public static final int ID_START=0, ID_RANGE=Type.values().length;
	
	
	/** 
	 * @return Number of grid data indices used with regard to all different types.
	 */
	public static int getGridDataSize(){
		int gridDataEnums = GridDataEnum.values().length;
		int types = Type.values().length;
		
	
		return gridDataEnums*types;
	}
	
	/**
	 * Full constructor
	 * @param imgName
	 * @param imgExtension
	 * @param toolTip
	 */
	GridDataEnum(String imgName, String imgExtension, String toolTip){
		this.imgName	  = imgName;
		this.imgExtension = imgExtension;
		this.toolTip      = toolTip;
	}
	
	
	
	/**
	 * Simple constructor: png is default file extension.
	 */
	GridDataEnum(String imgName, String toolTip){
		this(imgName, "png", toolTip);
	}
	
	
	
	/**
	 * @param imgName
	 * @param toolTip
	 * @param c
	 */
	GridDataEnum(String imgName, String toolTip, Class<? extends SmartGridObject> c){
		this(imgName, "png", toolTip);
		mappedClass = c;
		//GridDataTree.classToEnumMap.put(c, this);		//normally not ready in time. Done externally.
		
//		//TODO:
//		
//		//reset();
//		
//		//...GridDataEnum
//		selectClass(SmartGridObject.class)
//		setProperties(true,true, PVEnum.Voltage, PVEnum.Resistance);
//		
//		//finish();
		
	}
	
	
	GridDataEnum(String imgName, String toolTip, Class<? extends SmartGridObject> c, Class <? extends AbstractModel> m){
		this(imgName, "png", toolTip);
		mappedClass = c;
		mappedModel = m;
		//GridDataTree.classToEnumMap.put(c, this);		//normally not ready in time. Done externally.
		
//		//TODO:
//		
//		//reset();
//		
//		//...GridDataEnum
//		selectClass(SmartGridObject.class)
//		setProperties(true,true, PVEnum.Voltage, PVEnum.Resistance);
//		
//		//finish();
		
	}
	/**
	 * Map elements can be different depending on surrounding elements.
	 * > An icon must always exist.
	 * > Pure control icons do not have a map element.
	 * > The default and minimum definition for a map elements is the 
	 *   LeftRightUpDown type 'MAP_LRUD'.
	 * 
	 * Naming and order:
	 * 	1.Left, 2.Right, 3.Up, 4.Down
	 */
	public enum Type{
		
		ICON,	  /* ...the icon for the button */
		MAP_LRUD, /* ...the default&minimum map type */
		MAP_LRU, MAP_LRD, MAP_LUD, MAP_RUD, /* ...combinations of three directions */
		MAP_LR, MAP_UD, MAP_LU, MAP_RU, MAP_RD, MAP_LD /* ...combinations of two directions */
		/* ...single directions not used */
	}
	
	/**
	 * Get the Base-ID of the enum.
	 * 
	 * @return the base ID for this group of images, ordinal*ID_RANGE+ID_START.
	 */
	public int getID(){
		return super.ordinal()*ID_RANGE+ID_START;
	}
	/**
	 * @return maximum possible ID for this GridDataEnum
	 */
	public int getMaxID(){
		return getID() + ID_RANGE-1;
	}
	
	/**
	 * Test if this Enum is equal and defined before the other.
	 * ID is used as parameter for enabling the usage with any ID obtained before from an Enum.
	 * 
	 * @param otherID - the ID of another GridEnum obtained via a getter function.
	 * @return true if this Enum includes otherID or is defined before.
	 */
	public boolean lessOrEqual(int otherID){ 
		
		int myID   = getID();
		boolean is = myID <= otherID;
		return is;
	}
	
	/**
	 * Test if this Enum is equal and defined after the other.
	 * ID is used as parameter for enabling the usage with any ID obtained before from an Enum.
	 * 
	 * @param otherID - the ID of another GridEnum obtained via a getter function.
	 * @return true if this Enum includes otherID or is defined after it.
	 */
	public boolean greaterOrEqual(int otherID){
		
		int myID   = getMaxID();
		boolean is = myID >= otherID;
		return is;
	}
	
	/**
	 * @param id
	 * @return true if this GridDataEnum includes this unique ID.
	 */
	public boolean includesID(int id){
		boolean inc = ( getID()/ID_RANGE == id/ID_RANGE );
		// equivalent to:
		// inc = id>=getID() && id<=getMaxID()
		return inc;
	}
	
	/**
	 * @param type
	 * @return the unique ID for the specified Type/picture of this GridDataEnum.
	 */
	public int getID(Type type){
		
		int ordinal = getID();
		ordinal += type.ordinal();
		
		return ordinal;
	}
	
	/**
	 * @param type
	 * @return path + fullFileName
	 */
	public String getImage(Type type){
		String img = dataPath + imgName+'_'+type+'.'+imgExtension;
		return img;
	}
	
	/**
	 * @return path + fullIconFileName
	 */
	public String getIcon(){
		return getImage(Type.ICON);
	}
	
	/**
	 * @return predefined tool tip text.
	 */
	public String getToolTip(){
		return toolTip;
	}
	
	/**
	 * @return a array with all types of EXISTING images.
	 */
	public ArrayList<Type> getExistingTypes(){
		
		Type[] types = Type.values();
		ArrayList<Type> list = new ArrayList<Type>(types.length-1);	// Array list with max length
		
		for(Type type : types){
			
			if(exists(type))
				list.add(type);
		}
		
		return list;
	}
	
	/**
	 * @return true if this is also a map element. (map images exist)
	 */
	public boolean isMapElement(){
		
		boolean isMapElem = exists(Type.MAP_LRUD);
		
		// non map elements have no other type/images
		assert isMapElem || !isMapElem && getExistingTypes().size()==1;
		
		return isMapElem;
	}
	
	/**
	 * @param type
	 * @return true if image file with this type exists.
	 */
	public boolean exists(Type type){
		File f = new File( getImage(type) );
		return f.exists();
	}
	
	/**
	 * @return number of existing image files for this Enum.
	 */
	public int nrOfImages(){
		
		Type[] types = Type.values();
		int num = 0;
		
		for(Type type : types){
			if(exists(type))
				num++;
		}
		
		return num;
	}
	
	/**
	 * @param name
	 * @return Enum corresponding to name or null.
	 */
	public static GridDataEnum getEnum(String name){
		GridDataEnum[] values = GridDataEnum.values();
		
		for(GridDataEnum enu : values){
			if(enu.name().equalsIgnoreCase(name)){
				return enu;
			}
		}
		return null;
	}
	
	/**
	 * @param c
	 * @return
	 */
	public static GridDataEnum getMappedEnum(Class<? extends SmartGridObject> c) {
		GridDataEnum enu = GridDataTree.classToEnumMap.get(c);
		return enu;
	}
	public static Class<? extends SmartGridObject> getMappedClass(GridDataEnum enu) {
		return enu.mappedClass;
	}
	
	public Class<? extends SmartGridObject> getMappedClass() {
		return mappedClass;
	}
	public Class<? extends AbstractModel> getMappedModel() {
		return mappedModel;
	}
}
