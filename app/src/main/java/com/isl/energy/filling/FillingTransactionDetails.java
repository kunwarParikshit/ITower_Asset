package com.isl.energy.filling;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.incident.AdapterImages;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.modal.BeanLastFillingTransList;
import com.isl.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class FillingTransactionDetails extends Activity {
	Button bt_back;
	BeanLastFillingTransList fillingReportList;
	String flag;
	int position = 0;
	String[] imgPath = null, imgTimeStamp = null, imgName = null,imgLatitute = null , imgLongitute = null;
	List<String> image_path, img_uploadtimelist, imageName,imageLatitute,imageLongitute;
	ExpandableHeightGridView list_img;
	DisplayImageOptions op;
	AppPreferences mAppPreferences;
	ScrollView scrollview;
	LinearLayout ll_details;
	RelativeLayout rl_no_list;
	TextView txt_no_data;
	String sName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.filling_details);
		mAppPreferences = new AppPreferences(this);
		ll_details = (LinearLayout)findViewById( R.id.ll_details);
		rl_no_list = (RelativeLayout)findViewById( R.id.rl_no_list);
		txt_no_data = (TextView) findViewById( R.id.txt_no_data);
		list_img = (ExpandableHeightGridView) findViewById( R.id.list_img);
		scrollview = (ScrollView) findViewById( R.id.scrollView1);
		image_path = new ArrayList();
		img_uploadtimelist = new ArrayList();
		imageName = new ArrayList();
		imageLatitute = new ArrayList<>();
		imageLongitute = new ArrayList<>();
		
		TextView tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo);
		Utils.msgText( FillingTransactionDetails.this, "218", tv_brand_logo); // set Text Filling Transaction Report
		bt_back = (Button) findViewById( R.id.bt_back);
		Utils.msgButton( FillingTransactionDetails.this, "71", bt_back);
		String res = getIntent().getExtras().getString("res");
		flag = getIntent().getExtras().getString("flag");
		position = getIntent().getExtras().getInt("pos");
		Gson gson = new Gson();
		fillingReportList = gson.fromJson(res, BeanLastFillingTransList.class);
		if (flag.equalsIgnoreCase("A")) {
			position = 0;
		}
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		if(fillingReportList.getFillingReportList()!=null &&
				fillingReportList.getFillingReportList().size()>0){
		     ll_details.setVisibility(View.VISIBLE);
			 rl_no_list.setVisibility(View.GONE);
		     init();
		}else {
			rl_no_list.setVisibility( View.VISIBLE );
			ll_details.setVisibility( View.GONE );
			Utils.msgText( FillingTransactionDetails.this, "267", txt_no_data );
		  }
	    }

	public void init() {
		TextView tv_status = (TextView) findViewById( R.id.tv_status);
		if (fillingReportList.getFillingReportList().get(position).getApp() != null) {
			tv_status.setText("Status  : "+fillingReportList.getFillingReportList().get(position).getApp());
		} else {
			tv_status.setText("Status  : ");
		}


		if(mAppPreferences.getSiteNameEnable()==1 && fillingReportList.getFillingReportList().get(position).getsName()!=null){
			sName="("+fillingReportList.getFillingReportList().get(position).getsName()+")";
		}else{
			sName="";
		}
		TextView tv_site_id = (TextView) findViewById( R.id.tv_site_id);
		tv_site_id.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_site_id.setText( Utils.msg( FillingTransactionDetails.this, "77")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getSITE_ID()+sName);


		TextView tv_tran_id = (TextView) findViewById( R.id.tv_tran_id);
		tv_tran_id.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_tran_id.setText( Utils.msg( FillingTransactionDetails.this, "746")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
				.getTranId());

		TextView tv_fill_date = (TextView) findViewById( R.id.tv_fill_date);
		tv_fill_date
				.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_fill_date.setText( Utils.msg( FillingTransactionDetails.this, "214")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getTRAN_DATE());

		TextView tv_filled_qty = (TextView) findViewById( R.id.tv_filled_qty);
		tv_filled_qty.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		tv_filled_qty.setText( Utils.msg( FillingTransactionDetails.this, "172")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDIESEL_QTY()); // 0.1

		TextView tv_open_stock = (TextView) findViewById( R.id.tv_open_stock);
		tv_open_stock.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		tv_open_stock.setText( Utils.msg( FillingTransactionDetails.this, "173")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDG_STOCK());

		TextView tv_filler_details = (TextView) findViewById( R.id.tv_filler_details);
		tv_filler_details.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		tv_filler_details.setText( Utils.msg( FillingTransactionDetails.this,
				"213")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getFILLER_NAME());

		TextView tv_Genset = (TextView) findViewById( R.id.tv_dg_type);
		tv_Genset.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_Genset.setText( Utils.msg( FillingTransactionDetails.this, "168")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDG_TYPE());

		TextView tv_gen_deploy_date = (TextView) findViewById( R.id.tv_genset_deploy_date);
		TextView tv_gen_reson = (TextView) findViewById( R.id.tv_reson_genset);
		TextView tv_gen_capacity = (TextView) findViewById( R.id.tv_cap_genset);
		TextView tv_gRngNo = (TextView) findViewById( R.id.tv_gRngNo);
		tv_gen_deploy_date.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_gen_reson.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_gen_capacity.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		tv_gRngNo.setTypeface( Utils.typeFace( FillingTransactionDetails.this));

		if (fillingReportList.getFillingReportList().get(position).getDG_TYPE()
				.equalsIgnoreCase("R")
				||fillingReportList.getFillingReportList().get(position).getDG_TYPE()
				.equalsIgnoreCase("Rental")) {
			tv_gen_deploy_date.setVisibility(View.VISIBLE);
			tv_gen_reson.setVisibility(View.VISIBLE);
			tv_gen_capacity.setVisibility(View.VISIBLE);
			tv_gRngNo.setVisibility(View.VISIBLE);
		 if (fillingReportList.getFillingReportList().get(position)
					.getMOBI_GEN_DEPLY_DATE() != null) {
				tv_gen_deploy_date.setText( Utils.msg(
						FillingTransactionDetails.this, "169")
						+ " : "
						+ fillingReportList.getFillingReportList()
								.get(position).getMOBI_GEN_DEPLY_DATE());

			} else {
				tv_gen_deploy_date.setText( Utils.msg(
						FillingTransactionDetails.this, "169") + " : "); // 0.1
			}

			if (fillingReportList.getFillingReportList().get(position)
					.getREASON_MOBI_GEN() != null) {
				tv_gen_reson.setText( Utils.msg( FillingTransactionDetails.this,
						"170")
						+ " : "
						+ fillingReportList.getFillingReportList()
								.get(position).getREASON_MOBI_GEN());

			} else {
				tv_gen_reson.setText( Utils.msg( FillingTransactionDetails.this,
						"170") + " : "); // 0.1
			}

			if (fillingReportList.getFillingReportList().get(position)
					.getCAP_MOBI_GEN() != null) {
				tv_gen_capacity.setText( Utils.msg(
						FillingTransactionDetails.this, "171")
						+ " : "
						+ fillingReportList.getFillingReportList()
								.get(position).getCAP_MOBI_GEN());

			} else {
				tv_gen_capacity.setText( Utils.msg(
						FillingTransactionDetails.this, "171") + " : "); // 0.1
			}

			if (fillingReportList.getFillingReportList().get(position)
					.getgEngNo() != null) {
		 	tv_gRngNo.setText("Genset Engine No. : "
						+ fillingReportList.getFillingReportList()
						.get(position).getgEngNo());

			} else {
				tv_gRngNo.setText( "Genset Engine No. : "); // 0.1
			}

		} else {
			tv_gen_deploy_date.setVisibility(View.GONE);
			tv_gen_reson.setVisibility(View.GONE);
			tv_gen_capacity.setVisibility(View.GONE);
			tv_gRngNo.setVisibility(View.GONE);
		}
		TextView tv_genset_reading = (TextView) findViewById( R.id.tv_genset_reading);
		tv_genset_reading.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		tv_genset_reading.setText( Utils.msg( FillingTransactionDetails.this,
				"175")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDG_READING());
		
		TextView tv_genset_replace = (TextView)findViewById( R.id.tv_genset_replace);
		if (fillingReportList.getFillingReportList().get(position).getGerd() != null) {
			tv_genset_replace.setText( Utils.msg( FillingTransactionDetails.this,
					"303") + " : "+ fillingReportList.getFillingReportList().get(position).getGerd()); 
		} else {
			tv_genset_replace.setText( Utils.msg( FillingTransactionDetails.this,
					"303") + " : "); 
		}
	
		TextView tv_faulty_since = (TextView) findViewById( R.id.tv_faulty_since);
		tv_faulty_since.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position)
				.getFAULT_SINCE() != null) {
			tv_faulty_since.setVisibility(View.VISIBLE);
			tv_faulty_since.setText( Utils.msg( FillingTransactionDetails.this,
					"176")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getFAULT_SINCE());
		} else {
			tv_faulty_since.setVisibility(View.GONE);
		}

		TextView tv_grid_reading = (TextView) findViewById( R.id.tv_grid_reading);
		tv_grid_reading.setTypeface( Utils
				.typeFace( FillingTransactionDetails.this));
		tv_grid_reading.setText( Utils
				.msg( FillingTransactionDetails.this, "178")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getEB_READING());
		
		
		TextView tv_grid_replace = (TextView)findViewById( R.id.tv_grid_replace);
		if (fillingReportList.getFillingReportList().get(position).getGrrd() != null) {
			tv_grid_replace.setText( Utils.msg( FillingTransactionDetails.this,
					"304") + " : "+ fillingReportList.getFillingReportList().get(position).getGrrd()); 
		} else {
			tv_grid_replace.setText( Utils.msg( FillingTransactionDetails.this,
					"304") + " : "); 
		}
		
		TextView tv_lat = (TextView) findViewById( R.id.tv_lat);
		tv_lat.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position)
				.getLATITUDE() != null) {
			tv_lat.setText( Utils.msg( FillingTransactionDetails.this, "215")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getLATITUDE());

		} else {
			tv_lat.setText( Utils.msg( FillingTransactionDetails.this, "215")
					+ " : ");
		}

		TextView tv_log = (TextView) findViewById( R.id.tv_log);
		tv_log.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position)
				.getLONGITUDE() != null) {
			tv_log.setText( Utils.msg( FillingTransactionDetails.this, "216")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getLONGITUDE());

		} else {
			tv_log.setText( Utils.msg( FillingTransactionDetails.this, "216")
					+ " : ");
		}

		TextView tv_distance = (TextView) findViewById( R.id.tv_distance);
		tv_distance.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position)
				.getFILLER_DISTANCE() != null) {
			tv_distance.setText( Utils
					.msg( FillingTransactionDetails.this, "217")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getFILLER_DISTANCE()); // 0.1
		} else {
			tv_distance.setText( Utils
					.msg( FillingTransactionDetails.this, "217") + " : "); // 0.1
		}
		TextView tv_tranject = (TextView) findViewById( R.id.tv_trans);
		tv_tranject.setText("Transaction Type : "+ fillingReportList.getFillingReportList().get(position)
				.getTrantypeFF());
		TextView tv_plandt = (TextView) findViewById( R.id.tv_plandt);
		if ( fillingReportList.getFillingReportList().get(position)
				.getPdt()!=null) {
			tv_plandt.setText("Planned Date : "+ fillingReportList.getFillingReportList().get(position).getPdt());
		}else {
			tv_plandt.setText("Planned Date : ");
		}

		TextView tv_planqt = (TextView) findViewById( R.id.tv_planqt);
		if ( fillingReportList.getFillingReportList().get(position)
				.getPqty()!=null) {
			tv_planqt.setText("Planned Quantity : " + fillingReportList.getFillingReportList().get(position).getPqty());
		}else {
			tv_planqt.setText("Planned Quantity : ");
		}

		TextView tv_filling_st = (TextView) findViewById( R.id.tv_filling_st);
		if ( fillingReportList.getFillingReportList().get(position)
				.getFstatus()!=null) {
			tv_filling_st.setText("Filling Status : " + fillingReportList.getFillingReportList().get(position).getFstatus());
		}else {
			tv_filling_st.setText("Filling Status : ");
		}

		TextView tv_mode = (TextView) findViewById( R.id.tv_mode);
		tv_mode.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position).getfMode() != null) {
			tv_mode.setText( Utils.msg( FillingTransactionDetails.this, "287")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getfMode()); // 0.1
		} else {
			tv_mode.setText( Utils.msg( FillingTransactionDetails.this, "287")
					+ " : "); // 0.1
		}

		TextView tv_fuel_tank = (TextView) findViewById( R.id.tv_fuel_tank);
		tv_fuel_tank.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position).getfTank() != null) {
			tv_fuel_tank.setText( Utils.msg( FillingTransactionDetails.this,
					"288")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getfTank()); // 0.1
		} else {
			tv_fuel_tank.setText( Utils.msg( FillingTransactionDetails.this,
					"288") + " : "); // 0.1
		}

		TextView tv_ava_run_hours = (TextView) findViewById( R.id.tv_ava_run_hours);
		tv_ava_run_hours.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position).getArh() != null) {
			tv_ava_run_hours.setText( Utils.msg( FillingTransactionDetails.this,
					"314") + " : "+ fillingReportList.getFillingReportList().get(position).getArh()); 
		} else {
			tv_ava_run_hours.setText( Utils.msg( FillingTransactionDetails.this,
					"314") + " : "); 
		}
		
		TextView tv_cph = (TextView) findViewById( R.id.tv_cph);
		tv_cph.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position)
				.getActualCPH() != null) {
			tv_cph.setText( Utils.msg( FillingTransactionDetails.this, "289")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getActualCPH()); // 0.1
		} else {
			tv_cph.setText( Utils.msg( FillingTransactionDetails.this, "289")
					+ " : "); // 0.1
		}

		TextView tv_vender = (TextView) findViewById( R.id.tv_vender);
		tv_vender.setTypeface( Utils.typeFace( FillingTransactionDetails.this));
		if (fillingReportList.getFillingReportList().get(position).getVendor() != null) {
			tv_vender.setText( Utils.msg( FillingTransactionDetails.this, "290")
					+ " : "
					+ fillingReportList.getFillingReportList().get(position)
							.getVendor()); // 0.1
		} else {
			tv_vender.setText( Utils.msg( FillingTransactionDetails.this, "290")
					+ " : "); // 0.1
		}

		TextView tv_motorable = (TextView) findViewById( R.id.tv_motorable);
		if (fillingReportList.getFillingReportList().get(position).getHilst() != null) {
			tv_motorable.setText("Site Motorable : "+ fillingReportList.getFillingReportList().get(position)
					.getHilst()); // 0.1
		} else {
			tv_motorable.setText("Site Motorable : "); // 0.1
		}


		if (fillingReportList.getFillingReportList().get(position).getImgpath() != null) {

			imgPath = fillingReportList.getFillingReportList().get(position)
					.getImgpath().split("\\~");

			if(fillingReportList.getFillingReportList().get(position).getImgtimestamp() !=null){
				imgTimeStamp = fillingReportList.getFillingReportList()
						.get(position).getImgtimestamp().split("\\~");
			}

			if(fillingReportList.getFillingReportList().get(position).getImgname() !=null){
				imgName = fillingReportList.getFillingReportList().get(position)
						.getImgname().split("\\~");
			}

			if(fillingReportList.getFillingReportList().get(position).getImgLati() !=null){
				imgLatitute = fillingReportList.getFillingReportList().get(position)
						.getImgLati().split("\\~");
			}

			if(fillingReportList.getFillingReportList().get(position).getImgLongi() !=null){
				imgLongitute = fillingReportList.getFillingReportList().get(position)
						.getImgLongi().split("\\~");
			}

		}

		if (imgPath.length > 0) {
			for (int i = 0; i < imgPath.length; i++) {
				if (imgPath[i].contains(".jpg") || imgPath[i].contains(".JPG")
						|| imgPath[i].contains(".jpeg") || imgPath[i].contains(".JPEG")
						|| imgPath[i].contains(".png") || imgPath[i].contains(".PNG")) {
					image_path.add(imgPath[i]);

					if (imgTimeStamp !=null && i < imgTimeStamp.length) {
						img_uploadtimelist.add(imgTimeStamp[i]);
					}

					if (imgName !=null && i < imgName.length) {
						imageName.add(imgName[i]);
					}

					if (imgLatitute !=null && i < imgLatitute.length) {
						imageLatitute.add(imgLatitute[i]);
					}

					if (imgLongitute !=null && i < imgLongitute.length) {
						imageLongitute.add(imgLongitute[i]);
					}

				}
			}
		}
		setImage();
	}

	// Method to set images in List View
	private void setImage() {
		if (image_path!=null && image_path.size() > 0) {
			op = new DisplayImageOptions.Builder()
					.showStubImage( R.drawable.no_media_default)
					.showImageForEmptyUri( R.drawable.no_media_default)
					.showImageOnFail( R.drawable.no_media_default)
					.cacheInMemory().cacheOnDisc()
					.displayer(new RoundedBitmapDisplayer(1)).build();
			list_img.setVisibility(View.VISIBLE);
			list_img.setFastScrollEnabled(true);
			list_img.setAdapter(new AdapterImages( FillingTransactionDetails.this, image_path,img_uploadtimelist, imageName,imageLatitute,imageLongitute,op));
			list_img.setExpanded(true);
		}
	}


}