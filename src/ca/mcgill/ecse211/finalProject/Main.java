package ca.mcgill.ecse211.finalProject;

import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.pilot.Navigation;
import ca.mcgill.ecse211.pilot.USLocalizer;
import ca.mcgill.ecse211.pilot.Destinator;
import ca.mcgill.ecse211.pilot.LightLocalizer;
import ca.mcgill.ecse211.search.*;
import ca.mcgill.ecse211.finalProject.WiFi;

import java.util.Timer;
import java.util.TimerTask;

import ca.mcgill.ecse211.color.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor; //MOTOR for sensor
import lejos.hardware.sensor.*;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;

public class Main {

	/**
	 * This class is used as designated entry point to run program
	 * 
	 */
	// Motor Objects, and Robot related parameters
	
	
	static final String SERVER_IP = "192.168.2.11";
	static final int TEAM_NUMBER = 1;
	

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor sensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	

	private static SensorModes RSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	private static SampleProvider RColor = RSensor.getMode("Red");
	private static float[] RData = new float[RColor.sampleSize()];

	private static SensorModes LSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
	private static SampleProvider LColor = LSensor.getMode("Red");
	private static float[] LData = new float[LColor.sampleSize()];

	private static SensorModes RGBSensor = new EV3ColorSensor(LocalEV3.get().getPort("S4"));
	private static SampleProvider RGBColor = RGBSensor.getMode("RGB");
	private static float[] RGBData = new float[RGBColor.sampleSize()];

	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	private static final Port usPort = LocalEV3.get().getPort("S2");
	
	static SensorModes ultrasonicSensor = new EV3UltrasonicSensor(usPort);
	static SampleProvider usDistance = ultrasonicSensor.getMode("Distance");
	static float[] usData = new float[usDistance.sampleSize()];
	
	
	private static WiFi wifi = new WiFi();
	/*
	 * To get any map info its wifi.startCorner for example or
	 * wifi.targetColor
	 */
	public static int[] LL = new int[] { 2, 2 };
	public static int[] UR = new int[] { 5, 5 };

	public static final double WHEEL_RAD = 2.12;// 2.12
	public static final double TRACK = 10.2;//
	public static final double TILE_SIZE = 30.48;
	public static final int GRID_SIZE = 8;
	public static final int MOTOR_ROTATE = 105;
	public static final int MOTOR_STRAIGHT = 185;



	/**
	 * This is the main method
	 * 
	 * @throws OdometerExceptions
	 */

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;
		
		// Odometer related objects
		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);

		Display odometrydisplay = new Display(lcd); // No need to change
		// Navigation navigation = new Navigation(odometer,
		// leftMotor,rightMotor);
					
		Thread odoThread = new Thread(odometer);
		odoThread.start();

		Thread ododisplayThread = new Thread(odometrydisplay);
		ododisplayThread.start();

		 
		colorSensor colorSensor = new colorSensor(RGBData, RGBColor, wifi.targetColor);
		colorSensor.start();

		LightLocalizer lightLocalizer = new LightLocalizer(wifi.startCorner, odometer, leftMotor, rightMotor, RColor, LColor, RData, LData);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, lightLocalizer, LL, UR);
		Destinator destinator = new Destinator(navigator, odometer, wifi);
		USLocalizer USLocalizer = new USLocalizer(wifi.startCorner ,odometer, leftMotor, rightMotor, usDistance, navigator);
		Search searcher = new Search(LL, UR, colorSensor, odometer, USLocalizer, navigator,destinator , sensorMotor);

		lcd.clear();
		
		USLocalizer.localize();
		lightLocalizer.localize();

		destinator.gotoCheckPoint();
		
		
		//destinator.gotoLowerLeft(LL, UR);

		//searcher.beginSearch();


		/*
		 * navigator.travelTo(0, 6); lightLocalizer.correctLocation();
		 * navigator.travelTo(6, 6); lightLocalizer.correctLocation();
		 * navigator.travelTo(6, 0); lightLocalizer.correctLocation();
		 * navigator.travelTo(0, 0); lightLocalizer.correctLocation();
		 *
		*/
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;

		System.exit(0);

	}
	
	public static void localize() {
		
	}

}