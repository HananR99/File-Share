import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];

        JFrame jFrame = new JFrame("Client Interface");
        jFrame.setSize(400, 200);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        JLabel jlFileName = new JLabel("No file selected");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 16));
        jlFileName.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(jbChooseFile);
        buttonPanel.add(jbSendFile);

        jFrame.add(jlFileName, BorderLayout.NORTH);
        jFrame.add(buttonPanel, BorderLayout.CENTER);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send.");

                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("Selected file: " + fileToSend[0].getName());
                }
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    JOptionPane.showMessageDialog(jFrame, "Please choose a file first.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 8080);
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);

                        dataOutputStream.writeInt(fileContentBytes.length);
                        dataOutputStream.write(fileContentBytes);

                        JOptionPane.showMessageDialog(jFrame, "File sent successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException error) {
                        JOptionPane.showMessageDialog(jFrame, "Error occurred: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        error.printStackTrace();
                    }
                }
            }
        });

        jFrame.setVisible(true);
    }
}
