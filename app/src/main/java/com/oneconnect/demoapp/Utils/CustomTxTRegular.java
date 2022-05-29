package com.oneconnect.demoapp.Utils;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;



public class CustomTxTRegular extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTxTRegular(Context context, AttributeSet attributeSet, int defstyle)
    {
        super(context,attributeSet,defstyle);
        init();
    }

    public CustomTxTRegular(Context context, AttributeSet attributeSet)
    {
        super(context,attributeSet);
        init();
    }

    public CustomTxTRegular(Context context)
    {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()){
            Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
            setTypeface(normalTypeface);
        }
    }
}
