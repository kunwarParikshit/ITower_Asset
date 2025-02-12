package com.isl.audit.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import java.util.List;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
       // OnMultiChoiceClickListener {
        OnMultiChoiceClickListener, OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private int itemPos;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked)
            selected[which] = true;
        else
            selected[which] = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuffer spinnerBuffer = new StringBuffer();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i] == true) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }
        String spinnerText;
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });
        setAdapter(adapter);
        listener.onItemsSelected(selected,itemPos);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                (dialog, which) -> dialog.cancel());
        builder.setOnCancelListener(this::onCancel);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, String allText,
                         MultiSpinnerListener listener,int pos,List<String> selectedItems) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        this.itemPos=pos;

        // all selected by default
        selected = new boolean[items.size()];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { allText });
        if(selectedItems==null || selectedItems.size()==0){
            for (int i = 0; i < selected.length; i++){
                selected[i] = false;
            }
            // all text on the spinner
            adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, new String[] { allText });
        }else{
            allText="";
            for (int i = 0; i < selected.length; i++){
               for (int j=0;j<selectedItems.size();j++){
                   if(items.get(i).equals(selectedItems.get(j))){
                       selected[i]=true;
                       if(TextUtils.isEmpty(allText)){
                           allText=selectedItems.get(j);
                       }else{
                           allText=allText+","+selectedItems.get(j);
                       }
                       break;
                   }else{
                       selected[i]=false;
                   }
               }
                adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, new String[] { allText });
            }
        }




        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected,int pos);
    }
}