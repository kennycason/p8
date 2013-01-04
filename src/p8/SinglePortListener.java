package p8;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

public class SinglePortListener implements IPortListener {
	
	private final int port;
	
	private Queue<Integer> data;

	public SinglePortListener(int port) {
		this.port = port;
		data = new LinkedList<Integer>();
	}
	
	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Port && obj instanceof Integer) {
			Port p = (Port) o;
			Integer portRead = (Integer) obj;
			if(portRead == port) {
				// System.out.println("Reading Port: 0x" + Integer.toHexString(port).toUpperCase() + " => " + p.readByte(port));
				data.add( p.readByte(port));
			}
		}
	}

	public int getPort() {
		return port;
	}
	
	public int size() {
		return data.size();
	}
	
	public int pop() {
		if(data.size() > 0) {
			return data.poll();
		}
		return -1;
	}
	
}
