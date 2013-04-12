package authorverification.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LowercaseReader extends BufferedReader{

	public LowercaseReader(Reader arg0) {
		super(arg0);
	}
	
	@Override
	public int read() throws IOException{
		return Character.toLowerCase(super.read());
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException{
		int result = super.read(cbuf, off, len);
		
		for(int i = 0; i<cbuf.length; i++){
			cbuf[i] = Character.toLowerCase(cbuf[i]);
		}
		
		return result;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException{
		int result = super.read(cbuf);
		
		for(int i = 0; i<cbuf.length; i++){
			cbuf[i] = Character.toLowerCase(cbuf[i]);
		}
		
		return result;
	}
}
