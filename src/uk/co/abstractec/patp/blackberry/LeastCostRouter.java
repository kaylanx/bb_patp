package uk.co.abstractec.patp.blackberry;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.util.DataBuffer;

import java.io.EOFException;

public class LeastCostRouter {
    /**
     * CONFIG_TYPE_ constants which are used to find appropriate service books.
     * TODO Currently only Unite is detected this way.
     */
    private static final int CONFIG_TYPE_WAP = 0;
    private static final int CONFIG_TYPE_BES = 1;
    private static final int CONFIG_TYPE_WIFI = 3;
    private static final int CONFIG_TYPE_BIS = 4;
    private static final int CONFIG_TYPE_WAP2 = 7;
    private static final String UNITE_NAME = "Unite";

    private ServiceRecord srMDS, srBIS, srWAP, srWAP2, srWiFi, srUnite;
    private boolean coverageTCP = false, coverageMDS = false,
            coverageBIS = false, coverageWAP = false, coverageWAP2 = false,
            coverageWiFi = false, coverageUnite = false;

    public LeastCostRouter() {
        initializeTransportAvailability();
    }

    private void initializeTransportAvailability() {
        ServiceBook sb = ServiceBook.getSB();
        ServiceRecord[] records = sb.getRecords();

        for (int i = 0; i < records.length; i++) {
            ServiceRecord myRecord = records[i];
            String cid, uid;

            if (myRecord.isValid() && !myRecord.isDisabled()) {
                cid = myRecord.getCid().toLowerCase();
                uid = myRecord.getUid().toLowerCase();
                // BIS
                if (getUrl(cid).indexOf("ippp") != -1
                        && getUrl(uid).indexOf("gpmds") != -1) {
                    srBIS = myRecord;
                }

                // BES
                if (getUrl(cid).indexOf("ippp") != -1
                        && getUrl(uid).indexOf("gpmds") == -1) {
                    srMDS = myRecord;
                }
                // WiFi
                if (getUrl(cid).indexOf("wptcp") != -1
                        && getUrl(uid).indexOf("wifi") != -1) {
                    srWiFi = myRecord;
                }
                // Wap1.0
                if (getConfigType(myRecord) == CONFIG_TYPE_WAP
                        && getUrl(cid).equalsIgnoreCase("wap")) {
                    srWAP = myRecord;
                }
                // Wap2.0
                if (getUrl(cid).indexOf("wptcp") != -1
                        && getUrl(uid).indexOf("wifi") == -1
                        && getUrl(uid).indexOf("mms") == -1) {
                    srWAP2 = myRecord;
                }
                // Unite
                if (getConfigType(myRecord) == CONFIG_TYPE_BES
                        && myRecord.getName().equals(UNITE_NAME)) {
                    srUnite = myRecord;
                }
            }
        }
        if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B)) {
            coverageBIS = true;

        }
        if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT)) {
            coverageTCP = true;
            coverageWAP = true;
            coverageWAP2 = true;
        }
        if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS)) {
            coverageMDS = true;
            coverageUnite = true;
        }

        if (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) {
            coverageWiFi = true;
        }

    }

    /***
     * Me: I need an HTTP connection, is Wifi available?
     BB: No.
     Me: Is BES available?
     BB: No.
     Me: Is BIS available?
     BB: Yes.
     BB: Ooops, that file is over the size limit for BIS.
     Me: Is TCP available?
     BB: Yes.
     BB: Ooops, TCP looks available but it was blocked by the carrier's firewall.....
     * @param fullUrl
     * @return
     */
    public String getUrl(String fullUrl) {
        // ick...
        if (coverageWiFi) {
            EventLogger.logEvent(0x4e697e68da459c1cL, "Using WIFI".getBytes(), EventLogger.ALWAYS_LOG);
            return fullUrl + ";interface=wifi";
        } else if (coverageMDS) {
            EventLogger.logEvent(0x4e697e68da459c1cL, "Using MDS".getBytes(), EventLogger.ALWAYS_LOG);
            return fullUrl + ";deviceside=false";
        } else if (coverageBIS) {
            // Not implemented since this is only available to ISV partners of RIM
        } else if (coverageTCP) {
            EventLogger.logEvent(0x4e697e68da459c1cL, "Using TCP".getBytes(), EventLogger.ALWAYS_LOG);
            return fullUrl + ";deviceside=true";
        } else {
            EventLogger.logEvent(0x4e697e68da459c1cL, "Using Default".getBytes(), EventLogger.ALWAYS_LOG);
        }
        return fullUrl;
    }

    /**
     * Gets the config type of a ServiceRecord using getDataInt below
     *
     * @param record A ServiceRecord
     * @return configType of the ServiceRecord
     */
    private int getConfigType(ServiceRecord record) {
        return getDataInt(record, 12);
    }

    /**
     * Gets the config type of a ServiceRecord. Passing 12 as type returns the configType.
     *
     * @param record A ServiceRecord
     * @param type   dataType
     * @return configType
     */
    private int getDataInt(ServiceRecord record, int type) {
        DataBuffer buffer = null;
        buffer = getDataBuffer(record, type);

        if (buffer != null) {
            try {
                return ConverterUtilities.readInt(buffer);
            } catch (EOFException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Utility Method for getDataInt()
     */
    private DataBuffer getDataBuffer(ServiceRecord record, int type) {
        byte[] data = record.getApplicationData();
        if (data != null) {
            DataBuffer buffer = new DataBuffer(data, 0, data.length, true);
            try {
                buffer.readByte();
            } catch (EOFException e1) {
                return null;
            }
            if (ConverterUtilities.findType(buffer, type)) {
                return buffer;
            }
        }
        return null;
    }

}
