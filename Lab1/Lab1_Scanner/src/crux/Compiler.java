package crux;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";

	public static void main(String[] args) throws IOException
	{
		String sourceFile = args[0];
		Scanner s = null;
		boolean cont = true;
	
		try {
			s = new Scanner(new FileReader(sourceFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error accessing the source file: \"" + sourceFile + "\"");
			System.exit(-2);
		}

		Token t = null;
		//While we do not reach the end of file
		while (cont) {
			t = s.next();
			if(t != null){ //if token is NOT  null
				if(t.kind.getName().equals("EOF")){
					cont=false;
				}
				System.out.println(t.toString());
			}
		}

	}
}
