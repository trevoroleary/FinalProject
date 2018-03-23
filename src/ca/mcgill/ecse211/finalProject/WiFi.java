package ca.mcgill.ecse211.finalProject;

import java.util.Map;

import ca.mcgill.ecse211.WiFiClient.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * Example class using WifiConnection to communicate with a server and receive data concerning the
 * competition such as the starting corner the robot is placed in.
 * 
 * Keep in mind that this class is an **example** of how to use the WiFi code; you must use the
 * WifiConnection class yourself in your own code as appropriate. In this example, we simply show
 * how to get and process different types of data.
 * 
 * There are two variables you **MUST** set manually before trying to use this code.
 * 
 * 1. SERVER_IP: The IP address of the computer running the server application. This will be your
 * own laptop, until the beta beta demo or competition where this is the TA or professor's laptop.
 * In that case, set the IP to 192.168.2.3.
 * 
 * 2. TEAM_NUMBER: your project team number
 * 
 * Note: We System.out.println() instead of LCD printing so that full debug output (e.g. the very
 * long string containing the transmission) can be read on the screen OR a remote console such as
 * the EV3Control program via Bluetooth or WiFi. You can disable printing from the WiFi code via
 * ENABLE_DEBUG_WIFI_PRINT (below).
 * 
 * @author Michael Smith, Tharsan Ponnampalam
 *
 */
public class WiFi {

  // ** Set these as appropriate for your team and current situation **
  private static final String SERVER_IP = Main.SERVER_IP;
  private static final int TEAM_NUMBER = Main.TEAM_NUMBER;
  public boolean isRedTeam = false; //Red is true, Green is false
  
  //General
  public int startCorner;
  public int targetColor;
  
  //Zones
  public int[] Red_LL = new int[2];
  public int[] Red_UR = new int[2];
  public int[] Green_LL = new int[2];
  public int[] Green_UR = new int[2];
  
  //Obsticles
  public int[] Bridge_LL = new int[2];
  public int[] Bridge_UR = new int[2];
  public int[] Tunnel_LL = new int[2];
  public int[] Tunnel_UR = new int[2];
  
  //Search Regions
  public int[] SearchHome_LL = new int[2];
  public int[] SearchHome_UR = new int[2];
  public int[] Search_LL = new int[2];
  public int[] Search_UR = new int[2];
  

  // Enable/disable printing of debug info from the WiFi class
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

