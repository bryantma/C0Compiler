package generator;

import analyser.AST;

import analyser.ASTType;
import binaries.Const;
import binaries.ELF;
import javafx.util.Pair;
import symTable.Symbol;
import symTable.Table;
import tokenizer.Token;
import tokenizer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class Generator {
    private AST ast;
    private Table table;
    private ELF elf;
    private boolean hasGen;

    public Generator(AST ast){
        this.ast = ast;
        this.table = new Table();
        this.elf = new ELF();
        this.hasGen = false;
    }

    public ELF generate(){
        if (!hasGen){
            generateC0(this.ast);
            this.hasGen = true;
        }
        return this.elf;
    }

    private void checkAstType(AST ast, ASTType type){
//        System.out.println("hahaha");
        //System.out.println("check type: " + type.toString());
        if (ast.getType() != type){
            System.out.println("get:" +ast.getType());
            ast.draw(true);
            System.out.println("need: " + type);
            System.out.println("Fatal: Unexpected AST type "+ast.getType()+".");
            System.exit(0);
        }
    }

    private void generateC0(AST ast){
        checkAstType(ast, ASTType.ROOT);
//        System.out.println("gen c0");
        table.enterLevel();
        for (AST a: ast.getChildren()){
            if (a.getType() == ASTType.VAR_DECL){
                analyseVarDecl(a);
            } else {
                analyseFuncDef(a);
            }
        }

        if (!(table.hasSymbol("main") && table.symbolIsFunc("main"))){
            System.out.println("Fatal: Expected main function but there is none.");
            System.exit(0);
        }
    }

    private void analyseVarDecl(AST ast){
        checkAstType(ast, ASTType.VAR_DECL);

        int index;
        boolean isConst = (ast.getChild(0).getType()==ASTType.CONST_QLF);
        if (isConst) { index = 1;}
        else { index = 0; }
        TokenType type = analyseTypeSpec(ast.getChild(index));
        if (type == TokenType.VOID){
            System.out.println("Fatal: Var cannot be void!");
            System.exit(0);
        }
        Symbol sym = new Symbol(isConst,type);
        analyseInitList(ast.getChild(ast.getChildren().size()-2),sym);
    }


    private void analyseFuncDef(AST ast){
        checkAstType(ast, ASTType.FUNC_DEF);

        TokenType type = analyseTypeSpec(ast.getChild(0));
        String name = analyseIdentifier(ast.getChild(1));
        int index = elf.addConst(Const.getString(), name);
        if (table.currentLevel().hasSymbol(name)){
            System.out.println("Fatal: function already exists!");
            System.exit(0);
        }
        Symbol sym = new Symbol();
        sym.setFunc(true);
        table.addSymbol(name, sym);
        table.enterLevel(true);

        ArrayList<TokenType> params = analyseParamCls(ast.getChild(2));
        elf.addFunction(name, params, index, type);

        HashMap<TokenType, Integer> statements = analyseCompState(ast.getChild(3), false);
        if (!(statements.containsKey(TokenType.RETURN) || type == TokenType.VOID)){
            System.out.println("Expected return value but there is none!");
        }

        if (type == TokenType.VOID){
            addInstruction(PcodeType.RET);
        }
        else {
            addInstruction(PcodeType.IPUSH, 0);
            addInstruction(PcodeType.IRET);
        }
    }

    private void analyseInitList(AST ast, Symbol sym){
        checkAstType(ast, ASTType.INIT_LIST);
        for (AST a: ast.getChildren()){
            if (a.getType() == ASTType.INIT_DECL){
                analyseInitDecl(a, sym);
            }
        }
    }

    private void analyseInitDecl(AST ast, Symbol sym){
        checkAstType(ast, ASTType.INIT_DECL);

        String name = analyseIdentifier(ast.getChild(0));
        if (table.currentLevel().hasSymbol(name)){
            System.out.println("Fatal: Symbol already exists.");
            System.exit(0);
        }
        table.addSymbol(name, new Symbol(sym));

//        System.out.println("hahaha"+name);
        TokenType type = table.getType(name);
        //System.out.println("lalala");
        int size = table.getSize(name);
        Pair<Integer, Integer> off = table.getSymOff(name);

        addInstruction(PcodeType.SNEW, size);

        if (ast.getChildren().size() == 1) {
            if (sym.isConst()) {
                System.out.println("Fatal: constant cannot be initialized.");
                System.exit(0);
            }
        }
        else {
            addInstruction(PcodeType.LOADA, off.getKey(), off.getValue());
            TokenType tt = analyseInitializer(ast.getChild(1)).getKey();
//            System.out.println("tt: "+tt);
//            System.out.println("type: "+type);
            if (tt == type && type == TokenType.INT) {
                addInstruction(PcodeType.ISTORE);
            }
            else {
                System.out.println("Fatal: type does not match.");
                System.exit(0);
            }
        }
    }

    private Pair<TokenType, Integer> analyseInitializer(AST ast){
        checkAstType(ast, ASTType.INITIALIZER);

        AST a = ast.getChild(1);
        Pair<TokenType, Integer> expr = analyseExpr(a);
        if (expr.getKey() == TokenType.VOID){
            System.out.println("Fatal: Void cannot be called.");
            System.exit(0);
        }
        return expr;
    }

    private Pair<TokenType, Integer> analyseExpr(AST ast) {
        checkAstType(ast, ASTType.EXPR);
        AST addExpr = ast.getChild(0);
        TokenType type = analyseAddExpr(addExpr).getKey();
        return new Pair<>(type, 0);
    }

    private Pair<TokenType, Integer> analyseAddExpr(AST ast) {
        checkAstType(ast, ASTType.ADD_EXPR);

        AST mulExpr = ast.getChild(0);
        TokenType ltype = analyseMulExpr(mulExpr).getKey();
        elf.nextIndex();

        for (int i = 1;i < ast.getChildren().size();i += 2){
            TokenType tt = analyseAddOper(ast.getChild(i));
            mulExpr = ast.getChild(i+1);
            TokenType rtype = analyseMulExpr(mulExpr).getKey();

            if (ltype != TokenType.INT || rtype != TokenType.INT) {
                System.out.println("Fatal: Unexpected token type for analysing add expression.");
                System.exit(0);
            }

            if (tt == TokenType.PLUS){
                addInstruction(PcodeType.IADD);
            }
            else {
                addInstruction(PcodeType.ISUB);
            }
        }
        return new Pair<>(ltype, 0);
    }

    private TokenType analyseAddOper(AST ast) {
        //System.out.println("hahaha");
        //System.out.println(ast.getChild(0).getToken().getType());
        checkAstType(ast, ASTType.ADD_OPER);
//        System.out.println("hahaha");

        return ast.getChild(0).getToken().getType();
    }

    private Pair<TokenType, Integer> analyseMulExpr(AST ast) {
        checkAstType(ast, ASTType.MUL_EXPR);

        AST unaryExpr = ast.getChild(0);
        TokenType ltype = analyseUnaryExpr(unaryExpr).getKey();
        elf.nextIndex();

        for (int i = 1;i < ast.getChildren().size();i += 2){
            TokenType tt = analyseMulOper(ast.getChild(i));
            unaryExpr = ast.getChild(i+1);
            TokenType rtype = analyseUnaryExpr(unaryExpr).getKey();

            if (ltype != TokenType.INT || rtype != TokenType.INT) {
                System.out.println("Fatal: Unexpected token type for analysing mul expression.");
                System.exit(0);
            }

            if (tt == TokenType.MULTI){
                addInstruction(PcodeType.IMUL);
            }
            else {
                addInstruction(PcodeType.IDIV);
            }
        }
        return new Pair<>(ltype, 0);
    }

    private TokenType analyseMulOper(AST ast) {
        checkAstType(ast, ASTType.MUL_OPER);
        return ast.getChild(0).getToken().getType();
    }

    private Pair<TokenType, Integer> analyseUnaryExpr(AST ast) {
        checkAstType(ast, ASTType.UNARY_EXPR);

        AST priExpr = ast.getChild(-1);
        TokenType type = analysePriExpr(priExpr).getKey();

        if (ast.getChildren().size() == 2){
            if (type == TokenType.VOID){
                System.out.println("Fatal: type void cannot be called.");
                System.exit(0);
            }

            TokenType tt = analyseUnaryOper(ast.getChild(0));
            if (tt == TokenType.MINUS){
                addInstruction(PcodeType.INEG);
            }
            else {
                checkReturn(tt, TokenType.PLUS);
            }
        }
        return new Pair<>(type, 0);
    }


    private TokenType analyseUnaryOper(AST ast) {
        checkAstType(ast, ASTType.UNARY_OPER);
        return ast.getChild(0).getToken().getType();
    }

    private Pair<TokenType, Integer> analysePriExpr(AST ast) {
        checkAstType(ast, ASTType.PRI_EXPR);

        ASTType astType = ast.getChild(0).getType();

        if (astType == ASTType.TOKEN){
            return new Pair<>(analyseExpr(ast.getChild(1)).getKey(), 0);
        }
        else if (astType == ASTType.IDENTIFIER){
            String name = analyseIdentifier(ast.getChild(0));
            //System.out.println("jajhah: "+name);
            if (table.hasSymbol(name) == false){
                //System.out.println(table);
                System.out.println("Fatal: undefined variable.");
                System.exit(0);
            }
            if (table.symbolIsFunc(name)){
                //System.out.println("bigname: "+name);
                System.out.println("Fatal: symbol is already defined as function.");
                System.exit(0);
            }
            Pair<Integer, Integer> off = table.getSymOff(name);
            TokenType type = table.getType(name);
            addInstruction(PcodeType.LOADA, off.getKey(), off.getValue());

            if (type == TokenType.INT){
                addInstruction(PcodeType.ILOAD);
            }
            return new Pair<>(table.getType(name), 0);
        }
        else if (astType == ASTType.INTEGER){
            Integer val = analyseInteger(ast.getChild(0));
            addInstruction(PcodeType.IPUSH, val);
            return new Pair<>(TokenType.INT, val);
        }
        else {
            //System.out.println("hahaha: "+ astType);
            if (astType != ASTType.FUNC_CALL){
                System.out.println("Fatal: invalid primary expression.");
                System.exit(0);
                return null;
            }
            else {
                //System.out.println("haha" +astType);
                return analyseFuncCall(ast.getChild(0));
            }
        }
        //return null;
    }

    private Integer analyseInteger(AST ast) {
        checkAstType(ast, ASTType.INTEGER);
        return Integer.parseInt(ast.getChild(0).getToken().getValue().toString());
    }

    private TokenType analyseTypeSpec(AST ast) {
        checkAstType(ast, ASTType.TYPE_SPEC);
        return analyseSingleTypeSpec(ast.getChild(0));
    }

    private TokenType analyseSingleTypeSpec(AST ast){
        checkAstType(ast, ASTType.SINGLE_TYPE_SPEC);
        TokenType type = ast.getChild(0).getToken().getType();
        if (type != TokenType.INT && type != TokenType.VOID){
            System.out.println("Fatal: Unexpected type specifier for code generation.");
            System.exit(0);
        }
        return type;
    }

    private String analyseIdentifier(AST ast){
        checkAstType(ast, ASTType.IDENTIFIER);
        return ast.getChild(0).getToken().toString();
    }

    private ArrayList<TokenType> analyseParamCls(AST ast){
        checkAstType(ast, ASTType.PARAM_CLS);
        if (ast.getChild(1).getType() == ASTType.PARAM_LIST){
            return analyseParamList(ast.getChild(1));
        }
        return new ArrayList<TokenType>();
    }

    private ArrayList<TokenType> analyseParamList(AST ast){
        checkAstType(ast, ASTType.PARAM_LIST);

        ArrayList<AST> params = new ArrayList<AST>();
        ArrayList<TokenType> list = new ArrayList<TokenType>();
        for (AST a: ast.getChildren()){
            if (a.getType() == ASTType.PARAM_DECL){
                params.add(a);
            }
        }
        for (AST a: params){
            list.add(analyseParamDecl(a));
        }

        return list;
    }

    private TokenType analyseParamDecl(AST ast){
        checkAstType(ast, ASTType.PARAM_DECL);

        int index;
        boolean isConst = (ast.getChild(0).getType()==ASTType.CONST_QLF);
        if (isConst) { index = 1;}
        else { index = 0; }
        TokenType type = analyseTypeSpec(ast.getChild(index));
        if (type == TokenType.VOID){
            System.out.println("Fatal: Var cannot be void!");
            System.exit(0);
        }
        Symbol sym = new Symbol(isConst,type);
        String name = analyseIdentifier(ast.getChild(ast.getChildren().size()-1));
        table.addSymbol(name, sym);
        return type;
    }

    private HashMap<TokenType, Integer> analyseCompState(AST ast, boolean enter){
        checkAstType(ast, ASTType.COMP_STATE);

        if (enter){
            table.enterLevel();
        }
        ArrayList<AST> varDecl = new ArrayList<AST>();
        for (AST a: ast.getChildren()){
            if (a.getType() == ASTType.VAR_DECL){
                varDecl.add(a);
            }
        }
        for (AST v: varDecl){
            analyseVarDecl(v);
        }
        HashMap<TokenType, Integer> compState = analyseStateSeq(ast.getChild(ast.getChildren().size()-2));
        table.exitLevel();

        return compState;
    }

    private HashMap<TokenType, Integer> analyseStateSeq(AST ast) {
        checkAstType(ast, ASTType.STATE_SEQ);
        HashMap<TokenType, Integer> stateSeq = new HashMap<>();
        for (AST a: ast.getChildren()){
            HashMap<TokenType, Integer> state = analyseStatement(a);
            stateSeq.putAll(state);
        }
        return stateSeq;
    }

    private HashMap<TokenType, Integer> analyseStatement(AST ast){
        checkAstType(ast, ASTType.STATEMENT);

        HashMap<TokenType, Integer> stateSeq = new HashMap<>();
        ASTType astType = ast.getChild(0).getType();
        //System.out.println("asttype: "+astType);
        switch (astType){
            case COMP_STATE:
                return analyseCompState(ast.getChild(0), true);
            case COND_STATE:
                return analyseCondState(ast.getChild(0));
            case LOOP:
                return analyseLoop(ast.getChild(0));
            case JUMP:
                return analyseJump(ast.getChild(0));
            case PRINT:
                analysePrint(ast.getChild(0));
                stateSeq.put(TokenType.PRINT,1);
                return stateSeq;
            case SCAN:
                analyseScan(ast.getChild(0));
                stateSeq.put(TokenType.SCAN,1);
                return stateSeq;
            case ASSIGN:
                analyseAssign(ast.getChild(0));
                break;
            case FUNC_CALL:
                //System.out.println("lalalaalala   "+astType);
                analyseFuncCall(ast.getChild(0));
                break;
            default:
                if (astType != ASTType.TOKEN){
                    System.out.println("Fatal: Unexpected type of AST.");
                    System.exit(0);
                }

        }
        return stateSeq;
    }

    private Pair<TokenType, Integer> analyseFuncCall(AST ast) {
        //System.out.println(ast.getType());
        checkAstType(ast, ASTType.FUNC_CALL);
        String name = analyseIdentifier(ast.getChild(0));
        if (table.hasSymbol(name)){
            if (table.symbolIsFunc(name) == false){
                System.out.println("Fatal: Object is not a function.");
                System.exit(0);
            }
        }
        else {
            System.out.println("Fatal: function not defined.");
            System.exit(0);
        }

        ArrayList<TokenType> params = elf.getFunctionParams(name);
        int argNum = 0;
        if (ast.getChild(2).getType() == ASTType.EXPR_LIST){
            argNum = analyseExprList(ast.getChild(2), params);
        }

        int paramNum = elf.getFunctionParamsNum(name);
        if (argNum != paramNum){
            System.out.println("argnum: "+argNum);
            System.out.println("paramnum: "+paramNum);
            System.out.println("Fatal: function parameter numbers do not match.");
            System.exit(0);
        }

        int fIndex = elf.getFunctionIndex(name);
        addInstruction(PcodeType.CALL, fIndex);
        return new Pair<>(elf.getFunctionReturnType(name), 0);
    }

    private Integer analyseExprList(AST ast, ArrayList<TokenType> params) {
        checkAstType(ast, ASTType.EXPR_LIST);
        ArrayList<AST> args = new ArrayList<>();
        for (AST a: ast.getChildren()){
            if (a.getType() == ASTType.EXPR){
                args.add(a);
            }
        }
        for (int i = 0;i < args.size();i++){
            TokenType type = analyseExpr(args.get(i)).getKey();
            if (type != params.get(i)){
                checkReturn(type, params.get(i));
            }
        }
        return args.size();
    }

    private TokenType analyseRelationOper(AST ast){
        checkAstType(ast, ASTType.RELATION_OPER);
        return ast.getChild(0).getToken().getType();
    }

    private void analyseAssign(AST ast) {
        checkAstType(ast, ASTType.ASSIGN);
        String name = analyseIdentifier(ast.getChild(0));
        if (table.symbolIsConst(name)){
            System.out.println("Fatal: cannot assign to constant.");
            System.exit(0);
        }
        else if (table.symbolIsFunc(name)){
            System.out.println("Fatal: cannot assign to function.");
            System.exit(0);
        }
        TokenType type = table.getType(name);
        Pair<Integer, Integer> off = table.getSymOff(name);
        addInstruction(PcodeType.LOADA, off.getKey(), off.getValue());
        TokenType exprType = analyseExpr(ast.getChild(-1)).getKey();
        checkReturn(exprType, type);
        addInstruction(PcodeType.ISTORE);
    }

    private void analyseScan(AST ast) {
        checkAstType(ast, ASTType.SCAN);

        String name = analyseIdentifier(ast.getChild(2));

        if (table.symbolIsConst(name)){
            System.out.println("Fatal: cannot assign to constant.");
            System.exit(0);
        }
        else if (table.symbolIsFunc(name)){
            System.out.println("Fatal: cannot assign to function.");
            System.exit(0);
        }

        TokenType type = table.getType(name);
        Pair<Integer, Integer> off = table.getSymOff(name);
        addInstruction(PcodeType.LOADA, off.getKey(), off.getValue());

        if (type == TokenType.VOID){
            System.out.println("Fatal: cannot assign to void.");
            System.exit(0);
        }
        else if (type == TokenType.INT){
            addInstruction(PcodeType.ISCAN);
            addInstruction(PcodeType.ISTORE);
        }
        else {
            System.out.println("Fatal: Other scan errors.");
            System.exit(0);
        }
    }

    private void analysePrint(AST ast) {
        checkAstType(ast, ASTType.PRINT);
        if (ast.getChild(2).getType() == ASTType.PRINT_LIST){
            analysePrintList(ast.getChild(2));
        }
        addInstruction(PcodeType.PRINTL);
    }

    private void analysePrintList(AST ast) {
        checkAstType(ast, ASTType.PRINT_LIST);
        analysePrintable(ast.getChild(0));
        ArrayList<AST> children = ast.getChildren();
        for (int i = 1; i < children.size(); i++) {
            if (children.get(i).getType() != ASTType.PRINTABLE) {
                continue;
            }
            addInstruction(PcodeType.BIPUSH, 32);
            addInstruction(PcodeType.CPRINT);
            analysePrintable(children.get(i));
        }
    }

    private void analysePrintable(AST ast){
        checkAstType(ast, ASTType.PRINTABLE);
        AST child = ast.getChild(0);
        if (child.getType() == ASTType.EXPR){
            TokenType type = analyseExpr(child).getKey();
            if (type == TokenType.VOID){
                System.out.println("Fatal: void cannot be printed.");
                System.exit(0);
            }
            else if (type == TokenType.INT){
                addInstruction(PcodeType.IPRINT);
            }
            else {
                System.out.println("Fatal: given type cannot be printed.");
                System.exit(0);
            }
        }
        else {
            //todo: string print
        }
    }

    private void analyseReturn(AST ast){
        checkAstType(ast, ASTType.RETURN);

        if (elf.currentFunction().isEmpty()){
            System.out.println("Fatal: expected function but there is none.");
            System.exit(0);
        }

        TokenType type = elf.currentFunction().get().getReturnType();

        if (type == TokenType.VOID){
            if (ast.getChild(1).getType() == ASTType.EXPR){
                TokenType exprType = analyseExpr(ast.getChild(1)).getKey();
                if (exprType != TokenType.VOID){
                    System.out.println("Fatal: void function should not return value.");
                    System.exit(0);
                }
            }
            addInstruction(PcodeType.RET);
        } else {
            if (ast.getChild(1).getType() != ASTType.EXPR){
                System.out.println("Fatal: expected return value but there is none.");
                System.exit(0);
            }
            TokenType exprType = analyseExpr(ast.getChild(1)).getKey();
            checkReturn(exprType, type);
            addInstruction(PcodeType.IRET);
        }
    }

    private HashMap<TokenType, Integer> analyseJump(AST ast) {
        checkAstType(ast, ASTType.JUMP);
        HashMap<TokenType, Integer> jump = new HashMap<>();
        ASTType astType = ast.getChild(0).getType();
        if (astType == ASTType.TOKEN){
            System.out.println("Fatal: Unexpected token for analysing jump.");
            System.exit(0);
        }
        else {
            analyseReturn(ast.getChild(0));
            jump.put(TokenType.RETURN, 1);
            return jump;
        }
        return null;
    }

    private HashMap<TokenType, Integer> analyseLoop(AST ast) {
        checkAstType(ast, ASTType.LOOP);
        HashMap<TokenType, Integer> loop = new HashMap<>();

        Token t1 = ast.getChild(0).getToken();
        if (t1.getType() == TokenType.WHILE){
            AST condition = ast.getChild(2);
            AST whileState = ast.getChild(4);

            int condIndex = elf.nextIndex();
            PcodeType jIns = analyseCondition(condition);
            int jIndex = elf.nextIndex();
            addInstruction(jIns, 0);

            loop = analyseStatement(whileState);
            addInstruction(PcodeType.JMP, condIndex);

            int insIndex = elf.nextIndex();
            int off = insIndex;
            elf.updateInstructions(jIndex, off);
            return loop;
        }
        else {
            System.out.println("Fatal: invalid loop statement.");
            System.exit(0);
        }
        return null;
    }


    private HashMap<TokenType, Integer> analyseCondState(AST ast) {
        checkAstType(ast, ASTType.COND_STATE);
        HashMap<TokenType, Integer> condState = new HashMap<>();
        Token t1 = ast.getChild(0).getToken();
        if (t1.getType() == TokenType.IF){
            AST condition = ast.getChild(2);
            AST ifState = ast.getChild(4);
            PcodeType j = analyseCondition(condition);
            int jIndex = elf.nextIndex();
            addInstruction(j, 0);
            condState.putAll(analyseStatement(ifState));

            if (ast.getChild(-2).getToken().getType() == TokenType.ELSE){
                int jmpIndex = elf.nextIndex();
                addInstruction(PcodeType.JMP, 0);
                AST elseState = ast.getChild(-1);
                int elseIndex = elf.nextIndex();
                condState.putAll(analyseStatement(elseState));
                int insIndex = elf.nextIndex();

                int jOff = elseIndex;
                int jmpOff = insIndex;
                elf.updateInstructions(jIndex, jOff);
                elf.updateInstructions(jmpIndex, jmpOff);
            }
            else {
                int _insIndex = elf.nextIndex();
                int _jOff =  _insIndex;
                elf.updateInstructions(jIndex, _jOff);
            }
            return condState;
        } else {
            System.out.println("Fatal: expected IF token but there is none.");
            System.exit(0);
        }
        return null;
    }

    private PcodeType analyseCondition(AST ast) {
        checkAstType(ast, ASTType.CONDITION);

        TokenType ltype = analyseExpr(ast.getChild(0)).getKey();
        if (ltype == TokenType.VOID){
            System.out.println("Fatal: void cannot be called.");
            System.exit(0);
        }
        elf.nextIndex();
        if (ast.getChildren().size() == 1){
            return PcodeType.JE;
        }
        TokenType cmp = analyseRelationOper(ast.getChild(1));
        TokenType rtype = analyseExpr(ast.getChild(-1)).getKey();
        if (rtype == TokenType.VOID){
            System.out.println("Fatal: void cannot be called.");
            System.exit(0);
        }

        if (rtype != ltype){
            System.out.println("Fatal: Token cannot be compared.");
            System.exit(0);
        }

        addInstruction(PcodeType.ICMP);

        switch (cmp){
            case EQUAL:
                return PcodeType.JNE;
            case NEQ:
                return PcodeType.JE;
            case LESS:
                return PcodeType.JGE;
            case LEQ:
                return PcodeType.JG;
            case GRE:
                return PcodeType.JLE;
            case GEQ:
                return PcodeType.JL;
            default:
                System.out.println("Fatal: invalid compare operator.");
                System.exit(0);
        }
        return null;
    }


    private void addInstruction(PcodeType pcodeType, Integer... operands){
        ArrayList<Pcode> instructions = this.elf.currentInstructions();
        instructions.add(new Pcode(pcodeType, operands));
    }

    private void addInstruction(Integer index, PcodeType pcodeType, Integer... operands){
        ArrayList<Pcode> instructions = this.elf.currentInstructions();
        instructions.add(index, new Pcode(pcodeType, operands));
    }

    private void checkReturn(TokenType t1, TokenType t2) {
        if (t1 != t2){
            System.out.println("Fatal: Token type does not match.");
            System.exit(0);
        }
    }

}
