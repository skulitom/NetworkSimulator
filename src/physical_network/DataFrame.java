/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2016)
 *  
 *  YOU MAY MODIFY THIS CLASS TO IMPLEMENT Stop & Wait ARQ PROTOCOL.
 *  (You will submit this class to Moodle.)
 *  
 */

package physical_network;

/**
 * Encapsulates the data for a network 'data frame'.
 * At the moment this just includes a payload byte array.
 * This may need to be extended to include necessary header information.
 * 
 * @author kevin-b
 *
 */

public class DataFrame {
	public final byte[] payload;
	private int destination = 0;
	public byte ackB=0;
	public DataFrame(String payload) {
		this.payload = payload.getBytes();
	}
	public DataFrame(String payload, int destination) {
		this.payload = payload.getBytes();
		this.destination = destination;
	}
	public DataFrame(byte[] payload) {
		this.payload = payload;
	}
	
	public DataFrame(byte[] payload, int destination) {
		this.payload = payload;
		this.destination = destination;
	}
	public int getDestination() {
		return destination;
	}
	public byte[] getPayload() {
		return payload;
	}
	public String toString() {
		return new String(payload);		
	}
	public static DataFrame createFromReceivedBytes(byte[] byteArray) {
		DataFrame created = new DataFrame(byteArray);
		return created;
	}
	
	/*
	 * This method should return the byte sequence of the transmitted bytes.
	 * At the moment it is just the payload data ... but extensions should
	 * include needed header information for the data frame.
	 * Note that this does not need sentinel or byte stuffing
	 * to be implemented since this is carried out as the data
	 * frame is transmitted and received.
	 */
	public byte[] getTransmittedBytes(int sourceAddress) {
		String s = new String(payload);
		String sd = ';'+Integer.toString(destination)+';';
		byte[] dest = sd.getBytes();
		byte[] withDest = new byte[payload.length + dest.length];
		System.arraycopy(payload, 0, withDest, 0, payload.length);
		System.arraycopy(dest, 0, withDest, payload.length, dest.length);
		String sa = Integer.toString(sourceAddress)+';';
		byte[] surc = sa.getBytes();
		byte[] withSource = new byte[withDest.length + surc.length];
		System.arraycopy(withDest, 0, withSource, 0, withDest.length);
		System.arraycopy(surc, 0, withSource, withDest.length, surc.length);
		String anumb = Byte.toString(ackB)+';';
		byte[] ackN = anumb.getBytes();
		byte[] withAck = new byte[withSource.length + ackN.length];
		System.arraycopy(withSource, 0, withAck, 0, withSource.length);
		System.arraycopy(ackN, 0, withAck, withSource.length, ackN.length);
		long check = calculateChecksum(withAck);
		String thr = Long.toString(check)+';';
		byte[] withCheck = thr.getBytes();
		byte[] fourth = new byte[withAck.length + withCheck.length];
		System.arraycopy(withAck, 0, fourth, 0, withAck.length);
		System.arraycopy(withCheck, 0, fourth, withAck.length, withCheck.length);

		return fourth;
	}

	public long calculateChecksum(byte[] buf) {
		int length = buf.length;
		int i = 0;
		long sum = 0;
		long data;
		// Handle all pairs
		while (length > 1) {
			// Corrected to include @Andy's edits and various comments on Stack Overflow
			data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
			sum += data;
			// 1's complement carry bit correction in 16-bits (detecting sign extension)
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
			i += 2;
			length -= 2;
		}
		// Handle remaining byte in odd length buffers
		if (length > 0) {
			sum += (buf[i] << 8 & 0xFF00);
			// 1's complement carry bit correction in 16-bits (detecting sign extension)
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}
		// Final 1's complement value correction to 16-bits
		sum = ~sum;
		sum = sum & 0xFFFF;
		return sum;
	}
}

