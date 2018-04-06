/**
 * This is an used as an object created by Main
 * It is used once at the begining of operation to determine and correct the heading angle of the robot
 * It uses the US sensor and the walls near it todo so.
 * 
 * @author Trevor O'Leary & Ahmed H
 * @Version 2.0
 * @since 2018-02-12
 */
package ca.mcgill.ecse211.pilot;

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.finalProject.WiFi;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	


	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	SampleProvider usDistance;
    float[] usData;
    private Odometer odo;
    private WiFi wifi;

    /**
     * Class Constructor
     * 
     * @param wifi so that it can get information from wifi class
     * @param leftMotor so that it can access the left motor
     * @param rightMotor
     * @param odo So that it can access and correct odometer values
     * @param usDistance so that it can read values from the ultrasonic sensor
     */
  public USLocalizer(WiFi wifi, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      Odometer odo, SampleProvider usDistance, float[] usData) throws OdometerExceptions {
	  
	  this.wifi = wifi;
	  this.leftMotor = leftMotor;
	  this.odo = odo;
	  this.rightMotor = rightMotor;
	  this.usDistance = usDistance;
	  this.usData = usData;
	  
	  
  }
/**
 * This method will correct the robots heading direction to a known value
 * It uses the ultrasonic sensor readings and a short rotation to find the walls surrounding it
 * On completion the robot will be heading at 0 degrees if in corner 0, 270 for corner 1, 180 at 2 and 90 at 3
 * 
 * @author Trevor O @ Ahmed H
 */
  public void localize() {
				rightMotor.setSpeed(Main.MOTOR_ROTATE);
				leftMotor.setSpeed(Main.MOTOR_ROTATE);
		if( getFilteredData()  > 200) {
			while(getFilteredData() > 15) {
				rightMotor.forward();
				leftMotor.backward();
			}
			rightMotor.stop(true);
			leftMotor.stop(true);
			Navigation.turn(55,false);
			
			if(wifi.startCorner == 0) {
				odo.setTheta(0);
			} else if (wifi.startCorner == 1) {
				odo.setTheta(270);
			} else if (wifi.startCorner == 2) {
				odo.setTheta(180);
			} else {
				odo.setTheta(90);
			}
	  } else {
		  
		  while(getFilteredData() < 100) {
				rightMotor.backward();
				leftMotor.forward();
			}
			rightMotor.stop(true);
			leftMotor.stop(true);
			Navigation.turn(45,false);
			
			if(wifi.startCorner == 0) {
				odo.setTheta(0);
			} else if (wifi.startCorner == 1) {
				odo.setTheta(270);
			} else if (wifi.startCorner == 2) {
				odo.setTheta(180);
			} else {
				odo.setTheta(90);
			}
	  }
  }
  
  /**
   * This method simply gets data from the ultrasonic sensor multiplies it by 100 as to convert it to cm
   * @return the value of distance the ultrasonic sensor reads as an in
   */
	public int getFilteredData() {
		usDistance.fetchSample(usData, 0);
		return (int) (usData[0] * 100);
	}
 
  
}