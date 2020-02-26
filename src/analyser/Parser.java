package analyser;

import tokenizer.Token;
import tokenizer.TokenType;


import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Iterator;

public class Parser {
    private ArrayList<Token> tokens;
    private int index;
    private boolean isParsed;
    private AST ast;
    public Parser(ArrayList<Token> allTokens){
        this.tokens = allTokens;
        index = 0;
        isParsed = false;
        //ast = new AST();
    }

    public AST parseAll(){
        if (!isParsed){
            ast = parseC0();
            isParsed = true;
        }
        return ast;
    }

    public AST parseC0(){
        AST _ast = new AST(ASTType.ROOT);
        while (peek() != null){
            Token tok = peek();
            if (tok.getType() == TokenType.CONST){
                _ast.addChild(parseVarDecl());
            }
            else if (tok.getType() == TokenType.INT || tok.getType() == TokenType.VOID){
                Token advToken1 = nextToken();
                Token advToken2 = nextToken();
                Token advToken3 = nextToken();
                if (advToken3 == null){
                    System.out.println("Invalid var declaration.");
                    System.exit(0);
                }
                for (int i = 0;i < 3;i++){
                    unreadToken();
                }
                if (advToken3.getType() == TokenType.LPAR){
                    _ast.addChild(parseFuncDef());
                    break;
                }
                else {
                    _ast.addChild(parseVarDecl());
                }
            }
            else {
                System.out.println("Need type specifier.");
                System.exit(0);
            }
        }

        while (peek() != null){
            _ast.addChild(parseFuncDef());
        }

        return _ast;
    }

    public AST parseVarDecl(){
        AST _ast = new AST(ASTType.VAR_DECL);
        Token tok = peek();
        if (tok == null) {
            System.out.println("Need const qualifier.");
            System.exit(0);
        }
        if (tok.getType() == TokenType.CONST){
            _ast.addChild(parseConstQlf());
        }
        _ast.addChild(parseTypeSpec());
        _ast.addChild(parseInitList());
//        System.out.println("hhh");
        //raw(true);
        _ast.addChild(checkToken(TokenType.SEMICOLON));

        return _ast;
    }

    public AST parseFuncDef(){
        AST _ast = new AST(ASTType.FUNC_DEF);
        Token tok = peek();
        if (tok == null) {
            System.out.println("Need function definition.");
            System.exit(0);
        }
        _ast.addChild(parseTypeSpec());
        _ast.addChild(parseIdentifier());
        _ast.addChild(parseParamCls());
        _ast.addChild(parseCompState());

        return _ast;
    }

    public AST parseConstQlf(){
        AST _ast = new AST(ASTType.CONST_QLF);
        _ast.addChild(checkToken(TokenType.CONST));
        return _ast;
    }

    private AST parseTypeSpec() {
        AST _ast = new AST(ASTType.TYPE_SPEC);
        _ast.addChild(parseSingleTypeSpec());
        return _ast;
    }

    private AST parseSingleTypeSpec(){
        AST _ast = new AST(ASTType.SINGLE_TYPE_SPEC);
        Token tok = nextToken();
//        System.out.println(tok+"lalala");
        if (hasTokenType(tok.getType())){
            _ast.addChild(new AST(ASTType.TOKEN, tok));
        }
        else {
            System.out.println("Invalid variable type.");
            System.exit(0);
        }
        return _ast;
    }

    private AST parseInitList(){
        AST _ast = new AST(ASTType.INIT_LIST);
        _ast.addChild(parseInitDecl());

        while (true){
            Token tok = peek();
            if (tok == null || tok.getType() != TokenType.COMMA){
                break;
            }
            _ast.addChild(checkToken(TokenType.COMMA));
            _ast.addChild(parseInitDecl());
        }
        return _ast;
    }

