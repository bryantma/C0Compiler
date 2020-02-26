package exceptions.tokenizerExceptions;
//import exceptions.tokenizerExceptions.TokenizerException;

public class IntegerOverflowException extends TokenizerException{
    public IntegerOverflowException(int row, int col, int num){
        super(row, col, new StringBuilder("Integer "+num+" out of range."));
    }
}
