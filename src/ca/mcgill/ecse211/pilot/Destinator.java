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

	public void gotoCheckPoint() {
		if (destState == 0) {
			
			if (wifi.isRedTeam) {
				gotoBridge();
			} else {
				gotoTunnel();
			}
			

		} else if (destState == 1) {
		
			gotoSearch();
		

		} else if (destState == 2) {
			if (wifi.isRedTeam) {
				gotoTunnel();
			} else {
				gotoBridge();
			}
			
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
		destState++;
	}

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

	public void gotoLowerLeft(int[] LL, int[] UR) {
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int dx = x - LL[0];
		int dy = y - LL[1];

		if (x < LL[0]) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x, y - i, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(x, LL[1], true);
			nav.travelTo(LL[0], LL[1], true);
		}

		else if (y < LL[1]) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x - i, y, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(LL[0], y, true);
			nav.travelTo(LL[0], LL[1], true);
		}

		else if (UR[0] != x) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x, y - i, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(x, LL[1], true);
			for (int i = 1; i < dx; i++) {
				nav.travelTo(x - i, LL[1], true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(LL[0], LL[1], true);
		}

		nav.turnTo(90, true);
		nav.turnTo(0, true);
	}

	public void goToUpperRight(int[] UR) {
		int x = (int) ((odometer.getX() / Main.TILE_SIZE) + 0.5);
		int y = (int) ((odometer.getY() / Main.TILE_SIZE) + 0.5);
		int dx = x - UR[0];
		int dy = y - UR[1];

		if (x < UR[0]) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x, y - i, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(x, UR[1], true);
			nav.travelTo(UR[0], UR[1], true);
		}

		else if (y < UR[1]) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x - i, y, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(UR[0], y, true);
			nav.travelTo(UR[0], UR[1], true);
		}

		else if (UR[0] != x) {
			for (int i = 1; i < dy; i++) {
				nav.travelTo(x, y - i, true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(x, UR[1], true);
			for (int i = 1; i < dx; i++) {
				nav.travelTo(x - i, UR[1], true);
				if (i % 2 == 0)
					nav.turnTo(90, true);
			}
			nav.travelTo(UR[0], UR[1], true);
		}

		nav.turnTo(90, true);
		nav.turnTo(0, true);
	}
}
