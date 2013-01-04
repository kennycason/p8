package p8;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class P8Test {

	@Test
	public void test() {
		P8 p8 = new P8();
		
		int[] instructions = new int[] {
				0x66,	// 0b01100110  ADD A, data(0x4)
				0x04,
				
				0x6E,	// 0b01101110  SUB A, data(0x1)
				0x01,
				
				0x3E,	// 0b00111010  CMP A, data(0x00)
				0x00,
				0x36,	// 0b00110110  JZ data(0x00)
				0xFF,   // Will end program
				0x2E,	// 0b00110110  JNZ data(0x02)
				0x02,		
		};
		p8.loadInstructions(instructions);
		// System.out.println(p8);
		//p8.printStack = true;
		p8.dispatch();
		
	}
	
	@Test
	public void testLoadBinary() {
		P8 p8 = new P8();
		
		String[] instructions = new String[] {
				"01100110", // ADD A, data(0x4)
				"00000100",
				
				"01101110",	// SUB A, data(0x1)
				"00000001",
				
				"00111110",	// CMP A, data(0x00)
				"00000000",
				"00110110",	// JZ data(0x00)
				"11111111", // Will end program
				"00101110",	// JNZ data(0x02)
				"00000010",		
		};
		p8.loadInstructions(instructions);
		// p8.printStack = true;
		// System.out.println(p8);
		p8.dispatch();
		
	}
	
	@Test
	public void testOUT() {
		// test 1
		SinglePortListener port0 = new SinglePortListener(0);
		SinglePortListener port1 = new SinglePortListener(1);
		
		P8 p8 = new P8();
		p8.port.addObserver(port0);
		p8.port.addObserver(port1);
		
		String[] instructions = new String[] {
				"01100110", // ADD A, data(0x4)
				"00000100",
				
				"00010110", // OUT data(0x0) ; write A to port 0x00
				"00000000",
				
		};
		p8.loadInstructions(instructions);
		p8.exec();
		p8.exec();
		assertEquals(1, port0.size());
		assertEquals(0x4, port0.pop());
		assertEquals(0, port1.size());
		
		// test 2
		p8.reset();
		instructions = new String[] {
				"01100110", // ADD A, data(0x7)
				"00000111",
				
				"00010110", // OUT data(0x01) ; write A to port 0x01
				"00000001",
		};
		p8.loadInstructions(instructions);
		p8.exec();
		p8.exec();
		assertEquals(0, port0.size());
		assertEquals(1, port1.size());
		assertEquals(0x7, port1.pop());
		
	}
	
	@Test
	public void testPortIN() {

		P8 p8 = new P8();
		p8.port.writeByte(0x00, 0xFE); // write to port
		
		String[] instructions = new String[] {
			
				"00001010", // IN A ; read port(A)
				"01010110", // STA data
				
		};
		p8.loadInstructions(instructions);
		assertEquals(p8.r.A, 0x00);
		p8.exec();
		assertEquals(p8.r.A, 0xFE);
		p8.exec();
		assertEquals(0xFE, p8.mmu.readByte(0x00));
		
	}
	
	@Test
	public void testLoadFile() {
		P8 p8 = new P8();
		p8.loadInstructionsFromFile("src/p8/test.asm");
		assertEquals(p8.mmu.readByte(0x00), 0x66);
		assertEquals(p8.mmu.readByte(0x01), 0x04);
		assertEquals(p8.mmu.readByte(0x02), 0x6E);
		assertEquals(p8.mmu.readByte(0x03), 0x01);
		assertEquals(p8.mmu.readByte(0x04), 0x3E);
		assertEquals(p8.mmu.readByte(0x05), 0x00);
		assertEquals(p8.mmu.readByte(0x06), 0x36);
		assertEquals(p8.mmu.readByte(0x07), 0xFF);
		assertEquals(p8.mmu.readByte(0x08), 0x2E);
		assertEquals(p8.mmu.readByte(0x09), 0x02);
			
	}

}
