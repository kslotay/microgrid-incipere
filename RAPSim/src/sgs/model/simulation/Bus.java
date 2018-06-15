package sgs.model.simulation;
import java.util.LinkedList;

import sgs.model.gridObjects.GridPower;
import sgs.model.gridObjects.PowerPlant;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.VariableSet;
import sgs.model.variables.collector.VariableCollection;
import sgs.model.variables.collector.VariableOwner;

/**
 * A bus combines multiple (1 or more) grid objects, which are not seperated by any resistance.
 * 
 * (Was named Cluster before)
 * @author Tobi, Schweiger
 */
public class Bus implements VariableOwner {
	private int busNumber;
	
	public LinkedList<SmartGridObject> busGridObjects;
	public VariableCollection variableCollection;
	
	private NumericValue currentBusVoltage = new NumericValue(1.0);
	private VariableSet tmpVariableSet;
	private NumericValue tmpNettoPowerProduction;

	
	/**
	 * Constructor: 
	 * @param number
	 * @param busGridObjects
	 */
	public Bus(int busNumber, LinkedList<SmartGridObject> busGridObjects, VariableCollection variableCollection){
		this.busNumber= busNumber;
		this.busGridObjects = busGridObjects;
		this.variableCollection = variableCollection;
	}

	
	/**
	 * Calculate netto power from VariableSet (uses powerProduction, powerDemand)
	 */
	private void calculateNettoPowerProduction(){
		try{
			NumericValue powerProduction = tmpVariableSet.get(EnumPV.powerProduction).getValueNumeric();
			NumericValue powerDemand = tmpVariableSet.get(EnumPV.powerDemand).getValueNumeric();
			
			tmpNettoPowerProduction = powerProduction.copy().subtract(powerDemand);
		}
		catch(NullPointerException e){
			tmpNettoPowerProduction = null;
		}
	}


	/**
	 * calculates the bus voltage from nominal voltages,
	 * with power plants:  as weighted average among powerProducing objects 
	 * 						without production as average among generators
	 * for grid power: set to the value
	 * sets the voltage to all bus objects, with angle = 0 
	 *  @return 
	 **/
		private void estimateBusVoltage() {
			double actBusProduction = this.getValue(EnumPV.powerProduction).getReal(); 
//			if( actBusProduction  > this.getValue(EnumPV.powerConsumption).getReal() ){
			if( this.containsPowerPlant()  ){
				double busVolt=0d;
				double weight , volt;
				int numGen = 0;
				for(SmartGridObject sgo: busGridObjects){
					if( sgo.variableSet.isUsed(EnumPV.powerProduction) ){  // weighted average from powerProducing objects
						volt =  sgo.variableSet.get(EnumPV.nominalVoltage).getValueDouble() ;
						if( actBusProduction == 0 ){
							numGen += 1; 
							busVolt = busVolt*(numGen-1)/numGen + volt/numGen;	// iterative form of average
						} else {
							weight = sgo.getPowerProduction().getReal() / actBusProduction;
							busVolt += weight*volt;
						}
					}
				}
				this.setBusVoltage( new NumericValue( busVolt,0 )  );
			} else if (this.containsInstanceOf( new GridPower() )){
				for(SmartGridObject sgo: busGridObjects){
					if ( sgo.getClass().equals( new GridPower().getClass()  )   ) {
						this.setBusVoltage( sgo.getVariableSet().get(EnumPV.nominalVoltage).getValueNumeric() );
					}
				}
				
			}
		}


	/**
	 * Collects current simulation values with variableCollector
	 * executes calculateNettoPowerProduction() and estimateBusVoltage()
	 */
	public void refreshValues(){		// ------------ new default for this type of object
		variableCollection.resetValues();
		for(SmartGridObject sgo : busGridObjects)
			variableCollection.collectFrom(sgo);
		tmpVariableSet = variableCollection.getVariableSet();
		calculateNettoPowerProduction();
		estimateBusVoltage();
	}


