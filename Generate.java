public class Generate extends AbstractGenerate {
    
     /** Report an error to the user. */
     public void reportError( Token token, String explanatoryMessage ) throws CompilationException {
          String message = "Error at line: " + token.lineNumber + " | Expected: " + explanatoryMessage + " | Found: " + Token.getName(token.symbol);
          throw new CompilationException(message);
     }
}
