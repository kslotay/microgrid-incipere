package sgs.controller.simulation;

import java.util.Comparator;

import sgs.model.simulation.Bus;


public class BusNettoPowerComparator implements Comparator<Bus> {

	@Override
	public int compare(Bus a, Bus b) {
		
		double aPower = a.getNettoPowerProduction().getReal();
		double bPower = b.getNettoPowerProduction().getReal();
		
		if(aPower < bPower){ 
			return -1;
		}
		else if(aPower > bPower){
			return 1;
		}
		else{
			return 0;
		}
	}

	
}
