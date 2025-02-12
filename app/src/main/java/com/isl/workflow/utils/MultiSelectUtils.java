package com.isl.workflow.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isl.constant.AppConstants;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class MultiSelectUtils {
     public static void multiSelectDialog(final Context context,EditText editText, final Fields fields, final TextView valMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setTitle(AppConstants.DD_SELECT_VALUE);
         builder.setCancelable(false);
         List<String> selectedList = new ArrayList<>();
         for(DropdownValue temp : FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal()){
             selectedList.add(temp.getId());
         }
        List<DropdownValue> fieldValues = WorkFlowUtils.setFieldValues(context, fields, FormCacheManager.getFormControls().get(fields.getId()),true);
        if(fieldValues.size()>0) {
            fieldValues.remove( 0 );
        }
        String[] itemsArray = new String[fieldValues.size()];
        String[] itemsArrayId = new String[fieldValues.size()];
        boolean[] checkedItems = new boolean[fieldValues.size()];
        int index = 0;
         for(DropdownValue tempvalue : fieldValues){
            itemsArray[index]  = tempvalue.getValue();
            itemsArrayId[index]  = tempvalue.getId();
            if(selectedList.contains(tempvalue.getId())) {
                checkedItems[index] = true;
            }else{
                checkedItems[index] = false;
            }
            index++;
        }
        builder.setMultiChoiceItems( itemsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                FormCacheManager.getFormControls().get(fields.getId()).getMultiSelectCtrl().setTag(fields.getKey());

                int max = fields.getValidations().getMax();

                if(isChecked && max<=FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal().size()){
                    Utils.toastMsg(context,"Maximum " +max+ AppConstants.DD_SELECTED_VALUE);
                    dialog.dismiss();
                    return;
                }




                if(isChecked){
                    /*WorkFlowDatabaseHelper db=new WorkFlowDatabaseHelper(context );
                    db.open();
                    if(db.isSitelock(fieldValues.get(position).getId(),"-1207")){
                        Utils.toastMsg(context,"Site lock "+fieldValues.get(position).getValue()+ " not associated site.");
                        dialog.dismiss();
                        return;
                    }*/
                   FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal().add(fieldValues.get(position));
                }else{
                     List<DropdownValue> selectedFieldValues = FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal();
                     for(int a = 0; a<selectedFieldValues.size(); a++){
                        if(selectedFieldValues.get(a).getId().equalsIgnoreCase(itemsArrayId[position])) {
                            FormCacheManager.getFormControls().get( fields.getId() ).getSelectedVal().remove( a );
                        }
                     }
               }

                String selectCaption = ""+FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal().size()+ AppConstants.DD_SELECTED_VALUE;

                if(FormCacheManager.getFormControls().get(fields.getId()).getSelectedVal().size()==0){
                    selectCaption = AppConstants.DD_SELECT_VALUE;
                }
                FormCacheManager.getFormControls().get(fields.getId()).getMultiSelectCtrl().setText(selectCaption);
                // Toast.makeText( context, "Position: " + which + " Value: " + itemsArray[which] + " State: " + (isChecked ? "checked" : "unchecked"), Toast.LENGTH_LONG ).show();
            }
        } );

         builder.setPositiveButton( "Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        } );
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
