package com.isl.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class EditTextLength {
	private EditText editText;
	private int beforeDecimal;
	private int afterDecimal;
	public EditTextLength(final EditText editText, final int beforeDecimal,final int afterDecimal) {
		this.editText = editText;
		this.beforeDecimal = beforeDecimal;
		this.afterDecimal = afterDecimal;
		editText.setFilters(new InputFilter[] { new DigitsKeyListener(
				Boolean.FALSE, Boolean.TRUE) {
			// int beforeDecimal = 6, afterDecimal = 2;
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				String temp = editText.getText() + source.toString();
				if (temp.equals(".")) {
					return "0.";
				} else if (temp.toString().indexOf(".") == -1) {
					// no decimal point placed yet
					if (temp.length() > beforeDecimal) {
						return "";
					}
				} else {
					temp = temp.substring(temp.indexOf(".") + 1);
					if (temp.length() > afterDecimal) {
						return "";
					}
				}
				return super.filter(source, start, end, dest, dstart, dend);
			}
		} });
	}
}
