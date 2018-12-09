package uk.co.abstractec.patp.blackberry;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.LabelField;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import javax.microedition.location.*;

public class BlackberryPayAtThePump extends UiApplication implements LocationListener {

    public static void main(String[] args) {

        BlackberryPayAtThePump theApp = new BlackberryPayAtThePump();
        theApp.beginApplication();
        theApp.enterEventDispatcher();

    }

    private PayAtThePumpScreen mainScreen;
    private Connection conn;
    private double lat;
    private double lon;
    
    public void beginApplication() {

    	Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(500);
        criteria.setVerticalAccuracy(500);
        try {
            LocationProvider provider = LocationProvider.getInstance(criteria);
            if (provider == null || provider.getState() == LocationProvider.OUT_OF_SERVICE) {
            	MainScreen screen = new MainScreen();
            	screen.setTitle("Pay At The Pump - No GPS");
            	screen.add(new LabelField("Your BlackBerry does not have GPS functionality.  You need that to use this application, sorry!", LabelField.FIELD_HCENTER | LabelField.FIELD_VCENTER));
            	screen.addMenuItem(new MenuItem("Exit", 1, 2) {
                    public void run() {
                        System.exit(0);
                    }
                });
            	pushScreen(screen);
                return;
            }
            Location location = provider.getLocation(6000);
            final QualifiedCoordinates coords = location.getQualifiedCoordinates();
            lat = coords.getLatitude();
            lon = coords.getLongitude();
            conn = getConnection(coords);

            mainScreen = new PayAtThePumpScreen(conn.getGarages());

            mainScreen.addMenuItem(new MenuItem("Show Garage", 1, 1) {
                public void run() {
                    Garage garage = mainScreen.getSelectedGarage();
                    GarageDetailScreen garageScreen = new GarageDetailScreen(garage,coords);
                    pushScreen(garageScreen);
                }
            });

            pushScreen(mainScreen);
            provider.setLocationListener(this, 10, -1, -1);
        }
        catch (LocationException e) {
            e.printStackTrace();
            Dialog.alert(e.getMessage());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Dialog.alert(e.getMessage());
        }
    }

	private Connection getConnection(final QualifiedCoordinates coords) {
		Connection conn = new Connection(coords);
		conn.start();
		while (!conn.ready()) {
		    try {
		        Thread.sleep(100);
		    }
		    catch (InterruptedException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();

		    }
		}
		return conn;
	}

	public void locationUpdated(LocationProvider provider, Location newLocation) {
		
		Coordinates currentLocation = new Coordinates(lat,lon,newLocation.getQualifiedCoordinates().getAltitude());
		float distance = currentLocation.distance(newLocation.getQualifiedCoordinates());
		
		if ( distance > 1000 ) {
			lat = newLocation.getQualifiedCoordinates().getLatitude();
			lon = newLocation.getQualifiedCoordinates().getLongitude();
			conn = getConnection(newLocation.getQualifiedCoordinates());
			mainScreen.setGarages(conn.getGarages());
		}
		
	}

	public void providerStateChanged(LocationProvider provider, int newState) {
		// Don't care
	}

}
