package crux;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SymbolTable {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";
	LinkedList<Map<String,Symbol>> table;
	boolean didNotFind = false;
	public SymbolTable()
	{
		table = new LinkedList<Map<String,Symbol>>();
	}

	public Symbol lookup(String name) throws SymbolNotFoundError
	{
		Symbol toReturn = null;
		Map<String,Symbol> innerTable = null;
		for(int i = 0 ; i < table.size() ; i++)
		{
			innerTable = table.get(i);
			if(innerTable.containsKey(name)){
				toReturn = innerTable.get(name);
				didNotFind = false;
				break;
			}
			else
				didNotFind = true;
				
			
		}
		if(didNotFind == true){
			throw new SymbolNotFoundError(name);
		}
			
		return toReturn;
	}

	public Symbol insert(String name) throws RedeclarationError
	{
		Map<String,Symbol> currentTable = table.peek();
		if(currentTable.containsKey(name))
			throw new RedeclarationError(currentTable.get(name));
		return table.peek().put(name, new Symbol(name));
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (table.peek() != null)
		{
			String indent = new String();
			for(int i = table.size() ; i > 0 ; i--)
			{
				Map<String,Symbol> currentTable = table.get(i-1);
				for(Symbol s : currentTable.values())
					sb.append(indent + s.toString() + "\n");
				indent += "  ";
			}
		}
		return sb.toString();

	}
}

class SymbolNotFoundError extends Error
{
	private static final long serialVersionUID = 1L;
	private String name;

	SymbolNotFoundError(String name)
	{
		this.name = name;
	}

	public String name()
	{
		return name;
	}
}

class RedeclarationError extends Error
{
	private static final long serialVersionUID = 1L;

	public RedeclarationError(Symbol sym)
	{
		super("Symbol " + sym + " being redeclared.");}
}
