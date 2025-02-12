package com.isl.workflow.modal;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FormFieldControl {

    private String key;
    private TextView captionCtrl;
    private Spinner selectCtrl;
    private EditText textBoxCtrl;
    private EditText multiSelectCtrl;
    private AutoCompleteTextView autoCompleteCtrl;
    private Button buttonCtrl;
    private Switch switchControl;
    private CheckBox checkBoxControl;
    private RecyclerView imgGridView;
    private List<DropdownValue> selectedVal;
    //private DropdownValue selectedVal;
    private String value;
    private int imgCounter;

    public EditText getMultiSelectCtrl() {
        return multiSelectCtrl;
    }

    public void setMultiSelectCtrl(EditText multiSelectCtrl) {
        this.multiSelectCtrl = multiSelectCtrl;
    }

    public Switch getSwitchControl() {
        return switchControl;
    }

    public void setSwitchControl(Switch switchControl) {
        this.switchControl = switchControl;
    }

    public CheckBox getCheckBoxControl() {
        return checkBoxControl;
    }

    public void setCheckBoxControl(CheckBox checkBoxControl) {
        this.checkBoxControl = checkBoxControl;
    }

    public Switch getTgButtonCtrl() {
        return switchControl;
    }

    public void setTgButtonCtrl(Switch switchControl) {
        this.switchControl = switchControl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getImgCounter() {
        return imgCounter;
    }

    public void setImgCounter(int imgCounter) {
        this.imgCounter = imgCounter;
    }

    public void increaseImgCounter() {
        this.imgCounter=imgCounter+1;
    }

    public Button getButtonCtrl() {
        return buttonCtrl;
    }

    public void setButtonCtrl(Button buttonCtrl) {
        this.buttonCtrl = buttonCtrl;
    }

    public RecyclerView getImgGridView() {
        return imgGridView;
    }

    public void setImgGridView(RecyclerView imgGridView) {
        this.imgGridView = imgGridView;
    }

   /* public DropdownValue getSelectedVal() {
         return selectedVal;
    }

    public void setSelectedVal(DropdownValue selectedVal) {
        this.selectedVal = selectedVal;
    }
*/

    public List<DropdownValue> getSelectedVal() {
        if(selectedVal == null){
            selectedVal = new ArrayList<DropdownValue>();
        }
        return selectedVal;
    }

    public void setSelectedVal(List<DropdownValue> selectedVal) {
        this.selectedVal = selectedVal;
    }

    public AutoCompleteTextView getAutoCompleteCtrl() {
        return autoCompleteCtrl;
    }

    public void setAutoCompleteCtrl(AutoCompleteTextView autoCompleteCtrl) {
        this.autoCompleteCtrl = autoCompleteCtrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TextView getCaptionCtrl() {
        return captionCtrl;
    }

    public void setCaptionCtrl(TextView captionCtrl) {
        this.captionCtrl = captionCtrl;
    }

    public Spinner getSelectCtrl() {
        return selectCtrl;
    }

    public void setSelectCtrl(Spinner selectCtrl) {
        this.selectCtrl = selectCtrl;
    }

    public EditText getTextBoxCtrl() {
        return textBoxCtrl;
    }

    public void setTextBoxCtrl(EditText textBoxCtrl) {
        this.textBoxCtrl = textBoxCtrl;
    }
}
