package ca.mcgill.ecse211.pilot;

import ca.mcgill.ecse211.*;
import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class is used to Navigate through a series of way points
 */

public class Navigation extends Thread {

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private static final int MOTOR_STRAIGHT = Main.MOTOR_STRAIGHT;
	private static final int MOTOR_ROTATE = Main.MOTOR_ROTATE;
	public static final double WHEEL_RAD = 2.12;
	private boolean isNavigating;
	private Odometer odometer;
	public static int pointcounter;
	public LightLocalizer lightLocalizer;

	// angle toward destination
	private double Theta;
	private int[] LL;
	private int[] UR;
	// current position
	private double currentX;
	private double currentY;
	private double currentTheta;

	// difference between current position and destination position
	private double dX; //
	private double dY; //
	// distance between current position and destination position
	private double distance;

	/**
	 * This is the class constructor
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param LL
	 * @param UR
	 * 
	 */
	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			LightLocalizer lightLocalizer, int[] LL, int[] UR) {

		this.odometer = odo;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.lightLocalizer = lightLocalizer;
		this.UR = UR;
		this.LL = LL;

		try {
			this.odometer = Odometer.getOdometer();
		} catch (OdometerExceptions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	/**
	 * This method is used to calculate the distance to map point given
	 * Cartesian coordinates x and y
	 * 
	 * @param x
	 * @param y
	 * 
	 */
	public void travelTo(double x, double y, boolean correct) {
		isNavigating = true;

		x = x * Main.TILE_SIZE;
		y = y * Main.TILE_SIZE;

		// get current position
		currentX = odometer.getX();
		currentY = odometer.getY();

		// compute the difference
		dX = x - currentX;
		dY = y - currentY;

		distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		Theta = Math.toDegrees((Math.atan2(dX, dY))); // convert from radius to
														// degree

		// rotate toward destination
		turnTo(Theta, correct);

		// move straight
		rightMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RAD, distance), false);

		isNavigating = false;
	}

	/**
	 * This method is used to make sure robot rotates at the minimum angle when
	 * traveling to next waypoint
	 * 
	 */
	public void turnTo(double theta, boolean localizer) {
		boolean turnleft = false;
		double currTheta = odometer.getTheta();
		double angle = theta - currTheta;

		if (angle < -180) {
			angle = angle + 360;
		}
		if (angle > 180) {
			angle = angle - 360;
		}
		if (angle < 0) {
			turnleft = true;
			angle = Math.abs(angle);
		} else {
			turnleft = false;
		}

		leftMotor.setSpeed(MOTOR_ROTATE);
		rightMotor.setSpeed(MOTOR_ROTATE);

		if (turnleft) {
			leftMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, angle), true);
			rightMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, angle), false);
		} else {
			leftMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, angle), true);
			rightMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, angle), false);
		}

		currentTheta = theta;
		if (localizer)
			lightLocalizer.correctLocation();
	}

	public void turn(double theta, boolean correct) {
		leftMotor.setSpeed(MOTOR_ROTATE);
		rightMotor.setSpeed(MOTOR_ROTATE);

		leftMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, theta), true);
		rightMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, theta), false);
		
		if(correct) {
			lightLocalizer.correctLocation();
		}
	}

	/**
	 * This method determines whether another thread has called travelTo and
	 * turnTo methods or not
	 * 
	 * @return
	 */

	public boolean isNavigating() {
		return isNavigating;
	}

	/**
	 * This method allows the conversion of a distance to the total rotation of
	 * each wheel need to cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @return
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * This method allows the conversion of an angle to the total rotation of
	 * each wheel need to cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @param angle
	 * @return
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}


	public void travelToforEdge(double x, double y) {
		isNavigating = true;

		x = x * Main.TILE_SIZE;
		y = y * Main.TILE_SIZE;

		// get current position
		currentX = odometer.getX();
		currentY = odometer.getY();

		// compute the difference
		dX = x - currentX;
		dY = y - currentY;

		distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		Theta = Math.toDegrees((Math.atan2(dX, dY))); // convert from radius to
														// degree

		// rotate toward destination

		turnTo(Theta, false);

		// move straight
		rightMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RAD, distance), false);

		isNavigating = false;
	}
	
	public void travelToNearestEdge() {
		int xNext = (int) ((odometer.getX() / Main.TILE_SIZE) + 1);
		int xDown = (int) (odometer.getX() / Main.TILE_SIZE);
		int yNext = (int) ((odometer.getY() / Main.TILE_SIZE) + 1);
		int yDown = (int) (odometer.getY() / Main.TILE_SIZE);

		double theta = odometer.nearestHeading();

		if (theta == 90) {
			travelToforEdge(LL[0], yNext);
			turnTo(0, true);
		} else if (theta == 180) {
			travelToforEdge(xNext, UR[1]);
			turnTo(90, true);
		} else if (theta == 270) {
			travelToforEdge(UR[0], yDown);
			turnTo(180, true);
		} else if (theta == 0) {
			travelToforEdge(xDown, LL[1]);
			turnTo(270, true);
		}
	}

}
