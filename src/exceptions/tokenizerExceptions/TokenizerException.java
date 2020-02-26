package exceptions.tokenizerExceptions;

public class TokenizerException extends Exception{
    public int row;
    public int col;
    public StringBuilder msg;
    public TokenizerException(int row, int col, StringBuilder msg){
        this.row = row;
        this.col = col;
        this.msg = msg;
        System.out.println("Error: line "+row+" ,col "+col+" :\n"+"\t"+msg);
    }
}