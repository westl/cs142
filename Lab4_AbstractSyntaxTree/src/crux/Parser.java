package crux;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ast.Command;
import ast.Declaration;
import ast.Dereference;

public class Parser {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";


	// SymbolTable Management ==========================
	private SymbolTable symbolTable;

	private void initSymbolTable()
	{
		this.symbolTable = new SymbolTable();
	}

	private void enterScope()
	{
		symbolTable.table.push(new LinkedHashMap<String,Symbol>());
	}

	private void exitScope()
	{
		symbolTable.table.pop();
	}

	private Symbol tryResolveSymbol(Token ident)
	{
		assert(ident.is(Token.Kind.IDENTIFIER));
		String name = ident.lexeme();
		try {
			return symbolTable.lookup(name);
		} catch (SymbolNotFoundError e) {
			String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
			return new ErrorSymbol(message);
		}
	}

	private String reportResolveSymbolError(String name, int lineNum, int charPos)
	{
		String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
		errorBuffer.append(message + "\n");
		errorBuffer.append(symbolTable.toString() + "\n");
		return message;
	}

	private Symbol tryDeclareSymbol(Token ident)
	{
		assert(ident.is(Token.Kind.IDENTIFIER));
		String name = ident.lexeme();
		try {
			symbolTable.insert(name);
			return new Symbol(name);
		} catch (RedeclarationError re) {
			String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
			return new ErrorSymbol(message);
		}
	}

	private String reportDeclareSymbolError(String name, int lineNum, int charPos)
	{
		String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
		errorBuffer.append(message + "\n");
		errorBuffer.append(symbolTable.toString() + "\n");
		return message;
	}    
	// Parser ==========================================

	private Scanner scanner;
	private Token currentToken;

	public Parser(Scanner scanner) throws IOException
	{
		this.scanner = scanner;
		this.currentToken = scanner.next();
	}


	public ast.Command parse() throws IOException
	{
		initSymbolTable();
		try {
			return program();
		} catch (QuitParseException q) {
			return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
		}
	}

	// Error Reporting ==========================================
	private StringBuffer errorBuffer = new StringBuffer();

	private String reportSyntaxError(NonTerminal nt)
	{
		String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.getKind().name() + ".]";
		errorBuffer.append(message + "\n");
		return message;
	}

	private String reportSyntaxError(Token.Kind kind)
	{
		String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind.name() + " but got " + currentToken.getKind().name() + ".]";
		errorBuffer.append(message + "\n");
		return message;
	}

	public String errorReport()
	{
		return errorBuffer.toString();
	}

	public boolean hasError()
	{
		return errorBuffer.length() != 0;
	}

