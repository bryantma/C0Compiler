package tokenizer;
import com.sun.jdi.InvalidTypeException;
import exceptions.tokenizerExceptions.CharOverflowException;
import exceptions.tokenizerExceptions.IntegerOverflowException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import util.Pos;

public class Token {
    public Object value;
    TokenType type;
    Pos start;
    Pos end;
    public Token(Object input, TokenType type, Pos start, Pos end){
        this.value = input;
        this.type = type;
        this.start = start;
        this.end = end;
    }
    public Token(Object value, TokenType type){
        this.value = value;
        this.type = type;
    }

    public Object getValue(){
        return value;
    }

    @Override
    public String toString(){
         return value.toString();
    }
    /*
    public void check_char(char ch) throws CharOverflowException{
        if ((int)ch < 0 || (int) ch > 255){
            throw new CharOverflowException(start.getLeft(),start.getRight());
        }
    }
    public void check_int(Integer num) throws IntegerOverflowException{
        Integer min = -2147483648;
        Integer max = 2147483647;
        if (num < min || num > max){
            throw new IntegerOverflowException(start.getLeft(),start.getRight(),num);
        }
    }*/

    public TokenType getType() {
        return type;
    }



    /*
    public int value(){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        if (type == TokenType.INTEGER){
            try {
                int ret = (int) engine.eval(value.toString());
                //check_int(ret);
                return ret;
            } catch (ScriptException /*| IntegerOverflowException e){
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Invalid type for value function call!");
            System.exit(0);
        }
        return -2147483648;
    }*/
}
