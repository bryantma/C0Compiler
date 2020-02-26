package symTable;

import javafx.util.Pair;
import tokenizer.TokenType;

import java.util.ArrayList;

public class Table {
    private ArrayList<ScopedTable> tables;

    public Table(){
        this.tables = new ArrayList<ScopedTable>();
    }

    public ScopedTable currentLevel(){
        return this.tables.get(0);
    }

    public void enterLevel(Boolean... in){
        boolean top = false;
        if (in.length != 0){
            top = in[0];
        }
        int off = 0;
        int level = 0;
        if (tables.size() != 0){
            ScopedTable newScopedTable = currentLevel();
            off = top ? 0 : newScopedTable.getNextOff();
            level = (top ? 1 : 0) + newScopedTable.getLevel();
        }
        tables.add(0,new ScopedTable(off, level));
    }

    public void exitLevel(){
        tables.remove(0);
    }

    public boolean hasSymbol(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return true;
            }
        }
        return false;
    }

    public void addSymbol(String name, Symbol sym){
        this.currentLevel().addSymbol(name, sym);
    }

    public void addSymbol(String name){
        Symbol sym = new Symbol();
        this.currentLevel().addSymbol(name, sym);
    }

    public TokenType getType(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return t.getType(name);
            }
        }
        System.out.println(tables);
        System.out.println("Symbol not found for "+name+".");
        System.exit(0);
        return null;
    }

    public int getSize(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return t.getSize(name);
            }
        }
        System.out.println("Symbol not found for get size!");
        System.exit(0);
        return -1;
    }

    public int getIndex(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return t.getIndex(name);
            }
        }
        System.out.println("Symbol not found for get index!");
        System.exit(0);
        return -1;
    }

    public boolean symbolIsConst(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return t.symIsConst(name);
            }
        }
        System.out.println("Symbol not found for symIsConst!");
        System.exit(0);
        return false;
    }

    public boolean symbolIsFunc(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                return t.symIsFunc(name);
            }
        }
        System.out.println("Symbol not found for symIsFunc!");
        System.exit(0);
        return false;
    }

    public Pair<Integer, Integer> getSymOff(String name){
        for (ScopedTable t : this.tables){
            if (t.hasSymbol(name)){
                int val = currentLevel().getLevel() - t.getLevel();
                return new Pair<>(val, t.getOff(name));
            }
        }
        System.out.println("Symbol not found for pair!");
        System.exit(0);
        return null;
    }

    public void checkSymbol(String name){
        if (tables.contains(name) == false){
            System.out.println("Symbol does not exist!");
            System.exit(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int level = 0; level < this.tables.size(); level++) {
            String level_out = this.tables.get(level).toString();
            String[] lines = level_out.split("\n");
            for(String line: lines) {
                out.append(" ".repeat(level)).append(line).append("\n");
            }
        }

        return "SymbolTable{" +
                "level_tables=" + out.toString() +
                '}';
    }
}
