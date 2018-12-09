package uk.co.abstractec.patp.blackberry;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.lbs.MapField;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import javax.microedition.location.Coordinates;


public class GarageDetailScreen extends MainScreen {

	public GarageDetailScreen(final Garage garage, final Coordinates currentLocation) {
		// this.garage = garage.copy();
		setTitle(garage.getName());

		int distance = garage.getDistanceInMiles();
		add(new LabelField("Distance: " + distance + (distance>1?" miles" :" mile"),
				LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));
		add(new LabelField("Details: "+garage.getDescription(),LabelField.USE_ALL_WIDTH));

		ButtonField viewRouteButton = new ButtonField("View Route",
				ButtonField.FIELD_HCENTER);

		final XYPoint screenField = new XYPoint();

		MapField mapField = new MapField() {
			protected void paint(Graphics g) {
				g.setDrawingStyle(Graphics.DRAWSTYLE_AAPOLYGONS, true);
				super.paint(g);

				convertWorldToField(garage.getCoordinates(), screenField);
				g.drawRect(screenField.x, screenField.y, 10, 10);
			}
		};
		mapField.moveTo(garage.getCoordinates());

		viewRouteButton.setChangeListener(new FieldChangeListener() {

			public void fieldChanged(Field field, int i) {
				try {
					int HUNDRED_THOUSAND = 100000;
					int curLon = (int)(currentLocation.getLongitude()*HUNDRED_THOUSAND);
					int curLat = (int)(currentLocation.getLatitude()*HUNDRED_THOUSAND);

					int destLon = (int)(garage.getCoordinates().getLongitude()*HUNDRED_THOUSAND);
					int destLat = (int)(garage.getCoordinates().getLatitude()*HUNDRED_THOUSAND);
					
					String document = "<location-document>"
							+ "<GetRoute>"
							+ "<location lon='"+curLon+"' lat='"+curLat+"' label='My Location' description='Current Location' />"
							+ "<location lon='"+destLon+"' lat='"+destLat+"' label='"+garage.getName()+"' description='"+garage.getDescription()+"' />"
							+ "</GetRoute></location-document>";

					Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
							new MapsArguments(
									MapsArguments.ARG_LOCATION_DOCUMENT,
									document));

				} catch (Exception e) {
					Dialog.alert(e.getClass().getName() + " " + e.getMessage());
				}
			}
		});

		add(viewRouteButton);
		add(mapField);
	}

}
