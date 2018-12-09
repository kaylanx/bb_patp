package uk.co.abstractec.patp.blackberry;

import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.api.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.location.QualifiedCoordinates;
import java.io.IOException;
import java.util.Vector;

public class Connection extends Thread {

    private final static String GARAGES_URL = "http://www.payatthepump.co.uk/PayAtThePump/rest/garages.seam/";

    private QualifiedCoordinates coords;
    private boolean ready = false;
    private Vector garages = new Vector();

    public Connection(QualifiedCoordinates coords) {
        super();
        this.coords = coords;
    }

    public Vector getGarages() {
        return garages;
    }

    public boolean ready() {
        return ready;
    }

    public void run() {
        // define variables later used for parsing
        Document doc;
        StreamConnection conn;

        try {
            // providing the location of the XML file,
            // your address might be different
            String fullUrl = getUrl();
            conn = (StreamConnection) Connector.open(fullUrl);
            // next few lines creates variables to open a
            // stream, parse it, collect XML data and
            // extract the data which is required.
            // In this case they are elements,
            // node and the values of an element
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory
                    .newDocumentBuilder();
            docBuilder.isValidating();
            doc = docBuilder.parse(conn.openInputStream());
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("*");
            // this "for" loop is used to parse through the
            // XML document and extract all elements and their
            // value, so they can be displayed on the device

            Garage garage = new Garage();
            for (int i = 0; i < list.getLength(); i++) {

                String node;
                String element = "";

                if (list.item(i).hasAttributes()) {
                    Node value = list.item(i).getAttributes().item(0);
                    element = (value == null ? "nullAttrib" : value
                            .getNodeValue());
                } else if (list.item(i).hasChildNodes()) {
                    Node value = list.item(i).getChildNodes().item(0);
                    element = (value == null ? "nullChild" : value
                            .getNodeValue());
                }
                node = list.item(i).getNodeName();

                // name=ASDA, Rochdale
                // created=2008-10-13T22:11:11Z
                // createdBy=jimbob
                // description=Near the A606 and A58
                // latitude=53.6149
                // longitude=-2.16568
                // link=http://www.payatthepump.co.uk/PayAtThePump/mobile/garage.seam/316

                if (node.equals("name")) {
                    garage.setName(element);
                } else if (node.equals("created")) {
                    // garage.setCreated(element); // TODO parse date
                } else if (node.equals("createdBy")) {
                    garage.setCreatedBy(element);
                } else if (node.equals("description")) {
                    garage.setDescription(element);
                } else if (node.equals("latitude")) {
                    garage.setLatitude(Double.parseDouble(element));
                } else if (node.equals("longitude")) {
                    garage.setLongitude(Double.parseDouble(element));
                } else if (node.equals("link")) {
                    garage.setLink(element);
                    garage.calcDistance(coords);
                } else if (node.equals("garage")) {
                    garage = new Garage();
                    garages.addElement(garage);
                }

                // System.out.println(node + "=" + element);
                // updateField(_node,_element);
            }// end for

            ready = true;
        }// end try
        // will catch any exception thrown by the XML parser
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        catch (SAXException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }// end connection function

	private String getUrl() {
		String fullUrl = GARAGES_URL + coords.getLatitude() + "/" + coords.getLongitude() + "/50";
		LeastCostRouter router = new LeastCostRouter();
		return router.getUrl(fullUrl);
	}
}// end connection class


