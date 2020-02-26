package tokenizer;

public enum TokenType {
    //RESERVE WORDS
    CONST,
    VOID,
    INT,
    CHAR,
    DOUBLE,
    STRUCT,
    IF,
    ELSE,
    SWITCH,
    CASE,
    DEFAULT,
    WHILE,
    FOR,
    DO,
    RETURN,
    BREAK,
    CONTINUE,
    PRINT,
    SCAN,

    //TYPES
    IDENTIFIER,
    INTEGER,

    //operators
    PLUS,
    MINUS,
    MULTI,
    DIVI,
    ASSIGN,
    COLON,
    LPAR,
    RPAR,
    LBRA,
    RBRA,
    COMMA,
    SEMICOLON,
    LESS,
    LEQ,
    GRE,
    GEQ,
    EQUAL,
    NEQ,
    WARN

}
