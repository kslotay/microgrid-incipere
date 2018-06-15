package sgs.model.gridData;

import sgs.util.SortedLinkedListSet;

/**
 * "List" of Overlays for a single Grid Object
 * 
 * @author Kristofer Schweiger
 */
public class OverlayList extends SortedLinkedListSet<Overlay> {

	//---------------------------------------------------------------
	//@SuppressWarnings("unused")
	//private static final LayerComparator layerComparator = new LayerComparator();
	//private OverlayModeEnum overlayMode = OverlayModeEnum.DEFAULT;
	
	/**
	 * Constructor: 
	 * Sets overlay LayerComparator in super class.
	 */
	public OverlayList(){
		super(new LayerComparator());
	}
	
//	/**
//	 * Overlay will be defined by GridController
//	 */
//	public void setModeToDefault(){
//		overlayMode = OverlayModeEnum.DEFAULT;
//		this.clear();
//	}
//	/**
//	 * Overlay will not be used
//	 */
//	public void setModeToOFF(){
//		overlayMode = OverlayModeEnum.OFF;
//		this.clear();
//	}

	/**
	 * Set a single Overlay. Other Overlays will be deleted.
	 * @param overlay
	 */
	public void setOverlaySingle(Overlay overlay){
		//overlayMode = OverlayModeEnum.SINGLE;
		this.clear();
		this.put(overlay);
	}
	
	/**
	 * Set Overlay for specified layer.
	 * Overlay on equal layer will be replaced.
	 * Do not change layer (in Overlay) after adding.
	 * @param overlay
	 */
	public void setOverlay(Overlay overlay){
		//overlayMode = OverlayModeEnum.MULTI;
		this.add(overlay);
	}
	
	/** @return size **/
	public int getNumberOfOverlays(){
		return this.size();
	}
	
//	/** @return currently used overlay mode **/
//	public OverlayModeEnum getOverlayMode(){
//		return overlayMode;
//	}
	
}

/**
 * Comparator for distinguishing the levels of Overlays.
 * @author Kristofer Schweiger
 */
class LayerComparator implements java.util.Comparator<Overlay> {
	@Override
	public int compare(Overlay a, Overlay b) {
		return b.levelEnum.ordinal()-a.levelEnum.ordinal();
	}
	
}
