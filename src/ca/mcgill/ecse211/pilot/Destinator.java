/**
 * This class is an object created by Main
 * It posseses a simple finite state machine and high level movement methods
 * 
 * This class manages the checkpoints and tasks of the bot throughout execution
 * 
 * @author Trevor O & Ahmed H
 * @version 1.0
 * @since 2018-03-20
 */
package ca.mcgill.ecse211.pilot;

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.finalProject.WiFi;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.pilot.Navigation;

public class Destinator {

	private Navigation nav;
	private Odometer odometer;
	private WiFi wifi;
	/*
	 * 0 = Go to tunnel/bridge 1 = go to searchRegion start 2 = go to bridge/tunnel
	 * 3 = go home
	 */
	private int destState = 0;

	public Destinator(Navigation nav, Odometer odometer, WiFi wifi) {
		this.nav = nav;
		this.odometer = odometer;
		this.wifi = wifi;
	}
/**
 * this method is effectivly the finite state machine
 * It references an integer that increments as the bot moves through the checkpoint 
 * depending on the value of the integer this method can determine the current state of the robot and command its next task
 * 
 * @author Trevor O & Ahmed H
 */
	public void gotoCheckPoint() {
		if (destState == 0) {
			if (wifi.isRedTeam) {
				gotoBridge();
			} else {
				gotoTunnel();
			}
			destState = 2;
		} else if (destState == 1) {
			gotoSearch();

		} else if (destState == 2) {
			if (wifi.isRedTeam) {
				gotoTunnel();
			} else {
				gotoBridge();
			}
			destState = 3;
		} else {
			int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
			int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
			
			if (y > wifi.Bridge_UR[1]) {
				nav.travelTo(1,Main.GRID_SIZE -1,false);
				nav.turnTo(90, true);
			} else {
				nav.travelTo(Main.GRID_SIZE -1 ,1,false);
				nav.turnTo(270, true);
			}
		}
		//destState++;
	}

	
	/**
	 * This method is used privatly by gotoCheckPoint
	 * It considers the y location of the bot to determine where around the bridge it should navigate the robot to
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void gotoBridge() {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);

		if (y > wifi.Bridge_UR[1]) {

			// nav.travelTo(wifi.Bridge_UR[0] - 1, wifi.Bridge_UR[1] + 1);

			changeY(wifi.Bridge_UR[1] + 1);
			changeX(wifi.Bridge_UR[0] - 1);
			nav.turnTo(90, true);
		} else {

			// nav.travelTo(wifi.Bridge_LL[0] + 1, wifi.Bridge_LL[1] - 1);

			changeY(wifi.Bridge_LL[1] - 1);
			changeX(wifi.Bridge_LL[0] + 1);
			nav.turnTo(270, true);
		}

		goOverBridge();
	}

	/**
	 * This method places the bot in the middle of two tiles at the start of the bridge
	 * it squares up with the bridge to ensure its straight and then traveles 3 blocks forward with no correction
	 * Finally it corrects once it crosses the bridge
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void goOverBridge() {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		if (y > wifi.Bridge_UR[1]) {
			nav.travelTo(wifi.Bridge_LL[0] + 0.45, wifi.Bridge_UR[1] + 0.5, false);
			nav.turnTo(180, false);
			LightLocalizer.squareUp(false);
			odometer.setTheta(180);
			nav.travelTo(wifi.Bridge_LL[0] + 0.45, wifi.Bridge_UR[1] - 3, false);

			nav.travelTo(wifi.Bridge_LL[0] + 1, wifi.Bridge_LL[1] - 1, false);
			nav.turn(90, true);
		} else {

			nav.travelTo(wifi.Bridge_LL[0] + 0.45, wifi.Bridge_UR[1] - 0.5, false);
			nav.turnTo(0, false);
			LightLocalizer.squareUp(false);
			odometer.setTheta(0);
			
			nav.travelTo(wifi.Bridge_LL[0] + 0.45, wifi.Bridge_LL[1] + 3, false);
			LightLocalizer.squareUp(false);

			nav.travelTo(wifi.Bridge_LL[0], wifi.Bridge_UR[1] + 1, false);
			nav.turn(90, true);

		}
	}

	/**
	 * This method functions the same as go to Bridge but for the tunnel
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void gotoTunnel() {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);

		if (y < wifi.Tunnel_UR[1]) {

			// nav.travelTo(wifi.Bridge_UR[0] - 1, wifi.Bridge_UR[1] + 1);

			changeY(wifi.Tunnel_LL[1] - 1);
			changeX(wifi.Tunnel_LL[0] + 1);
			nav.turnTo(270, true);
		} else {

			// nav.travelTo(wifi.Bridge_LL[0] + 1, wifi.Bridge_LL[1] - 1);

			changeY(wifi.Tunnel_UR[1] + 1);
			changeX(wifi.Tunnel_UR[0] - 1);
			nav.turnTo(90, true);
		}

		goThroughTunnel();
	}

	
	/**
	 * This methid is the same as goOverBridge but for the tunnel
	 * 
	 * @author Trevor O & Ahmed H
	 */
	@SuppressWarnings("static-access")
	public void goThroughTunnel() {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		if (y > wifi.Tunnel_UR[1]) {
			nav.travelTo(wifi.Tunnel_LL[0] + 0.5, wifi.Tunnel_UR[1] + 0.5, false);
			nav.turnTo(180, false);
			LightLocalizer.squareUp(false);
			odometer.setTheta(180);
			
			nav.travelTo(wifi.Tunnel_LL[0] + 0.5, wifi.Tunnel_UR[1] - 3, false);

			nav.travelTo(wifi.Tunnel_LL[0] + 1, wifi.Tunnel_LL[1] - 1, false);
			nav.turn(90, true);
		} else {

			nav.travelTo(wifi.Tunnel_LL[0] + 0.5, wifi.Tunnel_LL[1] - 0.5, false);			
			nav.turnTo(0, false);
			LightLocalizer.squareUp(false);
			odometer.setTheta(0);
			
			nav.travelTo(wifi.Tunnel_LL[0] + 0.5, wifi.Tunnel_LL[1] + 3, false);
			

			nav.travelTo(wifi.Tunnel_LL[0], wifi.Tunnel_UR[1] + 1, false);
			nav.turn(90, true);

		}
	}

