package p8;

public class Registers {
	
	public int IP;	// Instruction Pointer
	
	public  int A;	// Accumulator
	
	public int R;	// Data/Address 
	
	public int Z;	// zero flag
	
	public void reset() {
		IP = 0;
		A = 0;
		R = 0;
		Z = 0;
	} 
	
	public String toString() {
		return "[\n\tIP: " + IP + "\n" + 
				"\tA: " + A + "\n" + 
				"\tR: " + R + "\n" + 
				"\tZ: " + Z + "\n]";
	}
	
}
