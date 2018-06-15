package sgs.controller.simulation;

import java.util.Iterator;
import java.util.LinkedList;

import sgs.controller.GridController;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;
import testing.Out_;


/**
 * This class helps to find Buses and Paths as dynamically as possible.
 * It uses the {@code gridModel.lockBusesPaths()} method for not manipulating the objects at the same time as they are used.
 * 
 * If the specific GridObject uses an attribute from {@code gridModel.resistanceAttributes}, "uses" defined by VariableSet.isUsed(name),
 * then it is handled as a resistor.
 * 
 * @author tobi, Schweiger
 */
public class NetworkAnalyser {

	//private static final Comparator<Bus> nettoPowerComparator = new BusNettoPowerComparator();
	
	private final GridController gridController;
	private final SgsGridModel gridModel;
	
	public NetworkAnalyser(GridController gridController){
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
	}
	
    /**
     * Locks Buses and Paths.
     * 
     */
    public void createBusesAndFindPaths() {
    	
    	Out_.pl("-- createBusesAndFindPaths() --");
    	
       	gridModel.lockBusesPaths();		// --- LOCK --- --------------
       	
       	for(SmartGridObject sgo : gridModel.gridObjectList)		// reset bus numbers at objects
       		sgo.setBusNumber(-1);								// -
       	
       	LinkedList<Bus> busList = findBuses();					// get bus list
       	gridModel.buses = busList;								// store buses in model
		LinkedList<Path> pathList = findPaths(new LinkedList<Bus> (busList));	// get path list
		gridModel.paths = pathList;								// store paths in model
//        Collections.sort(gridModel.buses, nettoPowerComparator);
        
        Out_.pl("Paths after create: ");
        Out_.pl(gridModel.paths);
		
        valueUpdate(false);
        
        gridModel.unlockBusesPaths();	// --- UNLOCK --- --------------
    }
    
    /**
     * @param withLock - Locks Buses and Paths if 'withLock'.
     * @see {@link #valueUpdate()}
     */
    private void valueUpdate(boolean withLock) {
    	
    	if(withLock)
    		gridModel.lockBusesPaths();		// --- LOCK --- --------------
    	
        for (Bus b : gridModel.buses) {
            b.refreshValues();
        }
        for (Path p : gridModel.paths) {
            p.refreshValues();
        }
        
        if(withLock)
        	gridModel.unlockBusesPaths();	// --- UNLOCK --- --------------
    }
    
    /**
     * Similar to createBusesAndFindPaths(), but does updates for new values only.
     */
    public void valueUpdate() {
    	valueUpdate(true);
    }

    /**
     * @return list with all buses
     */
    private LinkedList<Bus> findBuses() {
    	LinkedList<SmartGridObject> allBusObjects = new LinkedList<SmartGridObject>(gridModel.gridObjectList);	// shallow copy
    	LinkedList<Bus> buses = new LinkedList<>();

    	// --- remove resistors from list ---
    	Iterator<SmartGridObject> iterator = allBusObjects.iterator();
    	while (iterator.hasNext()) {
    		SmartGridObject sgo = iterator.next();
    		if (isResistor(sgo)) {
    			iterator.remove();
    		}
    	}
    	//  System.out.println( "this bus contains" + clone.getLast() + "buses" );

    	// --- create buses ---
    	while (!allBusObjects.isEmpty()) {
    		buses.add( createBus(allBusObjects.getFirst(), allBusObjects, buses.size()) );	// bus number is index
    	}
    	//  System.out.println( "this grid has " + busNumber + " buses " );
    	
    	return buses;
    }
    
