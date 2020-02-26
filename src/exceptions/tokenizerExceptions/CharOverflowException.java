package exceptions.tokenizerExceptions;

public class CharOverflowException extends TokenizerException {
    public CharOverflowException(int row, int col) {
        super(row, col, new StringBuilder("Character out of range."));
    }
}
