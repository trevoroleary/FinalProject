package ca.mcgill.ecse211.color;

import ca.mcgill.ecse211.finalProject.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class IDSensor extends Thread {

	public float[] RGBData;
	public static int targetColor;
	public static int sensorColor;
	public static int prevColor;
	public SampleProvider RGBColor;
	public static boolean correctColor = false;

	private static final long COLOR_PERIOD = 25;
	
	// Red = 1
	// Blue = 2
	// Yellow = 3
	// White = 4


	 

	public IDSensor(float[] RGBData, SampleProvider RGBColor, int targetColor) {
		this.RGBData=RGBData;
		this.RGBColor=RGBColor;
		this.targetColor = targetColor;
		Sound.setVolume(100);
	}

	public static int colorEnumCalculator(int color) {

		if (color == 0 && prevColor != 0) {
			//prevColor = 0;
			return 1; //Red
		} 
		else if (color == 2 && prevColor != 2) {
			//prevColor = 2;
			return 2; //Blue
		} 
		else if (color == 3 && prevColor != 3) {
			//prevColor = 3;
			return 3; //Yellow
		} 
		else if (color == 6 && prevColor != 6) {
			//prevColor = 6;
			return 4; //White
		} 
//		else if(color!=0 && color !=2 && color != 3 && color!= 6) {
//			//prevColor = -1;
//			return 0;
//		}
		else {
			return 0;
		}
	}
	
	
	public static void seeColor(){
		if (sensorColor!=0 && sensorColor!=targetColor){
			Sound.beep();
		}
	}
	

	public void run() {
		long updateStart, updateEnd;
		
		while(!correctColor) {
			updateStart=System.currentTimeMillis();
			
			RGBColor.fetchSample(RGBData, 0);
			
			sensorColor = colorEnumCalculator((int) (RGBData[0]));
			
			if (sensorColor  == targetColor) {
				correctColor = true;
				Sound.beepSequenceUp();	
			}			
			//seeColor();
			
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

