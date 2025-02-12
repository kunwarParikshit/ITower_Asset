package com.isl.workflow.form.control;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Parameter;
import com.isl.workflow.utils.WorkFlowUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class APIDataAutoCompleteAdapter extends ArrayAdapter<DropdownValue> implements Filterable {

    private ArrayList<DropdownValue> resultList;
    private String fieldKey;
    private Context context;

    public APIDataAutoCompleteAdapter(Context context, int textViewResourceId, String fieldKey) {
        super(context, textViewResourceId);
        this.fieldKey = fieldKey;
        this.resultList = new ArrayList<DropdownValue>();
        this.context = context;
    }

    @Override
    public int getCount() {
        if(resultList!=null) {
            return resultList.size();
        }else{
            return 0;
        }
    }

    @Override
    public DropdownValue getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = autocomplete(fieldKey,constraint.toString());

                    filterResults.values = resultList;
                    if(resultList==null){
                        filterResults.count = 0;
                    } else{
                        filterResults.count = resultList.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private ArrayList<DropdownValue> autocomplete(String fieldKey, String val){
        ArrayList<DropdownValue> ddValues = null;
        List< NameValuePair > nameValuePairs = new ArrayList<NameValuePair>( 1 );

        nameValuePairs.add( new BasicNameValuePair( "val", val));
        //nameValuePairs.add( new BasicNameValuePair( "flag", "0"));
        //System.out.println("autocomplete*************** fieldKey - "+fieldKey);
        //System.out.println("autocomplete*************** val - "+val);
        if(FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getData().getService().getParams()!=null){

            for(Parameter param : FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getData().getService().getParams()){

                switch(param.getType()){
                    case SESSION:
                        nameValuePairs.add( new BasicNameValuePair( param.getKey(), WorkFlowUtils.getSessionValue(param.getKey())));
                        break;
                    case CONSTANT:
                        nameValuePairs.add( new BasicNameValuePair( param.getKey(), param.getValue()));
                        break;
                    case FORM:
                        nameValuePairs.add( new BasicNameValuePair( param.getKey(), WorkFlowUtils.getValueFromLocalData(param.getField(),param.getKey(),false)));
                        break;
                    default:
                        break;
                }
            }
        }

        try{
            if (Utils.isNetworkAvailable(context)) {
                String result = HttpUtils.httpGetRequest(FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey).getData().getService().getUrl(),nameValuePairs);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<DropdownValue>>() {}.getType();
                ddValues = gson.fromJson(result, listType);
            }
        } catch (Exception exp){
            exp.printStackTrace();
        }
        return ddValues;
    }
}

