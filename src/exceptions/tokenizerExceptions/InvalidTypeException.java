package exceptions.tokenizerExceptions;

public class InvalidTypeException extends TokenizerException{
    public InvalidTypeException(int row, int col) {
        super(row, col, new StringBuilder("Invalid type for current operation."));
    }
}
