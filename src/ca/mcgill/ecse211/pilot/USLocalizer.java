/**
 * This class is an object
 * It is called whenever the main robot needs to get to a specific coordinate
 * 
 * 
 * @author Winnie Nyakundi & Trevor O'Leary
 * @Version 1.0
 * @since 2018-02-12
 */
package ca.mcgill.ecse211.pilot;

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.finalProject.WiFi;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
/**
 * This class is used to drive the robot on the demo floor.
 */
public class USLocalizer {
	


	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	SampleProvider usDistance;
    float[] usData;
    private Odometer odo;
    private WiFi wifi;

  public USLocalizer(WiFi wifi, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      Odometer odo, SampleProvider usDistance, float[] usData) throws OdometerExceptions {
	  
	  this.wifi = wifi;
	  this.leftMotor = leftMotor;
	  this.odo = odo;
	  this.rightMotor = rightMotor;
	  this.usDistance = usDistance;
	  this.usData = usData;
	  
	  
  }

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
  
	public int getFilteredData() {
		usDistance.fetchSample(usData, 0);
		return (int) (usData[0] * 100);
	}
 
  
}