    private AST parseInitDecl(){
        AST _ast = new AST(ASTType.INIT_DECL);
        _ast.addChild(parseIdentifier());

        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: yet another token bugllll.");
            System.exit(0);
        }
        if (tok.getType() == TokenType.ASSIGN){
            _ast.addChild(parseInitializer());
        }
        return _ast;
    }

    private AST parseIdentifier() {
        AST _ast = new AST(ASTType.IDENTIFIER);

        Token tok = nextToken();
        if (tok.getType() == null){
            System.out.println("Expected a token for parsing identifier.");
            System.exit(0);
        }

        if (tok.getType() != TokenType.IDENTIFIER){
            //_ast.draw(true);
            System.out.println("Expected an identifier for parsing identifier.");
            System.exit(0);
        }
        _ast.addChild(new AST(ASTType.TOKEN,tok));
        return _ast;
    }

    private AST parseInitializer(){
        AST _ast = new AST(ASTType.INITIALIZER);

        _ast.addChild(checkToken(TokenType.ASSIGN));
        _ast.addChild(parseExpr());
        return _ast;
    }

    private AST parseExpr(){
        AST _ast = new AST(ASTType.EXPR);
        _ast.addChild(parseAddExpr());
        return _ast;
    }

    private AST parseAddExpr(){
        AST _ast = new AST(ASTType.ADD_EXPR);
        _ast.addChild(parseMultExpr());

        while (true){
            Token tok = peek();
            if (tok == null || (tok.getType()!=TokenType.PLUS&&tok.getType()!=TokenType.MINUS)){
                break;
            }
            _ast.addChild(parseAddOper());
            _ast.addChild(parseMultExpr());
        }
        return _ast;
    }

    private AST parseMultExpr(){
        AST _ast = new AST(ASTType.MUL_EXPR);
        _ast.addChild(parseUnaryExpr());

        while (true){
            Token tok = peek();
            if (tok == null || (tok.getType()!=TokenType.MULTI&&tok.getType()!=TokenType.DIVI)){
                break;
            }
            _ast.addChild(parseMultOper());
            _ast.addChild(parseUnaryExpr());
        }
        return _ast;
    }

    private AST parseUnaryExpr(){
        AST _ast = new AST(ASTType.UNARY_EXPR);
        Token tok = peek();

        if (tok == null){
            System.out.println("Expected a token but there is none.");
            System.exit(0);
        }
        if (tok.getType()==TokenType.MINUS || tok.getType()==TokenType.PLUS){
            _ast.addChild(parseUnaryOper());
        }
        _ast.addChild(parsePriExpr());
        return _ast;
    }

    private AST parseAddOper(){
        AST _ast = new AST(ASTType.ADD_OPER);
        Token tok = nextToken();

        if (tok.getType()!=TokenType.MINUS && tok.getType()!=TokenType.PLUS){
            System.out.println("Unexpected token for parsing additive operator.");
            System.exit(0);
        }
        _ast.addChild(new AST(ASTType.TOKEN,tok));
        return _ast;
    }

    private AST parseMultOper(){
        AST _ast = new AST(ASTType.MUL_OPER);
        Token tok = nextToken();

        if (tok.getType()!=TokenType.MULTI && tok.getType()!=TokenType.DIVI){
            System.out.println("Unexpected token for parsing multiplicative operator.");
            System.exit(0);
        }
        _ast.addChild(new AST(ASTType.TOKEN,tok));
        return _ast;
    }

    private AST parseUnaryOper(){
        AST _ast = new AST(ASTType.UNARY_OPER);
        Token tok = nextToken();

        if (tok.getType()!=TokenType.MINUS && tok.getType()!=TokenType.PLUS){
            System.out.println("Unexpected token for parsing unary operator.");
            System.exit(0);
        }
        _ast.addChild(new AST(ASTType.TOKEN,tok));
        return _ast;
    }

    private AST parseFuncCall(){
        AST _ast = new AST(ASTType.FUNC_CALL);
        _ast.addChild(parseIdentifier());
        _ast.addChild(checkToken(TokenType.LPAR));
        Token tok = peek();
        if (tok == null){
            System.out.println("Expected token here but there is none.");
            System.exit(0);
        }
        if (tok.getType() != TokenType.RPAR){
            _ast.addChild(parseExprList());
        }
        _ast.addChild(checkToken(TokenType.RPAR));
        return _ast;
    }

    private AST parseExprList(){
        AST _ast = new AST(ASTType.EXPR_LIST);
        _ast.addChild(parseExpr());
        while (true){
            Token tok = peek();
            if (tok == null || tok.getType() != TokenType.COMMA){
                break;
            }
            _ast.addChild(checkToken(TokenType.COMMA));
            _ast.addChild(parseExpr());
        }
        return _ast;
    }

    private AST parseParamCls() {
        AST _ast = new AST(ASTType.PARAM_CLS);
        _ast.addChild(checkToken(TokenType.LPAR));
        Token tok = peek();
        if (tok == null){
            System.out.println("Expected token here but there is none.");
            System.exit(0);
        }
        if (tok.getType() != TokenType.RPAR){
            _ast.addChild(parseParamList());
        }
        _ast.addChild(checkToken(TokenType.RPAR));
        return _ast;
    }

    private AST parseParamList(){
        AST _ast = new AST(ASTType.PARAM_LIST);
        _ast.addChild(parseParamDecl());
        while (true){
            Token tok = peek();
            if (tok == null || tok.getType()!=TokenType.COMMA){
                break;
            }
            _ast.addChild(checkToken(TokenType.COMMA));
            _ast.addChild(parseParamDecl());
        }
        return _ast;
    }

    private AST parseParamDecl(){
        AST _ast = new AST(ASTType.PARAM_DECL);
        Token tok = peek();
        if (tok == null){
            System.out.println("Expected type specifier here but there is none.");
            System.exit(0);
        }
        if (tok.getType() == TokenType.CONST){
            _ast.addChild(parseConstQlf());
        }
        _ast.addChild(parseTypeSpec());
        _ast.addChild(parseIdentifier());
        return _ast;
    }

    private AST parseCompState(){
        AST _ast = new AST(ASTType.COMP_STATE);
        _ast.addChild(checkToken(TokenType.LBRA));
        while (true){
            Token tok = peek();
//            System.out.println("has tok:" + tok.toString());
            if (tok == null){
                System.out.println("Expected statement here but there is none.");
                System.exit(0);
            }
            //todo: check utility
            if (tok.getType() != TokenType.INT && tok.getType() != TokenType.CONST && tok.getType() != TokenType.VOID){
                break;
            }
            _ast.addChild(parseVarDecl());
        }
        _ast.addChild(parseStateSeq());
        _ast.addChild(checkToken(TokenType.RBRA));

        return _ast;
    }

    private AST parseStateSeq(){
        AST _ast = new AST(ASTType.STATE_SEQ);
        while (true){
            Token tok = peek();
            if (tok == null){
                System.out.println("Expected statement sequence here but there is none.");
                System.exit(0);
            }
            else if (tok.getType()==TokenType.LBRA ||
                     tok.getType()==TokenType.IF ||
                     tok.getType()==TokenType.SWITCH ||
                     tok.getType()==TokenType.WHILE ||
                     tok.getType()==TokenType.DO ||
                     tok.getType()==TokenType.FOR ||
                     tok.getType()==TokenType.BREAK ||
                     tok.getType()==TokenType.CONTINUE ||
                     tok.getType()==TokenType.RETURN ||
                     tok.getType()==TokenType.PRINT ||
                     tok.getType()==TokenType.SCAN ||
                     tok.getType()==TokenType.IDENTIFIER ||
                     tok.getType()==TokenType.SEMICOLON) {
                _ast.addChild(parseStatement());
            }
            else {
                break;
            }
        }
        return _ast;
    }

    private AST parseStatement(){
        AST _ast = new AST(ASTType.STATEMENT);
        Token tok = peek();
        if (tok.getType() == TokenType.LBRA){
            _ast.addChild(parseCompState());
        }
        else if (tok.getType() == TokenType.IF || tok.getType() == TokenType.SWITCH){
            _ast.addChild(parseCondState());
        }
        else if (tok.getType() == TokenType.FOR || tok.getType() == TokenType.DO || tok.getType() == TokenType.WHILE){
            _ast.addChild(parseLoop());
        }
        else if (tok.getType() == TokenType.RETURN || tok.getType() == TokenType.BREAK || tok.getType() == TokenType.CONTINUE){
            _ast.addChild(parseJump());
        }
        else if (tok.getType() == TokenType.PRINT){
            _ast.addChild(parsePrint());
        }
        else if (tok.getType() == TokenType.SCAN){
            _ast.addChild(parseScan());
        }
        else if (tok.getType() == TokenType.SEMICOLON){
            _ast.addChild(checkToken(TokenType.SEMICOLON));
        }
        else if (tok.getType() == TokenType.IDENTIFIER){
            tok = nextToken();
            Token tok2 = nextToken();
            if (tok2 == null || (tok2.getType() != TokenType.ASSIGN && tok2.getType() != TokenType.LPAR)){
                System.out.println("Expected left parentheses or assignment but there is none.");
                System.exit(0);
            }
            unreadToken();
            unreadToken();
            if (tok2.getType() == TokenType.ASSIGN){
                _ast.addChild(parseAssign());
                _ast.addChild(checkToken(TokenType.SEMICOLON));
            }
            else if (tok2.getType() == TokenType.LPAR){
                _ast.addChild(parseFuncCall());
                _ast.addChild(checkToken(TokenType.SEMICOLON));
            }
        }
        else {
            System.out.println("Fatal: Invalid statement.");
            System.exit(0);
        }
        return _ast;
    }

    private AST parseCondState(){
        AST _ast = new AST(ASTType.COND_STATE);
        Token tok = peek();
        //todo: catch exception?
        if (tok.getType() == TokenType.IF){
            _ast.addChild(checkToken(TokenType.IF));
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseCondition());
            _ast.addChild(checkToken(TokenType.RPAR));
            _ast.addChild(parseStatement());
        }
        tok = peek();
        if (tok.getType() == TokenType.ELSE){
            _ast.addChild(checkToken(TokenType.ELSE));
            _ast.addChild(parseStatement());
        }
        /*
        else if (tok.getType() == TokenType.SWITCH){
            _ast.addChild(checkToken(TokenType.SWITCH));
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseExpr());
            _ast.addChild(checkToken(TokenType.RPAR));
            _ast.addChild(checkToken(TokenType.LBRA));
            while (true){
                tok = peek();
                if (tok == null){
                    System.out.println("Fatal: Expected a label statement but there is none.");
                    System.exit(0);
                }
                if (tok.getType() != TokenType.CASE && tok.getType() != TokenType.DEFAULT){
                     break;
                }
                _ast.addChild(parseLabelState());
            }
        }*/
        /*else {
            System.out.println("Fatal: Expected a token but there is none.");
            System.exit(0);
        }*/
        return _ast;
    }


    private AST parseCondition(){
        AST _ast = new AST(ASTType.CONDITION);
        _ast.addChild(parseExpr());

        Token tok = peek();
        if (tok != null && (tok.getType() == TokenType.LESS||
                            tok.getType() == TokenType.GRE||
                            tok.getType() == TokenType.LEQ||
                            tok.getType() == TokenType.GEQ||
                            tok.getType() == TokenType.EQUAL||
                            tok.getType() == TokenType.NEQ)){
            _ast.addChild(parseRelationOper());
            _ast.addChild(parseExpr());
        }
        return _ast;
    }
    //todo: check grammar
    /*
    private AST parseLabelState(){
        AST _ast = new AST(ASTType.LABEL_STATE);
        Token tok = peek();
        if (tok.getType() == TokenType.CASE){
            _ast.addChild(checkToken(TokenType.CASE));
            tok = peek();
            if (tok == null){

            }
        }
        return _ast;
    }*/

    private AST parseLoop(){
        AST _ast = new AST(ASTType.LOOP);
        Token tok = peek();
        if (tok.getType() == TokenType.FOR){
            _ast.addChild(checkToken(TokenType.FOR));
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseInitState());

            Token tok2 = peek();
            if (tok2 == null){
                System.out.println("Fatal: Expected token but there is none.");
                System.exit(0);
            }
            if (tok2.getType() != TokenType.SEMICOLON){
                _ast.addChild(parseCondition());
            }
            _ast.addChild(checkToken(TokenType.SEMICOLON));

            tok2 = peek();
            if (tok2 == null){
                System.out.println("Fatal: Expected token but there is none.");
                System.exit(0);
            }
            if (tok2.getType() != TokenType.RPAR){
                _ast.addChild(parseUpdate());
            }
            _ast.addChild(checkToken(TokenType.RPAR));
            _ast.addChild(parseStatement());
        }
        else if (tok.getType() == TokenType.DO){
            _ast.addChild(checkToken(TokenType.DO));
            _ast.addChild(parseStatement());
            _ast.addChild(checkToken(TokenType.WHILE));
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseCondition());
            _ast.addChild(checkToken(TokenType.RPAR));
            _ast.addChild(checkToken(TokenType.SEMICOLON));
        }
        else if (tok.getType() == TokenType.WHILE){
            _ast.addChild(checkToken(TokenType.WHILE));
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseCondition());
            _ast.addChild(checkToken(TokenType.RPAR));
            _ast.addChild(parseStatement());
        }
        return _ast;
    }

    private AST parseInitState(){
        AST _ast = new AST(ASTType.INIT_STATE);
        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: Expected token but there is none.");
            System.exit(0);
        }
        if (tok.getType() != TokenType.SEMICOLON){
            _ast.addChild(parseAssign());
            while (true){
                Token tok2 = peek();
                if (tok2 == null || tok2.getType() != TokenType.COMMA){
                    break;
                }
                _ast.addChild(checkToken(TokenType.COMMA));
                _ast.addChild(parseAssign());
            }
        }
        return _ast;
    }

    /*private AST parseUpdate(){
        AST _ast = new AST(ASTType.UPDATE);
        _ast.addChild(parseAssignOrCall());
        while (true){

        }
        return _ast;
    }*/

    private AST parseJump(){
        AST _ast = new AST(ASTType.JUMP);
        _ast.addChild(parseReturn());
        return _ast;
    }

    private AST parseReturn(){
        AST _ast =  new AST(ASTType.RETURN);
        _ast.addChild(checkToken(TokenType.RETURN));

        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: Expected token but there is none.");
            System.exit(0);
        }

        if (tok.getType() != TokenType.SEMICOLON){
            _ast.addChild(parseExpr());
        }

        _ast.addChild(checkToken(TokenType.SEMICOLON));
        return _ast;
    }

    private AST parseScan(){
        AST _ast =  new AST(ASTType.SCAN);

        _ast.addChild(checkToken(TokenType.SCAN));
        _ast.addChild(checkToken(TokenType.LPAR));
        _ast.addChild(parseIdentifier());
        _ast.addChild(checkToken(TokenType.RPAR));
        _ast.addChild(checkToken(TokenType.SEMICOLON));

        return _ast;
    }

    private AST parseAssign(){
        AST _ast = new AST(ASTType.ASSIGN);
        _ast.addChild(parseIdentifier());
        _ast.addChild(parseAssignOper());
        _ast.addChild(parseExpr());
        return _ast;
    }

    private AST parsePrint(){
        AST _ast = new AST(ASTType.PRINT);

        _ast.addChild(checkToken(TokenType.PRINT));
        _ast.addChild(checkToken(TokenType.LPAR));
        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: Expected printable tokens but there is none.");
            System.exit(0);
        }
        if (tok.getType() != TokenType.RPAR){
            _ast.addChild(parsePrintList());
        }

        _ast.addChild(checkToken(TokenType.RPAR));
        _ast.addChild(checkToken(TokenType.SEMICOLON));
        return _ast;
    }

    private AST parsePrintList(){
        AST _ast = new AST(ASTType.PRINT_LIST);
        _ast.addChild(parsePrintable());

        while (true){
            Token tok = peek();
            if (tok == null || tok.getType() != TokenType.COMMA){
                break;
            }
            _ast.addChild(checkToken(TokenType.COMMA));
            _ast.addChild(parsePrintable());
        }
        return _ast;
    }

    private AST parsePrintable(){
        AST _ast = new AST(ASTType.PRINTABLE);
        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: Expected printable but there is none.");
            System.exit(0);
        }

        _ast.addChild(parseExpr());

        return _ast;
    }


    //todo: check below
    private AST parsePriExpr(){
        AST _ast = new AST(ASTType.PRI_EXPR);
        Token tok = peek();
        if (tok == null){
            System.out.println("Fatal: Expected token but there is none.");
            System.exit(0);
        }
        if (tok.getType() == TokenType.LPAR){
            _ast.addChild(checkToken(TokenType.LPAR));
            _ast.addChild(parseExpr());
            _ast.addChild(checkToken(TokenType.RPAR));
        }
        else if (tok.getType() == TokenType.IDENTIFIER){
            Token tok2 = nextToken();
            Token tok3 = peek();
            if (tok3 == null){
                System.out.println("Fatal: Expected token but there is none.");
                System.exit(0);
            }
            unreadToken();
            if (tok3.getType() == TokenType.LPAR){
                _ast.addChild(parseFuncCall());
            }
            else {
                _ast.addChild(parseIdentifier());
            }
        }
        else if (tok.getType() == TokenType.INTEGER){
            _ast.addChild(parseInteger());
        }
        else {
            System.out.println("Fatal: Expected primary expression.");
            System.exit(0);
        }
        return _ast;
    }

    private AST parseInteger() {
        AST _ast = new AST(ASTType.INTEGER);
        Token tok = peek();
        if (tok == null || tok.getType() != TokenType.INTEGER){
            System.out.println("Fatal: Expected integer literal.");
            System.exit(0);
        }
        _ast.addChild(checkToken(TokenType.INTEGER));
        return _ast;
    }

    private AST parseAssignOper(){
        AST _ast = new AST(ASTType.ASSIGN_OPER);
        _ast.addChild(checkToken(TokenType.ASSIGN));
        return _ast;
    }

    private AST parseUpdate(){
        System.out.println("Removed due to basic C0 grammar.");
        System.exit(0);
        return null;
    }


    private AST parseRelationOper(){
        AST _ast = new AST(ASTType.RELATION_OPER);
        Token tok = nextToken();
        if (tok == null){
            System.out.println("Fatal: Expected token but there is none.");

            System.exit(0);
        }
        _ast.addChild(new AST(ASTType.TOKEN, tok));
        return _ast;
    }

    //todo: check above

    public AST checkToken(TokenType type){
        Token tok = nextToken();
        if (tok == null){
            System.out.println("Fatal: Expected token but there is none.");

            System.exit(0);
        }
        if (tok.getType() != type){
//            System.out.println("pos:" +tok.toString());
//            System.out.println("get type: "+tok.getType());
//            System.out.println("expected: "+type);
            System.out.println("Fatal: Unexpected token type for parsing.");
            System.exit(0);
        }
        return new AST(ASTType.TOKEN, tok);
    }

    public Token peek(){
        if (index >= tokens.size()){
            return null;
        }
        return tokens.get(index);
    }

    public void unreadToken(){
        if (index <= 0){
            System.out.println("Cannot unread at this index.");
            System.exit(0);
        }
        index -= 1;
    }

    public Token nextToken(){
        Token ret = peek();
        index += 1;
        return ret;
    }

    public boolean isEOF(){
        return (index == tokens.size()-1);
    }

    public void addChild(ArrayList<AST> ast, ArrayList<AST> child){
        Iterator<AST> iter = child.iterator();
        while (iter.hasNext()){
            ast.add(iter.next());
        }
    }

    /*public make_pair<Integer, Integer> currentPos(){
        return tokens.get(index).getPos();
    }*/

    public boolean hasTokenType(TokenType type){
        TokenType[] all = TokenType.values();
        for (int i = 0;i < all.length;i++){
            if (type == all[i]){
                return true;
            }
        }
        return false;
    }
}
