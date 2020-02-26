package binaries;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import generator.Pcode;
import generator.PcodeInfo;
import tokenizer.TokenType;

public class ELF {
    ArrayList<Pcode> instructions;
    ArrayList<Const> consts;
    ArrayList<Function> functions;

    public ELF(){
        this.instructions = new ArrayList<Pcode>();
        this.consts = new ArrayList<Const>();
        this.functions = new ArrayList<Function>();
    }

    public Integer addConst(String type, String val){
        if (type.equals(Const.getInt()) == false && type.equals(Const.getString()) == false){
            System.out.println("Unexpected constant type!");
            System.exit(0);
        }
        for (int i = 0;i < consts.size();i++){
            if (consts.get(i).getConstType().equals(type) && consts.get(i).getConstVal().equals(val)){
                return i;
            }
        }
        consts.add(new Const(type, val));
        return consts.size()-1;
    }


    public Optional<Function> currentFunction(){
        if (functions.isEmpty() == false){
            return Optional.of(this.functions.get(functions.size()-1));
        }
        else {
            return Optional.empty();
        }
    }

    public void addFunction(String name, ArrayList<TokenType> params, int index, TokenType returnType){
        checkFunctionExists(name);
        functions.add(new Function(name, params, index, new ArrayList<>(), returnType));
    }

    public void checkFunctionExists(String name){
        if (this.functions.contains(name)){
            System.out.println("Function already exists!");
            System.exit(0);
        }
    }

    public Integer nextIndex(){
        if (functions.isEmpty() == false) {
            return this.functions.get(functions.size()-1).getInstructions().size();
        }
        return this.instructions.size();
    }

    public ArrayList<TokenType> getFunctionParams(String name){
        for (Function f : functions){
            if (f.getName().equals(name)){
                return f.getParams();
            }
        }
        System.out.println("No matching function for getting parameters!");
        System.exit(0);
        return null;
    }

    public int getFunctionIndex(String name){
        for (int i = 0;i < functions.size();i++){
            if (functions.get(i).getName().equals(name)){
                return i;
            }
        }
        System.out.println("No matching function for getting index!");
        System.exit(0);
        return -1;
    }

    public int getFunctionParamsNum(String name){
        for (Function f : functions){
            if (f.getName().equals(name)){
                return f.getParamSize();
            }
        }
        System.out.println("No matching function for getting parameters numbers!");
        System.exit(0);
        return -1;
    }

    public TokenType getFunctionReturnType(String name){
        for (Function f : functions){
            if (f.getName().equals(name)){
                return f.getReturnType();
            }
        }
        System.out.println("No matching function for getting return type!");
        System.exit(0);
        return null;
    }

    public ArrayList<Pcode> currentInstructions(){
        if (functions.size() == 0){
            return this.instructions;
        }
        else {
            return functions.get(functions.size()-1).getInstructions();
        }
    }

    public void updateInstructions(int index, Integer... ins){
        for (Integer i : ins){
            currentInstructions().get(index).update(i);
        }
    }

    public String generateAssembly(){
        StringBuilder asm = new StringBuilder();

        asm.append(".constants:\n");
        for (int i = 0;i < consts.size();i++){
            String val = "\"" + consts.get(i).getConstVal() + "\"";
            asm.append(String.format("  %5d %s %s\n", i, consts.get(i).getConstType(), val));
        }

        asm.append(".start:\n");
        for (int i = 0;i < instructions.size();i++){
            asm.append(String.format("  %5d %s\n", i, instructions.get(i).toString()));
        }

        asm.append(".functions:\n");
        for (int i = 0;i < functions.size();i++){
            asm.append(String.format("  %-3d %-3d %-3d %-3d\n", i, functions.get(i).getIndex(), functions.get(i).getParamSize(), 1));
        }
        for (Function f : functions){
            asm.append(f.getName()+":\n");
            ArrayList<Pcode> fi = f.getInstructions();
            for (int i = 0;i < fi.size();i++){
                asm.append(String.format("  %-3d %s\n", i, fi.get(i).toString()));
            }
        }

        return asm.toString();
    }

    public byte[] generateBinaries(){
        ArrayList<Byte> o = new ArrayList<>();
        ArrayList<Byte> magic = i2b(4, 0x43303a29);
        addBytes(o, magic);

        ArrayList<Byte> ver = i2b(4, 0x01);
        addBytes(o, ver);

        ArrayList<Byte> constNum = i2b(2, consts.size());
        addBytes(o, constNum);

        for (Const c: consts){
            ArrayList<Byte> type = i2b(1, c.getBinType());
            ArrayList<Byte> constInfo = new ArrayList<>();
            if (c.getConstType().equals(Const.getString())) {
                addBytes(constInfo, i2b(2, c.getConstVal().length()));
                ArrayList<Byte> val = new ArrayList<>();
                byte[] in = c.getConstVal().getBytes(StandardCharsets.US_ASCII);
                for (byte b: in){
                    val.add(b);
                }
                constInfo.addAll(val);
            }
            else if (c.getConstType().equals(Const.getInt())){
                addBytes(constInfo, i2b(4, compare(c.getInteger(),32)));
            }
            else {
                System.out.println("Fatal: generate binary error.");
                System.exit(0);
            }
            addBytes(o, type);
            addBytes(o, constInfo);
        }

        ArrayList<Byte> insNum = i2b(2, this.instructions.size());
        addBytes(o, insNum);
        Pcode2Output(o, this.instructions);

        ArrayList<Byte> funcNum = i2b(2, this.functions.size());
        addBytes(o, funcNum);

        for (Function f: functions){
            ArrayList<Byte> name = i2b(2, f.getIndex());
            addBytes(o, name);
            ArrayList<Byte> param = i2b(2, f.getParamSize());
            addBytes(o, param);
            ArrayList<Byte> level = i2b(2, 1);
            addBytes(o, level);

            insNum = i2b(2, f.getInstructions().size());
            addBytes(o, insNum);

            ArrayList<Pcode> func = f.getInstructions();
            Pcode2Output(o, func);
        }

        Byte[] bt = new Byte[o.size()];
        bt = o.toArray(bt);
        byte[] out = new byte[bt.length];
        for (int i = 0;i < bt.length;i++){
            out[i] = bt[i];
        }
        return out;
    }

    private int compare(int val1, int val2){
        return val1 >= 0 ?  val1 : val1+(1<<val2);
    }

    private void Pcode2Output(ArrayList<Byte> out, ArrayList<Pcode> instructions){
        for (Pcode pc: instructions){
            //todo: not robust
            PcodeInfo info = PcodeInfo.T2IMap.get(pc.getPcodeType());
            //System.out.println(pc.getOper().size());
            ArrayList<Byte> oper = i2b(1, info.getCode());
            addBytes(out, oper);
            for (int i = 0;i < pc.getOper().size();i++){
                ArrayList<Byte> operand = i2b(info.getSize().get(i+1), pc.getOper().get(i));
                addBytes(out, operand);
            }
        }
    }

    private ArrayList<Byte> i2b(int len, int code){
        ArrayList<Byte> res = new ArrayList<>(len);
        for (int i = 0;i < len;i++){
            res.add( (byte)(code >> ((len-1-i) << 3) & 0xff) );
        }
        return res;
    }

    private void addBytes(ArrayList<Byte> array, ArrayList<Byte> bytes){
        array.addAll(bytes);
    }
}


