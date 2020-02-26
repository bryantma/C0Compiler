package analyser;

import tokenizer.Token;

import java.util.ArrayList;
import java.util.Iterator;

public class AST {
    private ASTType type;
    private Token token;
    private ArrayList<AST> children;
    public AST(ASTType type, Token token){
        if (type != ASTType.TOKEN){
            if (token != null){
                System.out.println("Cannot generate AST with this token.");
                System.exit(0);
            }
        }
        this.type = type;
        this.token = token;
        this.children = new ArrayList<AST>();
    }

    public AST(ASTType type){
        this.type = type;
        this.children = new ArrayList<AST>();
    }

    public void addChild(AST child){
        this.children.add(child);
    }

    public AST getChild(int index){
        if (index >= 0){
            return this.children.get(index);
        }
        else {
            return this.children.get(children.size()+index);
        }
    }

    public ArrayList<AST> getChildren(){
        return children;
    }

    public ASTType getType() {
        return type;
    }

    public Token getToken() {
        return token;
    }

    public void draw(boolean full){
        System.out.println(drawAST(0,false,full).toString());
    }

    public StringBuilder drawAST(int ind, boolean isLast, boolean drawFull){

        StringBuilder output = new StringBuilder("");
        if (type == ASTType.STATEMENT && children.size() == 1 && drawFull == false){
            output.append(children.get(0).drawAST(ind, isLast, false));
        }
        else if (token == null){
            for (int i = 0;i < ind;i++){
                output.append(" ");
            }
            if (isLast){
                output.append("`-");
            }
            else {
                output.append("|-");
            }
            output.append(type.toString()+"\n");
            if (children.size() == 1 && drawFull == false){
                ArrayList<AST> ast = this.children;
                while (true){
                    if (ast.get(0).children.size() != 1){
                        break;
                    }
                    ast = ast.get(0).children;
                }
                for (int i = 0;i < ind;i++){
                    output.append(" ");
                }
                //isLast = true;
                output.append(this.children.get(0).drawAST(ind+1, isLast, false));
            }
            else {
                for (int i = 0;i < this.children.size();i++){
                    if (i == children.size()-1){
                        isLast = true;
                    } else { isLast = false; }
                    for (int j = 0;j < ind;j++){
                        output.append(" ");
                    }
                    output.append(this.children.get(i).drawAST(ind+1, isLast, drawFull));
                }
            }
        }
        else {
            for (int i = 0;i < ind;i++){
                output.append(" ");
            }
            output.append("|-token, " +"type = "+token.getType().toString()+", "+"value = "+token.value.toString()+"\n");
        }

        return output;
    }
}
