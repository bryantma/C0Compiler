package binaries;

import generator.Pcode;
import tokenizer.TokenType;

import java.util.ArrayList;

public class Function {
    private String name;
    private ArrayList<TokenType> params;
    private int index;
    private int paramSize;
    //todo: mod impl of instructions
    private ArrayList<Pcode> instructions;
    private TokenType returnType;

    public Function(String name, ArrayList<TokenType> params, int index, ArrayList<Pcode> instructions, TokenType returnType) {
        this.name = name;
        this.params = params;
        this.index = index;
        this.instructions = instructions;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TokenType> getParams() {
        return params;
    }

    public void setParams(ArrayList<TokenType> params) {
        this.params = params;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getParamSize() {
        return this.params.size();
    }

    public void setParamSize(int paramSize) {
        this.paramSize = paramSize;
    }

    public ArrayList<Pcode> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Pcode> instructions) {
        this.instructions = instructions;
    }

    public TokenType getReturnType() {
        return returnType;
    }

    public void setReturnType(TokenType returnType) {
        this.returnType = returnType;
    }
}
