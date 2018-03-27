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
	 * @return
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
	 * @return
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

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

	public void sensorRight() {
		if (!isRight) {
			isRight = true;
			sensorMotor.rotate(-90);
		}

	}

	public void sensorForward() {
		if (isRight) {
			isRight = false;
			sensorMotor.rotate(90);
		}
	}

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
