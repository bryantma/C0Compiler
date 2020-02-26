package analyser;

public enum ASTType {
    ROOT,
    VAR_DECL,
    CONST_QLF,
    TYPE_SPEC,
    SINGLE_TYPE_SPEC,
    INIT_LIST,
    INIT_DECL,
    INIT_STATE,
    IDENTIFIER,
    INITIALIZER,
    FUNC_DEF,
    PARAM_CLS,
    COMP_STATE,
    PARAM_LIST,
    PARAM_DECL,
    FUNC_CALL,
    EXPR_LIST,
    EXPR,
    ADD_EXPR,
    ADD_OPER,
    MUL_OPER,
    MUL_EXPR,
    UNARY_EXPR,
    PRI_EXPR,
    STATE_SEQ,
    STATEMENT,
    COND_STATE,
    LABEL_STATE,
    RELATION_OPER,
    UPDATE,
    LOOP,
    JUMP,
    PRINT,
    SCAN,
    ASSIGN,
    ASSIGN_OPER,
    RETURN,
    CONDITION,
    PRINT_LIST,
    PRINTABLE,
    INTEGER,
    TOKEN,
    UNARY_OPER;
}
