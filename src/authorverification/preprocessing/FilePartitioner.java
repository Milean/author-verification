package authorverification.preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FilePartitioner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			File f = null;
			int bytesPerFile = 10000000;

			if(args == null || args.length < 2){
				//f = new File("E:\\wikipedia\\html.lst");
				System.out.println("Needs two arguments!");
				System.exit(0);
			}
			else{
				f = new File(args[0]);
				try{
					bytesPerFile = Integer.parseInt(args[1]);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}

			FileReader fr = new FileReader(f);

			int part = 0;
			int c = fr.read();
			while(c != -1){

				File out = new File(f.getAbsolutePath()+".part"+part);
				FileWriter fw = new FileWriter(out);

				for(int i = 0; c != -1 && i<10000000; i++){
					fw.write(c);
					c = fr.read();
				}

				fw.flush();
				fw.close();

				part++;
			}
			fr.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}