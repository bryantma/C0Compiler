package tokenizer;

public enum DFAState {
    INIT,
    ZERO,
    HEX_BG,
    HEX,
    IDENTIFIER,
    RESERVED,
    SYMBOL,
    OPER,
    COLON,
    SEMICOLON,
    LPAR,
    RPAR,
    LBRA,
    RBRA,
    COMMA,

    NEQ,
    LESS,
    LEQ,
    GRE,
    GEQ,
    ASSIGN,
    EQUAL,
    PLUS,
    MINUS,
    DIVI,

    //comment

    SINGLE_COM,
    MULTI_COM,
    //COMMENT,
    MULTI,
    WARN,
    DEC,
    END
}
