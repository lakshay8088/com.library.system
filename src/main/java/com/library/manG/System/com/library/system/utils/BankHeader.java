package com.library.manG.System.com.library.system.utils;

import org.json.JSONObject;

public class BankHeader {

    public static JSONObject getHeader(){
        JSONObject bankHeader = new JSONObject();
        bankHeader.put("content-type","application/json");
        return bankHeader;
    }


}