    /**
     * @param sgo
     * @return true if grid object uses defined resistance attributes.
     * @see {@link #gridModel.resistanceAttributes, gridModel.resistanceAttributes}
     */
    private boolean isResistor(SmartGridObject sgo){
    	
    	for(EnumPV name : gridModel.resistanceAttributes){
    		if( sgo.variableSet.isUsed(name) ){
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Create a new bus.
     * @param startSgo
     * @param allBusObjects
     * @param busNumber
     * @return Bus with 'busNumber' and all 'allBusObjects' connected to 'startSgo' including 'startSgo'
     */
    private Bus createBus(SmartGridObject startSgo, LinkedList<SmartGridObject> allBusObjects, int busNumber) {
    	
    	allBusObjects.remove(startSgo);	// do not use start object
    	LinkedList<SmartGridObject> busObjects = gridController.getAllNeighborsFrom(allBusObjects, startSgo);
    	busObjects.add(startSgo);		// include start object
    	
    	Bus bus = new Bus(busNumber, busObjects, gridModel.busVariableCollection);
    	for (SmartGridObject sgo : bus.getGridObjects()) {
    		sgo.setBusNumber(busNumber);
    	}
    	
    	Out_.pl("> Bus created: "+bus.getBusNumber()+", "+bus.busGridObjects.toString());
    	return bus;
    }

    /**
     * @param buses
     * @return all paths for buses
     */
    private LinkedList<Path> findPaths(LinkedList<Bus> buses) {
        LinkedList<Path> allPaths = new LinkedList<>();
        
        for (int i=0; i<buses.size(); i++) {
        	Bus b = buses.get(i);
        	
        	LinkedList<Path> newPaths = findPaths(b);
        	
        	for(Path p : newPaths){				// Add only new paths:
        		if(p.getBus1().getBusNumber()>=i){	// buses in path are sorted (bus1, bus2)
        			allPaths.add(p);			// add iff connection contains new/higher path (>=i), other connections already added before. (bidirectional)
        		}
        	}
        }
        return allPaths;
    }
    
    /**
     * Finds all paths for the Bus 'b'
     * @param b
     * @return all paths for one bus
     */
    private LinkedList<Path> findPaths(Bus b){
    	
    	LinkedList<Path> toCheck = new LinkedList<Path>();			// unfinished paths
    	LinkedList<Path> foundPaths = new LinkedList<Path>();		// finished paths
    	
    	// --- prepare: --- ---------
    	{
    	    LinkedList<SmartGridObject> notVisited = new LinkedList<SmartGridObject>(gridModel.gridObjectList);
    		notVisited.removeAll(b.getGridObjects());					// objects which can be visited. Exclude start objects (objects in bus).
    		
        	LinkedList<SmartGridObject> nbs = gridController.getDirectNeighborsFrom(notVisited, b.busGridObjects);
        	boolean multiplePaths = nbs.size() > 1;		// split into multiple paths?
        	
        	for(SmartGridObject nb : nbs){
        		Path path = new Path(gridModel.pathVariableCollection);	// Create new Path with starting with 'b'
        		path.addBus(b);											// -
        		path.pathObjects.add(nb);								// add first path object
        		assert nb.getBusNumber()<0;								// path is not part of a bus
        		path.notVisitedTmp = multiplePaths? (new LinkedList<SmartGridObject>(notVisited)):notVisited;	// each path has its own list of objects if can contain. Copy for multiple paths.
        		toCheck.add(path);										// -> incomplete path is added to list
        	}
    	}

    	// --- get paths: --- ---------
    	while(!toCheck.isEmpty()){
    		Path path0 = toCheck.removeLast();		// get (+remove) last unfinished path. 
    		LinkedList<SmartGridObject> nbs;		// direct neighbors for continuing path:
    		{
    			SmartGridObject lastPathObject = path0.pathObjects.getLast();	// get last object in this path
    			nbs = gridController.getDirectNeighborsFrom(path0.notVisitedTmp, lastPathObject);
    		}
    		//Out_.p("foundPaths: "); Out_.p(foundPaths); Out_.pl();
    		//Out_.p("toCheck: "); Out_.p(toCheck); Out_.pl();
    		//Out_.pl("Path: " + path);

    		if( !nbs.isEmpty() ){
    			boolean multiplePaths = nbs.size() > 1;		// split into multiple paths?

    			for(SmartGridObject nb : nbs){				// for all direct neighbors of last path object...
    				Path p = multiplePaths? path0.copy():path0;	// copy path if it is split to multiple paths
    				p = handleNeighbor(p, nb);					// add path object / add bus at end of path
    				handleLists(p, toCheck, foundPaths);		// add to unfinished or finished path list
    			}
    		}
    		else{
    			// hanging path -> no second bus
    			// path to itself (will not find own bus)
    		}
    	}
    	
    	//Out_.pl("---");
		//Out_.p("foundPaths: "); Out_.p(foundPaths);
		//Out_.pl("---------------");
    	return foundPaths;
    }
    
    /**
     * Add path to correct list or discard it.
     * @param path - path with more than one objects
     * @param toCheck    - incomplete paths
     * @param foundPaths - complete paths
     */
    private static void handleLists(Path path, LinkedList<Path> toCheck, LinkedList<Path> foundPaths){
    	
    	if(path==null){						// --- path was discarded ---
    		//Out_.pl("> Discarded: "+path);
    		
    	}
    	else if(path.getBus2()==null){		// --- path still grows (has one Bus) ---
    		//Out_.pl("> Add toCheck: "+path);
    		
    		assert path.getBus1() != null;				// must already be set at init
    		if(toCheck.contains(path))
    			assert !toCheck.contains(path);				// must not be already contained in unfinished path list
    		toCheck.add(path);							// -> add to unfinished paths
    	}
    	else {								// --- path is finished ---
    		//Out_.pl("> Add foundPaths: "+path);
    		
    		assert path.getBus1() != null;				// bus1 must already be set
    		//assert !path.pathObjects.isEmpty();		// must contains objects
    		//assert path.getBus1() != path.getBus2();	// no connection to itself
    		path.notVisitedTmp = null;					// list no longer needed: delete
    		foundPaths.add(path);						// -> add to complete paths (path is finished)
    	}
    }
    
    /**
     * Handle found neighbor object.
     * Adds found resistor or connects to found bus.
     * 
     * @param path - current path, the first bus of the path is already set.
     * @param nb - neighbor
     * @return unfinished path (pathObject added) or finished path (2nd bus added). Allows empty finished paths.
     */
    private Path handleNeighbor(Path path, SmartGridObject nb){
    	
    	if(nb.getBusNumber() < 0){		// has no bus, is resistance	// TODO: what if definition changes?
    		path.pathObjects.add(nb);
    		return path;
    	}
    	else {							// has bus
    		//if(path.pathObjects.isEmpty()){
    		//	return null;				// empty path, return null
    		//}
    		//else {
    		path.addBus(nb.getBus());	// found second bus
    		return path;
    		//}
    	}
    }
    
    /**
     * Old method.
     */
//    public void resetPowerConsumption() {
//        for (SmartGridObject temp : gridModel.gridObjectList) {
//            temp.resetCurrentPowerConsumption();
//        }
//    }
}