package com.android.baking.ui;

import com.android.baking.model.Step;

import java.util.ArrayList;

public interface FragmentListener {

    void setStep(int index , ArrayList<Step> steps);
    void setCurrent(int index);

}