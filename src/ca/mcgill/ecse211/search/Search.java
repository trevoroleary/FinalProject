/**
 * This object is responsible for all of the searching the robot does
 * The search algorithem essentially tracks around the perimeter of the search region looking for blocks to its right
 * If a block is seen by the US sensor the robot moves towards it and attempts to scan it with the light sensor infront of it
 * 
 * @author Trevor O & Ahmed H
 * @version 1.0
 * @since 2018-02-1
 */
package ca.mcgill.ecse211.search;

import ca.mcgill.ecse211.color.colorSensor;
import ca.mcgill.ecse211.finalProject.*;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.pilot.Destinator;
import ca.mcgill.ecse211.pilot.Navigation;
import ca.mcgill.ecse211.pilot.USLocalizer;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import java.util.Timer;
import java.util.TimerTask;

public class Search {

	private EV3LargeRegulatedMotor sensorMotor;
	private final int[] LL;
	private final int[] UR;

	private Odometer odometer;
	private Navigation navigator;
	private Destinator destinator;
	private USLocalizer USData;
	private boolean upTrue = true;
	private boolean isRight = false;
	private float avgData;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	int turnCounter = 0;

	private float blockInFront = 50;
	private boolean navigating = false;
	private boolean foundSomething = false;

	public Search(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int[] LL, int[] UR,
			colorSensor colorSensor, Odometer odometer, USLocalizer USData, Navigation navigator, Destinator destinator,
			EV3LargeRegulatedMotor sensorMotor) {

		this.sensorMotor = sensorMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.LL = LL;
		this.UR = UR;
		this.odometer = odometer;
		this.navigator = navigator;
		this.USData = USData;

		leftMotor.setSpeed(Main.MOTOR_STRAIGHT);
		rightMotor.setSpeed(Main.MOTOR_STRAIGHT);
	}

	/**
	 * This method initiates searching should only be called one the robot is in the
	 * LL of the search area.
	 * 
	 */
	public void beginSearch() {

		while (!foundSomething && turnCounter != 4) {
			if (colorSensor.targetColor == colorSensor.sensorColor) {
				foundSomething = true;
				break;
			} else {
				goUp();
			}
		}
		sensorForward();
		// destinator.goToUpperRight(UR);
	}

	/**
	 * This method allows the conversion of a distance to the total rotation of each
	 * wheel need to cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @return the int of degrees required for each wheel to turn to cover the desired distance
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * This method allows the conversion of an angle to the total rotation of each
	 * wheel need to cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @param angle
	 * @return an int of the degrees required for each wheel to turn to turn the desired angle
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * This method is what is used once the ultrasonic sensor finds an object. 
	 * This method goes close to the block and scans the block using the color sensor.
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void getBlock() {
		navigator.turn(90, false);
		sensorForward();

		navigating = true;
		Odometer.leftMotor.rotate(convertDistance(Main.WHEEL_RAD, Main.TILE_SIZE), true);
		Odometer.rightMotor.rotate(convertDistance(Main.WHEEL_RAD, Main.TILE_SIZE), true);

		while (navigating) {
			if (USData.getFilteredData() < 4) {
				Odometer.leftMotor.stop(true);
				Odometer.rightMotor.stop(false);
				navigating = false;
			}
			if (!Odometer.leftMotor.isMoving() && !Odometer.rightMotor.isMoving()) {
				navigating = false;
			}
			if (colorSensor.seeColor()) {
				Odometer.leftMotor.stop(true);
				Odometer.rightMotor.stop(false);
				navigating = false;
				LCD.drawString("Found" + colorSensor.getResponse(), 0, 6, false);
				foundSomething = true;

			}
		}

		navigator.travelToNearestEdge();

	}

	/**
	 * When this method is called it ensures the sensor is facing towards the right
	 * 
	 * @author Ahmed H
	 */
	public void sensorRight() {
		if (!isRight) {
			isRight = true;
			sensorMotor.rotate(-90);
		}

	}

	/**
	 * When this method is called it ensures the sensor is facing forwards
	 * 
	 * @author Ahmed H
	 */
	public void sensorForward() {
		if (isRight) {
			isRight = false;
			sensorMotor.rotate(90);
		}
	}

	/**
	 * This method is what tracks the robot tile by tile aorund the search region
	 * each time its called it moves 1 tile length while scanning towards the right with the US sensor
	 * If the robot is at the corner of the search region it will turn and continue onwards
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void goUp() {

		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);

		sensorRight();

		double theta = odometer.nearestHeading();

		if (turnCounter < 4) {
			if (y == UR[1] && theta == 0) {
				navigator.turnTo(90, true);
				turnCounter++;
			} else if (x == UR[0] && theta == 90) {
				navigator.turnTo(180, true);
				turnCounter++;
			} else if (y == LL[1] && theta == 180) {
				navigator.turnTo(270, true);
				turnCounter++;
			} else if (x == LL[0] && theta == 270) {
				navigator.turnTo(0, true);
				turnCounter++;
				// destinator.goToUpperRight(UR);
				// foundSomething = true;
			}
		}
		if (turnCounter == 4) {
			foundSomething = true;
		}
		if (turnCounter != 4) {
			rightMotor.rotate(convertDistance(Main.WHEEL_RAD, Main.TILE_SIZE), true);
			leftMotor.rotate(convertDistance(Main.WHEEL_RAD, Main.TILE_SIZE), true);

			navigating = true;

			while (navigating) {

				if (USData.getFilteredData() < 30) {
					Odometer.rightMotor.stop(true);
					Odometer.leftMotor.stop(true);
					navigating = false;
					getBlock();

				} else if (!Odometer.rightMotor.isMoving() && !Odometer.leftMotor.isMoving()) {
					navigating = false;
				}
			}
		}
	}

}