	/**
	 * This method navigates the robot to the closest corner of the search region and orients it so that its facing clockwise with respect to the search region
	 * 
	 * @author Trevor O & Ahmed H
	 */
	public void gotoSearch() {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);

		if (y > wifi.Tunnel_UR[1]) {
			changeY(wifi.Search_LL[1]);
			changeX(wifi.Search_UR[0]);
			nav.turnTo(270, true);			
		} else {
			changeX(wifi.Search_LL[0]);
			changeY(wifi.Search_UR[1]);
			nav.turnTo(90, true);
		}
	}

	/**
	 * This method navigates the robot by single tile lengths in the x direction and corrects on each tile
	 * 
	 * @param destX is the desired x destination
	 * @author Trevor O
	 */
	public void changeX(int destX) {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		int deltaX = Math.abs(destX - x);

		if ((destX - x) > 0) {
			for (int i = deltaX - 1; i >= 0; i--) {
				if(i%2 == 1) {
					nav.travelTo(destX - i, y, false);
				}
				if (i % 2 == 0) {
					nav.travelTo(destX - i, y, true);
					nav.turn(90, true);
				}
			}
		} else {
			for (int i = deltaX - 1; i >= 0; i--) {
				if(i%2 == 1) {
					nav.travelTo(destX + i, y, false);
				}
				if (i % 2 == 0) {
					nav.travelTo(destX + i, y, true);
					nav.turn(90, true);
				}
			}
		}
	}

	/**
	 * This method navigates the robot by single tile length in the y direction and corrects on each tile
	 * 
	 * @param destY is the desired y position
	 * @author Trevor O
	 */
	public void changeY(int destY) {
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		int deltaY = Math.abs(destY - y);

		if ((destY - y) > 0) {
			for (int i = deltaY - 1; i >= 0; i--) {
				if(i%2==1) {
					nav.travelTo(x, destY - i, false);
				}
				if (i % 2 == 0) {
					nav.travelTo(x, destY - i, true);
					nav.turn(90, true);
				}
			}
		} else {
			for (int i = deltaY - 1; i >= 0; i--) {
				if(i%2 == 1) {
					nav.travelTo(x, destY + i, false);
				}
				if (i % 2 == 0) {
					nav.travelTo(x, destY + i, true);
					nav.turn(90, true);
				}
			}
		}
	}
}
