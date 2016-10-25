package backend;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ServerTextFileIOTests {
	
	private ServerTextFileIO file;
	
	@Before
	public final void Setup() {
		file = ServerTextFileIO.getInstance();
		file.clear();
	}

	@Test
	public final void testClear() {
		file.clear();
		assertEmpty();
		
		file.write("testing");
		file.clear();
		assertEmpty();
		
		file.write("testing");
		file.addLine("desting");
		file.clear();
		assertEmpty();
		
		file.write("testing");
		file.addLine("desting");
		file.removeLine("testing");
		file.clear();
		assertEmpty();
		
		file.write("testing");
		file.addLine("desting");
		file.removeLine("testing");
		file.removeLine("desting");
		file.clear();
		assertEmpty();
	}
	
	@Test
	public final void testWrite() {
		String text = "";
		file.clear();
		file.write(text);
		assertSameContent(text);
		
		text = "testing";
		file.clear();
		file.write(text);
		assertSameContent(text);
		
		file.clear();
		file.write(text);
		file.addLine("desting");
		file.write(text);
		assertSameContent(text);
		
		file.clear();
		file.write(text);
		file.addLine("desting");
		file.removeLine(text);
		file.write(text);
		assertSameContent(text);
	}
	
	@Test
	public final void testAddLine() {
		String text = "";
		file.clear();
		file.addLine(text);
		assertContains(text);
		
		text = "test";
		file.clear();
		file.write("testing");
		file.addLine(text);
		assertContains(text);
		
		text = "test";
		file.clear();
		file.write("testing");
		file.removeLine("testing");
		file.addLine(text);
		assertContains(text);
	}
	
	@Test
	public final void testRemoveLine() {		
		String text = "test";
		file.clear();
		file.write("testing");
		file.addLine(text);
		file.removeLine(text);
		assertNotContainsLine(text);
		
		text = "test";
		file.clear();
		file.write("testing");
		file.removeLine("testing");
		file.addLine(text);
		file.removeLine(text);
		assertNotContainsLine(text);
		
		text = "test";
		file.clear();
		file.write("testing");
		file.removeLine("testing");
		file.addLine(text);
		file.addLine(text);
		file.addLine(text);
		file.addLine(text);
		file.removeLine(text);
		assertNotContainsLine(text);
	}
	
	public final void assertNotContainsLine(String data) {
		for(String line : file.read().split("\n")) {
			if(data.equals(line)) {
				fail();
			}
		}
	}
	public final void assertContains(String data) {
		for(String line : file.read().split("\n")) {
			if(data.equals(line)) {
				return;
			}
		}
		
		fail();
	}
	public final void assertSameContent(String data) {
		assertTrue(data.equals(file.read()));
	}
	public final void assertEmpty() {
		assertTrue(file.read().length()==0);
	}
}
