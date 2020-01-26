package com.example.hospi;


import java.io.Serializable;

class Room implements Serializable {

    public long Capacity,Filled;

    public Room(){}

    public Room(long Capacity) {
        this.Capacity = Capacity;
        this.Filled = 0L;
    }

}