package util;

import javafx.util.Pair;


public class Pos extends Pair<Integer, Integer> {

    public Pos(Integer key, Integer value) {
        super(key, value);
    }

    public Pos (Pos pos){
        super(pos.getKey(), pos.getValue());
    }

}
