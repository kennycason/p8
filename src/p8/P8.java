package p8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Simple 8-bit CPU modeled after: http://www.rexfisher.com/P8/P8_TOC.htm
 * http://www.rexfisher.com/P8/P8_Instructions.htm
 * 
 * @author Kenny
 * 
 */
public class P8 {
	
	private static final String[] OPCODE_NAMES = new String[] {
		 "", 		// ---
		 "IN", 		// 00001
		 "OUT",		// 00010
		 "", 		// ---
		 "JMP", 	// 00100
		 "JNZ",  	// 00101
		 "JZ",  	// 00110
		 "CMP",  	// 00111
		 "LDA",  	// 01000
		 "LDR",  	// 01001
		 "STA",  	// 01010
		 "STR",  	// 01011
		 "ADD",  	// 01100
		 "SUB",  	// 01101
		 "DEC",  	// 01110
		 "", 		// ---
		 "OR", 		// 10000
		 "INV",  	// 10001
		 "SHL" 		// 10010
	};
	
	private static final String[] ADDRESS_MODE_NAMES = new String[] {
		 "MEM(address)",	// Direct Memory Address in Byte 2
		 "---", 			// Not Used
		 "A", 				// A Register
		 "R", 				// R Register
		 "MEM(R)", 			// Memory Address in R Register
		 "---", 			// Not Used
		 "data", 			// Byte 2 of Instruction
		 "---"				// Not Used
	};

	public Registers r;

	public MMU mmu;

	public Port port;

	public boolean halt;

	public boolean printStack = false;
	
	public boolean printOp = true;

	public P8() {
		this(0x100, 0x100);
	}
	
	public P8(int stackSize) {
		this(stackSize, 0x100);
	}
	
	public P8(int stackSize, int portSize) {
		r = new Registers();
		mmu = new MMU(stackSize); // 0x00 - 0xFF
		port = new Port(portSize);
		halt = false;
	}

	public void exec() {
		if (!halt) {
			try {
				int instr = mmu.readByte(r.IP++);
				int opCode = (instr & 0xF8) >> 3; // high 5 bits
				int operand = getOperand(instr);
				callOp(opCode, operand);
				if(printOp) {
					System.out.println(getInstructionName(instr));
				}
			} catch (Exception e) {
				halt = true;
				System.out.println("["  + e.getMessage() + "] Halting...");
			}
		}

	}

	public void dispatch() {
		while (!halt) {
			exec();
			if (printStack) {
				System.out.println(this);
			}
		}
	}
	
	public void reset() {
		r.reset();
		mmu.reset();
		port.reset();
		halt = false;
	}

	private int getOperand(int instr) {
		int addrMode = instr & 0x07; // low 3 bits
		switch (addrMode) {
			case 0x00: // Direct Memory Address in Byte 2
				return mmu.readByte(mmu.readByte(r.IP++));
			case 0x01: // Not Used ---
				halt = true;
				return 0x00;
			case 0x02: // Register A Register
				return r.A;
			case 0x03: // Register R Regsiter
				return r.R;
			case 0x04: // Indirect Memory Address in R Register
				r.IP++;
				return mmu.readByte(r.R);
			case 0x05: // Not Used ---
				halt = true;
				return 0x00;
			case 0x06: // Immediate Byte 2 of Instruction
				return mmu.readByte(r.IP++);
			case 0x07: // Not Used ---
				halt = true;
				return 0x00;
			default:
				halt = true;
				return 0x00;
		}
	}

	private void callOp(int opCode, int operand) {
		switch (opCode) {
			case 0x01: // IN 00001
				IN(operand);
				break;
			case 0x02: // OUT 00010
				OUT(operand);
				break;
			case 0x04: // JMP 00100
				JMP(operand);
				break;
			case 0x05: // JNZ 00101
				JNZ(operand);
				break;
			case 0x06: // JZ 00110
				JZ(operand);
				break;
			case 0x07: // CMP 00111
				CMP(operand);
				break;
			case 0x08: // LDA 01000
				LDA(operand);
				break;
			case 0x09: // LDR 01001
				LDR(operand);
				break;
			case 0x0A: // STA 01010
				STA(operand);
				break;
			case 0X0B: // STR 01011
				STR(operand);
				break;
			case 0x0C: // ADD 01100
				ADD(operand);
				break;
			case 0x0D: // SUB 01101
				SUB(operand);
				break;
			case 0x0E: // DEC 01110
				DEC(operand);
				break;
			case 0x10: // OR 10000
				OR(operand);
				break;
			case 0x11: // INV 10001
				INV(operand);
				break;
			case 0x12: // SHL 10010
				SHL(operand);
				break;
			default:
				XX();
				break;
		}
	}

