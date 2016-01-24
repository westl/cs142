package crux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jdk.nashorn.internal.parser.TokenKind;
import crux.Token.Kind;

public class Parser {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";

	// Grammar Rule Reporting ==========================================
	private int parseTreeRecursionDepth = 0;
	private StringBuffer parseTreeBuffer = new StringBuffer();

	public void enterRule(NonTerminal nonTerminal) {
		String lineData = new String();
		for(int i = 0; i < parseTreeRecursionDepth; i++)
		{
			lineData += "  ";
		}
		lineData += nonTerminal.name();
		//System.out.println("descending " + lineData);
		parseTreeBuffer.append(lineData + "\n");
		parseTreeRecursionDepth++;
	}

	private void exitRule(NonTerminal nonTerminal)
	{
		parseTreeRecursionDepth--;
	}

	public String parseTreeReport()
	{
		return parseTreeBuffer.toString();
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

	// Parser ==========================================
			private Scanner scanner;
	private Token currentToken;

	public Parser(Scanner scanner) throws IOException
	{
		this.scanner = scanner;
		this.currentToken = scanner.next();
	}

	public void parse() throws IOException
	{
		try {
			program();
		} catch (QuitParseException q) {
			errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
			errorBuffer.append("[Could not complete parsing.]");
		}
	}

	// Helper Methods ==========================================
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
	public void literal() throws IOException
	{
		enterRule(NonTerminal.LITERAL);
		if(currentToken.kind.name().equals("INTEGER")){
			accept(Token.Kind.INTEGER);
		}
		else if(currentToken.kind.name().equals("FLOAT")){
			accept(Token.Kind.FLOAT);
		}
		else if(currentToken.kind.name().equals("TRUE")){
			accept(Token.Kind.TRUE);
		}
		else if(currentToken.kind.name().equals("FALSE")){
			accept(Token.Kind.FALSE);
		}
		exitRule(NonTerminal.LITERAL);
	}

	// designator := IDENTIFIER { "[" expression0 "]" } .
	public void designator() throws IOException
	{
		enterRule(NonTerminal.DESIGNATOR);

		expect(Token.Kind.IDENTIFIER);
		while (accept(Token.Kind.OPEN_BRACKET)) {
			expression0();
			expect(Token.Kind.CLOSE_BRACKET);
		}

		exitRule(NonTerminal.DESIGNATOR);
	}


	//type := IDENTIFIER .
	private void type() throws IOException {
		// TODO Auto-generated method stub
		//expects IDENTIFIER
		enterRule(NonTerminal.TYPE);
		expect(Token.Kind.IDENTIFIER);
		exitRule(NonTerminal.TYPE);
	}

	//op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
	private void op0() throws IOException {
		// TODO Auto-generated method stub
		//accepts ">=" | "<=" | "!=" | "==" | ">" | "<" .
		enterRule(NonTerminal.OP0);
		if(NonTerminal.OP0.firstSet.contains(currentToken.kind)){
			accept(currentToken.kind);
		}
		else{
			reportError(currentToken.kind);
		}
		exitRule(NonTerminal.OP0);
	}

	//op1 := "+" | "-" | "or" .
	private void op1() throws IOException {
		// TODO Auto-generated method stub
		//accepts "+" | "-" | "or" .
		enterRule(NonTerminal.OP1);
		if(NonTerminal.OP1.firstSet.contains(currentToken.kind)){
			accept(currentToken.kind);
		}
		else{
			reportError(currentToken.kind);
		}
		exitRule(NonTerminal.OP1);
	}

	//op2 := "*" | "/" | "and" .
	private void op2() throws IOException {
		// TODO Auto-generated method stub
		//accepts "*" | "/" | "and" .
		enterRule(NonTerminal.OP2);
		if(NonTerminal.OP2.firstSet.contains(currentToken.kind)){
			accept(currentToken.kind);
		}
		else{
			reportError(currentToken.kind);
		}
		exitRule(NonTerminal.OP2);
	}

	//expression0 := expression1 [ op0 expression1 ] .
	private void expression0() throws IOException {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.EXPRESSION0);
		expression1();
		if(!currentToken.is(Token.Kind.SEMICOLON)){
			if(NonTerminal.OP0.firstSet.contains(currentToken.kind)){
				op0();
				expression1();
			}
		}
		exitRule(NonTerminal.EXPRESSION0);

	}
	//expression1 := expression2 { op1  expression2 } .
	private void expression1() throws IOException {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.EXPRESSION1);
		expression2();
		while(NonTerminal.OP1.firstSet.contains(currentToken.kind)){
			op1();
			expression2();
		}
		exitRule(NonTerminal.EXPRESSION1);
	}

	//expression2 := expression3 { op2 expression3 } .
	private void expression2() throws IOException {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.EXPRESSION2);
		expression3();
		while(NonTerminal.OP2.firstSet.contains(currentToken.kind)){
			op2();
			expression3();
		}
		exitRule(NonTerminal.EXPRESSION2);
	}

	/*
	 * expression3 := "not" expression3
       | "(" expression0 ")"
       | designator
       | call-expression
       | literal .
	 */
	private void expression3() throws IOException {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.EXPRESSION3);
		if(currentToken.kind.name().equals("NOT")){
			accept(Token.Kind.NOT);
			expression3();
		}
		else if(currentToken.kind.name().equals("OPEN_PAREN")){
			accept(Token.Kind.OPEN_PAREN);
			expression0();
			expect(Token.Kind.CLOSE_PAREN);
		}
		else if(currentToken.kind.name().equals("IDENTIFIER")){
			designator();
		}
		else if(currentToken.kind.name().equals("CALL")){
			call_expression();
		}
		else if(currentToken.kind.name().equals("INTEGER") || currentToken.kind.name().equals("FLOAT") || currentToken.kind.name().equals("TRUE") || currentToken.kind.name().equals("FALSE")){
			literal();
		}
		exitRule(NonTerminal.EXPRESSION3);
	}

	//call-expression := "::" IDENTIFIER "(" expression-list ")" .
	private void call_expression() throws IOException {
		enterRule(NonTerminal.CALL_EXPRESSION);
		expect(Token.Kind.CALL);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.OPEN_PAREN);
		expression_list();
		expect(Token.Kind.CLOSE_PAREN);
		exitRule(NonTerminal.CALL_EXPRESSION);
	}
	//expression-list := [ expression0 { "," expression0 } ] .
	private void expression_list() throws IOException {
		enterRule(NonTerminal.EXPRESSION_LIST);
		//expression0();
		if(!currentToken.kind.name().equals("CLOSE_PAREN")){
			expression0();
			while(!currentToken.kind.name().equals("CLOSE_PAREN")){
				expect(Token.Kind.COMMA);
				expression0();
			}
		}
		exitRule(NonTerminal.EXPRESSION_LIST);
	}

	//parameter := IDENTIFIER ":" type .
	private void parameter() throws IOException {
		enterRule(NonTerminal.PARAMETER);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.COLON);
		type();
		exitRule(NonTerminal.PARAMETER);
	}

	//parameter-list := [ parameter { "," parameter } ] .
	private void parameter_list() throws IOException {
		enterRule(NonTerminal.PARAMETER_LIST);
		if(!currentToken.getKind().name().equals("CLOSE_PAREN")){
			parameter();
			while(accept(Token.Kind.COMMA)){
				parameter();
			}
		}
		exitRule(NonTerminal.PARAMETER_LIST);
	}

	//variable-declaration := "var" IDENTIFIER ":" type ";"
	private void variable_declaration() throws IOException{
		enterRule(NonTerminal.VARIABLE_DECLARATION);
		accept(Token.Kind.VAR);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.COLON);
		type();
		expect(Token.Kind.SEMICOLON);
		exitRule(NonTerminal.VARIABLE_DECLARATION);
	}

	//array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
	private void array_declaration() throws IOException{
		enterRule(NonTerminal.ARRAY_DECLARATION);
		accept(Token.Kind.ARRAY);
		expect(Token.Kind.IDENTIFIER);
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
		exitRule(NonTerminal.ARRAY_DECLARATION);
	}

	//function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
	private void function_definition() throws IOException{
		enterRule(NonTerminal.FUNCTION_DEFINITION);
		expect(Token.Kind.FUNC);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.OPEN_PAREN);
		parameter_list();
		expect(Token.Kind.CLOSE_PAREN);
		expect(Token.Kind.COLON);
		type();
		statement_block();
		exitRule(NonTerminal.FUNCTION_DEFINITION);
	}

	//declaration := variable-declaration | array-declaration | function-definition .
	private void declaration() throws IOException{
		Token token = this.currentToken;
		enterRule(NonTerminal.DECLARATION);
		if(token.getKind().name().equals("VAR")){
			variable_declaration();
		}
		else if(token.getKind().name().equals("FUNC")){
			function_definition();
		}
		else if(token.getKind().name().equals("ARRAY")){
			array_declaration();
		}
		else{
			reportError(token.kind);
		}
		exitRule(NonTerminal.DECLARATION);
	}

	//declaration-list := { declaration } .
	private void declaration_list() throws IOException{
		enterRule(NonTerminal.DECLARATION_LIST);
		while(NonTerminal.DECLARATION.firstSet.contains(currentToken.kind))
			declaration();
		exitRule(NonTerminal.DECLARATION_LIST);

	}
	//assignment-statement := "let" designator "=" expression0 ";"
	private void assignment_statement() throws IOException{
		enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
		expect(Token.Kind.LET);
		designator();
		expect(Token.Kind.ASSIGN);
		expression0();
		expect(Token.Kind.SEMICOLON);
		exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
	}
	//call-statement := call-expression ";"
	private void call_statement() throws IOException{
		enterRule(NonTerminal.CALL_STATEMENT);
		call_expression();
		expect(Token.Kind.SEMICOLON);
		exitRule(NonTerminal.CALL_STATEMENT);
	}
	//if-statement := "if" expression0 statement-block [ "else" statement-block ] .
	private void if_statement() throws IOException{
		enterRule(NonTerminal.IF_STATEMENT);
		expect(Token.Kind.IF);
		expression0();
		statement_block();
		if(currentToken.kind.name().equals("ELSE")){
			accept(Token.Kind.ELSE);
			statement_block();
		}
		exitRule(NonTerminal.IF_STATEMENT);
	}
	//while-statement := "while" expression0 statement-block .
	private void while_statement() throws IOException{
		enterRule(NonTerminal.WHILE_STATEMENT);
		expect(Token.Kind.WHILE);
		expression0();
		statement_block();
		exitRule(NonTerminal.WHILE_STATEMENT);
	}
	//return-statement := "return" expression0 ";" .
	private void return_statement() throws IOException{
		enterRule(NonTerminal.RETURN_STATEMENT);
		expect(Token.Kind.RETURN);
		expression0();
		expect(Token.Kind.SEMICOLON);
		exitRule(NonTerminal.RETURN_STATEMENT);
	}
	/*statement := variable-declaration
           | call-statement
           | assignment-statement
           | if-statement
           | while-statement
           | return-statement .*/
	private void statement() throws IOException{
		enterRule(NonTerminal.STATEMENT);
		if(currentToken.getKind().name().equals("VAR")){
			variable_declaration();
		}
		else if(currentToken.getKind().name().equals("CALL")){
			call_statement();
		}
		else if(currentToken.getKind().name().equals("LET")){
			assignment_statement();
		}
		else if(currentToken.getKind().name().equals("IF")){
			if_statement();
		}
		else if(currentToken.getKind().name().equals("WHILE")){
			while_statement();
		}
		else if(currentToken.getKind().name().equals("RETURN")){
			return_statement();
		}
		else{
			reportError(Token.Kind.CLOSE_BRACE);
		}

		exitRule(NonTerminal.STATEMENT);
	}
	//statement-list := { statement } .
	private void statement_list() throws IOException{
		enterRule(NonTerminal.STATEMENT_LIST);
		while(this.currentToken.kind != Token.Kind.CLOSE_BRACE)
			statement();
		exitRule(NonTerminal.STATEMENT_LIST);
	}
	//statement-block := "{" statement-list "}" .
	private void statement_block() throws IOException{
		enterRule(NonTerminal.STATEMENT_BLOCK);
		expect(Token.Kind.OPEN_BRACE);
		statement_list();
		expect(Token.Kind.CLOSE_BRACE);
		exitRule(NonTerminal.STATEMENT_BLOCK);
	}
	// program := declaration-list EOF .
	public void program() throws IOException
	{
		enterRule(NonTerminal.PROGRAM);
		// if(expect(NonTerminal.DECLARATION_LIST))
		declaration_list();
		//expect(Token.Kind.EOF);
		exitRule(NonTerminal.PROGRAM);
	}

}
