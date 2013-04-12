package authorverification.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import authorverification.Tools;

public class BasicAlphabetReader extends BufferedReader{

	public BasicAlphabetReader(Reader arg0) {
		super(arg0);
	}
	
	@Override
	public int read() throws IOException{
		int ch = super.read();
		
		if(ch == -1){
			return -1;
		}
		
		char[] cbuf = new char[1];
		cbuf[0] = (char)ch;
		cbuf = Tools.removeAccents(cbuf);
				
		return (int)cbuf[0];

	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException{
		if(len <= 0){
			return 0;
		}
		
		int ch = super.read();
		if(ch == -1){
			return -1;
		}
		
		int amount = 0;
		
		while(off+amount < cbuf.length && ch > -1){
			cbuf[off+amount] = (char)ch;

			amount++;
			if(amount == len){
				break;
			}

			ch = read();
		}
		
		char[] basicbuf = Tools.removeAccents(cbuf);
		for(int i = off; i<cbuf.length && i<off+len; i++){
			cbuf[i] = basicbuf[i];
		}
		
		return amount;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException{
		int result = read(cbuf, 0, cbuf.length);
		return result;
	}
}
