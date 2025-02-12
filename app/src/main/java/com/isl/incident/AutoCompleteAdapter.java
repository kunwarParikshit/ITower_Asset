package com.isl.incident;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.modal.DropdownValue;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<DropdownValue> implements Filterable {
    private ArrayList<DropdownValue> resultList;
    private Context context;
    private String url;
    int flag;
    public AutoCompleteAdapter(Context context,String url,int flag, int textViewResourceId) {
        super(context, textViewResourceId);
        this.resultList = new ArrayList<DropdownValue>();
        this.url = url;
        this.context = context;
        this.flag = flag;
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
                    resultList = autocomplete(constraint.toString());

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

    private ArrayList<DropdownValue> autocomplete(String val){
        ArrayList<DropdownValue> ddValues = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );

        nameValuePairs.add( new BasicNameValuePair( "val", val));
        nameValuePairs.add( new BasicNameValuePair( "flag", ""+flag));
        nameValuePairs.add( new BasicNameValuePair( "circle", "0"));
        nameValuePairs.add( new BasicNameValuePair( "zone", "0"));
        nameValuePairs.add( new BasicNameValuePair( "cluster", "0"));


        try{
            if (Utils.isNetworkAvailable(context)) {
                String response = HttpUtils.httpGetRequest(url, nameValuePairs);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<DropdownValue>>() {}.getType();
                ddValues = gson.fromJson(response, listType);
            }
        } catch (Exception exp){
            exp.printStackTrace();
        }
        return ddValues;
    }
}
