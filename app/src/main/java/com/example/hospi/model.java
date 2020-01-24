package com.example.hospi;

class model {
    private colleges clg;
    private String Key;

    public model(colleges clg, String key) {
        this.clg = clg;
        this.Key = key;
    }

    public colleges getClg() {
        return clg;
    }

    public String getKey() {
        return Key;
    }
}
