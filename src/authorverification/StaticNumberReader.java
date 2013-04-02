package authorverification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class StaticNumberReader extends BufferedReader{

	public StaticNumberReader(Reader arg0) {
		super(arg0);
	}
	
	@Override
	public int read() throws IOException{
		int res = super.read();
		if(Character.isDigit((char)res)){
			res = '@';
		}
		return res;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException{
		int result = super.read(cbuf, off, len);
		
		for(int i = 0; i<cbuf.length; i++){
			if(Character.isDigit(cbuf[i])){
				cbuf[i] = '@';
			}
		}
		
		return result;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException{
		int result = super.read(cbuf);
		
		for(int i = 0; i<cbuf.length; i++){
			if(Character.isDigit(cbuf[i])){
				cbuf[i] = '@';
			}
		}
		
		return result;
	}
}
