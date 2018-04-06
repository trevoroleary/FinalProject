/**
 * This is an object created by Main
 * It has an important method used immediately after the USLocalizer that corrects the bots x and y values
 * This object also possesses other important correction methods that are to be used during operation
 * 
 * @author Trevor O
 * @Version 2.0
 * @Since 2018-02-12
 */
package ca.mcgill.ecse211.pilot;

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.finalProject.WiFi;
import ca.mcgill.ecse211.odometer.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Wifi;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread  {
	
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private EV3ColorSensor colorSensorL, colorSensorR;
	public static SampleProvider RColor;
	public static SampleProvider LColor;
	public static float[] RData;
	public static float[] LData;
	private Odometer odometer;
	private WiFi wifi;
	
	//initialize parameters
	public static final int MOTOR_ROTATE = Main.MOTOR_ROTATE;
	private static final int MOTOR_STRAIGHT = Main.MOTOR_STRAIGHT;
	public static final double WHEEL_RAD = 2.05;	//17.115
	private final double SENSOR_OFFSET = 5.0;	//distance between center of the track and light sensor
	private final int GRID_COORDS = Main.GRID_SIZE - 1;
	private final int startCorner;
	
	//destination position
	private double x;
	private double y;
	
	//difference between current position and destination position
	private double dX;
	private double dY;
	private double dTheta;
	
	//angle toward destination
	private double currentX;
	private double currentY;
	private double Theta;
	
	//current position
	private double currentTheta;
	
	//distance between current position and destination position
	private double distance;
	private boolean Navigating;

			
	//constructor
	/**
	 * Class constructor
	 * 
	 * @param startCorner for localization to correct the odometer properly
	 * @param odo so this method has access to the odometer values
	 * @param leftMotor so this method can access the left motor and send it commands
	 * @param rightMotor 
	 */
	
	public LightLocalizer(int startCorner, Odometer odo,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, SampleProvider RColor, SampleProvider LColor, float[] RData, float[] LData ) {
		
		this.RColor = RColor;
		this.RData = RData;
		
		this.LColor = LColor;
		this.LData = LData;
		
		this.startCorner = startCorner;
		
		this.odometer = odo;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		
	    try {
			this.odometer = Odometer.getOdometer();
		} catch (OdometerExceptions e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method simplifies the correction process
	 * Call this method after the US localizer and the robot (given the proper startcorner on construction) will localize its self to the nearest corner
	 * 
	 * @author Trevor O
	 */
	public void localize() {
		correctXY();
		setXTOffset();
	}
	
	/**
	 * This method is only used privately by correction methods
	 * depending on the start corner this updates one coordinate of odometer values once the bot is in the right position
	 * The coordinates updated depend on the starting corner
	 * 
	 * @author Trevor O
	 */
	private void setYOffset() {
		if(startCorner == 0) 
			odometer.setY((1*Main.TILE_SIZE) - SENSOR_OFFSET);
		else if(startCorner == 1) 
			odometer.setX((GRID_COORDS*Main.TILE_SIZE) + SENSOR_OFFSET);	
		else if(startCorner == 2) 
			odometer.setY((GRID_COORDS*Main.TILE_SIZE) + SENSOR_OFFSET);
		else if(startCorner == 3) 
			odometer.setX((1*Main.TILE_SIZE) - SENSOR_OFFSET);
	}
	
	/**
	 * This method is used locally to correct one coordinate of the odometer after a correction made by other methods
	 * 
	 * @author Trevor O
	 */
	public void setXTOffset() {
		if(startCorner == 0) {
			odometer.setX((1*Main.TILE_SIZE) - SENSOR_OFFSET);
			travelTo(1,1);
			turnTo(0);
		}
		else if(startCorner == 1) {
			odometer.setY((1*Main.TILE_SIZE) - SENSOR_OFFSET);
			travelTo(GRID_COORDS,1);
			turnTo(270);
		}	
		else if(startCorner == 2) {
			odometer.setX((GRID_COORDS*Main.TILE_SIZE) + SENSOR_OFFSET);
			travelTo(GRID_COORDS, GRID_COORDS);
			turnTo(180);
		}
		else if(startCorner == 3) {
			odometer.setY((GRID_COORDS*Main.TILE_SIZE) + SENSOR_OFFSET);
			travelTo(1, GRID_COORDS);
			turnTo(90);
		}
		
	}
	
	/**
	 * this method travels forward to square up with the line infront,
	 * it then calls correctX to square up with the line 90 degrees to the right
	 * 
	 * this method should only be called when the robot is facing clockwise in one of the corners
	 * @author Trevor O & Winnie N
	 */
	public void correctXY() {
				
		leftMotor.setSpeed(MOTOR_ROTATE);
		rightMotor.setSpeed(MOTOR_ROTATE);
		
		leftMotor.forward();
		rightMotor.forward();
		
		squareUp(false);

	  odometer.setTheta(odometer.nearestHeading());
	  correctX();
	}
	
	
	/**
	 * this method is called by correctXY, it squares up with the line that is 90degress to the robots right
	 * @author Trevor O & Winnie N
	 */
	public void correctX() {

		setYOffset();
		
		leftMotor.rotate(-180, true);
		rightMotor.rotate(-180, false);
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		leftMotor.rotate(convertAngle(WHEEL_RAD, Main.TRACK, 90), true);
		rightMotor.rotate(-convertAngle(WHEEL_RAD, Main.TRACK, 90), false);
		
		leftMotor.forward();
		rightMotor.forward();
		
		squareUp(false);

	  odometer.setTheta(odometer.nearestHeading());
	  
	}
	
	
	/**
	 * This method is very powerful, it can correct the location of the robot at any coordinate on the board
	 * As long as the robot is within some margin of error this method will correct to the nearest coordinate
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void correctLocation() {
		turnTo(odometer.nearestHeading());
		double heading = odometer.nearestHeading();
		double x = ( (int) (0.5 + (odometer.getX() / Main.TILE_SIZE)));
		
		if(heading == 0) {
			squareUp(true);
			double y = Main.TILE_SIZE * ( (int) (0.5 + (odometer.getY() / Main.TILE_SIZE)));
			odometer.setY(y - SENSOR_OFFSET);
		}
		else if(heading == 90) {
			squareUp(true);
			x = Main.TILE_SIZE*x;
			odometer.setX(x - SENSOR_OFFSET);
		}
		else if(heading == 180) {
			squareUp(true);
			double y = Main.TILE_SIZE * ( (int) (0.5 + (odometer.getY() / Main.TILE_SIZE)));
			odometer.setY(y + SENSOR_OFFSET);
		}
		else if(heading == 270) {
			squareUp(true);
			x = Main.TILE_SIZE *x;
			odometer.setX(x + SENSOR_OFFSET);
		}
		
		odometer.setTheta(heading);
		leftMotor.rotate(convertDistance(WHEEL_RAD, SENSOR_OFFSET), true);
		rightMotor.rotate(convertDistance(WHEEL_RAD, SENSOR_OFFSET), false);
	}

	
	/**
	 * Square up uses the color sensors to square up with the line either infront or behind it
	 * this corrects one coordinate as well as the heading angle
	 * @param reverse a boolean that specifies if the bot should correct with the line infront or behind it
	 * @author Trevor O & Ahmed H
	 */
	public static void squareUp(boolean reverse) {
		
		rightMotor.setSpeed(MOTOR_ROTATE);
		leftMotor.setSpeed(MOTOR_ROTATE);
		
		if(reverse) {
			rightMotor.backward();
			leftMotor.backward();
		}
		else {
			rightMotor.forward();
			leftMotor.forward();
		}
		
	  while(rightMotor.isMoving() || leftMotor.isMoving()) {
		  
			RColor.fetchSample(RData, 0); // acquire data
			LColor.fetchSample(LData, 0);
			
		   	if(RData[0] < 0.4) {
		   		rightMotor.stop(true);
	    	}
		   	if(LData[0] < 0.4) {
		   		leftMotor.stop(true);
		   	}

		  }
	}
	
	/**
	 * This method will travel the shortest distance to the location you specify
	 * @param x is the final desired x position
	 * @param y is the final desired y position
	 * @author Trevor O & Winnie N
	 */
	public void travelTo(double x, double y){
		
		x = x * Main.TILE_SIZE;
		y = y * Main.TILE_SIZE;
		
		//get current position
		currentX = odometer.getX();
		currentY = odometer.getY();
		
		//compute the difference
		dX = x - currentX;
		dY = y - currentY;
		
		distance = Math.sqrt(Math.pow(dX, 2.0)+ Math.pow(dY, 2.0));
		Theta = (Math.atan2(dX, dY))*180/Math.PI;					//convert from radius to degree
		
		//rotate toward destination
		turnTo(Theta);
				
		//if there isn't block, just move straight
		rightMotor.setSpeed(MOTOR_STRAIGHT); 
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RAD, distance), false);

		
		}
	
	/**
	 * This method is used privately. It will turn the robot to a desired heading 
	 * @param Theta a double that denotes the desired heading direction
	 * @author Trevor O & Winnie N
	 */
	public void turnTo(double Theta) { 
		currentTheta = odometer.getTheta();		//currentTheta is in degree
		dTheta = Theta - currentTheta;
		
		//avoid maximal angle turn
		if(dTheta > 180){
			dTheta =  360 - dTheta;
		}
		else if(dTheta < -180){
			dTheta = 360 + dTheta;
		}

		//turn minimal Theta degree 
    	leftMotor.setSpeed(MOTOR_ROTATE);
    	rightMotor.setSpeed(MOTOR_ROTATE);
    	leftMotor.rotate(convertAngle(WHEEL_RAD, Main.TRACK, dTheta), true);
    	rightMotor.rotate(-convertAngle(WHEEL_RAD, Main.TRACK, dTheta), false);
		
    	try{
    		Thread.sleep(150);
    	} catch(InterruptedException e){} 
    	
	}
	
	
	  /**
	   * This method allows the conversion of a distance to the total rotation of each wheel need to
	   * cover that distance.
	   * 
	   * @param radius
	   * @param distance
	   * @return returns how many degrees each wheel must rotate to go a given distance
	   */
	  public static int convertDistance(double radius, double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * radius));
		  }

	  /**
	   * This method allwos the conversion of an angle to the total rotation of each wheel to turn that amount
	   * @param radius of the wheels
	   * @param width between the two wheels
	   * @param angle the desired angle to turn
	   * @return returns an int of degrees that is how much each wheel must rotate for the given angle
	   */
	  public static int convertAngle(double radius, double width, double angle) {
		    return convertDistance(radius, Math.PI * width * angle / 360.0);
		  }
}

