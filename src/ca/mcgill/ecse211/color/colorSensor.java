package ca.mcgill.ecse211.color;

import ca.mcgill.ecse211.finalProject.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class colorSensor extends Thread {

	public float[] RGBData;
	public static int targetColor;
	public static int sensorColor;
	public static int prevColor;
	public float red;
	public float green;
	public float blue;
	public SampleProvider RGBColor;
	public static boolean correctColor = false;
	public static String foundColor="";
	public static String colorResponse="";

	private static final long COLOR_PERIOD = 25;
	
	// Red = 1
	// Blue = 2
	// Yellow = 3
	// White = 4

	private static float redR = 197;
	private static float redG = 34;
	private static float redB = 19;
	private static float blueR = 73;
	private static float blueG = 191;
	private static float blueB = 279;
	private static float yellowR = 330;
	private static float yellowG = 200;
	private static float yellowB = 39;
	private static float whiteR = 311;
	private static float whiteG = 224;
	private static float whiteB = 197;
	
	private static float redRSD = 10; 
	private static float redGSD = 10;
	private static float redBSD = 5;
	private static float blueRSD = 5;
	private static float blueGSD = 5;
	private static float blueBSD = 5;
	private static float yellowRSD = 5;
	private static float yellowGSD = 15;
	private static float yellowBSD = 10;
	private static float whiteRSD = 5;
	private static float whiteGSD = 5;
	private static float whiteBSD = 5;
	
	private static double sdMultiplier = 8.0; 

	public colorSensor(float[] RGBData, SampleProvider RGBColor, int targetColor ) {
		this.RGBData=RGBData;
		this.RGBColor=RGBColor;
		this.targetColor = targetColor;
		Sound.setVolume(100);
	}

	public static int colorEnumCalculator(float colorF) {

		int color = (int) colorF;
		if (color == 0) {
			return 1; //Red
		} 
		else if (color == 2) {
			return 2; //Blue
		} 
		else if (color == 3) {
			return 3; //Yellow
		} 
		else if (color == 6) {
			return 4; //White
		} 
		else {
			return 0;
		}
	}
	
	
	public static boolean seeColor(){
		if (sensorColor!=0 && sensorColor!=targetColor){
			//Sound.beep();
			return true;
		}
		else {
			return false;
		}
	}
	

	public void run() {
		long updateStart, updateEnd;
		
		while(!correctColor) {
			//updateStart=System.currentTimeMillis();
			
			RGBColor.fetchSample(RGBData, 0);
			
			red = RGBData[0];
			//green = 10000* RGBData[1];
			//blue = 10000* RGBData[2];
			
			sensorColor = colorEnumCalculator(red);
			
			if (sensorColor==targetColor) {
				correctColor = true;
				//Sound.beepSequenceUp();	
			}			
			//seeColor();
			
			//updateEnd=System.currentTimeMillis();
			
//			if (updateEnd-updateStart < COLOR_PERIOD) {
//				
//				try {
//					Thread.sleep(COLOR_PERIOD-(updateEnd-updateStart));
//				} catch (InterruptedException e) {
//				}
//			}
		}
	}

}

