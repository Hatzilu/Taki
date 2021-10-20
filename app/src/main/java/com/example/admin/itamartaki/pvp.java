package com.example.admin.itamartaki;

import android.os.Bundle;

public class pvp extends MainActivity {
    private CustomView cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cv = new CustomView(this);
        cv.setBackground(getResources().getDrawable(R.drawable.gamebg));
        setContentView(cv);
    }
}
