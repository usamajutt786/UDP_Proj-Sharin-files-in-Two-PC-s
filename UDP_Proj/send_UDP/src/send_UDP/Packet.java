/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package send_UDP;

import java.nio.ByteBuffer;

/**
 *
 * @author MAZ
 */
public class Packet {
    private static final int HEADER_SIZE = 12;
    private int seqNum;
    private int dataLength;
    private byte[] data;
    private boolean isAckReceived;

    public Packet(int seqNum, int dataLength, byte[] data) {
        this.seqNum = seqNum;
        this.dataLength = dataLength;
        this.data = data;
        this.isAckReceived = false;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isAckReceived() {
        return isAckReceived;
    }

    public void setAckReceived(boolean ackReceived) {
        isAckReceived = ackReceived;
    }

    public boolean isLastPacket() {
        return dataLength < 500;
    }

   public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + dataLength);
        buffer.putInt(seqNum);
        buffer.putInt(dataLength);
        buffer.put(data, 0, dataLength);
        return buffer.array();
    }
   
public static Packet fromBytes(byte[] bytes) {
    System.out.println(bytes.length);
    if (bytes.length < HEADER_SIZE) {
        throw new IllegalArgumentException("Invalid byte array length");
    }

    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    int seqNum = buffer.getInt();
    int dataLength = buffer.getInt();

    if (bytes.length < HEADER_SIZE + dataLength) {
        throw new IllegalArgumentException("Invalid byte array length for data");
    }

    byte[] data = new byte[dataLength];
    buffer.get(data, 0, dataLength);

    return new Packet(seqNum, dataLength, data);
}
}
