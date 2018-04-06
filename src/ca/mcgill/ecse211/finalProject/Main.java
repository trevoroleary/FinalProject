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
	
	
	static final String SERVER_IP = "192.168.2.19";
	static final int TEAM_NUMBER = 17;
	

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
	private static SampleProvider RGBColor = RGBSensor.getMode("ColorID");
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


	public static final double WHEEL_RAD = 2.12;// 2.12
	public static final double TRACK = 10.45;//
	public static final double TILE_SIZE = 30.48;
	public static final int GRID_SIZE = 8;
	public static final int MOTOR_ROTATE = 105;
	public static final int MOTOR_STRAIGHT = 185; //185


	public static void main(String[] args) throws OdometerExceptions {

		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		LightLocalizer lightLocalizer = new LightLocalizer(wifi.startCorner, odometer, leftMotor, rightMotor, RColor, LColor, RData, LData);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, lightLocalizer, wifi.Search_LL, wifi.Search_UR);
		Destinator destinator = new Destinator(navigator, odometer, wifi);
		USLocalizer USLocalizer = new USLocalizer(wifi ,leftMotor, rightMotor, odometer, usDistance, usData );
		

		leftMotor.setAcceleration(3000);
		rightMotor.setAcceleration(3000);
		
		
		/*
		 * 
		 * --------------------------------PROGRAM BEGINS--------------------------------
		 * 
		 */

		
		USLocalizer.localize();
		lightLocalizer.localize();
		
		
		navigator.turn(90, true);
		
		destinator.gotoCheckPoint();
		destinator.gotoCheckPoint();
		
		
		
		//odometer.setXYT(wifi.Search_LL[0]*TILE_SIZE, wifi.Search_LL[1]*TILE_SIZE, 0);
		
		
		colorSensor colorSensor = new colorSensor(RGBData, RGBColor, wifi.targetColor);
		colorSensor.start();
		Search searcher = new Search(leftMotor, rightMotor, wifi.Search_LL, wifi.Search_UR, colorSensor, odometer, USLocalizer, navigator,destinator , sensorMotor);
		searcher.beginSearch();
		//colorSensor.interrupt();
		
		destinator.gotoCheckPoint();
		destinator.gotoCheckPoint();
		
		
//		IDSensor IDSensor = new IDSensor(RGBData, RGBColor, wifi.targetColor);
//		IDSensor.start();
//		
//		Search searcher = new Search(leftMotor, rightMotor, wifi.Search_LL, wifi.Search_UR, IDSensor, odometer, USLocalizer, navigator,destinator , sensorMotor);
//		searcher.beginSearch();
		
		
		
		//colorSensor.interrupt();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;

		System.exit(0);

	}
	
	public static void localize() {
		
	}

}