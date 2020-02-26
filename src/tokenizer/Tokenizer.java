package tokenizer;

import exceptions.tokenizerExceptions.TokenizerException;
import util.Pos;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class Tokenizer {

    private InputStream in;
    private ArrayList<String> lines;
    private boolean initialized;
    Pos current;

    private ArrayList<Token> allTokens;


    public Tokenizer(InputStream in){
        this.in = in;
        lines = new ArrayList<>();
        initialized = false;
        current = new Pos(0,0);
    }

    public void readAll() throws IOException {
        if (initialized){
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        for (String str; null != (str = br.readLine());){
            lines.add(str+"\n");
        }
        initialized = true;
        return;
    }

    private Optional<Token> Next() throws Exception {
        if (initialized == false){
            readAll();
        }
        if (isEOF()){

        }
        Optional<Token> token = nextToken();
        if (token.isEmpty()){
            //System.out.println(current+", lines: "+lines.size());
            return Optional.empty();
            //System.out.println("Get token failed.");
            //System.exit(0);
        }
//        System.out.println(token);
        return token;
    }

    private boolean isEOF() {
        return current.getKey() >= lines.size();
    }

    public ArrayList<Token> getAllTokens() throws Exception{
        allTokens = new ArrayList<Token>();
        while (true){
            Optional<Token> getToken = Next();
            if (getToken.isPresent()){
                allTokens.add(getToken.get());
            }
            else {
                break;
            }
        }
        return allTokens;
    }

    public Optional<Token> nextToken() throws Exception {
        StringBuilder ss = new StringBuilder();
        DFAState state = DFAState.INIT;
        Pos p = new Pos(0,0);

        while (true) {
            Optional<Character> next = nextChar();
//            System.out.println("Next character is " + next.get() + ", current state is " + state);
            if (state == DFAState.INIT){
                if (next.isEmpty()){
                    return Optional.empty();
                }
                boolean valid = true;
                char ch = next.get();
                if (isBlank(ch)){
                    state = DFAState.INIT;
                }
                else if (Character.isISOControl(ch)){
                    valid = false;
                }
                else if (ch == '0'){
                    state = DFAState.ZERO;
                }
                else if (isDigit(ch)){
                    state = DFAState.DEC;
                }
                else if (isAlpha(ch)){
                    state = DFAState.IDENTIFIER;
                }
                else if (ch == ','){
                    state = DFAState.COMMA;
                }
                else if (ch == '<'){
                    state = DFAState.LESS;
                }
                else if (ch == '>'){
                    state = DFAState.GRE;
                }
                else if (ch == '='){
                    state = DFAState.ASSIGN;
                }
                else if (ch == '+'){
                    state = DFAState.PLUS;
                }
                else if (ch == '-'){
                    state = DFAState.MINUS;
                }
                else if (ch == '/'){
                    state = DFAState.DIVI;
                }
                else if (ch == '*'){
                    state = DFAState.MULTI;
                }
                else if (ch == '!') {
                    state = DFAState.WARN;
                }
                else if (ch == ';'){
                    state = DFAState.SEMICOLON;
                }
                else if (ch == '{'){
                    state = DFAState.LBRA;
                }
                else if (ch == '}'){
                    state = DFAState.RBRA;
                }
                else if (ch == '('){
                    state = DFAState.LPAR;
                }
                else if (ch == ')'){
                    state = DFAState.RPAR;
                }
                else {
                    valid = false;
                }

                if (state != DFAState.INIT){
                    //unreadLast();
                    //ss.append(next);
                }

                if (valid == false){
                    unreadLast();
                    System.out.println("Fatal: invalid characters.");
                    System.exit(0);
                }
                if (state != DFAState.INIT){
                    //unreadLast();
                    ss.append(ch);
                }
                continue;
            }
            else if (state == DFAState.ZERO){
                if (next.isPresent() && (next.get() == 'x' || next.get() == 'X')){
                    state = DFAState.HEX;
                    ss = new StringBuilder();
                    continue;
                }
                else {
                    unreadLast();
                    return Optional.of(new Token(0, TokenType.INTEGER, current, current));
                }
            }
            else if (state == DFAState.DEC){
                if (next.isEmpty()){
                    try {
                        BigInteger num = new BigInteger(ss.toString(), 10);
                        return Optional.of(new Token(num, TokenType.INTEGER, current, current));
                    } catch (Exception e){
                        System.out.println("Fatal: integer overflow.");
                        System.exit(0);
                    }
                }
                char ch = next.get();
                if (isDigit(ch)){
                    ss.append(ch);
                }
                else if (isAlpha(ch)){
                    ss.append(ch);
                    state = DFAState.IDENTIFIER;
                }
                else {
                    unreadLast();
                    try {
                        //System.out.println(ss.toString());
                        BigInteger num = new BigInteger(ss.toString(), 10);
                        return Optional.of(new Token(num, TokenType.INTEGER, current, current));
                    } catch (Exception e){
                        System.out.println("Fatal: integer overflow.");
                        System.exit(0);
                    }
                }
                continue;
            }
            else if (state == DFAState.HEX) {
                if (next.isEmpty()){
                    try {
                        BigInteger num = new BigInteger(ss.toString(), 16);
                        return Optional.of(new Token(num, TokenType.INTEGER, current, current));
                    } catch (Exception e){
                        System.out.println("Fatal: hex integer overflow.");
                        System.exit(0);
                    }
                }
                char ch = next.get();
                if (isDigit(ch) || isHex(ch)){
                    ss.append(ch);
                }
                else if (isAlpha(ch)){
                    unreadLast();
                    unreadLast();
                    return Optional.of(new Token(ss.toString(), TokenType.IDENTIFIER, current, current));
                }
                else {
                    unreadLast();
                    try {
                        BigInteger num = new BigInteger(ss.toString(), 16);
                        return Optional.of(new Token(num, TokenType.INTEGER, current, current));
                    } catch (Exception e){
                        System.out.println("Fatal: hex integer overflow.");
                        System.exit(0);
                    }
                }
                continue;
            }
            else if (state == DFAState.IDENTIFIER){
                if (next.isEmpty()){
                    return checkReserved(ss);
                }
                char ch = next.get();
                if (isAlpha(ch) || isDigit(ch)){
                    ss.append(ch);
                }
                else {
                    unreadLast();
                    return checkReserved(ss);
                }
                continue;
            }
            else if (state == DFAState.DIVI){
                if (next.isEmpty()){
                    unreadLast();
                    return Optional.of(new Token(ss.toString(), TokenType.DIVI, current,current));
                }
                char ch = next.get();
                if (ch == '/'){
                    state = DFAState.SINGLE_COM;
                    continue;
                }
                else if (ch == '*'){
                    state = DFAState.MULTI_COM;
                }
                else {
                    unreadLast();
                    return Optional.of(new Token(ss.toString(), TokenType.DIVI, current,current));
                }
                continue;
            }
            else if (state == DFAState.SINGLE_COM){
                if (next.isEmpty() || next.get() == '\n' || next.get() == '\r'){
                    ss = new StringBuilder();
                    state = DFAState.INIT;
                }
                continue;
            }
            else if (state == DFAState.MULTI_COM){
                if (next.isEmpty()){
                    System.out.println("Fatal: multi line comment unfinished.");
                    System.exit(0);
                }
                if (next.get() == '*'){
                    Optional<Character> nc = nextChar();
                    if (nc.isEmpty()){
                        System.out.println("Fatal: multi line comment unfinished.");
                        System.exit(0);
                    }
                    if (nc.get() == '/'){
                        ss = new StringBuilder();
                        state = DFAState.INIT;
                    }
                    else {
                        unreadLast();
                    }
                }
                continue;
            }
            else if (state == DFAState.WARN){
                if (next.isEmpty() || next.get() != '='){
                    System.out.println("Fatal: invalid exclamation mark.");
                    System.exit(0);
                }
                ss.append('=');
                state = DFAState.NEQ;
                continue;
            }
            else {
                if (TokenLib.s2t.containsKey(state)){
                    if (next.isEmpty() || next.get() != '='){
//                        System.out.println("Shall return, while pointer at " + next.get());
                        unreadLast();
                        return Optional.of(new Token(ss.toString(), TokenLib.s2t.get(state), current, current));
                    }
                    ss.append("=");
                    if (state == DFAState.GRE){
                        state = DFAState.GEQ;
                    }
                    else if (state == DFAState.LESS){
                        state = DFAState.LEQ;
                    }
                    else if (state == DFAState.ASSIGN){
                        state = DFAState.EQUAL;
                    }
                    continue;
                }
                /*if (TokenLib.s2t.get(state) != null){
                    unreadLast();
                    return Optional.of(new Token(ss.toString(), TokenLib.s2t.get(state), current, current));
                }*/
                System.out.println("Fatal: invalid DFAState.");
                System.exit(0);
            }
        }
    }

    private Optional<Token> checkReserved(StringBuilder ss) {
        if (isDigit(ss.toString().charAt(0))){
            System.out.println("Fatal: Identifier cannot initialize with digits.");
            System.exit(0);
        }
        else if (TokenLib.reserved.containsKey(ss.toString())){
            return Optional.of(new Token(ss.toString(), TokenLib.reserved.get(ss.toString()), current, current));
        }
        return Optional.of(new Token(ss.toString(), TokenType.IDENTIFIER, current, current));
    }


    private Optional<Character> nextChar() throws Exception {
        if (isEOF()){
            return Optional.empty();
        }
        char ret = lines.get(current.getKey()).charAt(current.getValue());
        current = nextPos();
        return Optional.of(ret);
    }

    private Pos nextPos(){
        if (current.getKey() >= lines.size()){
            System.out.println("EOF. Aborting... ");
            System.exit(0);
        }
        if (current.getValue() == lines.get(current.getKey()).length()-1){
            return new Pos(current.getKey()+1, 0);
        }
        else {
            return new Pos(current.getKey(), current.getValue()+1);
        }
    }

    private Pos prevPos() throws Exception {
        if (current.getKey() == 0 && current.getValue() == 0){
            throw new TokenizerException(0,0, new StringBuilder("Invalid unread."));
        }
        if (current.getValue() == 0){
            return new Pos(current.getKey()-1, lines.get(current.getKey()-1).length()-1);
        }
        else {
            return new Pos(current.getKey(), current.getValue()-1);
        }
    }

    private void unreadLast() throws Exception {
        current = prevPos();
    }

    public boolean isBlank(char ch){ return TokenLib.blanks.contains(ch); }
    public boolean isDigit(char ch){ return TokenLib.numbers.contains(ch); }
    public boolean isAlpha(char ch){ return TokenLib.alphas.contains(ch); }
    public boolean isSymbol(char ch){ return TokenLib.symbols.contains(ch); }
    public boolean isValid(char ch){ return (isBlank(ch) || isDigit(ch) || isAlpha(ch) || isSymbol(ch)); }
    public boolean isHex(char ch){ return TokenLib.hex.contains(ch); }
}

