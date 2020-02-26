package tokenizer;

import java.util.HashMap;
import java.util.HashSet;

public class TokenLib {

    public static HashMap<DFAState, TokenType> s2t = new HashMap<>();
    static {
        s2t.put(DFAState.PLUS, TokenType.PLUS);
        s2t.put(DFAState.MINUS, TokenType.MINUS);
        s2t.put(DFAState.DIVI, TokenType.DIVI);
        s2t.put(DFAState.MULTI, TokenType.MULTI);
        s2t.put(DFAState.EQUAL, TokenType.EQUAL);
        s2t.put(DFAState.SEMICOLON, TokenType.SEMICOLON);
        s2t.put(DFAState.LPAR, TokenType.LPAR);
        s2t.put(DFAState.RPAR, TokenType.RPAR);
        s2t.put(DFAState.LBRA, TokenType.LBRA);
        s2t.put(DFAState.RBRA, TokenType.RBRA);
        s2t.put(DFAState.COMMA, TokenType.COMMA);
        s2t.put(DFAState.WARN, TokenType.WARN);
        s2t.put(DFAState.GRE, TokenType.GRE);
        s2t.put(DFAState.GEQ, TokenType.GEQ);
        s2t.put(DFAState.LESS, TokenType.LESS);
        s2t.put(DFAState.LEQ, TokenType.LEQ);
        s2t.put(DFAState.ASSIGN, TokenType.ASSIGN);
        s2t.put(DFAState.EQUAL, TokenType.EQUAL);
        s2t.put(DFAState.NEQ, TokenType.NEQ);
    }

    public static HashSet<Character> numbers = new HashSet<Character>();
    static {
        numbers.add('1');
        numbers.add('2');
        numbers.add('3');
        numbers.add('4');
        numbers.add('5');
        numbers.add('6');
        numbers.add('7');
        numbers.add('8');
        numbers.add('9');
        numbers.add('0');
    }
    public static HashSet<Character> alphas = new HashSet<Character>();
    static {
        for (int i = 65;i <= 90;i++){
            alphas.add((char) i);
        }
        for (int i = 97;i <= 122;i++){
            alphas.add((char) i);
        }
    }
    public static HashSet<Character> symbols = new HashSet<Character>();
    static {
        symbols.add(':');
        symbols.add('(');
        symbols.add(')');
        symbols.add('{');
        symbols.add('}');
        symbols.add(',');
        symbols.add(';');
        symbols.add('!');
        symbols.add('<');
        symbols.add('=');
        symbols.add('>');
        symbols.add('+');
        symbols.add('-');
        symbols.add('*');
        symbols.add('/');
    }
    public static HashSet<Character> blanks = new HashSet<Character>();
    static {
        blanks.add(' ');
        blanks.add('\t');
        blanks.add('\n');
        blanks.add('\r');
    }
    public static HashMap<String, TokenType> reserved = new HashMap<String, TokenType>();
    static {
        reserved.put("const", TokenType.CONST);
        reserved.put("void", TokenType.VOID);
        reserved.put("int", TokenType.INT);
        reserved.put("char", TokenType.CHAR);
        reserved.put("double", TokenType.DOUBLE);
        reserved.put("struct", TokenType.STRUCT);
        reserved.put("if", TokenType.IF);
        reserved.put("else", TokenType.ELSE);
        reserved.put("switch", TokenType.SWITCH);
        reserved.put("case", TokenType.CASE);
        reserved.put("default", TokenType.DEFAULT);
        reserved.put("while", TokenType.WHILE);
        reserved.put("for", TokenType.FOR);
        reserved.put("do", TokenType.DO);
        reserved.put("return", TokenType.RETURN);
        reserved.put("break", TokenType.BREAK);
        reserved.put("continue", TokenType.CONTINUE);
        reserved.put("print", TokenType.PRINT);
        reserved.put("scan", TokenType.SCAN);
    }

    public static HashSet<Character> hex = new HashSet<Character>();
    static {
        hex.add('a');
        hex.add('b');
        hex.add('c');
        hex.add('d');
        hex.add('e');
        hex.add('f');
        hex.add('A');
        hex.add('B');
        hex.add('C');
        hex.add('D');
        hex.add('E');
        hex.add('F');
        hex.add('x');
        hex.add('X');
    }
}