	private class QuitParseException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		public QuitParseException(String errorMessage) {
			super(errorMessage);
		}
	}


	private int lineNumber()
	{
		return currentToken.lineNumber();
	}

	private int charPosition()
	{
		return currentToken.charPosition();
	}

	// Helper Methods ==========================================
	 private Token expectRetrieve(Token.Kind kind) throws IOException
	    {
	        Token tok = currentToken;
	        if (accept(kind))
	            return tok;
	        String errorMessage = reportSyntaxError(kind);
	        throw new QuitParseException(errorMessage);
	        //return ErrorToken(errorMessage);
	    }
	        
	    private Token expectRetrieve(NonTerminal nt) throws IOException
	    {
	        Token tok = currentToken;
	        if (accept(nt))
	            return tok;
	        String errorMessage = reportSyntaxError(nt);
	        throw new QuitParseException(errorMessage);
	        //return ErrorToken(errorMessage);
	    }
	    
	 	private boolean have(Token.Kind kind)
	 	{
	 		return currentToken.is(kind);
	 	}

	 	private boolean have(NonTerminal nt)
	 	{
	 		return nt.firstSet().contains(currentToken.getKind());
	 	}

	 	private boolean accept(Token.Kind kind) throws IOException
	 	{
	 		if (have(kind)) {
	 			currentToken = scanner.next();
	 			return true;
	 		}
	 		return false;
	 	}    

	 	private boolean accept(NonTerminal nt) throws IOException
	 	{
	 		if (have(nt)) {
	 			currentToken = scanner.next();
	 			return true;
	 		}
	 		return false;
	 	}

	 	private boolean expect(Token.Kind kind) throws IOException
	 	{
	 		if (accept(kind))
	 			return true;
	 		reportError(kind);
	 		//return false;
	 		return false;
	 	}

	 	private boolean expect(NonTerminal nt) throws IOException 
	 	{
	 		if (accept(nt))
	 			return true;
	 		reportError(nt);
	 		return false;
	 	}
	 	private void reportError(NonTerminal nt)throws IOException{
	 		String errorMessage = reportSyntaxError(nt);
	 		throw new QuitParseException(errorMessage);
	 	}
	 	private void reportError(Token.Kind kind) throws IOException{
	 		String errorMessage = reportSyntaxError(kind);
	 		throw new QuitParseException(errorMessage);
	 	}

	// Grammar Rules =====================================================

	 	// literal := INTEGER | FLOAT | TRUE | FALSE .
	 	public ast.Expression literal() throws IOException
	 	{
	 		ast.Expression expr = null;
	 		
	 		if(currentToken.kind.name().equals("INTEGER")){
	 			expr = Command.newLiteral(expectRetrieve(Token.Kind.INTEGER));
	 		}
	 		else if(currentToken.kind.name().equals("FLOAT")){
	 			expr = Command.newLiteral(expectRetrieve(Token.Kind.FLOAT));
	 		}
	 		else if(currentToken.kind.name().equals("TRUE")){
	 			expr = Command.newLiteral(expectRetrieve(Token.Kind.TRUE));
	 		}
	 		else if(currentToken.kind.name().equals("FALSE")){
	 			expr = Command.newLiteral(expectRetrieve(Token.Kind.FALSE));
	 		}
	 		
	 		return expr; 
	 	}

	 	// designator := IDENTIFIER { "[" expression0 "]" } .
	 	public ast.Expression designator() throws IOException
	 	{
	 		Token cur = null;
	 		Token ident = expectRetrieve(Token.Kind.IDENTIFIER);
	 		Symbol identSym = tryResolveSymbol(ident);
	 		ast.Expression expr = new ast.AddressOf(ident.lineNumber(), ident.charPosition(), identSym);
	 		while (accept(Token.Kind.OPEN_BRACKET)) {
	 			cur = currentToken;
	 			ast.Expression amount = expression0();
	 			expr = new ast.Index(cur.lineNumber(), cur.charPosition(), expr, amount);
	 			expect(Token.Kind.CLOSE_BRACKET);
	 		}

	 		return expr;
	 		
	 	}


	 	//type := IDENTIFIER .
	 	private ast.Expression type() throws IOException {
	 		// TODO Auto-generated method stub
	 		//expects IDENTIFIER
	 		return Command.newLiteral(expectRetrieve(Token.Kind.IDENTIFIER));

	 	}

	 	//op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
	 	private Token op0() throws IOException {
	 		// TODO Auto-generated method stub
	 		//accepts ">=" | "<=" | "!=" | "==" | ">" | "<" .
	 		Token toReturn = null;
	 		if(NonTerminal.OP0.firstSet.contains(currentToken.kind)){
	 			toReturn = expectRetrieve(currentToken.kind);
	 		}
	 		else{
	 			reportError(currentToken.kind);
	 		}
	 		return toReturn;
	 	}

	 	//op1 := "+" | "-" | "or" .
	 	private Token op1() throws IOException {
	 		// TODO Auto-generated method stub
	 		//accepts "+" | "-" | "or" .
	 		Token toReturn = null;
	 		if(NonTerminal.OP1.firstSet.contains(currentToken.kind)){
	 			toReturn = expectRetrieve(currentToken.kind);
	 		}
	 		else{
	 			reportError(currentToken.kind);
	 		}
	 		return toReturn;
	 	}

	 	//op2 := "*" | "/" | "and" .
	 	private Token op2() throws IOException {
	 		// TODO Auto-generated method stub
	 		//accepts "*" | "/" | "and" .
	 		Token toReturn = null;
	 		if(NonTerminal.OP2.firstSet.contains(currentToken.kind)){
	 			toReturn = expectRetrieve(currentToken.kind);
	 		}
	 		else{
	 			reportError(currentToken.kind);
	 		}
			return toReturn;

	 	}

	 	//expression0 := expression1 [ op0 expression1 ] .
	 	private ast.Expression expression0() throws IOException {
	 		// TODO Auto-generated method stub
	 		ast.Expression expr = null;
	 		expr = expression1();
	 		if(!currentToken.is(Token.Kind.SEMICOLON)){
	 			if(NonTerminal.OP0.firstSet.contains(currentToken.kind)){
	 				ast.Expression left = expr;
	 				Token op0 = op0();
	 				ast.Expression right = expression1();
	 				expr = Command.newExpression(left, op0, right);
	 			}
	 		}
	 	
	 		return expr;
	 	}
	 	//expression1 := expression2 { op1  expression2 } .
	 	private ast.Expression expression1() throws IOException {
	 		// TODO Auto-generated method stub
	 		ast.Expression expr1 = null;
	 		expr1 = expression2();
	 		while(NonTerminal.OP1.firstSet.contains(currentToken.kind)){
	 			ast.Expression left = expr1;
	 			Token op1 = op1();
	 			ast.Expression right = expression2();
	 			expr1 = Command.newExpression(left, op1, right);
	 		}
	 	
	 		return expr1;
	 	}

	 	//expression2 := expression3 { op2 expression3 } .
	 	private ast.Expression expression2() throws IOException {
	 		// TODO Auto-generated method stub
	 	ast.Expression expr2 = null;
	 		expr2 = expression3();
	 		while(NonTerminal.OP2.firstSet.contains(currentToken.kind)){
	 			ast.Expression left = expr2;
	 			Token op2 = op2();
	 			ast.Expression right = expression3();
	 			expr2 = Command.newExpression(left, op2, right);
	 		}
	 		
	 		return expr2;

	 	}

	 	/*
	 	 * expression3 := "not" expression3
	        | "(" expression0 ")"
	        | designator
	        | call-expression
	        | literal .
	 	 */
	 	private ast.Expression expression3() throws IOException {
	 		// TODO Auto-generated method stub
	 		ast.Expression expr3 = null;
	 		if(currentToken.kind.name().equals("NOT")){
	 			Token op = expectRetrieve(Token.Kind.NOT);
	 			ast.Expression right = expression3();
	 			expr3 = Command.newExpression(right,op ,null);
	 		}
	 		else if(currentToken.kind.name().equals("OPEN_PAREN")){
	 			accept(Token.Kind.OPEN_PAREN);
	 			expr3 = expression0();
	 			expect(Token.Kind.CLOSE_PAREN);
	 		}
	 		else if(currentToken.kind.name().equals("IDENTIFIER")){
	 			int lineNum = currentToken.lineNumber();
	 			int charPos = currentToken.charPosition();
	 			ast.Expression deref = designator();
	 			expr3 = new ast.Dereference(lineNum, charPos, deref);
	 		}
	 		else if(currentToken.kind.name().equals("CALL")){
	 			expr3 = call_expression();
	 		}
	 		else if(currentToken.kind.name().equals("INTEGER") || currentToken.kind.name().equals("FLOAT") || currentToken.kind.name().equals("TRUE") || currentToken.kind.name().equals("FALSE")){
	 			expr3 = literal();
	 		}
			return expr3;
	 		
	 	}

	 	//call-expression := "::" IDENTIFIER "(" expression-list ")" .
	 	private ast.Call call_expression() throws IOException {
	 		ast.Call call_exp = null; 
	 		Token callToken = expectRetrieve(Token.Kind.CALL);	
	 		Symbol sym = tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
	 		expect(Token.Kind.OPEN_PAREN);
	 		ast.ExpressionList args = expression_list();
	 		expect(Token.Kind.CLOSE_PAREN);
	 		call_exp = new ast.Call(callToken.lineNumber(), callToken.charPosition(), sym, args);
	 		
			return call_exp;
	 	
	 	}
	 	//expression-list := [ expression0 { "," expression0 } ] .
	 	private ast.ExpressionList expression_list() throws IOException {
	 		//expression0();
	 		ast.ExpressionList expr_list = new ast.ExpressionList(currentToken.lineNumber(), currentToken.charPosition());
	 		if(!currentToken.kind.name().equals("CLOSE_PAREN")){
	 			ast.Expression expr = expression0();
	 			expr_list.add(expr);
	 			while(!currentToken.kind.name().equals("CLOSE_PAREN")){
	 				expect(Token.Kind.COMMA);
	 				expr = expression0();
	 				expr_list.add(expr);
	 			}
	 		}
			return expr_list;
	 	
	 	}

	 	//parameter := IDENTIFIER ":" type .
	 	private Symbol parameter() throws IOException {
	 	
	 		Symbol toReturn = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
	 		expect(Token.Kind.COLON);
	 		type();
			return toReturn;
	 	
	 	}

	 	//parameter-list := [ parameter { "," parameter } ] .
	 	private List<Symbol> parameter_list() throws IOException {
	 		List<Symbol> args = new LinkedList<Symbol>();
	 		if(!currentToken.getKind().name().equals("CLOSE_PAREN")){
	 			args.add(parameter());
	 			while(accept(Token.Kind.COMMA)){
	 				args.add(parameter());
	 			}
	 		}
			return args;
	 		
	 	}

	 	//variable-declaration := "var" IDENTIFIER ":" type ";"
	 	private ast.VariableDeclaration variable_declaration() throws IOException{
	 		ast.VariableDeclaration varDeclaration;
	 		Token varToken = expectRetrieve(Token.Kind.VAR);
	 		Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
	 		expect(Token.Kind.COLON);
	 		type();
	 		expect(Token.Kind.SEMICOLON);
	 		varDeclaration =  new ast.VariableDeclaration(varToken.lineNumber(), varToken.charPosition(), symbol);
	 		
			return varDeclaration;
	 		
	 	}

	 	//array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
	 	private ast.ArrayDeclaration array_declaration() throws IOException{
	 		ast.ArrayDeclaration array = null;
	 		Token arrayToken = expectRetrieve(Token.Kind.ARRAY);
	 		Token ident = expectRetrieve(Token.Kind.IDENTIFIER);
	 		Symbol symbol = tryDeclareSymbol(ident);
	 		expect(Token.Kind.COLON);
	 		type();
	 		expect(Token.Kind.OPEN_BRACKET);
	 		expect(Token.Kind.INTEGER);
	 		expect(Token.Kind.CLOSE_BRACKET);
	 		while(currentToken.kind.name().equals("OPEN_BRACKET")){
	 			accept(Token.Kind.OPEN_BRACKET);
	 			expect(Token.Kind.INTEGER);
	 			expect(Token.Kind.CLOSE_BRACKET);
	 		}
	 		expect(Token.Kind.SEMICOLON);
	 		array = new ast.ArrayDeclaration(arrayToken.lineNumber(), arrayToken.charPosition(), symbol);
			return array;
	 		
	 	}

	 	//function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
	 	private ast.FunctionDefinition function_definition() throws IOException{
	 		ast.FunctionDefinition funcDef = null;
	 		Token funcToken = expectRetrieve(Token.Kind.FUNC);
	 		Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
	 		expect(Token.Kind.OPEN_PAREN);
	 		enterScope();
	 		List<Symbol> args =  parameter_list();
	 		expect(Token.Kind.CLOSE_PAREN);
	 		expect(Token.Kind.COLON);
	 		type();
	 		ast.StatementList body = statement_block();
	 		funcDef = new ast.FunctionDefinition(funcToken.lineNumber(), funcToken.charPosition(), symbol, args, body);
	 		exitScope();
	 		
	 		
			return funcDef;
	 	}

	 	//declaration := variable-declaration | array-declaration | function-definition .
	 	private ast.Declaration declaration() throws IOException{
	 		Token token = this.currentToken;
	 		ast.Declaration declaration = null;
	 		if(token.getKind().name().equals("VAR")){
	 			declaration = variable_declaration();
	 		}
	 		else if(token.getKind().name().equals("FUNC")){
	 			declaration = function_definition();
	 		}
	 		else if(token.getKind().name().equals("ARRAY")){
	 			declaration = array_declaration();
	 		}
	 		else{
	 			reportError(token.kind);
	 		}
			return declaration;
	 	
	 	}

	 	//declaration-list := { declaration } .
	 	private ast.DeclarationList declaration_list() throws IOException{
	 		ast.DeclarationList declist = new ast.DeclarationList(currentToken.lineNumber(), currentToken.charPosition());
	 		while(NonTerminal.DECLARATION.firstSet.contains(currentToken.kind)){
	 			ast.Declaration declaration = declaration();
	 			declist.add(declaration);
	 		}
	 		return declist;
	 	}
	 	
	 	//assignment-statement := "let" designator "=" expression0 ";"
	 	private ast.Assignment assignment_statement() throws IOException{
	 		ast.Assignment  assignment = null;
	 		Token letToken = expectRetrieve(Token.Kind.LET);
	 		ast.Expression dest = designator();
	 		expect(Token.Kind.ASSIGN);
	 		ast.Expression source = expression0();
	 		expect(Token.Kind.SEMICOLON);
	 		assignment = new ast.Assignment(letToken.lineNumber(), letToken.charPosition(), dest, source);
			return assignment;
	 	
	 	}
	 	//call-statement := call-expression ";"
	 	private ast.Call call_statement() throws IOException{
	 		ast.Call call = null;
	 		call = call_expression();
	 		expect(Token.Kind.SEMICOLON);
			return call;
	 		
	 	}
	 	//if-statement := "if" expression0 statement-block [ "else" statement-block ] .
	 	private ast.IfElseBranch if_statement() throws IOException{
	 		ast.IfElseBranch if_statement = null;
	 		Token ifToken = expectRetrieve(Token.Kind.IF);
	 		ast.Expression cond = expression0();
	 		enterScope();
	 		ast.StatementList thenBlock = statement_block();
	 		ast.StatementList elseBlock = null;
	 		if(currentToken.kind.name().equals("ELSE")){
	 			accept(Token.Kind.ELSE);
	 			elseBlock = statement_block();
	 		}
	 		else
	 			elseBlock = new ast.StatementList(currentToken.lineNumber(), currentToken.charPosition());
	 	
	 		if_statement = new ast.IfElseBranch(ifToken.lineNumber(), ifToken.charPosition(), cond, thenBlock, elseBlock);
	 		exitScope();
	 		
	 		return if_statement;
	 		
	 	}
	 	//while-statement := "while" expression0 statement-block .
	 	private ast.WhileLoop while_statement() throws IOException{
	 		ast.WhileLoop while_loop = null;
	 		Token whileToken = expectRetrieve(Token.Kind.WHILE);
	 		ast.Expression cond = expression0();
	 		enterScope();
	 		ast.StatementList body = statement_block();
	 		exitScope();
	 		while_loop = new ast.WhileLoop(whileToken.lineNumber(), whileToken.charPosition(), cond, body);
	 		return while_loop;
	 	}
	 	//return-statement := "return" expression0 ";" .
	 	private ast.Return return_statement() throws IOException{
	 		ast.Return return_statement = null;
	 		Token returnToken = expectRetrieve(Token.Kind.RETURN);
	 		ast.Expression arg = expression0();
	 		expect(Token.Kind.SEMICOLON);
	 		return_statement = new ast.Return(returnToken.lineNumber(), returnToken.charPosition(), arg);
			return return_statement;
	 		
	 	}
	 	/*statement := variable-declaration
	            | call-statement
	            | assignment-statement
	            | if-statement
	            | while-statement
	            | return-statement .*/
	 	private ast.Statement statement() throws IOException{
	 		ast.Statement statement = null;
	 		if(currentToken.getKind().name().equals("VAR")){
	 			statement = variable_declaration();
	 		}
	 		else if(currentToken.getKind().name().equals("CALL")){
	 			statement = call_statement();
	 		}
	 		else if(currentToken.getKind().name().equals("LET")){
	 			statement = assignment_statement();
	 		}
	 		else if(currentToken.getKind().name().equals("IF")){
	 			statement = if_statement();
	 		}
	 		else if(currentToken.getKind().name().equals("WHILE")){
	 			statement = while_statement();
	 		}
	 		else if(currentToken.getKind().name().equals("RETURN")){
	 			statement = return_statement();
	 		}
	 		else{
	 			reportError(Token.Kind.CLOSE_BRACE);
	 		}
			return statement;
	 	}
	 	//statement-list := { statement } .
	 	private ast.StatementList statement_list() throws IOException{
	 		ast.StatementList statement_list  = new ast.StatementList(currentToken.lineNumber(), currentToken.charPosition());
	 		while(this.currentToken.kind != Token.Kind.CLOSE_BRACE)
	 			statement_list.add(statement());
			
	 		return statement_list;
	 		
	 	}
	 	//statement-block := "{" statement-list "}" .
	 	private ast.StatementList statement_block() throws IOException{
	 		expect(Token.Kind.OPEN_BRACE);
	 		ast.StatementList stment_list  = statement_list();
	 		expect(Token.Kind.CLOSE_BRACE);
			return stment_list;
	 	}
	 	// program := declaration-list EOF .
	 	public ast.DeclarationList program() throws IOException
	 	{
	 		
	 		
	 		LinkedHashMap<String,Symbol> defaults = new LinkedHashMap<String,Symbol>();
	 		defaults.put("readInt", new Symbol("readInt"));
	 		defaults.put("readFloat",new Symbol("readFloat"));
	 		defaults.put("printBool", new Symbol("printBool"));
	 		defaults.put("printInt", new Symbol("printInt"));
	 		defaults.put("printFloat", new Symbol("printFloat"));
			defaults.put("println",new Symbol("println"));
	 		symbolTable.table.push(new LinkedHashMap<String,Symbol>());
	 		symbolTable.table.peekFirst().putAll(defaults);
	 		// if(expect(NonTerminal.DECLARATION_LIST))
	 
	 		ast.DeclarationList declist  = declaration_list();
	 		//expect(Token.Kind.EOF);
	 	   return declist;
	 	}

}
