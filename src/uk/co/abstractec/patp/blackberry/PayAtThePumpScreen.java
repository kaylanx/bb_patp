package uk.co.abstractec.patp.blackberry;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import java.util.Vector;

public class PayAtThePumpScreen extends MainScreen {
    private GarageListField garageList;

    public PayAtThePumpScreen(Vector garages) {

        LabelField title = new LabelField("Pay At The Pump ",
                LabelField.ELLIPSIS | LabelField.FIELD_HCENTER);

        setTitle(title);

        garageList = new GarageListField();

        add(garageList);

        setGarages(garages);

        setStatus(new LabelField("(c)2009 Abstractec Ltd."));
    }

    public void setGarages(Vector garages) {
        Garage[] arrayOfGarages = new Garage[garages.size()];
        garages.copyInto(arrayOfGarages);

        garageList.setGarages(arrayOfGarages);

        this.addMenuItem(new MenuItem("Add Garage", 1, 2) {
            public void run() {
                addGarage();
            }
        });
    }

    private void addGarage() {
        Dialog.alert("Add Garage not currently supported");
    }

    /*
     * private static final GarageListItem[] FAKE_DATA = new GarageListItem[6];
     * static { // long today = (new java.util.Date()).getTime(); // long
     * yesterday = today - 1000 * 60 * 60 * 24; FAKE_DATA[0] = new
     * GarageListItem( "ASDA, Burnley1",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3);
     * FAKE_DATA[1] = new GarageListItem( "ASDA, Burnley2",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3);
     * FAKE_DATA[2] = new GarageListItem( "ASDA, Burnley3",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3);
     * FAKE_DATA[3] = new GarageListItem( "ASDA, Burnley4",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3);
     * FAKE_DATA[4] = new GarageListItem( "ASDA, Burnley5",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3);
     * FAKE_DATA[5] = new GarageListItem( "ASDA, Burnley6",
     * "Just off the B6434 and near the A679 and junction 11 of the M65", 3); }
     */

    public Garage getSelectedGarage() {
        return garageList.getSelectedGarage();
    }
}

