package symTable;
import tokenizer.TokenType;

public class Symbol {
    private boolean init = false;
    private boolean isConst;
    private boolean isFunc;
    private TokenType type;
    private int index;
    private int off;
    private int size;

    public Symbol(){
        init = false;
        this.isConst = false;
        this.type = null;
        this.index = 0;
        this.off = 0;
        this.size = 0;
        this.isFunc = false;
    }

    public Symbol(Symbol sym){
        init = true;
        this.isConst = sym.isConst();
        this.type = sym.getType();
        this.index = sym.getIndex();
        this.off = sym.getOff();
        this.size = sym.getSize();
        this.isFunc = sym.isFunc();
    }

    public Symbol(boolean isConst, boolean isFunc, TokenType type, int index, int off, int size) {
        init = true;
        this.isConst = isConst;
        this.isFunc = isFunc;
        this.type = type;
        this.index = index;
        this.off = off;
        this.size = size;
    }

    public Symbol(boolean isConst, TokenType type) {
        init = true;
        this.isConst = isConst;
        this.type = type;
        this.index = 0;
        this.off = 0;
        this.size = 0;
        this.isFunc = false;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isFunc() {
        return isFunc;
    }

    public void setFunc(boolean func) {
        isFunc = func;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOff() {
        return off;
    }

    public void setOff(int off) {
        this.off = off;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
