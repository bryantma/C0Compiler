package generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pcode{
    private PcodeType pcodeType;
    private ArrayList<Integer> oper;
    private ArrayList<Integer> size;

    public Pcode (PcodeType type, Integer... oper){
        this.pcodeType = type;
        this.oper = new ArrayList<Integer>();
        this.oper.addAll(Arrays.asList(oper));
        this.size = PcodeInfo.T2IMap.get(type).getSize();
        validate();
    }

    public PcodeType getPcodeType() {
        return pcodeType;
    }

    public ArrayList<Integer> getOper() {
        return oper;
    }

    public ArrayList<Integer> getSize() {
        return size;
    }

    public void update(Integer... oper){
        this.oper = new ArrayList<Integer>();
        this.oper.addAll(Arrays.asList(oper));
        validate();
    }

    private void validate(){
        PcodeInfo inf = PcodeInfo.T2IMap.get(this.pcodeType);
        int oper = inf.getOper();
        ArrayList<Integer> size = inf.getSize();
        if (oper != size.size()-1){
            System.out.println("Operand and size does not match!");
            System.exit(0);
        }
        if (oper != this.oper.size()){
            System.out.println("Operands do not match!");
            System.exit(0);
        }
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(pcodeType);
        for (int i = 0;i < oper.size();i++){
            if (i != 0){ str.append(", ");}
            str.append(String.format(" %s", oper.get(i)));
        }
        return str.toString();
    }
}

