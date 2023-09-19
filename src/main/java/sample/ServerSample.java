package sample;

import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.messages.TransformMessage;
import org.medcare.igtl.network.GenericIGTLinkServer;
import org.medcare.igtl.network.IOpenIgtPacketListener;
import org.medcare.igtl.util.Status;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import util.Matrix3D;
import util.TransformNR;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSample implements IOpenIgtPacketListener {
    static Logger logger = Logger.getLogger(ServerSample.class.getName());
    HashMap<String, Object> IGTData = null;

    public ServerSample() {
        IGTData = new HashMap<>();

        IGTData.put("theta", 0);
        IGTData.put("insertion_depth", 2.0);
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        GenericIGTLinkServer server;

        try {
            //Set up server
            server = new GenericIGTLinkServer(18944);

            //Add local event listener
            server.addIOpenIgtOnPacket(new ServerSample());

            while (!server.isConnected()) {
                Thread.sleep(100);
            }

            logger.log(Level.FINE, "Pushing packet");
            //Create an identify matrix
            TransformNR t = new TransformNR();

            //Push a transform object upstream
            while (true) {
                Thread.sleep(1000);
                if (server.isConnected()) {
                    //Log.debug("Push");
                    //server.pushPose("TransformPush", t);
                    float[] data = {(float) 1.0, (float) 2.12231233, (float) 4.5};

                    //server.sendMessage(new StringMessage("CMD_001", "Hello World") );
                    double[] position = t.getPositionArray();
                    position[0] = position[0] + 1;
                    position[1] = position[1] + 1;
                    position[2] = Math.random() * 100;
                    double[][] rotation = t.getRotationMatrixArray();
                    rotation[0][1] = Math.random();
                    server.sendMessage(new TransformMessage("TGT_001", position, rotation));
                } else {
                    logger.log(Level.FINE, "Waiting...");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onRxTransform(String name, TransformNR t) {
        logger.log(Level.FINE, "Received Transform with name: " + name + "and transform:" + t);

        if (name.equals("RegistrationTransform") || name.equals("CALIBRATION")) {
            System.err.println("Received Registration Transform");
            logger.log(Level.FINE, "Setting fiducial registration matrix: " + t);
            return;
        } else if (name.equals("TARGET")) {
            System.err.println("Received RAS Transform: TARGET");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else if (name.equals("myTransform")) {
            System.err.println("Received Transformation Matrix: myTransform");
            logger.log(Level.FINE, "Setting task space pose: " + t);

        } else {
            System.err.println("Received unidentified transform matrix");
            logger.log(Level.FINE, "Setting task space pose: " + t);
        }
    }

    @Override
    public TransformNR getTxTransform(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Status onGetStatus(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxString(String name, String body) {
        //check if its XML format message
        if (body.startsWith("<") && body.endsWith("/>")) {
            // TODO Auto-generated method stub
            System.out.println("Device Name = " + name + " body=" + body);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(body));
                Document data = builder.parse(is);
                Element xmlNode = data.getDocumentElement();
                StringBuffer treeData = new StringBuffer();
                traverseNode(xmlNode, treeData);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void traverseNode(Node n, StringBuffer treeData) {
        if (n.getNodeName().equalsIgnoreCase("command")) {
            NamedNodeMap atts = n.getAttributes();
            Node tempNode = atts.item(0);
            if (tempNode.getNodeName().equalsIgnoreCase("Name")) {
                if (tempNode.getNodeValue().equalsIgnoreCase("setVar")) {
                    for (int i = 1; i < atts.getLength(); i++) {
                        tempNode = atts.item(i);
                        if (IGTData.containsKey(tempNode.getNodeName())) {
                            System.out.println("Name =" + tempNode.getNodeName() + " : Value = " + tempNode.getNodeValue());
                            IGTData.put(tempNode.getNodeName(), tempNode.getNodeValue());
                        }
                    }
                }
            }
            if (n.hasChildNodes()) {
                NodeList nl = n.getChildNodes();
                int size = nl.getLength();
                for (int i = 0; i < size; i++) {
                    traverseNode(nl.item(i), treeData);
                }
            }
        }
    }

    @Override
    public String onTxString(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxDataArray(String name, Matrix3D data) {
        // TODO Auto-generated method stub

    }

    @Override
    public double[] onTxDataArray(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRxImage(String name, ImageMessage image) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTxNDArray(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRxNDArray(String name, float[] data) {
        // TODO Auto-generated method stub
        logger.log(Level.FINE, "Received NDArray with name: " + name);
        for (int i = 0; i < data.length; i++) {
            logger.log(Level.FINE, "Data[" + i + "]=" + (double) data[i]);
        }
    }

}
