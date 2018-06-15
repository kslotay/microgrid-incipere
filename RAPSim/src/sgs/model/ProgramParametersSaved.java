package sgs.model;


/**
 * 
 * Program parameters which will be saved/loaded to/from a file.
 * Can further include everything which is helpful to know in combination with the result-file  
 * SAVED BY: sgs.controller.fileManagement.FileManager;
 * FILE-Definition: ProgramConstants.parameterFile
 * 
 * 
 * 
 * @author Kristofer Schweiger
 */
public class ProgramParametersSaved {
	
	/** true if parameters changed at runtime and need to be saved **/
	public boolean parametersChanged = false;
	/** true if program started for the first time on this machine **/
	private boolean firstStart = true;
	/** path for the simulation file **/
	private String simResultFile = "./simulation.txt";
	/** parameter tree for result file **/
//	private Tree simResultParameterTree; 
	
//	public String varName; //der Variable Name den sich der Benutzer aussuchen kann.
//	private String simResultFile2 = "./" + varName;
	
	
	
	// ----------------------------------------------------
	
	public String getSimResultFile() {
		return simResultFile;
	}
	
	
	public void setSimResultFile(String simResultFile) {
		if(!this.simResultFile.equals(simResultFile)){
			this.simResultFile = simResultFile;
		}
	}


	/**
	 * @return true if this is the first start of the program
	 */
	public boolean isFirstStart(){
		return firstStart;
	}
	/** 
	 * @param firstStart - Set the first start parameter.
	 */
	public void setFirstStart(boolean firstStart){
		if(this.firstStart != firstStart){
			parametersChanged = true;
		this.firstStart = firstStart;
	}
	}
	
	
}
