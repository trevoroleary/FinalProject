/**
 * This class manages the color sensor positioned in front and above the robot
 * When initialized it constantly checks to see if a color is seen under the light sensor
 * It identifies the color comparing values gathered from the color sensor to a gausian distribution of test values
 * 
 * @author Tre M
 * @version 2.0
 * @since 2018-02-1
 */
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

	
	private static float redR = 500;
	private static float redG = 69;
	private static float redB = 64;
	private static float blueR = 73;
	private static float blueG = 191;
	private static float blueB = 279;
	private static float yellowR = 936;
	private static float yellowG = 652;
	private static float yellowB = 118;
	private static float whiteR = 957;
	private static float whiteG = 985;
	private static float whiteB = 760;
	
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

	/**
	 * This is the class constructor
	 * @param RGBData a location for the class to store color sensor values
	 * @param RGBColor the sample provider for the color sensor
	 * @param targetColor and the target color the bot is looking for
	 * 
	 * @author Tre M
	 */
	public colorSensor(float[] RGBData, SampleProvider RGBColor, int targetColor ) {
		this.RGBData=RGBData;
		this.RGBColor=RGBColor;
		this.targetColor = targetColor;
	}

	/**
	 * This is the method that determines which color the light sensor is measuring using gausian distribution
	 * @param Red These are the values measured by the color sensor
	 * @param Green
	 * @param Blue
	 * @return an int identifying which color is measured
	 * @author Tre M
	 */
	public static int colorEnumCalculator(float Red, float Green, float Blue) {

		if ((redR - sdMultiplier * redRSD) < Red && Red < redR + sdMultiplier * redRSD && redG - sdMultiplier * redGSD < Green
				&& Green < redG + sdMultiplier * redGSD && redB - sdMultiplier * redBSD < Blue && Blue < redB + sdMultiplier * redBSD) {
			return 1;
		}if ((blueR - sdMultiplier * blueRSD) < Red && Red < blueR + sdMultiplier * blueRSD && blueG - sdMultiplier * blueGSD < Green
				&& Green < blueG + sdMultiplier * blueGSD && blueB - sdMultiplier * blueBSD < Blue && Blue < blueB + sdMultiplier * blueBSD) {
			return 2;
		}if ((yellowR - sdMultiplier * yellowRSD) < Red && Red < yellowR + sdMultiplier * yellowRSD && yellowG - sdMultiplier * yellowGSD < Green
				&& Green < yellowG + sdMultiplier * yellowGSD && yellowB - sdMultiplier * yellowBSD < Blue && Blue < yellowB + sdMultiplier * yellowBSD) {
			return 3;
		}if ((whiteR - sdMultiplier * whiteRSD) < Red && Red < whiteR + sdMultiplier * whiteRSD && whiteG - sdMultiplier * whiteGSD < Green
				&& Green < whiteG + sdMultiplier * whiteGSD && whiteB - sdMultiplier * whiteBSD < Blue && Blue < whiteB + sdMultiplier * whiteBSD) {
			return 4;
		} else {
			return 0;
		}
	}

	/**
	 * This method converts the color identified to a string to print on the screen
	 * @param color the int that denotes what color the sensor is looking at
	 * @param correctColor a boolean that shows whether this is the right color or not
	 * @return the string identified
	 * @author Tre M
	 */
	public static String colorToString(int color, boolean correctColor) {
		if (correctColor && prevColor!=color) {
			Sound.twoBeeps();
		} else if (!correctColor && color!=0 && prevColor!=color) {
			Sound.beep();
		}
		if (color==1) {
			prevColor=color;
			return "RED";
		}
		if (color==2) {
			prevColor=color;
			return "BLUE";
		}
		if (color==3) {
			prevColor=color;
			return "YELLOW";
		}
		if (color==4) {
			prevColor=color;
			return "WHITE";
		} else {
			return "";
		}
	}
	
	/**
	 * this method can be called to check if the color of the block identified is correct
	 * @return a string of the color that is identified
	 * @author Tre M
	 */
	public static String getResponse(){
		return foundColor;
	}
	
	/**
	 * This method is used to check if the desired block has been found
	 * @return a boolean denoting if the bot found the desired color
	 * @author Tre M
	 */
	public static boolean seeColor(){
		if (sensorColor!=0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void run() {
		long updateStart, updateEnd;
		while(true) {
		updateStart=System.currentTimeMillis();
		RGBColor.fetchSample(RGBData, 0);
		red =10000* RGBData[0];
		green =10000* RGBData[1];
		blue =10000* RGBData[2];
		sensorColor = colorEnumCalculator(red, green, blue);
		if (sensorColor==targetColor) {
			correctColor = true;
		} else {
			correctColor = false;
		}
		foundColor = colorToString(sensorColor, correctColor);
		updateEnd=System.currentTimeMillis();
		if (updateEnd-updateStart < COLOR_PERIOD) {
			try {
				Thread.sleep(COLOR_PERIOD-(updateEnd-updateStart));
			} catch (InterruptedException e) {
			}
			}
		}
	}

}

