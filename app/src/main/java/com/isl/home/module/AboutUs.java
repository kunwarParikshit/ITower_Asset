package com.isl.home.module;
/*Created By : Dhakan Lal Sharma
Modified On : 24-Aug-2016
Version     : 0.1
CR          : iMaintan 1.9.1.1*/
import infozech.itower.R;
import com.isl.util.Utils;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
public class AboutUs extends Fragment {
    TextView app_version,app_id;
    PackageInfo pInfo = null;
    ImageView iv_logo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_us,container, false);
        iv_logo = (ImageView) view.findViewById(R.id.iv_logo);
        app_version = (TextView) view.findViewById(R.id.app_version);
        app_version.setTypeface(Utils.typeFace(getActivity()));
        app_id = (TextView) view.findViewById(R.id.app_id);
        app_id.setTypeface(Utils.typeFace(getActivity()));
        app_version.setTypeface(Utils.typeFace(getActivity())); //set TypeFace
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        app_version.setText(Utils.msg(getActivity(),"61")+" - "+pInfo.versionName);
        if (getActivity().getPackageName().equalsIgnoreCase("infozech.tawal")){
            app_id.setVisibility(View.GONE);
        }else {
            app_id.setText("App Id - "+getActivity().getPackageName());
        }
        clientIcon(getActivity().getApplicationContext().getPackageName());
        return view;
    }

    public void clientIcon(String appId){
        switch (appId) {
            case "tawal.com.sa" :
                iv_logo.setBackgroundResource(R.drawable.tawal_icon);
                break;
            case "infozech.tawal" :
                iv_logo.setBackgroundResource(R.drawable.midc_logo);
                app_id.setText("App Id - "+"midc.com.sa");
                break;
           /* case "infozech.tawal" :
                iv_logo.setBackgroundResource(R.drawable.infozech_logo);
                break;*/
            case "infozech.safari" :
                iv_logo.setBackgroundResource(R.drawable.infozech_logo);
                break;
            case "apollo.com.sa" :
                iv_logo.setBackgroundResource(R.drawable.appollo_logo);
                break;
            case "voltalia.com.sa" :
                iv_logo.setBackgroundResource(R.drawable.voltalia_logo);
                break;
            case "infozech.zamil" :
                iv_logo.setBackgroundResource(R.drawable.voltalia_logo);
                break;
            case "ock.com.sa" :
                iv_logo.setBackgroundResource(R.drawable.ock_logo);
                break;
            case "eft.com.sa" :
                iv_logo.setBackgroundResource(R.drawable.eft_logo);
                break;
        }
    }
}