	/**
	 * @return (powerProduction-powerDemand) or null, if values do not exist
	 */
	public NumericValue getNettoPowerProduction() {
		return tmpNettoPowerProduction;
	}

	
	/**
	 * Sets production and consumption of bus objects to fit the wishedPower
	 * @return difference of wished power and new netto bus power -return value of 0,0 is best ;-)  
	 * 
	 */
	public NumericValue balanceBusPowerto(NumericValue wishedPower) {
		this.refreshValues();		// get newest values for buses and paths

		
		NumericValue nettoBusPower = this.getNettoPowerProduction(); // powerProduction minus powerDemand

		
		NumericValue availableBusProduction = this.getValue(EnumPV.powerProduction);
		NumericValue totalBusDemand = this.getValue(EnumPV.powerDemand);
		NumericValue totalBusConsumption = this.getValue(EnumPV.powerConsumption);

		boolean gridPowerAvailable =  this.containsInstanceOf( new GridPower() );
		
		double realBusPower = availableBusProduction.getReal() - totalBusConsumption.getReal();
		double imagBusPower = availableBusProduction.getImaginary() - totalBusConsumption.getImaginary();
		
		// bus is already in balance
		if (realBusPower== wishedPower.getReal() && imagBusPower==wishedPower.getImaginary() ){
			System.out.println( this.toString()+ " in balance");
		} 
//		else if(!gridPowerAvailable){
//			if( availableBusProduction.getReal()<wishedPower.getReal() || totalBusDemand.getReal() < -wishedPower.getReal()  ){
//				System.out.println( "wishedPower out of range for bus resources");
//			}
//		}
		else {	
		
		if ( wishedPower.getReal() < 0d && !gridPowerAvailable){  // nur ohne gridpower
			availableBusProduction.subtract(wishedPower);	
		} else if( wishedPower.getReal() > 0d && !gridPowerAvailable ) {
			totalBusDemand.add(wishedPower);
		}
		
		NumericValue consumptionTmp = new NumericValue( 0d,0d);
		NumericValue productionTmp = new NumericValue( 0d,0d);
		
		NumericValue demandSGO  = new NumericValue( 0d,0d);
		NumericValue prodSGO = new NumericValue( 0d,0d);
		
		
		for(SmartGridObject sgo : this.busGridObjects){		// for every Bus Object:
			//sgo.variableSet.get(EnumPV.powerDemand).getValueDouble();
								
			if(sgo.variableSet.isUsed(EnumPV.powerDemand)){
				
				demandSGO = sgo.getPowerDemand();
				consumptionTmp = consumptionTmp.add( demandSGO ); 
				
				if ( availableBusProduction.getReal() < consumptionTmp.getReal() && !gridPowerAvailable ){
					demandSGO.subtract( consumptionTmp.copy().subtract(availableBusProduction)	 );
					if ( demandSGO.getReal()<0 ){
						demandSGO = new NumericValue(0d,0d);
					} else {
					// calculate reduced reactive power
						double demandReAct = sgo.getPowerDemand().getImaginary() * demandSGO.getReal()/sgo.getPowerDemand().getReal();
						demandSGO.setImaginary(demandReAct);
					}
				}
				sgo.setCurrentPowerConsumption(demandSGO.roundValue());
				
			} 
			else if(sgo.variableSet.isUsed(EnumPV.powerProduction)){
				
				if(sgo instanceof GridPower ){		
					prodSGO = wishedPower.subtract(nettoBusPower);  
					if ( prodSGO.getReal()>0d ) {
						sgo.setPowerProduction(  prodSGO ) ;
						sgo.setCurrentPowerConsumption( new NumericValue(0d,0d));
						}  
					else if ( prodSGO.getReal()<=0d ) {
						sgo.setCurrentPowerConsumption( new NumericValue(-prodSGO.getReal(), -prodSGO.getImaginary() ).roundValue() );
						sgo.setPowerProduction( new NumericValue( 0d,0d) ) ;
						} 
				}
				else {  // generators
					prodSGO = sgo.getPowerProduction().roundValue();
					productionTmp = productionTmp.add( prodSGO );						
					if ( totalBusDemand.getReal()<productionTmp.getReal() && !gridPowerAvailable ) {	
						
						prodSGO = prodSGO.subtract( productionTmp.copy().subtract(totalBusDemand) ); 							
						if (prodSGO.getReal()<0){
							prodSGO = new NumericValue(0d,0d);
							} 
						}
					if (!gridPowerAvailable && totalBusDemand.getImaginary() != -wishedPower.getImaginary() ){ // calculate reactive power to set in case of isolated bus and AC-PF for gen-bus  
						double prodReAct;
						double gapReAct = totalBusDemand.getImaginary()+wishedPower.getImaginary();
						if ( totalBusDemand.getReal()>0 && prodSGO.getReal()>0 ){
//							 prodReAct = totalBusDemand.getImaginary() * prodSGO.getReal() / totalBusDemand.getReal() ;	
							 prodReAct = gapReAct * prodSGO.getReal() / totalBusDemand.getReal() ;								 
						} else {
//							 prodReAct = wishedPower.getImaginary();
							 prodReAct = gapReAct;							 
						}
						prodSGO.setImaginary(prodReAct);
						productionTmp.add( new NumericValue( 0d,prodReAct)  );  //TODO test if necessary
						}
					sgo.setPowerProduction( prodSGO.roundValue() );
				}
			}
			 			
			
		}
	}	
		this.refreshValues();  // time consuming ??
		NumericValue retPower = new NumericValue( wishedPower.subtract( this.getNettoPowerProduction() ) ) ;
		return retPower;
	}
	

