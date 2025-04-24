package com.sharedsystemshome.dsa.datatype;

import java.util.HashMap;
import java.util.Map;

public abstract class Datatype {

    private Map valueMap;

    public Datatype(Map valueMap) {

        super();

        if(null == valueMap){
            this.valueMap = new HashMap();
        } else {
            this.valueMap = valueMap;
        }
    }

    public Datatype() {
        this.valueMap = new HashMap();
    }

    protected Map getValueMap(){
        return this.valueMap;
    }
}
