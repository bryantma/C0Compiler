package binaries;

public class Const {
    private String constType;
    private String constVal;
    private int binType;

    public static String getString(){
        return "S";
    }

    public static String getInt(){
        return "I";
    }

    public Const(String constType, String constVal) {
        this.constType = constType;
        this.constVal = constVal;
        if (this.constType.equals(Const.getString())){
            this.binType = 0;
        }
        else {
            this.binType = 1;
        }
    }

    public String getConstType(){
        return constType;
    }

    public String getConstVal(){
        return constVal;
    }

    public int getBinType(){
        return binType;
    }

    public int getInteger(){
        return Integer.parseInt(constVal);
    }
}
