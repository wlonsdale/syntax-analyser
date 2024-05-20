/**
 * @Author Will Lonsdale
 **/

import java.io.* ;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {
    /**
     * Constructor for SyntaxAnalyser
     * @param filename the current file
     * @throws IOException if there is an error with compilation
     */
    public SyntaxAnalyser(String filename) throws IOException {
        lex = new LexicalAnalyser(filename);
    }

    /** 
     * Begin processing the first (top level) token.
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("statement part");
        try {
            acceptTerminal(Token.beginSymbol);
            statementList();
            acceptTerminal(Token.endSymbol);
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "statement part");
        }
        myGenerate.finishNonterminal("statement part");
        return;
    }

    /** 
     * Accept a token based on context.
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if (nextToken.symbol == symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();  
        }
        else {
            // Report error
        }
        return;
    }

    /** 
     * This method is called when the next token is a statement list
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void statementList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("statement list");
        try {
            statement();
            while(nextToken.symbol == Token.semicolonSymbol) {
                acceptTerminal(Token.semicolonSymbol);
                statement();
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "statement list");
        }
        myGenerate.finishNonterminal("statement list");
        return;
    }

    /** 
     * Checks the token and calls the corresponding method
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void statement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("statement");
        try {
            switch (nextToken.symbol) {
                case Token.identifier:
                    assignmentStatement();
                    break;
                case Token.ifSymbol:
                    ifStatement();
                    break;
                case Token.whileSymbol:
                    whileStatement();
                    break;
                case Token.callSymbol:
                    procedureStatement();
                    break;
                case Token.untilSymbol:
                    untilStatement();
                    break;
                case Token.forSymbol:
                    forStatement();
                    break;
                default:
                myGenerate.reportError(nextToken, "statement (identifier, if, while, call, until, for)");
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "statement (identifier, if, while, call, until, for)");
        }
        myGenerate.finishNonterminal("statement");
        return;
    }

    /** 
     * This method is called when the next token is an assignment statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void assignmentStatement() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("assignment statement");
        try {
            acceptTerminal(Token.identifier);
            acceptTerminal(Token.becomesSymbol);
            if (nextToken.symbol == Token.stringConstant) {
                acceptTerminal(Token.stringConstant);
            }
            else {
                expression();
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "identifier");
        }
        myGenerate.finishNonterminal("assignment statement");
        return;
    }

    /** 
     * This method is called when the next token is an if statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void ifStatement() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("if statement");
        try {
            acceptTerminal(Token.ifSymbol);
            condition();
            acceptTerminal(Token.thenSymbol);
            statementList();
            if(nextToken.symbol == Token.endSymbol) {
                acceptTerminal(Token.endSymbol);
                acceptTerminal(Token.ifSymbol);
            }
            else {
                acceptTerminal(Token.elseSymbol);
                statementList();
                acceptTerminal(Token.endSymbol);
                acceptTerminal(Token.ifSymbol);
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "if");
        }
        myGenerate.finishNonterminal("if statement");
        return;
    }

    /** 
     * This method is called when the next token is a while statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void whileStatement() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("while statement");
        try {
            acceptTerminal(Token.whileSymbol);
            condition();
            acceptTerminal(Token.loopSymbol);
            statementList();
            acceptTerminal(Token.endSymbol);
            acceptTerminal(Token.loopSymbol);
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "while");
        }
        myGenerate.finishNonterminal("while statement");
        return;
    }

    /** 
     * This method is called when the next token is a procedure statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void procedureStatement() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("procedure statement");
        try {
            acceptTerminal(Token.callSymbol);
            acceptTerminal(Token.identifier);
            acceptTerminal(Token.leftParenthesis);
            argumentList();
            acceptTerminal(Token.rightParenthesis);
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "call, identifier, (, )");
        }
        myGenerate.finishNonterminal("procedure statement");
        return;
    }

    /** 
     * This method is called when the next token is an until statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void untilStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("until statement");
        try {
            acceptTerminal(Token.doSymbol);
            statementList();
            acceptTerminal(Token.untilSymbol);
            condition();
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "until");
        }
        myGenerate.finishNonterminal("until statement");
        return;
    }

    /** 
     * This method is called when the next token is a for statement
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void forStatement() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("for statement");
        try {
            acceptTerminal(Token.forSymbol);
            acceptTerminal(Token.leftParenthesis);
            assignmentStatement();
            acceptTerminal(Token.semicolonSymbol);
            condition();
            acceptTerminal(Token.semicolonSymbol);
            assignmentStatement();
            acceptTerminal(Token.rightParenthesis);
            acceptTerminal(Token.doSymbol);
            statementList();
            acceptTerminal(Token.endSymbol);
            acceptTerminal(Token.loopSymbol);
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "for");
        }
        myGenerate.finishNonterminal("for statement");
        return;
    }

    /** 
     * This method is called when the next token is a condition
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void condition() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("condition");
        try {
            acceptTerminal(Token.identifier);
            conditionalOperator();
            switch(nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.stringConstant:
                    acceptTerminal(Token.stringConstant);
                    break;
                default:
                myGenerate.reportError(nextToken, "identifier, number-constant, string-constant");
                    break;
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "identifier, number-constant, string-constant");
        }
        myGenerate.finishNonterminal("condition");
        return;
    }

    /** 
     * This method is called when the next token is a conditional operator
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    public void conditionalOperator() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("conditional operator");
        try {
            switch(nextToken.symbol) {
                case Token.greaterThanSymbol:
                    acceptTerminal(Token.greaterThanSymbol);
                    break;
                case Token.greaterEqualSymbol:
                    acceptTerminal(Token.greaterEqualSymbol);
                    break;
                case Token.equalSymbol:
                    acceptTerminal(Token.equalSymbol);
                    break;
                case Token.notEqualSymbol:
                    acceptTerminal(Token.notEqualSymbol);
                    break;
                case Token.lessThanSymbol:
                    acceptTerminal(Token.lessThanSymbol);
                    break;
                case Token.lessEqualSymbol:
                    acceptTerminal(Token.lessEqualSymbol);
                    break;
                default:
                myGenerate.reportError(nextToken, ">, >=, =, !=, <, <=");
                    break;
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, ">, >=, =, !=, <, <=");
        }
        myGenerate.finishNonterminal("conditional operator");
        return;
    }

    /** 
     * This method is called when the next token is an argument list
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void argumentList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("argument list");
        try {
            acceptTerminal(Token.identifier);
            while(nextToken.symbol == Token.commaSymbol){
                acceptTerminal(Token.commaSymbol);
                argumentList();
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "comma");
        }
        myGenerate.finishNonterminal("argument list");
        return;
    }

    /** 
     * This method is called when the next token is an expression
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void expression() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("expression");
        try {
            term();
            if (nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
                switch (nextToken.symbol) {
                    case Token.plusSymbol:
                        acceptTerminal(Token.plusSymbol);
                        break;
                    case Token.minusSymbol:
                        acceptTerminal(Token.minusSymbol);
                        break;
                    default:
                    myGenerate.reportError(nextToken, "+, -");
                        break;
                }
                expression();
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "+, -");
        }
        myGenerate.finishNonterminal("expression");
        return;
    }

    /** 
     * This method is called when the next token is a term
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void term() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("term");
        try {
            factor();
            if (nextToken.symbol == Token.timesSymbol || nextToken.symbol == Token.divideSymbol) { 
                switch (nextToken.symbol) {
                    case Token.timesSymbol:
                        acceptTerminal(Token.timesSymbol);
                        break;
                    case Token.divideSymbol:
                        acceptTerminal(Token.divideSymbol);
                        break;
                    default:
                    myGenerate.reportError(nextToken, "*, /");
                        break;
                }
                term(); 
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "*, /");
        }
        myGenerate.finishNonterminal("term");
        return;
    }

    /** 
     * This method is called when the next token is a factor
     * @throws IOException if there is an error reading the file.
     * @throws CompilationException if there is an error with compilation
    */
    private void factor() throws CompilationException, IOException {
        myGenerate.commenceNonterminal("factor");
        try {
            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.leftParenthesis:
                    acceptTerminal(Token.leftParenthesis);
                    expression();
                    acceptTerminal(Token.rightParenthesis);    
                    break;
                default:
                myGenerate.reportError(nextToken, "identifier, number-constant, (, )");
                    break;
            }
        } catch (CompilationException e) {
            myGenerate.reportError(nextToken, "identifier, number-constant, (, )");
        }
        myGenerate.finishNonterminal("factor");
        return;
    }
}
