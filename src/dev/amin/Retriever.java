package dev.amin;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Retriever {

    private static final Logger LOGGER = Logger.getLogger(Retriever.class.getName());

    private int port;

    public Retriever(int port) {
        this.port = port;
    }

    public void TurnOnListener() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            System.out.println(Constants.CONNECTION_SUCCESS);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            getFileInformation(dataInputStream);
            System.out.println(Constants.DATA_RECEIVED);
        }catch (Exception e){
            LOGGER.log(Level.WARNING, Constants.CONNECTION_FAILURE);
        }
    }

    private void getFileInformation(DataInputStream dataInputStream) throws Exception {
        String fileName = dataInputStream.readUTF();
        long fileSize = dataInputStream.readLong();
        System.out.println(Constants.INFO_RECEIVED);
        createFileBasedOnInformationRecieved(fileName, fileSize, dataInputStream);
    }

    private void createFileBasedOnInformationRecieved(String fileName, long fileSize, DataInputStream dataInputStream) throws Exception {
        File file = new File("../Directory");
        file.mkdir();
        FileOutputStream fileOutputStream = new FileOutputStream(file + "/" + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        startRetrievingFile(bufferedOutputStream, dataInputStream);
        fileOutputStream.close();
        bufferedOutputStream.close();
    }

    private void startRetrievingFile(BufferedOutputStream bufferedOutputStream, DataInputStream dataInputStream) throws Exception{
        System.out.println(Constants.DATA_BEING_RECEIVED);
        int readBytes, packetSize = 1024, theEnd = -1, theStart = 0;
        byte[] buffer = new byte[packetSize];

        while ((readBytes = dataInputStream.read(buffer, theStart, packetSize)) != theEnd) {
            bufferedOutputStream.write(buffer, theStart, readBytes);
            System.out.println(readBytes);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
