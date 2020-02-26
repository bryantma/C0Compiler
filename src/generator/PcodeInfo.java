package generator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PcodeInfo{
    private int oper;
    private int code;
    private ArrayList<Integer> size;

    public PcodeInfo(int code, Integer... size){
        this.code = code;
        this.size = new ArrayList<Integer>();
        this.size.addAll(Arrays.asList(size));
    }

    public int getOper() {
        return size.size()-1;
    }

    public int getCode() {
        return code;
    }

    public ArrayList<Integer> getSize() {
        return size;
    }

    public static HashMap<PcodeType, PcodeInfo> T2IMap = new HashMap<PcodeType, PcodeInfo>();
    static {
        T2IMap.put(PcodeType.SNEW, new PcodeInfo(0x0c, 1,4));
        T2IMap.put(PcodeType.DSCAN, new PcodeInfo(0xb1, 1));
        T2IMap.put(PcodeType.ISCAN, new PcodeInfo(0xb0, 1));
        T2IMap.put(PcodeType.CSCAN, new PcodeInfo(0xb2, 1));
        T2IMap.put(PcodeType.IPRINT, new PcodeInfo(0xa0, 1));
        T2IMap.put(PcodeType.DPRINT, new PcodeInfo(0xa1, 1));
        T2IMap.put(PcodeType.CPRINT, new PcodeInfo(0xa2, 1));
        T2IMap.put(PcodeType.SPRINT, new PcodeInfo(0xa3, 1));
        T2IMap.put(PcodeType.PRINTL, new PcodeInfo(0xaf, 1));
        T2IMap.put(PcodeType.CALL, new PcodeInfo(0x80, 1,2));
        T2IMap.put(PcodeType.RET, new PcodeInfo(0x88, 1));
        T2IMap.put(PcodeType.IRET, new PcodeInfo(0x89, 1));
        T2IMap.put(PcodeType.DRET, new PcodeInfo(0x8a, 1));
        T2IMap.put(PcodeType.JE, new PcodeInfo(0x71, 1,2));
        T2IMap.put(PcodeType.JNE, new PcodeInfo(0x7, 1,2));
        T2IMap.put(PcodeType.JL, new PcodeInfo(0x73, 1,2));
        T2IMap.put(PcodeType.JLE, new PcodeInfo(0x76, 1,2));
        T2IMap.put(PcodeType.JG, new PcodeInfo(0x75, 1,2));
        T2IMap.put(PcodeType.JGE, new PcodeInfo(0x74, 1,2));
        T2IMap.put(PcodeType.JMP, new PcodeInfo(0x70, 1,2));
        T2IMap.put(PcodeType.LOADA, new PcodeInfo(0x0a,  1,2,4));
        T2IMap.put(PcodeType.ILOAD, new PcodeInfo(0x10, 1));
        T2IMap.put(PcodeType.DLOAD, new PcodeInfo(0x11, 1));
        T2IMap.put(PcodeType.LOADC, new PcodeInfo(0x09, 1,2));
        T2IMap.put(PcodeType.ISTORE, new PcodeInfo(0x20, 1));
        T2IMap.put(PcodeType.DSTORE, new PcodeInfo(0x21, 1));
        T2IMap.put(PcodeType.I2D, new PcodeInfo(0x60, 1));
        T2IMap.put(PcodeType.D2I, new PcodeInfo(0x61, 1));
        T2IMap.put(PcodeType.I2C, new PcodeInfo(0x62, 1));
        T2IMap.put(PcodeType.DADD, new PcodeInfo(0x31, 1));
        T2IMap.put(PcodeType.IADD, new PcodeInfo(0x30, 1));
        T2IMap.put(PcodeType.DSUB, new PcodeInfo(0x35, 1));
        T2IMap.put(PcodeType.ISUB, new PcodeInfo(0x34, 1));
        T2IMap.put(PcodeType.DMUL, new PcodeInfo(0x39, 1));
        T2IMap.put(PcodeType.IMUL, new PcodeInfo(0x38, 1));
        T2IMap.put(PcodeType.DDIV, new PcodeInfo(0x3d, 1));
        T2IMap.put(PcodeType.IDIV, new PcodeInfo(0x3c, 1));
        T2IMap.put(PcodeType.DNEG, new PcodeInfo(0x41, 1));
        T2IMap.put(PcodeType.INEG, new PcodeInfo(0x40, 1));
        T2IMap.put(PcodeType.BIPUSH, new PcodeInfo(0x01, 1,1));
        T2IMap.put(PcodeType.IPUSH, new PcodeInfo(0x02, 1,4));
        T2IMap.put(PcodeType.ICMP, new PcodeInfo(0x44, 1));
        T2IMap.put(PcodeType.DCMP, new PcodeInfo(0x45, 1));
    }
}