	/** @param voltage - voltage to set for every object (special) **/
	public void setBusVoltage(NumericValue voltage){
		currentBusVoltage = voltage;
		double voltageAngle = Math.atan( currentBusVoltage.getImaginary()/currentBusVoltage.getReal() );
		voltageAngle = voltageAngle/Math.PI*180.0;
		voltageAngle = Math.round(voltageAngle*100.0)/100.0;
		NumericValue roundVolt =  voltage.roundValue();       // (new NumericValue( voltage.abs() )).roundValue().getValueDouble() ;  // workaround to use the roundValue method 
		for (SmartGridObject sgo : busGridObjects){
			sgo.setCurrentVoltage( roundVolt  );
			sgo.variableSet.get(EnumPV.voltageAngle).setValue(voltageAngle);
		}
	}


	/** @return the Bus voltage (should be equal for whole bus), stored locally **/
	// TODO refactor this function
	public NumericValue getBusVoltage(){
//		estimateBusVoltage(); 
	
		return currentBusVoltage.copy();
	}


	/** 
	 * Call after values were refreshed.
	 * 
	 * @return 	true if real netto power production <= 0
	 * @see {@link #refreshValues(), #isGeneratorBus(), #isSwingBus()}}
	 */
	public boolean isNettoConsumer(){
		assert tmpNettoPowerProduction!=null;
		double realNettoPowerProduction = tmpNettoPowerProduction.r();
		return realNettoPowerProduction <= 0;
	}
	/** 
	 * Call after values were refreshed.
	 * 
	 * @return 	true if the generation is higher than demand in the bus 
	 * 			false otherwise or at error.
	 * @see {@link #refreshValues(), #isLoadBus(), #isSwingBus()}}
	 */
	public boolean isNettoProducer(){
		assert tmpNettoPowerProduction!=null;
		double realNettoPowerProduction = tmpNettoPowerProduction.r();
		return realNettoPowerProduction > 0;
	}
	
	/** 
	 * Call after values were refreshed.
	 * 
	 * @return 	true if there is any instance of PowerPlant in the bus 
	 * 			false otherwise or at error.
	 * @see {@link #refreshValues(), #isLoadBus(), #isSwingBus()}}
	 */
	public boolean containsPowerPlant(){
		boolean flag = false; 
		for(SmartGridObject sgo : busGridObjects){
			if (sgo instanceof PowerPlant){
				flag = true;
				break;
			}
		}
		return flag;
	}
	

	
	
	@Override
	public VariableSet getVariableSet() {		// ------------ new default for this type of object
		return tmpVariableSet;
	}


	/**
	 * @param variableName
	 * @return value for variableName, or null if name is not found.
	 */
	public NumericValue getValue(EnumPV variableName){		// ------------ new default for this type of object
		return variableCollection.getValue(variableName);
	}


	/**
	 * @param sgo
	 * @return true, if object is contained in bus
	 */
	public boolean contains(SmartGridObject sgo){
		return getGridObjects().contains(sgo);
	}

	/**
	 * @param sgo
	 * @return true, if any object of the same type as sgo is contained in the bus. recommended to use with constructor as argument sgo
	 */
	public boolean containsInstanceOf(SmartGridObject sgo){
		Class<?> cls = sgo.getClass();
		boolean flag = false;
		for (int cnt=0;  cnt<busGridObjects.size(); cnt++ ){
			if ( busGridObjects.get(cnt).getClass().equals(cls)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * @param cls
	 * @return true, if any object of the same type as sgo is contained in the bus. recommended to use with constructor as argument sgo
	 */
	public boolean containsInstanceOf(Class<?> cls){
		boolean flag = false;
		for (int cnt=0;  cnt<busGridObjects.size(); cnt++ ){
			if ( busGridObjects.get(cnt).getClass().equals(cls) | busGridObjects.get(cnt).getClass().getSuperclass().equals(cls) ) {
				flag = true;
				break;
			}  
		}
		return flag;
	}
	/**
	 * @return the contained grid objects
	 */
	public LinkedList<SmartGridObject> getGridObjects() {
		return busGridObjects;
	}


	/**
	 * @param objects the objects to set
	 */
	public void setGridObjects(LinkedList<SmartGridObject> objects) {
		this.busGridObjects = objects;
	}


	/**
	 * Add object to bus
	 * @param sgo
	 */
	public void addObject(SmartGridObject sgo){
		busGridObjects.add(sgo);
	}

	/** @return the busNumber **/
	public int getBusNumber() {
		return busNumber;
	}


	/** @param busNumber - busNumber to set **/
	public void setBusNumber(int busNumber) {
		this.busNumber = busNumber;
	}

	/**
	 * returns the bus name as a string
	 */
	@Override
	public String toString(){
		return "Bus"+busNumber;
	}
}