  @SuppressWarnings("rawtypes")
  
  
  public WiFi() {
	  
    //System.out.println("Running..");

    // Initialize WifiConnection class
    WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

    // Connect to server and get the data, catching any errors that might occur
    try {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
       * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
       * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
       * but receives corrupted data or a message from the server saying something went wrong. For
       * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
       * will receive a message saying an invalid team number was specified and getData() will throw
       * an exception letting you know.
       */
      Map data = conn.getData();

    
      int redTeamNumber = ((Long) data.get("RedTeam")).intValue();
      
	  if(TEAM_NUMBER == redTeamNumber) {
		 isRedTeam = true;
		 startCorner = ((Long) data.get("RedCorner")).intValue();
		 targetColor = ((Long) data.get("OG")).intValue();
		 
		 SearchHome_LL[0] = ((Long) data.get("SR_LL_x")).intValue();
		 SearchHome_LL[1] = ((Long) data.get("SR_LL_y")).intValue();
		 SearchHome_UR[0] = ((Long) data.get("SR_UR_x")).intValue();
		 SearchHome_UR[1] = ((Long) data.get("SR_UR_y")).intValue();
		 
		 Search_LL[0] = ((Long) data.get("SG_LL_x")).intValue();
		 Search_LL[1] = ((Long) data.get("SG_LL_y")).intValue();
		 Search_UR[0] = ((Long) data.get("SG_UR_x")).intValue();
		 Search_UR[1] = ((Long) data.get("SG_UR_y")).intValue();
	  } else {
		 startCorner = ((Long) data.get("GreenCorner")).intValue();
		 targetColor = ((Long) data.get("OR")).intValue();
		 
		 SearchHome_LL[0] = ((Long) data.get("SG_LL_x")).intValue();
		 SearchHome_LL[1] = ((Long) data.get("SG_LL_y")).intValue();
		 SearchHome_UR[0] = ((Long) data.get("SG_UR_x")).intValue();
		 SearchHome_UR[1] = ((Long) data.get("SG_UR_y")).intValue();
		 
		 Search_LL[0] = ((Long) data.get("SR_LL_x")).intValue();
		 Search_LL[1] = ((Long) data.get("SR_LL_y")).intValue();
		 Search_UR[0] = ((Long) data.get("SR_UR_x")).intValue();
		 Search_UR[1] = ((Long) data.get("SR_UR_y")).intValue();
	  }
	  
	  Red_LL[0] = ((Long) data.get("Red_LL_x")).intValue();
	  Red_LL[1] = ((Long) data.get("Red_LL_y")).intValue();
	  Red_UR[0] = ((Long) data.get("Red_UR_x")).intValue();
	  Red_UR[1] = ((Long) data.get("Red_UR_y")).intValue();
	  
	  Green_LL[0] = ((Long) data.get("Green_LL_x")).intValue();
	  Green_LL[1] = ((Long) data.get("Green_LL_y")).intValue();
	  Green_UR[0] = ((Long) data.get("Green_UR_x")).intValue();
	  Green_UR[1] = ((Long) data.get("Green_UR_y")).intValue();
	  
	  Bridge_LL[0] = ((Long) data.get("BR_LL_x")).intValue();
	  Bridge_LL[1] = ((Long) data.get("BR_LL_y")).intValue();
	  Bridge_UR[0] = ((Long) data.get("BR_UR_x")).intValue();
	  Bridge_UR[1] = ((Long) data.get("BR_UR_y")).intValue();
	  
	  Tunnel_LL[0] = ((Long) data.get("TN_LL_x")).intValue();
	  Tunnel_LL[1] = ((Long) data.get("TN_LL_y")).intValue();
	  Tunnel_UR[0] = ((Long) data.get("TN_UR_x")).intValue();
	  Tunnel_UR[1] = ((Long) data.get("TN_UR_y")).intValue();
	  
    	/*
    	int redTeamNumber = 1;
        
  	  if(TEAM_NUMBER == redTeamNumber) {
  		 isRedTeam = true;
  		 startCorner = 3;
  		 targetColor = 3;
  		 
  		 SearchHome_LL[0] = 1;
  		 SearchHome_LL[1] = 4;
  		 SearchHome_UR[0] = 3;
  		 SearchHome_UR[1] = 6;
  		 
  		 Search_LL[0] = 5;
  		 Search_LL[1] = 0;
  		 Search_UR[0] = 6;
  		 Search_UR[1] = 1;
  	  } else {
  		 startCorner = 1;
  		 targetColor = 1;
  		 
  		 SearchHome_LL[0] = 5;
  		 SearchHome_LL[1] = 0;
  		 SearchHome_UR[0] = 6;
  		 SearchHome_UR[1] = 1;
  		 
  		 Search_LL[0] = 1;
  		 Search_LL[1] = 4;
  		 Search_UR[0] = 3;
  		 Search_UR[1] = 6;
  	  }
  	  
  	  Red_LL[0] = 0;
  	  Red_LL[1] = 4;
  	  Red_UR[0] = 5;
  	  Red_UR[1] = 8;
  	  
  	  Green_LL[0] = 3;
  	  Green_LL[1] = 0;
  	  Green_UR[0] = 8;
  	  Green_UR[1] = 2;
  	  
  	  Bridge_LL[0] = 4;
  	  Bridge_LL[1] = 2;
  	  Bridge_UR[0] = 5;
  	  Bridge_UR[1] = 4;
  	  
  	  Tunnel_LL[0] = 3;
  	  Tunnel_LL[1] = 2;
  	  Tunnel_UR[0] = 4;
  	  Tunnel_UR[1] = 4;
	  */
	  LCD.clear();

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  
}
