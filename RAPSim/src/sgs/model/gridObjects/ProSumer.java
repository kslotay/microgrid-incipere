package sgs.model.gridObjects;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;



/**
 * A SmartGridObject.
 * Anyone using or providing energy
 * @author tobi, Kristofer Schweiger
 */
public abstract class ProSumer extends SmartGridObject {

    //private LinkedList<Offer> offers = new LinkedList<>();
    //private Profile profile = null;
    //private int profileID = -1;
    
	
    /**
     * Constructor with shared attributes.
     * @param powerDemand
     * @param powerProduction
     * @param currentCharge
     */
    public ProSumer(){
    	super();
    }
    
//    /**
//     * @return the offers
//     */
//    public LinkedList<Offer> getOffers() {
//        return offers;
//    }
//    
//    /** TODO: test amount, remove function
//     * @param offer to add
//     */
//    public void addOffer(Offer offer) {
//        offers.add(offer);
//    }

//    /**
//     * @return the profileID
//     */
//    public int getProfileID() {
//        return profileID;
//    }
//
//    /**
//     * @param profileID the profileID to set
//     */
//    public void setProfileID(int profileID) {
//        this.profileID = profileID;
//    }
    
    
    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------
//TODO: remove this method 
    @Override
    public void setProductionToWeatherAndTime(GregorianCalendar currentTime, Weather weather) {
    	
    	// use EnumPV.powerProductionOptimal for EnumPV.powerProduction - default definition
    	NumericValue defaultPP = variableSet.get(EnumPV.powerProductionOptimal).getValueNumeric();
    	variableSet.get(EnumPV.powerProduction).setValue(defaultPP);
    }
    
    // ---------------------------------------------------------
    // --- Abstract Methods --- --------------------------------
    
    
}
