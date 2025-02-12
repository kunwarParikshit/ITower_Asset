package com.isl.incident;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;

import java.util.HashMap;
import infozech.itower.R;

public class FragEnergyDescription extends Fragment {
    View view;
    AppPreferences mAppPreferences;
    ListView lv;
    TextView txt_no_remarks_added;

    TextView consumedpwr,gridpwr,ebvltg,genpwr,dgruntm,fuellvl,batsoc,batvltg,batterychamp,batterydischamp,
    et_consumedpwr,et_gridpwr,et_ebvltg,et_genpwr,et_dgruntm,et_fuellvl,et_batsoc,et_batvltg,et_batterychamp,
    et_batterydischamp,tv_source,et_source;

    public FragEnergyDescription() {

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
        dgruntm = (TextView) view.findViewById(R.id.tv_dgruntm);
        fuellvl = (TextView) view.findViewById(R.id.tv_fuellvl);
        batsoc = (TextView) view.findViewById(R.id.tv_batsoc);
        batvltg = (TextView) view.findViewById(R.id.tv_batvltg);
        batterychamp = (TextView) view.findViewById(R.id.tv_batterychamp);
        batterydischamp = (TextView) view.findViewById(R.id.tv_batterydischamp);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_source.setVisibility(View.VISIBLE);
        consumedpwr.setVisibility(View.VISIBLE);
        gridpwr.setVisibility(View.VISIBLE);
        ebvltg.setVisibility(View.VISIBLE);
        genpwr.setVisibility(View.VISIBLE);
        dgruntm.setVisibility(View.VISIBLE);
        fuellvl.setVisibility(View.VISIBLE);
        batsoc.setVisibility(View.VISIBLE);
        batvltg.setVisibility(View.VISIBLE);
        batterychamp.setVisibility(View.VISIBLE);
        batterydischamp.setVisibility(View.VISIBLE);



        et_consumedpwr = (TextView) view.findViewById(R.id.et_consumedpwr);
        et_gridpwr = (TextView) view.findViewById(R.id.et_gridpwr);
        et_ebvltg = (TextView) view.findViewById(R.id.et_ebvltg);
        et_genpwr = (TextView) view.findViewById(R.id.et_genpwr);
        et_dgruntm = (TextView) view.findViewById(R.id.et_dgruntm);
        et_fuellvl = (TextView) view.findViewById(R.id.et_fuellvl);
        et_batsoc = (TextView) view.findViewById(R.id.et_batsoc);
        et_batvltg = (TextView) view.findViewById(R.id.et_batvltg);
        et_batterychamp = (TextView) view.findViewById(R.id.et_batterychamp);
        et_batterydischamp = (TextView) view.findViewById(R.id.et_batterydischamp);
        et_source = (TextView) view.findViewById(R.id.et_source);

        et_source.setVisibility(View.VISIBLE);
        et_consumedpwr.setVisibility(View.VISIBLE);
        et_gridpwr.setVisibility(View.VISIBLE);
        et_ebvltg.setVisibility(View.VISIBLE);
        et_genpwr.setVisibility(View.VISIBLE);
        et_dgruntm.setVisibility(View.VISIBLE);
        et_fuellvl.setVisibility(View.VISIBLE);
        et_batsoc.setVisibility(View.VISIBLE);
        et_batvltg.setVisibility(View.VISIBLE);
        et_batterychamp.setVisibility(View.VISIBLE);
        et_batterydischamp.setVisibility(View.VISIBLE);

        mAppPreferences = new AppPreferences(getActivity());

        //convert string to Hasmap
        String ttEnergyData = mAppPreferences.getTTEnergyDescription();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Gson gson = new Gson();
        HashMap<String, String> hashMap = gson.fromJson(ttEnergyData, type);


        tv_source.setText("Energy Source");
        String str_source = hashMap.get("energySource");
        if(str_source!=null && !str_source.equalsIgnoreCase("null")){
            et_source.setText(""+hashMap.get("energySource"));
        }

        consumedpwr.setText("Consumed Power(W)");
        String str_consumedpwr = hashMap.get("consumedpwr");
        if(str_consumedpwr!=null && !str_consumedpwr.equalsIgnoreCase("null")){
            et_consumedpwr.setText(""+hashMap.get("consumedpwr"));
        }


        gridpwr.setText("Grid Power(W)");
        String str_gridpwr = hashMap.get("gridpwr");
        if(str_gridpwr!=null && !str_gridpwr.equalsIgnoreCase("null")){
            et_gridpwr.setText(""+hashMap.get("gridpwr"));
        }


        ebvltg.setText("Grid Voltage(L1/L2/L3)(V)");
        String str_ebvltg = hashMap.get("ebvltg");
        if(str_ebvltg!=null && !str_ebvltg.equalsIgnoreCase("null")){
            et_ebvltg.setText(""+hashMap.get("ebvltg"));
        }


        genpwr.setText("Genset Power(W)");
        String str_genpwr = hashMap.get("genpwr");
        if(str_genpwr!=null && !str_genpwr.equalsIgnoreCase("null")){
            et_genpwr.setText(""+hashMap.get("genpwr"));
        }


        dgruntm.setText("DG run time(DG1/DG2)(Hrs)");
        String str_dgruntm = hashMap.get("dgruntm");
        if(str_dgruntm!=null && !str_dgruntm.equalsIgnoreCase("null")){
            et_dgruntm.setText(""+hashMap.get("dgruntm"));
        }

        fuellvl.setText("Fuel Level(T1/T2)(Ltrs)");
        String str_fuellvl = hashMap.get("fuellvl");
        if(str_fuellvl!=null && !str_fuellvl.equalsIgnoreCase("null")){
            et_fuellvl.setText(""+hashMap.get("fuellvl"));
        }


        batsoc.setText("Battery SOC(%)");
        String str_batsoc = hashMap.get("batsoc");
        if(str_batsoc!=null && !str_batsoc.equalsIgnoreCase("null")){
            et_batsoc.setText(""+hashMap.get("batsoc"));
        }


        batvltg.setText("Battery Voltage(V)");
        String str_batvltg = hashMap.get("batvltg");
        if(str_batvltg!=null && !str_batvltg.equalsIgnoreCase("null")){
            et_batvltg.setText(""+hashMap.get("batvltg"));
        }


        batterychamp.setText("Battery Charging Current(Amps.)");
        String str_batterychamp = hashMap.get("batterychamp");
        if(str_batterychamp!=null && !str_batterychamp.equalsIgnoreCase("null")){
            et_batterychamp.setText(""+hashMap.get("batterychamp"));
        }


        batterydischamp.setText("Battery Discharging Current(Amps.)");
        String str_batterydischamp = hashMap.get("batterydischamp");
        if(str_batterydischamp!=null && !str_batterydischamp.equalsIgnoreCase("null")){
            et_batterydischamp.setText(""+hashMap.get("batterydischamp"));
        }

        return view;
    }
}