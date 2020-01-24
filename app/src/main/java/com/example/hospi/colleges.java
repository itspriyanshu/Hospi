package com.example.hospi;

import java.io.Serializable;

class colleges implements Serializable {
    private String clgName, leaderName, number;
    private long size;

    public colleges(){}

    public colleges(String clgName, String leaderName, String number,long size) {
        this.clgName = clgName;
        this.leaderName = leaderName;
        this.number = number;
        this.size = size;
    }



    public long getSize() {
        return size;
    }

    public String getClgName() {
        return clgName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public String getnumber() {
        return number;
    }
}
