package authorverification;

import java.io.IOException;
import java.io.Reader;

public class NoFormatReader extends LowercaseReader{

	public NoFormatReader(Reader arg0) {
		super(arg0);
	}
	
	@Override
	public int read() throws IOException{
		int ch = super.read();
		while(!Character.isLetter((char)ch)){
			if(ch == -1){
				return -1;
			}
			ch = super.read();
		}
		return ch;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException{
		if(len <= 0){
			return 0;
		}
		
		int ch = read();
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
		
		return amount;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException{
		int result = read(cbuf, 0, cbuf.length);
		return result;
	}
}
