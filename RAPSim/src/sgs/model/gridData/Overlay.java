package sgs.model.gridData;

import java.awt.Color;

/**
 * Single Overlay, defined by Level and Color.
 * 
 * @author Kristofer Schweiger
 */
public class Overlay {

//	private static Random RAND = GridObject.RAND;
	
	/** Enum defining the dynamic overlay color in the grid **/
	public enum OverlayColorEnum {

		/* Single colors: from light gray to COLOR (0..1) */
		RED, ORANGE, YELLOW, GREEN, TURQUOIS, BLUE, VIOLET,
		
		/* Two colors: from COLOR_1 to COLOR_2 */
		RED_GREEN,		// from red to green (0..1)
		BLACK_BLUE,		// -
		BLACK_WHITE,	// -
		RED_BLUE,
		
		/* Three colors: from COLOR_1 to COLOR_2 to COLOR_3 (0..1)*/
		RED_GREEN_BLUE,		// (0..0,37..1)
		GREEN_YELLOW_RED, //actual
		BLACK_WHITE_BLACK,//actual
		RED_WHITE_BLUE, //actual & firebrick red and midnightblue
		;
	}
	/** 
	 * Enum defining the dynamic overlay in the grid
	 * Only one Overlay per level can be defied!
	 */
	public enum OverlayLevelEnum {
		/** Overlay for marking things **/
		_0,
		/** Big level 1 overlay **/
		_1,
		/**  Smaller level 2 overlay **/
		_2,
		/** Small level 3 overlay **/
		_3,
		;
	} //
	//----------------------------------------------------------
	
	public double overlayValue;
	public OverlayColorEnum colorEnum;
	public OverlayLevelEnum levelEnum;
	private boolean inUsed; //is it currently being used
	
//	public Overlay(){
//		this(0, OverlayColorEnum.GREEN_YELLOW_RED, OverlayLevelEnum._1);
//	}
//	public Overlay(double overlayValue){
//		this(overlayValue, OverlayColorEnum.GREEN_YELLOW_RED, OverlayLevelEnum._1);
//	}
//	public Overlay(double overlayValue, OverlayColorEnum mode){
//		this(overlayValue, mode, OverlayLevelEnum._1);
//	}
	public Overlay(double overlayValue, OverlayColorEnum color, OverlayLevelEnum level, boolean isUsed){
		this.overlayValue = overlayValue;
		this.colorEnum = color;
		this.levelEnum = level;
		this.inUsed = isUsed; 
	}
	
	/**
	 * @return level starting at 1.
	 */
	public int getLevelNr(){
		return levelEnum.ordinal()+1;
	}
	
	public boolean getIsUsed(){
		return inUsed;
	}
	
	public void setIsUsed(boolean newUsedStatus){
		inUsed = newUsedStatus;
	}
	
	/**
	 * Defined for color enum, except OFF and DEFAULT
	 * @return Color defined in Enum 
	 * 			or null if color for type not defined in here.
	 */
	public Color getOverlayColor(){
		Color col = null;
		float value = (float)overlayValue;
		
		switch(colorEnum){ /* color: hue, saturation, brightness */
		
		case RED: 
			col = Color.getHSBColor(0f /*red*/, value, 0.9f+value/10f); 
			break;
		case ORANGE: 
			col = Color.getHSBColor(0.11f, value, 0.9f+value/10f); 
			break;
		case YELLOW: 
			col = Color.getHSBColor(0.16f, value, 0.9f+value/10f); 
			break;
		case GREEN: 
			col = Color.getHSBColor(0.37f, value, 0.9f+value/10f); 
			break;
		case TURQUOIS: 
			col = Color.getHSBColor(0.50f, value, 0.9f+value/10f); 
			break;
		case BLUE: 
			col = Color.getHSBColor(0.62f, value, 0.9f+value/10f); 
			break;
		case VIOLET: 
			col = Color.getHSBColor(0.78f, value, 0.9f+value/10f); 
			break;
		case RED_BLUE:
			col = Color.getHSBColor(value*0.5f - 0.01f, 1f,1f);
			break;
		case RED_GREEN: //over part yellow
			col = Color.getHSBColor(value*0.3f - 0.01f, 1f, 1f); 
			break;
		case BLACK_BLUE: 
			col = Color.getHSBColor(0.62f, 1f, value);
			break;
		case BLACK_WHITE: 
			col = Color.getHSBColor(0f, 0f, value);
			break;
		
		case RED_GREEN_BLUE: 
			col = Color.getHSBColor(value*0.72f - 0.01f, 1f, 1f);
			break;
		case GREEN_YELLOW_RED:
			col = Color.getHSBColor(value*0.3f - 0.01f, 1f, 1f); 
			break;
		case BLACK_WHITE_BLACK:
			col = Color.getHSBColor(0, 0, 1f-value);
			break;
		case RED_WHITE_BLUE:
//			col = Color.getHSBColor();
			break;
		default:
			throw new RuntimeException("Unknown color enum.");
		}
		
		return col;
	}
	
//	/**
//	 * @return a new random overlay
//	 */
//	public static Overlay randomOverlay(){
//		OverlayLevelEnum level        = OverlayLevelEnum.values()[ RAND.nextInt(OverlayLevelEnum.values().length) ];
//		OverlayColorEnum overlayColor = OverlayColorEnum.values()[ RAND.nextInt(OverlayColorEnum.values().length) ];
//		double overlayValue           = RAND.nextDouble();
//		boolean isUsed 				  = getIsUsed();
//		
//		return new Overlay(overlayValue, overlayColor, level, isUsed);
//	}
//	
//	
//	public static Overlay randomOverlay(double overlayValue, OverlayColorEnum color, OverlayLevelEnum level){
//		Overlay overlay = randomOverlay();
//		
//		if(overlayValue>=0){
//			overlay.overlayValue = overlayValue;
//		}
//		if(color!=null){
//			overlay.colorEnum = color;
//		}
//		if(level!=null){
//			overlay.levelEnum = level;
//		}
//		
//		return overlay;
//	}
	
	
//	@Override
//	public int compareTo(Overlay overlay2) {
//		if(this.level.ordinal() < overlay2.level.ordinal())
//			return -1;
//		else if(this.level.ordinal() > overlay2.level.ordinal())
//			return 1;
//		return 0;
//	}
//	/**
//	 * Definition for use in TreeSet. Overlay is "equal" if it is on the same level. 
//	 * Therefore it will by replaced by the new overlay.
//	 * @param overlay2
//	 * @return
//	 */
//	public boolean equals(Overlay overlay2) {
//		return this.level.ordinal() == overlay2.level.ordinal();
//	}
	

	
	
}
