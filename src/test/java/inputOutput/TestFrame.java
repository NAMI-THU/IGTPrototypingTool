package inputOutput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Mat;

/*This class needs TestPanel and TestFrameThread to work properly*/
public class TestFrame extends JFrame implements ActionListener {

    JPanel main = new JPanel(); // this panel will contain a panel for buttons and one for painting the frames
    JPanel buttonPanel = new JPanel();
    JButton startLive = new JButton("Start LiveStream");
    JButton startLiveIGT = new JButton("Start LiveStream with OpenIGT");
    JButton startFile = new JButton("Load File");
    JButton stop = new JButton("Stop");
    JButton saveVideo = new JButton("Save Video");
    JButton saveImage = new JButton("Save Image");
    JButton stopSaveVideo = new JButton("Stop Saving Video");
    TestPanel videoPanel = new TestPanel();
    AbstractImageSource imgSrc;
    Mat mat;
    BufferedImage bufImg;
    TestFrameThread thread;

    public TestFrame() {
        init();
    }

    public void init() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1000, 700));

        main.setLayout(new BorderLayout());
        main.add(videoPanel, BorderLayout.CENTER);

        buttonPanel.setLayout(new FlowLayout());
        startLive.setSize(100, 20);
        startFile.setSize(100, 20);
        stop.setSize(100, 20);
        startLiveIGT.setSize(100, 20);
        saveVideo.setSize(100, 20);
        buttonPanel.add(startLive);
        buttonPanel.add(startLiveIGT);
        buttonPanel.add(startFile);
        buttonPanel.add(stop);
        buttonPanel.add(saveVideo);
        buttonPanel.add(saveImage);
        buttonPanel.add(stopSaveVideo);

        startLive.addActionListener(this);
        startLiveIGT.addActionListener(this);
        startFile.addActionListener(this);
        stop.addActionListener(this);
        saveVideo.addActionListener(this);
        stopSaveVideo.addActionListener(this);
        saveImage.addActionListener(this);
        startLiveIGT.addActionListener(this);

        main.add(buttonPanel, BorderLayout.PAGE_END);
        this.setContentPane(main);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        // The datatransport starts after user interaction
        if (src == startLive) {
            LivestreamSource liveStream = LivestreamSource.forDevice(0);
            imgSrc = liveStream;
            thread = new TestFrameThread(videoPanel, imgSrc);
        }

        if (src == buttonPanel.add(startLiveIGT)) {
            OIGTImageSource openIGT = new OIGTImageSource();
            imgSrc = openIGT;
            thread = new TestFrameThread(videoPanel, imgSrc);
            thread.threadSleep = true;
        }

        if (src == startFile) {
        // filechooser will open the explorer;
            final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnVal = fc.showOpenDialog(fc);
            String loadFile = null;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                loadFile = fc.getSelectedFile().getAbsolutePath();

                FilestreamSource fileStream = new FilestreamSource(loadFile);
                imgSrc = fileStream;
                thread = new TestFrameThread(videoPanel, imgSrc);
                thread.threadSleep = true;
            }
        }

        if (src == stop) {
            if (imgSrc.closeConnection()) {
                System.out.println("Connection stopped!");
            }
            thread.threadSleep = false;
        }

        if (src == saveVideo) {
            if (!thread.saveVideoOn) {
                final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnVal = fc.showSaveDialog(fc);
                String fileForSaving = null;

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileForSaving = fc.getSelectedFile().getAbsolutePath();
                    thread.saveVideoStart(fileForSaving);
                }
            }

        }
        if (src == saveImage) {
            if (true) {
                final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnVal = fc.showSaveDialog(fc);
                String fileForSaving = null;

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileForSaving = fc.getSelectedFile().getAbsolutePath();
                    thread.saveImageStart(fileForSaving);
                }
            }
        }

        if (src == stopSaveVideo) {
            if (thread.saveVideoOn) {
                thread.stopSave();
            }
        }
    }

    public static void startTestMainCV1() {
        TestFrame frame = new TestFrame();
        frame.validate();
        frame.setVisible(true);
    }

}
