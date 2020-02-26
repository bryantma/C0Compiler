package symTable;

import tokenizer.Token;
import tokenizer.TokenType;

import java.util.HashMap;

public class ScopedTable {
    private HashMap<String, Symbol> symbolMap;
    private int off;
    private int level;

    public ScopedTable(int off, int level){
        this.symbolMap = new HashMap<String, Symbol>();
        this.off = off;
        this.level = level;
    }

    public void addSymbol(String name, Symbol sym){
        if (sym.isFunc() == false){
            if (sym.getType() == null){
                System.out.println("Expected symbol type but there is none.");
                System.exit(0);
            }
            else if (sym.getType() == TokenType.INTEGER){

            }
            sym.setSize(1);
            //todo: add sizes here if impl char & double
            sym.setOff(this.off);
            this.off += sym.getSize();
        }
        symbolMap.put(name, sym);
    }

    public void updateOff(String name, int val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setOff(val);
        this.symbolMap.put(name, sym);
    }

    public void updateSize(String name, int val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setSize(val);
        this.symbolMap.put(name, sym);
    }

    public void updateIndex(String name, int val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setIndex(val);
        this.symbolMap.put(name, sym);
    }

    public void updateType(String name, TokenType val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setType(val);
        this.symbolMap.put(name, sym);
    }

    public void updateFunc(String name, boolean val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setFunc(val);
        this.symbolMap.put(name, sym);
    }

    public void updateConst(String name, boolean val){
        Symbol sym = this.symbolMap.remove(name);
        sym.setConst(val);
        this.symbolMap.put(name, sym);
    }

    public void setOff(int off){
        this.off = off;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public boolean symIsConst(String name){
        return this.symbolMap.get(name).isConst();
    }

    public boolean symIsFunc(String name){
        return this.symbolMap.get(name).isFunc();
    }

    public TokenType getType(String name){
        return this.symbolMap.get(name).getType();
    }

    public int getOff(String name){
        if (this.symbolMap.get(name).isFunc()){
            System.out.println("Function symbol cannot have offset attribute.");
            System.exit(0);
        }
        return this.symbolMap.get(name).getOff();
    }

    public int getSize(String name){
        return this.symbolMap.get(name).getSize();
    }

    public int getIndex(String name){
        return this.symbolMap.get(name).getIndex();
    }

    public int getLevel(){
        return level;
    }

    public int getNextOff(){
        return off;
    }

    public Symbol getSymbol(String name){
        return this.symbolMap.get(name);
    }

    public boolean initialized(String name){
        return this.symbolMap.get(name).isInit();
    }

    public boolean hasSymbol(String name){
        return this.symbolMap.containsKey(name);
    }

    /*@Override
    public String toString(){
        StringBuilder out= new StringBuilder();
        for (String name:symbolMap.keySet()){
            out.append("\t").append(name).append(": ").append(symbolMap.get(name).toString()).append("\n");
        }
        return "ScopedSymbolTable{" +
                "symbols=" + out +
                ", nextOffset=" + off +
                ", stackLevel=" + level +
                '}';
    }*/
}
