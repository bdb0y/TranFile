package dev.amin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dispatcher {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

    private Socket socket;
    private String targetIp;
    private int port;
    private boolean inProgress;

    Dispatcher(String targetIp, int port) {
        this.socket = null;
        this.targetIp = targetIp.trim();
        this.port = port;
        this.inProgress = false;
    }

    private void Connect() throws Exception {
        if (socket == null)
            socket = new Socket(targetIp, port);
    }

    private void Close() throws Exception {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    void Dispatch(String filePath) throws Exception {
        Connect();
        if (filePath.length() > 0) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            OutputStream outputStream = socket.getOutputStream();
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            if (sendFileInformation(file.getName(), file.length(), dataOutputStream)) {
                BEGIN_PROGRESS();
                if (sendFile(fileInputStream, outputStream)) {
                    System.out.println(Constants.SUCCESS_MESSAGE);
                    Close();
                    END_PROGRESS();
                } else System.out.println(Constants.FAILURE_MESSAGE);
            } else System.out.println(Constants.FAILURE_MESSAGE);
        } else System.out.println(Constants.FAILURE_MESSAGE);
    }

    private boolean sendFileInformation(String fileName, long fileSize, DataOutputStream dataOutputStream) {
        if (socket != null) {
            try {
                dataOutputStream.writeUTF(fileName);
                dataOutputStream.writeLong(fileSize);
                dataOutputStream.flush();
                System.out.format("File information sent to %s on port %d\n", getTargetIp(), getPort());
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, Constants.INFO_NOT_SENT);
            }
        } else return false;
        return false;
    }

    private boolean sendFile(FileInputStream fileInputStream, OutputStream dataOutputStream) {
        if (socket != null) {
            try {
                int packetSize = 1024;
                byte[] buffer = new byte[packetSize];
                int readBytes;
                System.out.format("File is being sent to %s on port %d\n", getTargetIp(), getPort());
                while ((readBytes = fileInputStream.read(buffer)) > 0) {
                    dataOutputStream.write(buffer, 0, readBytes);
                    System.out.println(readBytes);
                }
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, Constants.DATA_NOT_SENT + " - " + e.getMessage());
            }
        } else return false;
        return false;
    }

    // getter and setters

    private String getTargetIp() {
        return targetIp;
    }

    private int getPort() {
        return port;
    }

    private void END_PROGRESS() {
        this.inProgress = false;
    }

    private void BEGIN_PROGRESS() {
        this.inProgress = true;
    }

    public boolean GET_PROGRESS_STATE() {
        return inProgress;
    }

    public void setPort(int port) {
        this.port = port;
    }
}