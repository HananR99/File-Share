import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        JFrame jFrame = new JFrame("Server Interface");
        jFrame.setSize(400, 300);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());

        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel downloadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JComboBox<String> fileComboBox = new JComboBox<>();
        JButton downloadButton = new JButton("Download");
        downloadPanel.add(fileComboBox);
        downloadPanel.add(downloadButton);

        downloadButton.addActionListener(e -> {
            String selectedFileName = (String) fileComboBox.getSelectedItem();
            if (selectedFileName != null) {
                MyFile selectedFile = null;
                for (MyFile file : myFiles) {
                    if (file.getName().equals(selectedFileName)) {
                        selectedFile = file;
                        break;
                    }
                }
                if (selectedFile != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(selectedFile.getName()));
                    int userSelection = fileChooser.showSaveDialog(null);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File saveFile = fileChooser.getSelectedFile();
                        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                            fos.write(selectedFile.getData());
                            logTextArea.append("File downloaded: " + saveFile.getAbsolutePath() + "\n");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        contentPanel.add(downloadPanel, BorderLayout.SOUTH);

        ServerSocket serverSocket = new ServerSocket(8080);
        logTextArea.append("Server started on port 8080\n");

        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    String senderIp = socket.getInetAddress().getHostAddress(); // Get the IP address
                    handleClient(socket, senderIp, logTextArea, fileComboBox);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }).start();

        jFrame.add(contentPanel, BorderLayout.CENTER);
        jFrame.setVisible(true);
    }

    private static void handleClient(Socket socket, String senderIp, JTextArea logTextArea, JComboBox<String> fileComboBox) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            int fileNameLength = dataInputStream.readInt();

            if (fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);

                int fileContentLength = dataInputStream.readInt();

                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                    MyFile receivedFile = new MyFile(Thread.currentThread().getId(), fileName, fileContentBytes, getFileExtension(fileName));
                    myFiles.add(receivedFile);

                    logTextArea.append("Received file from " + senderIp + ": " + fileName + "\n");
                    fileComboBox.addItem(fileName);
                }
            }
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found";
        }
    }

    static class MyFile {
        private long id;
        private String name;
        private byte[] data;
        private String fileExtension;

        public MyFile(long id, String name, byte[] data, String fileExtension) {
            this.id = id;
            this.name = name;
            this.data = data;
            this.fileExtension = fileExtension;
        }

        public long getId() {
            return id;
        }

        public void setD(int d) {
            this.id = d;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }
    }
}
