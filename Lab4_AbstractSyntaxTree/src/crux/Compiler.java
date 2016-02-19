package crux;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Compiler {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";

    
    public static void main(String[] args) throws IOException
    {
        String sourceFilename = args[0];
        
        Scanner s = null;
        try {
            s = new Scanner(new FileReader(sourceFilename));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error accessing the source file: \"" + sourceFilename + "\"");
            System.exit(-2);
        }

        Parser p = new Parser(s);
        ast.Command syntaxTree = p.parse();
        if (p.hasError()) {
            System.out.println("Error parsing file " + sourceFilename);
            System.out.println(p.errorReport());
            System.exit(-3);
        }
        
        ast.PrettyPrinter pp = new ast.PrettyPrinter();
        syntaxTree.accept(pp);
        System.out.println(pp.toString());
    }
}
    
