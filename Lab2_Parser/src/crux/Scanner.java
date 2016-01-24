package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import crux.Token.Kind;

public class Scanner implements Iterable<Token> {
	public static String studentName = "Lamar West";
	public static String studentID = "79872428";
	public static String uciNetID = "westl";

	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int peakCount; //char pos if we peak
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;

	private StringBuilder sb = null;

	//For characters such as new line and spaces
	private String badChars = "\\n\\s\\r*|\\s";
	//For any tokens that may be operators
	private String ops = "[<>=:,!;][=:]?";
	//For any words 
	private String words = "[{}a-zA-Z]+";
	//For any numbers 
	private String numbers ="(?:\\d+)\\.?\\d*";
	//For when we have to look forward 
	private boolean lookingForward = false;
	Scanner(Reader reader)
	{
		// TODO: initialize the Scanner
		lineNum = 1;
		charPos = 0;
		nextChar = 0;
		peakCount = 0;
		this.input = reader;
		sb = new StringBuilder();
	}	

	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.

	private int readChar() throws IOException {
		int currentChar = input.read();
		while(currentChar == 32 && sb.length() == 0 )
			currentChar = input.read();
			
		return currentChar;
	}



	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next() throws IOException
	{
		
		if(lookingForward == false){
			nextChar = readChar();
			charPos++;
		}
		int currentChar = nextChar;
		Token tobeReturned = null;
		//EOF or Null
		if(currentChar != -1 && currentChar != 0 || sb.length()>0 ){
			String value = Character.toString ((char)currentChar);
			//Cool to append as long as it's not a bad char
			sb.append(value);
			//System.out.println(sb.toString());
			while(sb.toString().matches(numbers) || sb.toString().matches(ops) || sb.toString().matches(words)){
				lookingForward=true;
				nextChar = readChar();
				peakCount++;
				sb.append(Character.toString((char)nextChar));
			}
			String spoiledToken = Character.toString((char)sb.charAt(sb.toString().length()-1));
			lookingForward=false;

			if(spoiledToken.matches(badChars)|| nextChar ==-1)
				sb.deleteCharAt(sb.toString().length()-1);
			else
				sb = new StringBuilder(sb.deleteCharAt(sb.toString().length()-1));
			//start a new string builder with the valid item so we remove the spoiled token
			tobeReturned = tokenize(sb.toString());
			charPos += peakCount;
			peakCount =0;
			//since the token that was valid is created, restart the stringbuilder
			//with the last token we seen
			if(spoiledToken.contains("\n")){
				lineNum++;
				charPos = 1 ;
				peakCount = 0 ;
				//System.out.println("NEW LINE REACHED");
			}
			sb = new StringBuilder();
			if(!spoiledToken.matches(badChars) && nextChar != -1){
				sb.append(spoiledToken);
			}
			else
				return (tobeReturned != null ? tobeReturned :  null);	
		}
		else{
			//must be EOF
			//EOF is reached yet there are things to be tokenized
			return new Token("EOF",lineNum,charPos-1);
		}
		return tobeReturned;
	}
	
		
		
		
		
	/*	
		
		if(lookingForward == false){
			nextChar = readChar();
			charPos++;
		}
		else{
			peakCount++;
		}
		int currentChar = nextChar;
		Token tobeReturned = null;
		//EOF or Null
		if(currentChar != -1 && currentChar != 0 || sb.length()>0 ){
			String value = Character.toString ((char)currentChar);
			//Cool to append as long as it's not a bad char
			sb.append(value);
			//System.out.println(sb.toString());
			if(sb.toString().matches(numbers) || sb.toString().matches(ops) || sb.toString().matches(words)){
				//Get another variable
				
			}
			//The most recent variable we got spoiled the string, tokenize it after removing the last concatenation
			else{
				String spoiledToken = Character.toString((char)sb.charAt(sb.toString().length()-1));
				lookingForward=false;

				if(spoiledToken.matches(badChars)|| nextChar ==-1)
					sb.deleteCharAt(sb.toString().length()-1);
				else
					sb = new StringBuilder(sb.deleteCharAt(sb.toString().length()-1));
				//start a new string builder with the valid item so we remove the spoiled token
				tobeReturned = tokenize(sb.toString());
				charPos += peakCount;
				peakCount =0;
				//since the token that was valid is created, restart the stringbuilder
				//with the last token we seen
				if(spoiledToken.contains("\n")){
					lineNum++;
					charPos = 1 ;
					peakCount = 0 ;
					//System.out.println("NEW LINE REACHED");
				}
				sb = new StringBuilder();
				if(!spoiledToken.matches(badChars) && nextChar != -1){
					sb.append(spoiledToken);
				}
				else
					return (tobeReturned != null ? tobeReturned :  null);	
			}
		}
		else{
			//must be EOF
			//EOF is reached yet there are things to be tokenized
			return new Token("EOF",lineNum,charPos-1);
		}
		return tobeReturned;
	}*/
		
		
	public Token tokenize(String tokenName){
		if(tokenName.equals("EOF"))
			return getEOFToken();
		Token currentToken = null;
		if(!tokenName.matches(badChars) && !tokenName.equals("")){
			int cornerInt = (charPos!=1 ?  1:0);
			currentToken = new Token(tokenName,lineNum,charPos-cornerInt);
		}
		return currentToken;
	}
	@Override
	public Iterator<Token> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public Token getEOFToken(){
		Token cornerCase = new Token("EOF",lineNum,charPos-1);
		cornerCase.setKind(Kind.IDENTIFIER);
		cornerCase.setLexeme("EOF");
		return cornerCase;
	}

}
