package com.isl.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhakan on 6/25/2019.
 */

public class DigitsAfterDecimal implements InputFilter {

    Pattern mPattern;

    public DigitsAfterDecimal(int digitsAfterZero) {
        mPattern=Pattern.compile("[0-9]+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
     }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}

