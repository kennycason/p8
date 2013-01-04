package p8;

import java.util.Observable;

public class AbstractObservableBuffer extends Observable {

	protected int[] buffer;
	
	public AbstractObservableBuffer(int size) {
		buffer = new int[size];
	}
	
	public void reset() {
		for(int i = 0; i < buffer.length; i++) {
			buffer[i] = 0x00;
		}
	}
	
	public void writeByte(int address, int value) throws ArrayIndexOutOfBoundsException {
		if(address < 0 || address >= buffer.length) {
			throw new ArrayIndexOutOfBoundsException(address);
		}
		buffer[address] = value & 0xFF;
		setChanged();
		notifyObservers(address);
	}
	
	public void writeWord(int address, int value)  throws ArrayIndexOutOfBoundsException {
		writeByte(address, value & 0xFF);
		writeByte(address + 1, (value >> 8) & 0xFF);
	}
	
	public int readByte(int address) throws ArrayIndexOutOfBoundsException {
		if(address < 0 || address >= buffer.length) {
			throw new ArrayIndexOutOfBoundsException(address);
		}
		// System.out.println("Reading: " + Integer.toHexString(address).toUpperCase() + " => " +  Integer.toBinaryString(buffer[address]).toUpperCase());
		return buffer[address];
	}
	
	public int readWord(int address) throws ArrayIndexOutOfBoundsException {
		return readByte(address) + (readByte(address + 1) << 8);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < buffer.length; i++) {
			sb.append("[" + String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0')  + "] 0x" + Integer.toHexString(i).toUpperCase());
			sb.append("\t=>\t");
			sb.append("[" + String.format("%8s", Integer.toBinaryString(buffer[i])).replace(' ', '0')  + "] 0x" + Integer.toHexString(buffer[i]).toUpperCase());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
