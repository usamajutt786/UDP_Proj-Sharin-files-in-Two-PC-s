package receive_udp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
/**
 *
 * @author MAZ
 */
public class Receive_UDP {

    /**
     * @param args the command line arguments
     */
    
    private static final int SENDER_PORT = 6000;
    private static final int RECEIVER_PORT = 6500;
    private static final int PACKET_SIZE = 1000;
    private DatagramSocket receiverSocket;
    private FileOutputStream fileOutputStream;
    private String filename;
    private long fileSize;
    private boolean isTransferComplete;
    private InetAddress senderAddress;

    public Receive_UDP() throws IOException {
        this.receiverSocket = new DatagramSocket(RECEIVER_PORT);
        this.senderAddress = null;
        this.fileOutputStream = null;
        this.filename = null;
        this.fileSize = 0;
        this.isTransferComplete = false;
    }

    public void start() throws Exception {
        // Receive filename from the sender
        receiveFilename();
        System.out.println("1");

        // Exchange file size with the sender
          exchangeFileSize();
        System.out.println("2");

        // Start receiving packets
        while (!isTransferComplete) {
            System.out.println("3");
            byte[] packetBytes = new byte[PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length);
            System.out.println("4");
            receiverSocket.receive(packet);
            System.out.println("5");
            Packet receivedPacket = Packet.fromBytes(packetBytes);
            processPacket(receivedPacket);
        }

        fileOutputStream.close();
        receiverSocket.close();
    }

    private void receiveFilename() throws IOException {
        byte[] filenameBytes = new byte[PACKET_SIZE];
        DatagramPacket filenamePacket = new DatagramPacket(filenameBytes, filenameBytes.length);
        receiverSocket.receive(filenamePacket);
        senderAddress = filenamePacket.getAddress();
        System.out.println(senderAddress);
        filename = new String(filenameBytes).trim();
        System.out.println(filename);
        fileOutputStream = new FileOutputStream(new File(filename));
    }
    
    
    private void exchangeFileSize() throws IOException {
    File file = new File(filename);
    fileSize = file.length();
    byte[] fileSizeBytes = ByteBuffer.allocate(8).putLong(fileSize).array();
    DatagramPacket fileSizePacket = new DatagramPacket(fileSizeBytes, fileSizeBytes.length);
    fileSizePacket.setAddress(senderAddress); // تعيين عنوان المرسل إلى المستقبل
    fileSizePacket.setPort(SENDER_PORT); // تعيين رقم المنفذ
    receiverSocket.send(fileSizePacket);
}
     private void processPacket(Packet packet) throws IOException {
        if (packet.isLastPacket()) {
            isTransferComplete = true;
        }

        fileOutputStream.write(packet.getData(), 0, packet.getDataLength());

        sendAcknowledgment(packet.getSeqNum());
    }

    private void sendAcknowledgment(int seqNum) throws IOException {
        byte[] ackBytes = ByteBuffer.allocate(4).putInt(seqNum).array();
        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, senderAddress,
                SENDER_PORT);
        receiverSocket.send(ackPacket);
    }
    
    
    public static void main(String[] args) throws IOException, Exception {
        
        Receive_UDP receiver = new Receive_UDP();
            receiver.start();
    }
    
}