	private String getInstructionName(int instr) {
		int opCode = (instr & 0xF8) >> 3; // high 5 bits
		int addrMode = instr & 0x07; // low 3 bits
		String instrName = " ["
				+ String.format("%5s", Integer.toBinaryString(opCode)).replace(
						' ', '0')
				+ " "
				+ String.format("%3s", Integer.toBinaryString(addrMode))
						.replace(' ', '0') + "] ";
	
		if(opCode < OPCODE_NAMES.length) {
			instrName += P8.OPCODE_NAMES[opCode] + " " + P8.ADDRESS_MODE_NAMES[addrMode];
		}
		return instrName;
	}

	/*
	 * Instructions
	 */
	private void XX() {
		System.out.println("Unknown Instruction. Halting...");
		halt = true;
	}

	// 1. Input / Output. These instructions transfer data between the
	// accumulator and external I/O devices.
	/**
	 * IN = Read Input Port
	 * 
	 * @param operand
	 */
	private void IN(int operand) {
		r.A = port.readByte(operand);
	}

	/**
	 * OUT = Write Output Port
	 * 
	 * @param operand
	 */
	private void OUT(int operand) {
		port.writeByte(operand, r.A);
	}

	// 2. Program Control. These instructions change the sequence of program
	// execution. They are often called branch instructions.

	/**
	 * JMP = Unconditional Jump
	 */
	private void JMP(int operand) {
		r.IP = operand;
	}

	/**
	 * JNZ = Jump If Not Zero (Conditional Jump)
	 */
	private void JNZ(int operand) {
		if (r.Z == 0) {
			JMP(operand);
		}
	}

	/**
	 * JZ = Jump If Zero (Conditional Jump)
	 */
	private void JZ(int operand) {
		if (r.Z == 1) {
			JMP(operand);
		}
	}

	/**
	 * CMP = Compare (Sets / Resets Zero Bit For Conditional Jumps)
	 */
	private void CMP(int operand) {
		if (r.A - operand == 0) {
			r.Z = 1;
		}
	}

	// 3. Data Transfer. These instructions cause data in one location (either
	// the internal registers or external memory) to be copied to another
	// location.

	/**
	 * LDA = Load A Register
	 */
	private void LDA(int operand) {
		r.A = operand;
	}

	/**
	 * LDR = Load R Register
	 */
	private void LDR(int operand) {
		r.R = operand;
	}

	/**
	 * STA = Store A Register
	 * 
	 * @param operand
	 */
	private void STA(int operand) {
		mmu.writeByte(operand, r.A);
	}

	/**
	 * STR = Store R Register
	 */
	private void STR(int operand) {
		mmu.writeByte(operand, r.R);
	}

	// 4. Arithmetic. These instructions perform numerical operations on data.
	// Floating point operations are not supported.

	/**
	 * ADD = Add To A Register
	 */
	private void ADD(int operand) {
		r.A += operand;
	}

	/**
	 * SUB = Subtract From A Register
	 */
	private void SUB(int operand) {
		r.A -= operand;
	}

	/**
	 * DEC = Decrement
	 */
	private void DEC(int operand) {
		r.A = operand--;
	}

	// 5. Logical. These instructions perform Boolean operations on data,
	// including bit shifting.

	/**
	 * OR = Or With A Register
	 * 
	 * @param operand
	 */
	private void OR(int operand) {
		r.A |= operand;
	}

	/**
	 * INV = Invert & Move To A Register
	 */
	private void INV(int operand) {
		r.A = ~operand;
	}

	/**
	 * SHL = Shift Left & Move To A Register
	 */
	private void SHL(int operand) {
		r.A = operand << 1;
	}
	
	/**
	 * 
	 * @param instructions
	 */
	public void loadInstructions(int[] instructions) {
		for (int i = 0; i < instructions.length; i++) {
			mmu.writeByte(i, instructions[i]);
		}
	}

	/**
	 * Binary representation
	 * 
	 * @param instructions
	 */
	public void loadInstructions(String[] instructions) {
		for (int i = 0; i < instructions.length; i++) {
			mmu.writeByte(i, Integer.parseInt(instructions[i], 2));
		}
	}
	
	/**
	 * 
	 */
	public void loadInstructionsFromFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = "";
			ArrayList<String> instructions = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				instructions.add(line.substring(0, 8));
			}
			br.close();
			loadInstructions(instructions.toArray(new String[instructions.size()]));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Halting...");
			halt = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Halting...");
			halt = true;
		}
	}

	public String toString() {
		return r.toString() + "\n" + mmu.toString();
	}

}
