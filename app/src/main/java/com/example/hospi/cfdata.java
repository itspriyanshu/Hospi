package com.example.hospi;

import java.io.Serializable;

class cfdata implements Serializable {

    private String name;
    private long filled,capacity;

    public cfdata(String name, long filled, long capacity) {
        this.name = name;
        this.filled = filled;
        this.capacity = capacity;
    }

    public void setFilled(long filled) {
        this.filled = filled;
    }

    protected String getName() {
        return name;
    }

    protected long getFilled() {
        return filled;
    }

    protected long getCapacity() {
        return capacity;
    }
}
