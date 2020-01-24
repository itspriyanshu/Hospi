package com.example.hospi;

import android.app.Activity;

class refreshclass {

    private Activity a;

    public refreshclass(Activity a){
        this.a = a;
    }

    protected void refresh(){
        a.recreate();
    }
}
