package inputOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class startStopIGTLink extends JFrame implements ActionListener {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    JRadioButton rdbtnCsvFileReader;
    JRadioButton rdbtnOpenIgtLink;
    private JButton start = new JButton("Start");
    private JButton stop = new JButton("Stop");
    private JButton close = new JButton("Close Application");
    private JButton exitConnection = new JButton("Exit Connection");
    private JLabel label = new JLabel("Simple Test Application");
    private JTextField ipAddress;
    private JTextField filename;
    private Timer timer;
    private TrackingDataSource source;

    public startStopIGTLink() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    public static void startIGTWindow() {
        startStopIGTLink frame;
        frame = new startStopIGTLink();
        frame.validate();
        frame.setVisible(true);
    }

    private void init() {

        getContentPane().setLayout(null);
        this.setLocationRelativeTo(null);
        this.setSize(new Dimension(800, 600));

        label.setBounds(250, 20, 300, 100);
        label.setFont(new Font("Arial", Font.BOLD, 20));

        start.setBounds(121, 221, 200, 100);
        start.setFont(new Font("Arial", Font.BOLD, 20));
        start.addActionListener(this);

        stop.setBounds(488, 221, 200, 100);
        stop.setFont(new Font("Arial", Font.BOLD, 20));
        stop.addActionListener(this);

        close.setBounds(488, 369, 200, 100);
        close.setFont(new Font("Arial", Font.BOLD, 20));
        close.addActionListener(this);

        exitConnection.setBounds(121, 369, 200, 100);
        exitConnection.setFont(new Font("Arial", Font.BOLD, 20));
        exitConnection.addActionListener(this);

        getContentPane().add(exitConnection);
        getContentPane().add(close);
        getContentPane().add(label);
        getContentPane().add(start);
        getContentPane().add(stop);

        ipAddress = new JTextField();
        ipAddress.setText("127.0.0.1");
        ipAddress.setBounds(410, 151, 284, 20);
        getContentPane().add(ipAddress);
        ipAddress.setColumns(10);

        filename = new JTextField();
        filename.setBounds(410, 111, 284, 20);
        getContentPane().add(filename);
        filename.setColumns(10);

        rdbtnCsvFileReader = new JRadioButton("CSV File Reader");
        buttonGroup.add(rdbtnCsvFileReader);
        rdbtnCsvFileReader.setSelected(true);
        rdbtnCsvFileReader.setBounds(90, 110, 109, 23);
        getContentPane().add(rdbtnCsvFileReader);

        rdbtnOpenIgtLink = new JRadioButton("Open IGT Link Connection");
        buttonGroup.add(rdbtnOpenIgtLink);
        rdbtnOpenIgtLink.setBounds(90, 150, 174, 23);
        getContentPane().add(rdbtnOpenIgtLink);

        JLabel lblFilename = new JLabel("Filename:");
        lblFilename.setBounds(341, 114, 59, 14);
        getContentPane().add(lblFilename);

        JLabel lblIpAdress = new JLabel("IP Adress:");
        lblIpAdress.setBounds(341, 154, 59, 14);
        getContentPane().add(lblIpAdress);

    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();

        if (src == start) {

            if (rdbtnCsvFileReader.isSelected()) {
                CSVFileReader newSource = new CSVFileReader();
                newSource.setPath(filename.getText());
                newSource.setRepeatMode(true);
                source = newSource;
            } else {
                OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
                newSource.setIpAddress(this.ipAddress.getText());
                newSource.update();
                source = newSource;
            }

            //Set up timer to drive animation events.
            timer = new Timer(50, this);
            timer.setInitialDelay(0);
            timer.start();


        } else if (src == stop) {

            timer.stop();

        } else if (src == close) {
            System.exit(0);
        } else if (src == exitConnection) {
            if (this.rdbtnOpenIgtLink.isSelected())
                ((OpenIGTLinkConnection) source).closeConnection();
        } else {
            ArrayList<Tool> tools = source.update();
            System.out.print("Data: ");
            for (Tool t : tools)
                System.out.print("{" + t.getName() + ":"
                        + t.getCoordinat().getX() + ";"
                        + t.getCoordinat().getY() + ";"
                        + t.getCoordinat().getZ() + "}");
            System.out.println();
        }
    }
}
