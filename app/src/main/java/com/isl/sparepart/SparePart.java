package com.isl.sparepart;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.constant.WebMethods;
import com.isl.modal.BeanSpareList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.modal.IncidentMetaList;
import com.isl.modal.ServerResponse;
import infozech.itower.R;
public class SparePart extends Activity{
	DataBaseHelper db;
	AppPreferences mAppPreferences;
	String moduleUrl = "",serialFlag = "F",datatype="I",url = "",etsSid,spareId,serialno,tranID,activityMode="0",serialnum="",typeId = "";
	ArrayList<String> spareNameList,spareIdList,spareDataType,spareMaxQty,flag,userQty,org_Qty,userStatus,SerialListt,list_category;
	ArrayList<EditText> arr_et;
	SearchableSpinner spinner_spare,sp_category;
	int editableMode=0,editablePosition=0;
	ArrayList<String> nonEdatbleList;
	EditText editText_quantity;
	float maxQty,org_quantity;
	ListView listView_Spare;
	Button button_add,bt_save;
	LinearLayout ll_spare_top,ll_category;
	RelativeLayout rl;
	TextView tv_category,tv_spare,tv_spaction,tv_divider,tv_main_header,tv_spPart,tv_spQty;
	LayoutParams TVParam = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	LayoutParams INPUTTVParam = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	ProgressBar pBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spare_part);
		pBar = (ProgressBar) findViewById(R.id.pBar);
		tv_category = (TextView) findViewById(R.id.tv_category);
		ll_category = (LinearLayout) findViewById(R.id.ll_category);
		tv_spare = (TextView) findViewById(R.id.tv_spare);
		sp_category = (SearchableSpinner) findViewById(R.id.sp_category);
		rl = (RelativeLayout)findViewById(R.id.rl);
		tv_spPart = (TextView)findViewById(R.id.tv_spPart);
		Utils.msgText( SparePart.this, "282", tv_spPart);
		tv_spQty = (TextView)findViewById(R.id.tv_spQty);
		Utils.msgText( SparePart.this, "283", tv_spQty);
		tv_spaction = (TextView) findViewById(R.id.tv_spaction);
		Utils.msgText( SparePart.this, "311", tv_spaction);

		editText_quantity = (EditText)findViewById(R.id.editText_quantity);
		editText_quantity.setBackgroundResource(R.drawable.input_box );
		listView_Spare = (ListView)findViewById(R.id.listView_Spare);
		ll_spare_top = (LinearLayout) findViewById(R.id.ll_spare_top);

		tv_divider = (TextView) findViewById(R.id.tv_divider);
		mAppPreferences = new AppPreferences( SparePart.this);
		db = new DataBaseHelper(this);
		db.open();
		Utils.msgText( SparePart.this, "282", tv_spare);
		Utils.msgText( SparePart.this, "582", tv_category);
		list_category = db.getInciParam1("405",tv_category,"654");
		sp_category.setBackgroundResource(R.drawable.doted);
		addItemsOnSpinner(sp_category,list_category);

		sp_category.setOnItemSelectedListener( new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(!sp_category.getSelectedItem().toString().equalsIgnoreCase("Select Category")){
					fetchCursor();
					addItemsOnSpinner(spinner_spare,spareNameList);
				}else{
					//spareNameList.clear();
					//spareNameList.add("Select Spare Parts");
					//addItemsOnSpinner(spinner_spare,spareNameList);
				}

			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		tv_main_header = (TextView) findViewById(R.id.tv_main_header);
		Utils.msgText( SparePart.this, "280", tv_main_header);
		moduleUrl = db.getModuleIP("Preventive");
		tranID = getIntent().getExtras().getString("tranID");
		etsSid = getIntent().getExtras().getString("etsSid");
		activityMode = getIntent().getExtras().getString("activityMode");
		typeId = getIntent().getExtras().getString("typeId");

		if (Utils.isNetworkAvailable( SparePart.this)) {
			//if (!metaDataType().equalsIgnoreCase("")) {
			new IncidentMetaDataTask( SparePart.this).execute();
			//}
		}
		sparePartFromLocal();
		Button bt_close = (Button) findViewById(R.id.bt_close);
		bt_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				db.updateSparePartStatus();
				db.close();
				finish();

			}
		});

		bt_save = (Button)findViewById(R.id.button_submit);
		Utils.msgButton( SparePart.this, "27", bt_save);
		bt_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buildSpareData("1");
			}
		});

		button_add = (Button) findViewById(R.id.button_add);
		Utils.msgButton( SparePart.this, "284", button_add);
		button_add.setText(""+button_add.getText().toString());
		button_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				spinner_spare.setEnabled(true);
				if(sp_category.getSelectedItem().toString().trim().equalsIgnoreCase
						("Select "+tv_category.getText().toString().trim())){
					Toast.makeText(SparePart.this, "Please select "+tv_category.getText().toString().trim()+".",
							Toast.LENGTH_LONG).show();
				}else if(spinner_spare.getSelectedItem().toString().trim().equalsIgnoreCase("Select Spare Parts")){
					Toast.makeText(SparePart.this, "Please select Spare Parts.",
							Toast.LENGTH_LONG).show();
				}else if (editText_quantity.getText().toString().length() == 0) {
					// Toast.makeText(PMChecklist.this, "Enter Quantity",
					// Toast.LENGTH_LONG).show();
					Utils.toast( SparePart.this, "286");
				}else if (editText_quantity.getText().toString().length() >0 && etSum(editText_quantity)==0) {
					Toast.makeText(SparePart.this, "Quantity Cannot be zero.",Toast.LENGTH_LONG).show();
				}else if(maxQty!=0
						&& Float.parseFloat(editText_quantity.getText().toString())
						> maxQty){
					//Toast.makeText(SparePart.this, "Quantity exceed ! Maximum quantity should be "+maxQty,
					//Toast.LENGTH_LONG).show();
					Utils.toastMsg( SparePart.this,Utils.msg( SparePart.this,"310")+" "+maxQty);
				}else if(!editText_quantity.getText().toString().contains(".") &&
						serialFlag.equalsIgnoreCase("1") &&
						org_quantity!=0	&&
						org_quantity > Float.parseFloat(editText_quantity.getText().toString())){
					spareNumberDetails(spinner_spare.getSelectedItem().toString().trim(),serialnum,spareId);
					addItemsOnSpinner(spinner_spare, spareNameList);
					//Toast.makeText( SparePart.this, "Quantity can't be less than from current Quantity.",Toast.LENGTH_LONG).show();
				}//open popup and add serial no. T>add serial no. F> not add serial no.
				else if(!editText_quantity.getText().toString().contains(".") && serialFlag.equalsIgnoreCase("1")){
					spareNumber(serialnum,Integer.parseInt(editText_quantity.getText().toString().trim()));
					spinner_spare.setEnabled(true);
					//button_add.setText("+Add");
					//Utils.msgButton( SparePart.this, "284", button_add);
					button_add.setText(""+button_add.getText().toString());
				}else {
					//saveLocal(spareId,editText_quantity.getText().toString().trim(),"");
					db.addSpareQty(spareId,editText_quantity.getText().toString().trim(),"","1",
							sp_category.getSelectedItem().toString().trim());
					spinner_spare.setEnabled(true);
					//button_add.setText("+Add");
					Utils.msgButton( SparePart.this, "284", button_add);
					button_add.setText(""+button_add.getText().toString());
					showSpare();
					addItemsOnSpinner(spinner_spare, spareNameList);
				}
			}
		});

		if(activityMode.equalsIgnoreCase("0")){//not visable from viewPM,Approved pm checklist.
			displayView();
		}
	}


	IncidentMetaList baseDatRresposnse = null;
	public class IncidentMetaDataTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog pd;

		Context con;

		public IncidentMetaDataTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			//pd = ProgressDialog.show(con, null, "Loading...");
			//pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
				Gson gson = new Gson();
				nameValuePairs.add(new BasicNameValuePair("module", "Incident"));
				//nameValuePairs.add(new BasicNameValuePair("module", "Incident"));
				//nameValuePairs.add(new BasicNameValuePair("datatype",metaDataType()));
				nameValuePairs.add(new BasicNameValuePair("datatype","26"));
				nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				String logi = "1";
				if (typeId.equalsIgnoreCase("-1")) {
					logi = "2";
				}
				nameValuePairs.add( new BasicNameValuePair("lat", typeId));
				nameValuePairs.add( new BasicNameValuePair("lng", logi ));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
				}else{
					url=moduleUrl+ WebMethods.url_GetMetadata;
				}
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				baseDatRresposnse = gson.fromJson(res,IncidentMetaList.class);
			} catch (Exception e) {
				e.printStackTrace();
				baseDatRresposnse = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			db.clearSparePart();
			if ((baseDatRresposnse == null)) {
				// Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
				Utils.toast( SparePart.this, "70");
			} else if (baseDatRresposnse != null) {
				if (baseDatRresposnse.getSpareParts().size() > 0) {
					db.insertSparePart(baseDatRresposnse.getSpareParts());
					db.dataTS(null, null, "26",
							db.getLoginTimeStmp("26","0"), 2,"0");
				}
			} else {
				// Toast.makeText(IncidentManagement.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				Utils.toast( SparePart.this, "13");
			}
			sparePartFromLocal();
			/*if (pd.isShowing()) {
				pd.dismiss();
			}*/
			pBar.setVisibility(View.GONE);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			new GetSparePartsDetails( SparePart.this).execute();
		}
	}

	public String metaDataType() {
		String DataType_Str = "1";
		String i = Utils.CompareDates(db.getSaveTimeStmp("26","0"),db.getLoginTimeStmp("26","0"), "26");
		if (i != "1") {
			DataType_Str = i;
		}
		if (DataType_Str == "1") {
			DataType_Str = "";
		}
		return DataType_Str;
	}
	//get spare part details in drop down
	public void sparePartFromLocal(){
		fetchCursor();
		spinner_spare = (SearchableSpinner)findViewById(R.id.spinner_spare);
		spinner_spare.setBackgroundResource(R.drawable.doted);
		addItemsOnSpinner(spinner_spare, spareNameList);
		spinner_spare.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
				fetchCursor();
				if(userStatus.get(pos).equalsIgnoreCase("0")){
					Utils.msgButton( SparePart.this, "284", button_add);
					button_add.setText(""+button_add.getText().toString());
				}else{
					Utils.msgButton( SparePart.this, "121", button_add);
					button_add.setText(""+button_add.getText().toString());
				}
				editText_quantity.setText(""+userQty.get(pos));
				if (org_Qty.get(pos)!=null
						&& !org_Qty.get(pos).isEmpty()) {
					org_quantity = Float.parseFloat(org_Qty.get(pos));
				}else{
					org_quantity = 0;
				}
				serialFlag = flag.get(pos);
				datatype = spareDataType.get(pos);
				serialnum =SerialListt.get(pos);
				spareId  = spareIdList.get(pos);
				//Toast.makeText(SparePart.this, ""+serialnum, Toast.LENGTH_LONG).show();
				if (spareMaxQty.get(pos)!=null
						&& !spareMaxQty.get(pos).isEmpty()) {
					maxQty = Float.parseFloat(spareMaxQty.get(pos));
				}else{
					maxQty = 0;
				}

				if (datatype.equalsIgnoreCase("I")) {
					editText_quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else if (datatype.equalsIgnoreCase("F")) {
					editText_quantity.setInputType(InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	// set dropdown value and backgroung
	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_text, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
		spinner.setAdapter(dataAdapter);
	}

	Dialog dialog_spare_part;
	public void spareNumber(String s,int length){
		EditText editText;
		TextView textView;
		String tmpArray[] = new String[length];
		if (!s.equalsIgnoreCase("null")	&& !s.isEmpty()) {
			tmpArray = s.split("\\,");
			//Toast.makeText(SparePart.this,"blank111==="+s,Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(SparePart.this,"blank222=="+s,Toast.LENGTH_LONG).show();

		}

		arr_et = new ArrayList<EditText>();
		arr_et.clear();
		dialog_spare_part = new Dialog( SparePart.this, R.style.FullHeightDialog);
		dialog_spare_part.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_spare_part.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		dialog_spare_part.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.spare_serial_no, null);
		dialog_spare_part.setContentView(view);
		LinearLayout ll_spare_part = (LinearLayout) view.findViewById(R.id.ll_spare_part);
		TextView tv_header = (TextView) view.findViewById(R.id.tv_header);
		tv_header.setText(spinner_spare.getSelectedItem().toString());
		Button bt_popup_close = (Button) view.findViewById(R.id.bt_popup_close);
		TextView bt_add_serialNo = (TextView) view.findViewById(R.id.bt_add_serialNo);
		Utils.msgText( SparePart.this, "307", bt_add_serialNo);
		//bt_add_serialNo.setText("Add Serial Number");
		bt_add_serialNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				serialno = "";
				int mode = 0;
				for(int i=0; i < arr_et.size(); i++){
					if(arr_et.get(i).getText().toString().length()==0){
						// Toast.makeText(SparePart.this,"Serial no. can not be blank.",Toast.LENGTH_LONG).show();
						Utils.toast( SparePart.this, "308");
						mode=0;
						break;
					}else if(findDuplicates(arr_et)==1){
						Utils.toastMsg( SparePart.this,"Serial no. can't be duplicate.");
						//Toast.makeText(SparePart.this,"Serial no. can't be duplicate.",Toast.LENGTH_SHORT).show();
						mode=0;
						break;
					}else{
						serialno = serialno + arr_et.get(i).getText().toString()+",";
						mode=1;
					}
				}
				if (mode==1 && serialno.length() > 0) {
					serialno = serialno.substring(0,serialno.length() - 1);
					//saveLocal(spareId,editText_quantity.getText().toString().trim(),serialno);
					db.addSpareQty(spareId,editText_quantity.getText().toString().trim(),serialno,"1",
							sp_category.getSelectedItem().toString().trim());
					showSpare();
					dialog_spare_part.dismiss();
					addItemsOnSpinner(spinner_spare, spareNameList);
				}
			}
		});

		bt_popup_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog_spare_part.dismiss();
			}
		});

		final Window window_SignIn = dialog_spare_part.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog_spare_part.show();

		for(int i=0; i<length; i++){
			final float scale = SparePart.this.getResources().getDisplayMetrics().density;
			textView = new TextView(this);
			textView.setPadding(0, 10, 0, 0);
			textView.setTextColor(getResources().getColor(R.color.textcolor));
			textView.setTextSize(15);
			TVParam.setMargins( 0, (int)(10 * scale), 0, 0 );
			textView.setLayoutParams(TVParam);
			textView.setMinimumHeight((int)(40 * scale));
			int a=i+1;
			textView.setText("Serial Number "+a);
			ll_spare_part.addView(textView);


			INPUTTVParam.setMargins( 0, (int)(-5 * scale), 0, 0 );
			editText = new EditText(this);
			editText.setPadding(7,-5, 0, 0);
			editText.setTextColor(getResources().getColor(R.color.input_textcolor));
			editText.setBackgroundResource(R.drawable.input_box );
			editText.setLayoutParams(INPUTTVParam);
			editText.setMinimumHeight((int)(40 * scale));

			editText.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(20)});
			if(tmpArray.length==0){

			}else{
				if(tmpArray.length>0 && i<tmpArray.length && tmpArray[i]!=null ){
					editText.setText(""+tmpArray[i]);
					if(nonEdatbleList.size()>editablePosition && nonEdatbleList.get(editablePosition)!=null
							&& !nonEdatbleList.get(editablePosition).isEmpty()
							&& Integer.parseInt(nonEdatbleList.get(editablePosition))>i){
						editText.setEnabled(false);
						//Toast.makeText(SparePart.this, "Quantity=="+editableMode+"sss==="+nonEdatbleList.get(editablePosition),Toast.LENGTH_LONG).show();
					}
				}
			}
			arr_et.add(editText);
			ll_spare_part.addView(editText);
		}
	}

	InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start,
								   int end, Spanned dest, int dstart, int dend) {
			for (int i = start;i < end;i++) {
				if (!Character.isLetterOrDigit(source.charAt(i)) &&
						!Character.toString(source.charAt(i)).equals("/") &&
						!Character.toString(source.charAt(i)).equals("-")
				)
				{
					return "";
				}
			}
			return null;
		}
	};

	public void showSpare(){
		//db.addSpareQty(id,qty,serialNo);
		String categoryID =db.getInciParamId("405", sp_category.getSelectedItem()
				.toString().trim(),"654");
		if(categoryID.equalsIgnoreCase("R")){
			categoryID = "-1";
		}
		Cursor cursor = db.getSpareParts(1,categoryID);
		ArrayList<String> adp_spare_name = new ArrayList<>();
		ArrayList<String> adp_spare_qty = new ArrayList<>();
		ArrayList<String> adp_flag = new ArrayList<>();
		ArrayList<String> adp_SerialNumber = new ArrayList<>();
		ArrayList<String> adp_spare_org_qty = new ArrayList<>();
		ArrayList<String> adp_spare_id = new ArrayList<String>();
		ArrayList<String> adp_category_id = new ArrayList<String>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				adp_spare_name.add(cursor.getString(cursor.getColumnIndex("SpareName")));
				adp_spare_qty.add(cursor.getString(cursor.getColumnIndex("Qty")));
				adp_flag.add(cursor.getString(cursor.getColumnIndex("Flag")));
				adp_SerialNumber.add(cursor.getString(cursor.getColumnIndex("SerialNumber")));
				adp_spare_org_qty.add(cursor.getString(cursor.getColumnIndex("OrgQty")));
				adp_spare_id.add(cursor.getString(cursor.getColumnIndex("ID")));
				adp_category_id.add(cursor.getString(cursor.getColumnIndex("CATEGORY_ID")));
			}
		}
		listView_Spare.setAdapter(new AdapterSpareParts( SparePart.this,adp_spare_name,adp_spare_qty,adp_flag,adp_SerialNumber,adp_spare_org_qty,adp_spare_id,adp_category_id));
		if(editableMode==0){
			nonEdatbleList=adp_spare_org_qty;
		}
	}

	public class AdapterSpareParts extends BaseAdapter {
		Context con;
		private LayoutInflater inflater = null;
		ArrayList<String> adp_spare_name, adp_spare_qty,adp_flag,adp_SerialNumber,adp_spare_org_qty,
				adp_spare_id,adp_category_id;
		public AdapterSpareParts(Context con, ArrayList<String> adp_spare_name,
								 ArrayList<String> adp_spare_qty,ArrayList<String> adp_flag,
								 ArrayList<String> adp_SerialNumber,ArrayList<String> adp_spare_org_qty,
								 ArrayList<String> adp_spare_id,ArrayList<String> adp_category_id) {
			this.con = con;
			this.adp_spare_name = adp_spare_name;
			this.adp_spare_qty = adp_spare_qty;
			this.adp_flag = adp_flag;
			this.adp_SerialNumber = adp_SerialNumber;
			this.adp_spare_org_qty=adp_spare_org_qty;
			this.adp_spare_id = adp_spare_id;
			this.adp_category_id = adp_category_id;
			inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adp_spare_name.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(final int position, View arg1, ViewGroup parent) {
			View vi = arg1;
			if (arg1 == null)
				vi = inflater.inflate(R.layout.spare_part_list, null);
			final TextView tv_part_name = (TextView) vi.findViewById(R.id.tv_part_name);
			final TextView et_quantity = (TextView) vi.findViewById(R.id.tv_quantitiy);
			ImageView action = (ImageView) vi.findViewById(R.id.action);
			TextView tv_action_before = (TextView) vi.findViewById(R.id.tv_action_before);
			LinearLayout ll_spare_delete = (LinearLayout) vi.findViewById(R.id.ll_spare_delete);
			TextView tv_spare_delete = (TextView) vi.findViewById(R.id.tv_spare_delete);

			tv_part_name.setText(adp_spare_name.get(position));
			//tv_part_name.setTag(adp_category_id.get(position));
			et_quantity.setText(adp_spare_qty.get(position));

			if(activityMode.equalsIgnoreCase("0")){//not visable from viewPM,Approved pm checklist.
				action.setVisibility(View.GONE);
				tv_action_before.setVisibility(View.GONE);
				ll_spare_delete.setVisibility(View.GONE);
			}
			action.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String catName = db.getInciParamName("405",adp_category_id.get(position),"654");
					int categoryPos = getCategoryPos(catName, list_category);
					sp_category.setSelection(categoryPos);
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							int statusPos = getCategoryPos(adp_spare_name.get(position),spareNameList);
							spinner_spare.setSelection(statusPos);
						}
					},100);

					String orgQty=adp_spare_org_qty.get(position);
					editText_quantity.setText( ""+orgQty);
					serialnum =adp_SerialNumber.get(position);
					editablePosition=position;
					editableMode=1;
					if (orgQty!=null
							&& !orgQty.isEmpty()) {
						org_quantity = Float.parseFloat(orgQty);
					}else{
						org_quantity = 0;
					}
				}
			});

			if(adp_flag.get(position).equalsIgnoreCase("1")){
				et_quantity.setPaintFlags(et_quantity.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);
			}

			et_quantity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					editableMode=1;
					if(adp_flag.get(position).equalsIgnoreCase("1")){
						if(adp_SerialNumber.get(position).length()==0){
							//Toast.makeText(SparePart.this,"details not available",Toast.LENGTH_LONG).show();
							Utils.toast( SparePart.this, "309");
						}else{
							spareNumberDetails(adp_spare_name.get(position),adp_SerialNumber.get(position),adp_spare_id.get(position));
						}
					}
				}
			});

			tv_spare_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alert(adp_spare_id.get(position),"0","","Do you want to delete this Spare Part?",0);
				}
			});

			return vi;
		}
	}

	Dialog dialog_serialDetails;
	public void spareNumberDetails(String name,String Details,String spreIDD){
		dialog_serialDetails = new Dialog( SparePart.this, R.style.FullHeightDialog);
		dialog_serialDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_serialDetails.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		dialog_serialDetails.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.serial_spare_details, null);
		dialog_serialDetails.setContentView(view);
		ListView lv_serial = (ListView) view.findViewById(R.id.lv_seriall);
		TextView tv_ser_name = (TextView) view.findViewById(R.id.tv_ser_hader);
		TextView tv_del_div = (TextView) view.findViewById(R.id.tv_del_div);
		LinearLayout ll_del = (LinearLayout) view.findViewById(R.id.ll_del);
		if(activityMode.equalsIgnoreCase("0")){//not visable from viewPM,Approved pm checklist.
			ll_del.setVisibility(View.GONE);
			tv_del_div.setVisibility(View.GONE);
		}
		tv_ser_name.setText(""+name);
		TextView tv_no = (TextView) view.findViewById(R.id.tv_no);
		Utils.msgText( SparePart.this, "312", tv_no);
		TextView tv_top_serNO = (TextView) view.findViewById(R.id.tv_top_serNO);
		Utils.msgText( SparePart.this, "313", tv_top_serNO);

		Button bt_popup_close = (Button) view.findViewById(R.id.bt_popup_close);

		bt_popup_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog_serialDetails.dismiss();
			}
		});

		final Window window_SignIn = dialog_serialDetails.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog_serialDetails.show();
		String baseArray[] = Details.split(",");

		if (baseArray.length > 0) {
			lv_serial.setAdapter(new AdapterNumberDetails( SparePart.this,baseArray,Details,spreIDD));
		}
	}


	public class AdapterNumberDetails extends BaseAdapter {
		Context con;
		private LayoutInflater inflater = null;
		String baseArray[];
		String detai;
		String spreIDD;
		public AdapterNumberDetails(Context con, String baseArray[],String detai,String spreIDD) {
			this.con = con;
			this.baseArray = baseArray;
			this.detai = detai;
			this.spreIDD = spreIDD;
			inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return baseArray.length;
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(final int position, View arg1, ViewGroup parent) {
			View vi = arg1;
			if (arg1 == null)
				vi = inflater.inflate(R.layout.spare_serial_details_list, null);
			final TextView tv_no = (TextView) vi.findViewById(R.id.tv_no);
			final TextView tv_ser_no = (TextView) vi.findViewById(R.id.tv_ser_no);
			final TextView tv_delete = (TextView) vi.findViewById(R.id.tv_delete);
			TextView tv_div = (TextView) vi.findViewById(R.id.tv_div);
			LinearLayout ll_delete = (LinearLayout)vi.findViewById(R.id.ll_delete);
			if(activityMode.equalsIgnoreCase("0")){//not visable from viewPM,Approved pm checklist.
				ll_delete.setVisibility(View.GONE);
				tv_div.setVisibility(View.GONE);
			}

			tv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String baseArray1[] = detai.split(",");
					String updated_serNo = "";
					if (baseArray1.length > 0) {
						for(int a=0;a<baseArray1.length;a++){
							if(!baseArray1[a].equalsIgnoreCase(tv_ser_no.getText().toString().trim())){
								updated_serNo = updated_serNo+baseArray1[a]+",";
							}
						}
						if (updated_serNo.length() > 0) {
							updated_serNo = updated_serNo.substring( 0,updated_serNo.length() - 1);
						}
					}
					int x=baseArray.length-1;
					alert(spreIDD,""+x,updated_serNo,"Do you want to delete this serial no.?",1);
					//
					//db.addSpareQty(spareId,""+x,updated_serNo);
				}
			});
			tv_ser_no.setText(baseArray[position]);
			int c=position+1;
			tv_no.setText(""+c);
			return vi;
		}
	}

	// saved spare parts
	public class SaveSpareParts extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		ServerResponse serverResponse;
		String sparePartDetails;
		String dupFlag;
		public SaveSpareParts(Context con,String sparePartDetails,String flag) {
			this.con = con;
			this.sparePartDetails=sparePartDetails;
			this.dupFlag=flag;
		}

		@Override
		protected void onPreExecute() {
			//pBar.setVisibility(View.VISIBLE);
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}


		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						5);
				nameValuePairs.add(new BasicNameValuePair("tranID",tranID));
				nameValuePairs.add(new BasicNameValuePair("spareParts",sparePartDetails));
				nameValuePairs.add(new BasicNameValuePair("flag", activityMode));
				nameValuePairs.add(new BasicNameValuePair("etsSiteID", etsSid));
				nameValuePairs.add(new BasicNameValuePair("dupFlag", ""+dupFlag));
				nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_SaveSparePartDetails;
				}else{
					url=moduleUrl+ WebMethods.url_SaveSparePartDetails;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_SaveSparePartDetails
						:  "http://" + moduleUrl + "/Service.asmx/saveSparePartDetails";*/
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				String new_res = res.replace("[", "").replace("]", "");
				serverResponse = new Gson().fromJson(new_res, ServerResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				serverResponse = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (serverResponse != null) {

				if (serverResponse.getFlag().equalsIgnoreCase("S")) {
					Utils.toastMsg( SparePart.this, serverResponse.getMessage());
					if(activityMode.equalsIgnoreCase("2")){
						db.updateSparePartStatus();
					}else{
						mAppPreferences.setSpareSerialFlagScreen(1);
					}
					db.close();
					finish();

				}else if (serverResponse.getFlag().equalsIgnoreCase("D")) { //duplicate spare part add or not
					addDupSparePart( serverResponse.getMessage());
				}else{
					Utils.toastMsg( SparePart.this, serverResponse.getMessage());
				}
			} else {
				Utils.toast( SparePart.this, "13");
			}
			//pBar.setVisibility(View.GONE);
			super.onPostExecute(result);
		}
	};

	// get all spare part which already saved
	BeanSpareList spare_res_list;
	public class GetSparePartsDetails extends AsyncTask<Void, Void, Void> {
		//ProgressDialog pd;
		Context con;

		public GetSparePartsDetails(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pBar.setVisibility(View.GONE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			//pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("ID", tranID));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetSparePartsDetails;
				}else{
					url=moduleUrl+ WebMethods.url_GetSparePartsDetails;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_GetSparePartsDetails
						:  "http://" + moduleUrl + "/Service.asmx/GetSparePartsDetails";*/
				String response = Utils.httpPostRequest(con,url, nameValuePairs);
				Gson gson = new Gson();
				spare_res_list = gson.fromJson(response, BeanSpareList.class);
			} catch (Exception e) {
				e.printStackTrace();
				spare_res_list = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if ((spare_res_list == null)) {
				// Toast.makeText(AssignedTicketList.this,"Server Not Available",
				// Toast.LENGTH_LONG).show();
				Utils.toast( SparePart.this, "13");
			} else if (spare_res_list.getSpareList().size() > 0) {
				db.updateSpareQty(spare_res_list.getSpareList());
				showSpare();
			} else {

			}
			/*if (pd.isShowing()) {
				pd.dismiss();
			}*/

			pBar.setVisibility(View.GONE);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			super.onPostExecute(result);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	public void displayView(){
		rl.setVisibility(View.GONE);
		ll_spare_top.setVisibility(View.GONE);
		tv_spaction.setVisibility(View.GONE);
		tv_divider.setVisibility(View.GONE);
		ll_category.setVisibility(View.GONE);
		tv_spare.setVisibility(View.GONE);
	}

	private int getCategoryPos(String category, ArrayList<String> list) {
		return list.indexOf(category);
	}

	public void fetchCursor(){
		//String activityId = null;
		spareNameList = new ArrayList<String>();
		spareIdList = new ArrayList<String>();
		spareDataType = new ArrayList<String>();
		SerialListt = new ArrayList<String>();
		spareMaxQty = new ArrayList<String>();
		flag = new ArrayList<String>();
		userQty = new ArrayList<String>();
		org_Qty = new ArrayList<String>();
		userStatus = new ArrayList<String>();
		spareNameList.clear();
		spareNameList.add("Select Spare Parts");
		spareIdList.clear();
		spareIdList.add("-1");
		spareDataType.clear();
		spareDataType.add("I");
		SerialListt.clear();
		SerialListt.add("");
		spareMaxQty.clear();
		spareMaxQty.add("0");
		flag.clear();
		flag.add("F");
		userStatus.clear();
		userStatus.add("0");
		userQty.clear();
		userQty.add("");
		org_Qty.clear();
		org_Qty.add("");
		String categoryID =db.getInciParamId("405", sp_category.getSelectedItem()
				.toString().trim(),"654");
		if(categoryID.equalsIgnoreCase("R")){
			categoryID = "-1";
		}
		Cursor cursor = db.getSpareParts(0,categoryID);
		/*if (!typeId.equalsIgnoreCase("-1") && cursor != null && cursor.getCount()>0) {
			while (cursor.moveToNext()) {
				activityId = cursor.getString(cursor.getColumnIndex("ACTIVITY_ID"));
				if (!typeId.equalsIgnoreCase("-1") &&!activityId.equalsIgnoreCase("null") && !activityId.isEmpty()) {
					String arr[] = cursor.getString(cursor.getColumnIndex("ACTIVITY_ID")).split(",");
                    if(arr!=null && arr.length>0){
                    	for(int a = 0; a<arr.length; a++){
                    		if(arr[a].equalsIgnoreCase(typeId)) {
								spareNameList.add(cursor.getString(cursor.getColumnIndex("SpareName")));
								spareIdList.add(cursor.getString(cursor.getColumnIndex("ID")));
								spareDataType.add(cursor.getString(cursor.getColumnIndex("DataType")));
								SerialListt.add(cursor.getString(cursor.getColumnIndex("SerialNumber")));
								spareMaxQty.add(cursor.getString(cursor.getColumnIndex("MaxQty")));
								flag.add(cursor.getString(cursor.getColumnIndex("Flag")));
								userQty.add(cursor.getString(cursor.getColumnIndex("Qty")));
								org_Qty.add(cursor.getString(cursor.getColumnIndex("OrgQty")));
								userStatus.add(cursor.getString(cursor.getColumnIndex("Status")));
							}
						}
					}
				}
			}
		}else */
		if(cursor != null && cursor.getCount()>0){
			while (cursor.moveToNext()) {
				spareNameList.add( cursor.getString( cursor.getColumnIndex( "SpareName" ) ) );
				spareIdList.add( cursor.getString( cursor.getColumnIndex( "ID" ) ) );
				spareDataType.add( cursor.getString( cursor.getColumnIndex( "DataType" ) ) );
				SerialListt.add( cursor.getString( cursor.getColumnIndex( "SerialNumber" ) ) );
				spareMaxQty.add( cursor.getString( cursor.getColumnIndex( "MaxQty" ) ) );
				flag.add( cursor.getString( cursor.getColumnIndex( "Flag" ) ) );
				userQty.add( cursor.getString( cursor.getColumnIndex( "Qty" ) ) );
				org_Qty.add( cursor.getString( cursor.getColumnIndex( "OrgQty" ) ) );
				userStatus.add( cursor.getString( cursor.getColumnIndex( "Status" ) ) );
			}
		}
	}

	public void addDupSparePart(String msg) {
		final Dialog actvity_dialog = new Dialog(SparePart.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();
		Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
		Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
		tv_header.setTypeface( Utils.typeFace( SparePart.this ) );
		positive.setTypeface( Utils.typeFace( SparePart.this ) );
		negative.setTypeface( Utils.typeFace( SparePart.this ) );
		title.setTypeface( Utils.typeFace( SparePart.this ) );
		title.setText(msg);
		positive.setText("YES");
		negative.setText("NO");


		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				if(activityMode.equalsIgnoreCase("1")){
					mAppPreferences.setSpareSerialFlagScreen(1);
					db.close();
					finish();
				}else{
					buildSpareData("2");
				}
			}
		});

		negative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();

			}
		});
	}

	public void buildSpareData(String dupFlag){
		String categoryID =db.getInciParamId("405", sp_category.getSelectedItem()
				.toString().trim(),"654");
		if(categoryID.equalsIgnoreCase("R")){
			categoryID = "-1";
		}
		Cursor cursor = db.getSpareParts(1,categoryID);
		String submissionData="";
		if (cursor != null) {
			while (cursor.moveToNext()) {
				submissionData=submissionData+cursor.getString(cursor.getColumnIndex("ID"))+"$"+
						cursor.getString(cursor.getColumnIndex("Qty"))+"$"+
						cursor.getString(cursor.getColumnIndex("SerialNumber"))+"@";
			}
		}
		if (submissionData.length() > 0) {
			submissionData = submissionData.substring(0,
					submissionData.length() - 1);
		}
		if (Utils.isNetworkAvailable( SparePart.this)) {
			new SaveSpareParts( SparePart.this,submissionData,dupFlag).execute();
		} else {
			Utils.toast( SparePart.this, "17");// No internet connection
		}
	}

	public static int findDuplicates(List<EditText> listContainingDuplicates) {
		//final Set<EditText> setToReturn = new HashSet<EditText>();
		final Set<String> set1 = new HashSet<String>();
		int a=0;
		for (EditText yourInt : listContainingDuplicates) {

			if (yourInt.getText().toString().length()!=0 && !set1.add(yourInt.getText().toString().toUpperCase())) {
				a=1;
				break;
			}
		}
		return a;
	}

	public int etSum(EditText et){
		int sum1 = 0,sum2 = 0,sum=0;
		if(et.getText().toString().trim().contains( "." )){
			String input = "0"+et.getText().toString().trim()+"0";
			String[] data = input.split("\\.");
			//String[] data = et.getText().toString().trim().split("\\.");
			String before=data[0];
			int digits1 = Integer.parseInt(before);
			String after=data[1];
			int digits2 = Integer.parseInt(after);
			while (digits1 != 0) {
				int lastdigit = digits1 % 10;
				sum1 += lastdigit;
				digits1 /= 10;
			}

			while (digits2 != 0) {
				int lastdigit = digits2 % 10;
				sum2 += lastdigit;
				digits2 /= 10;
			}
			sum=sum1+sum2;
		}else{
			int digits = Integer.parseInt(et.getText().toString().trim());
			while (digits != 0) {
				int lastdigit = digits % 10;
				sum += lastdigit;
				digits /= 10;
			}
		}
		return sum;
	}

	public void alert(final String iddd,final String q,final String ser,String msg,final int delete_mode) {
		//delete_mode is 0 means spare part delete ,1 means serial no delete
		final Dialog alertDialog;
		alertDialog = new Dialog(SparePart.this, R.style.FullHeightDialog);
		alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		alertDialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		alertDialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = alertDialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		alertDialog.show();
		Button positive = (Button) alertDialog.findViewById( R.id.bt_ok );
		Button negative = (Button) alertDialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) alertDialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) alertDialog.findViewById( R.id.tv_header);

		positive.setTypeface( Utils.typeFace(SparePart.this));
		negative.setTypeface( Utils.typeFace(SparePart.this));
		title.setTypeface( Utils.typeFace(SparePart.this));
		tv_header.setTypeface( Utils.typeFace(SparePart.this));

		tv_header.setText( Utils.msg(SparePart.this, "508"));
		title.setText(msg);
		positive.setText( Utils.msg(SparePart.this, "63"));
		negative.setText( Utils.msg(SparePart.this, "64"));

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(delete_mode==1) {
					dialog_serialDetails.dismiss();
				}
				if(q.equalsIgnoreCase("0")){
					db.addSpareQty(iddd,"","","0","Select Category");
				}else{
					db.addSpareQty(iddd,q,ser,"1",sp_category.getSelectedItem().toString().trim());
					spareNumberDetails("",ser,iddd);
				}
				alertDialog.cancel();
				showSpare();
			}
		});
		negative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
			}
		});
	}
}
