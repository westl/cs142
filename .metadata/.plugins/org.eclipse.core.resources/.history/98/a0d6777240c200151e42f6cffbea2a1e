package crux;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    public static String studentName = "TODO: Your Name";
    public static String studentID = "TODO: Your 8-digit id";
    public static String uciNetID = "TODO: uci-net id";
    
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
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
     
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
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
    
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        this.currentToken = scanner.next();
    }
    
    public void parse()
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
    	
        return nt.firstSet().contains(currentToken.kind());
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }    
    
    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
   
    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }
        
    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }
    
    private void error(NonTerminal nt){   
    	String errorMessage = reportSyntaxError(nt);
    	throw new QuitParseException(errorMessage);
    }
    
    private void error(Token.Kind kind){
    	 String errorMessage = reportSyntaxError(kind);
         throw new QuitParseException(errorMessage);
    }
// Grammar Rules =====================================================
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public void literal()
    {
        throw new RuntimeException("implement this");
    }
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
 /*   public void designator()
    {
        enterRule(NonTerminal.DESIGNATOR);

        expect(Token.Kind.IDENTIFIER);
        while (accept(Token.Kind.OPEN_BRACKET)) {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }
        
        exitRule(NonTerminal.DESIGNATOR);
    } */

    //program := declaration-list EOF .
    public void program()
    {
        enterRule(NonTerminal.PROGRAM);
        declerationList();
        expect(Token.Kind.EOF);
        exitRule(NonTerminal.PROGRAM);
    }
    
    //declaration-list := { declaration } .
    private void declerationList() {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.DECLARATION_LIST);
		decleration();
		exitRule(NonTerminal.DECLARATION_LIST);
	}

    //declaration := variable-declaration | array-declaration | function-definition .
	private void decleration() {
		enterRule(NonTerminal.DECLARATION);
		
		if(this.currentToken.kind == Token.Kind.VAR){
			variableDeclaration();
		}
		else if(this.currentToken.kind == Token.Kind.ARRAY){
			arrayDeclaration();
		}
		else if(this.currentToken.kind == Token.Kind.FUNC){
			functionDefinition();
		}
		else{
			error(currentToken.kind);
		}
		
		exitRule(NonTerminal.DECLARATION);
		
	}

	//function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
	private void functionDefinition() {
		enterRule(NonTerminal.FUNCTION_DEFINITION);
		expect(Token.Kind.FUNC);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.OPEN_PAREN);
		paramerterList();
		expect(Token.Kind.CLOSE_PAREN);
		expect(Token.Kind.COLON);
		type();
		statementBlock();
		exitRule(NonTerminal.FUNCTION_DEFINITION);
		
	}

	//statement-block := "{" statement-list "}"
	private void statementBlock() {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.STATEMENT_BLOCK);
		expect(Token.Kind.OPEN_BRACE);
		statementList();
		expect(Token.Kind.CLOSE_BRACE);
		exitRule(NonTerminal.STATEMENT_BLOCK);
		
	}

	//statement-list := { statement } .
	private void statementList() {
		// TODO Auto-generated method stub

		enterRule(NonTerminal.STATEMENT_LIST);
		while(accept(NonTerminal.STATEMENT))
			statement();
		
		exitRule(NonTerminal.STATEMENT_LIST);
		
	}

	/*statement := variable-declaration
           | call-statement
           | assignment-statement
           | if-statement
           | while-statement
           | return-statement . */
	private void statement() {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.STATEMENT);
		if(this.currentToken.kind == Token.Kind.VAR)
			this.variableDeclaration();
		else if(this.currentToken.kind == Token.Kind.ASSIGN)
			this.assignmentStatement();
		else if(this.currentToken.kind == Token.Kind.IF)
			this.ifStatement();
		else if(this.currentToken.kind == Token.Kind.WHILE)
			this.whileStatement();
		else if(this.currentToken.kind == Token.Kind.RETURN)
			this.returnStatement();
		else
			this.error(this.currentToken.kind);
		exitRule(NonTerminal.STATEMENT);
		
	}

	private void returnStatement() {
		// TODO Auto-generated method stub
		
	}

	private void whileStatement() {
		// TODO Auto-generated method stub
		
	}

	private void ifStatement() {
		// TODO Auto-generated method stub
		
	}

	private void assignmentStatement() {
		
		
	}

	//type := IDENTIFIER .
	private void type() {
		enterRule(NonTerminal.TYPE);
		expect(Token.Kind.IDENTIFIER);
		exitRule(NonTerminal.TYPE);
		
	}
	//parameter-list := [ parameter { "," parameter } ] .
	private void paramerterList() {
		// TODO Auto-generated method stub
		enterRule(NonTerminal.PARAMETER_LIST);
		if(currentToken.kind != Token.Kind.CLOSE_PAREN){
			parameter();
			while(accept(Token.Kind.COMMA))
				parameter();
		}
		else
			exitRule(NonTerminal.PARAMETER_LIST);
	}

	//parameter := IDENTIFIER ":" type .
	private void parameter() {
		enterRule(NonTerminal.PARAMETER);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.COLON);
		type();
		exitRule(NonTerminal.PARAMETER);
	}

	private void arrayDeclaration() {
		// TODO Auto-generated method stub
		
	}

	//variable-declaration := "var" IDENTIFIER ":" type ";"
	private void variableDeclaration() {
		enterRule(NonTerminal.VARIABLE_DECLARATION);
		expect(Token.Kind.VAR);
		expect(Token.Kind.IDENTIFIER);
		expect(Token.Kind.COLON);
		type();
		expect(Token.Kind.SEMICOLON);
		exitRule(NonTerminal.VARIABLE_DECLARATION);
	}


	private void expression0() {
		// TODO Auto-generated method stub
		
	}
    

}
