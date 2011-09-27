package ASP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

public class MassEncodingWriter {

	/**
	 * all files in bench directory will be translated to ASP
	 * @param args no one expected
	 * @throws FileNotFoundException shouldn't happen
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		String modi = "clingo";
		ASPEncodingConstructor ASPtranslator = new ASPEncodingConstructor(modi);
		
		File f = new File("./ASPEncodings/bench/");
		File [] files = f.listFiles();
				      
				   
		for (int i=0; i< files.length; i++) {
			if (!files[i].isDirectory())
			{
				ASPtranslator.maxTimeSteps=201;
				System.out.println(files[i].toString());
				BufferedReader reader = new BufferedReader( new FileReader(files[i].getAbsoluteFile()));
				String encoding = "";
				String line = "";
				try {
					while ((line = reader.readLine()) != null)
					{
						encoding += line+"\n";
					}
				} catch (IOException e) {
					System.err.println("IOException - why?");
					e.printStackTrace();
				}
				
				System.out.println("encoding "+i+" : "+encoding);
				
				try {
					ASPtranslator.translate(new StringReader(encoding), new PrintStream(new File("./ASPEncodings/bench/clingo/"+ files[i].getName() + modi +".pl")));
				} 
				catch (FileNotFoundException e) {
					System.err.println("Couldn't find File for ASP Encoding (ASPOracle)");
					e.printStackTrace();
				} 
				catch (IOException e) {
					System.err.println("IOException - wtf - (ASPOracle)");
					e.printStackTrace();
				} 
				catch (ASP.ASPTranslator.ParseException e) {
					System.err.println("ParseException while translating ASP to GDL (ASPOracle)");
					e.printStackTrace();
				}
				
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("IOException(2) - why?");
					e.printStackTrace();
				}
			}
		}
	}

	

}
