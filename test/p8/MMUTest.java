package p8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MMUTest {

	@Test
	public void testReadWrite() {
		MMU mmu = new MMU(0x10);
		
		// first
		mmu.writeByte(0x00, 0xFF);
		assertEquals(0xFF, mmu.readByte(0x00));
		
		// last
		mmu.writeByte(0xF, 0xFF);
		assertEquals(0xFF, mmu.readByte(0x0F));
		
		// out of bounds
		try {
			mmu.readByte(0x10);
			assertTrue(false);
		} catch(ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		try {
			mmu.readByte(-1);
			assertTrue(false);
		} catch(ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		// read/write word
		mmu.reset();
		mmu.writeWord(0x00, 0xABCD);
		assertEquals(0xABCD, mmu.readWord(0x00));
		
		mmu.writeWord(0xE, 0xABCD);
		assertEquals(0xABCD, mmu.readWord(0x0E));
		
		System.out.println(mmu.toString());
	}
	
	@Test
	public void testDefault() {
		MMU mmu = new MMU(0x10);
		for(int i = 0; i < 0x10; i++) {
			assertEquals(0, mmu.readByte(i));
		}
	}
	
	@Test
	public void miscTests() {
		MMU mmu = new MMU(0x10);
		int ip = 0x00;
		mmu.writeByte(ip++, 0xFF);
		mmu.writeByte(ip++, 0xCC);
		System.out.println(mmu.toString());
		
		//System.out.println(Integer.toBinaryString(0xF8));
	}

}
