package com.isl.incident;

import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;

import java.util.HashMap;

import infozech.itower.R;

public class FragSiteAccessDescription extends Fragment {
    View view;
    AppPreferences mAppPreferences;
    ListView lv;
    TextView txt_no_remarks_added;

    TextView consumedpwr,gridpwr,ebvltg,genpwr,et_consumedpwr,et_gridpwr,et_ebvltg, et_genpwr;

    public FragSiteAccessDescription() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.tt_energy_description, container, false );
        //lv = (ListView) view.findViewById(R.id.lv);
        //txt_no_remarks_added = (TextView) view.findViewById(R.id.txt_no_remarks_added);
        consumedpwr = (TextView) view.findViewById(R.id.tv_consumedpwr);
        gridpwr = (TextView) view.findViewById(R.id.tv_gridpwr);
        ebvltg = (TextView) view.findViewById(R.id.tv_ebvltg);
        genpwr = (TextView) view.findViewById(R.id.tv_genpwr);


        consumedpwr.setVisibility(View.VISIBLE);
        gridpwr.setVisibility(View.VISIBLE);
        ebvltg.setVisibility(View.VISIBLE);
        genpwr.setVisibility(View.VISIBLE);

        et_consumedpwr = (TextView) view.findViewById(R.id.et_consumedpwr);
        et_gridpwr = (TextView) view.findViewById(R.id.et_gridpwr);
        et_ebvltg = (TextView) view.findViewById(R.id.et_ebvltg);
        et_genpwr = (TextView) view.findViewById(R.id.et_genpwr);


        et_consumedpwr.setVisibility(View.VISIBLE);
        et_gridpwr.setVisibility(View.VISIBLE);
        et_ebvltg.setVisibility(View.VISIBLE);
        et_genpwr.setVisibility(View.VISIBLE);

       mAppPreferences = new AppPreferences(getActivity());

        //convert string to Hasmap
        String ttEnergyData = mAppPreferences.getTTAccessDescription();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Gson gson = new Gson();
        HashMap<String, String> hashMap = gson.fromJson(ttEnergyData, type);


        consumedpwr.setText("Actual Start Date");
        String str_actstdt = hashMap.get("actstdt");
        String str_actsttime = hashMap.get("actsttime");
        if(str_actstdt!=null && !str_actstdt.equalsIgnoreCase("null")
           && str_actsttime!=null && !str_actsttime.equalsIgnoreCase("null")){
            et_consumedpwr.setText(""+hashMap.get("actstdt")
                    +" "+hashMap.get("actsttime"));
        }

        gridpwr.setText("Actual End Date");
        String str_actenddt = hashMap.get("actenddt");
        String str_actendtime = hashMap.get("actendtime");
        if(str_actenddt!=null && !str_actenddt.equalsIgnoreCase("null")
                && str_actendtime!=null && !str_actendtime.equalsIgnoreCase("null")){
            et_gridpwr.setText(""+hashMap.get("actenddt")
                    +" "+hashMap.get("actendtime"));
        }

        ebvltg.setText("Access ID");
        String str_accessid = hashMap.get("accessid");
        if(str_accessid!=null && !str_accessid.equalsIgnoreCase("null")){
            et_ebvltg.setText(""+hashMap.get("accessid"));
        }

        genpwr.setText("Access Request Status");
        String str_accreqsts = hashMap.get("accreqsts");
        if(str_accessid!=null && !str_accessid.equalsIgnoreCase("null")){
            et_genpwr.setText(""+hashMap.get("accreqsts"));
        }

        return view;
    }
}