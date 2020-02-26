package exceptions.tokenizerExceptions;

public class InvalidCharacterException extends TokenizerException{
    public InvalidCharacterException(int row, int col, char ch){
        super(row, col, new StringBuilder("Invalid character "+ch+" ."));
    }
}
