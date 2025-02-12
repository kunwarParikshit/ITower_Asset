package com.isl.audit.adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.audit.model.AttributeResult;
import com.isl.audit.model.ValueModel;
import com.isl.audit.util.DateUtil;
import com.isl.audit.util.ImageRemoveListener;
import com.isl.audit.util.ItemClickListener;
import com.isl.audit.util.MultiSpinner;
import com.isl.constant.AppConstants;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterAttributesItems extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageRemoveListener, MultiSpinner.MultiSpinnerListener {
    private final List<AttributeResult> attributesList;
    private List<String> attributesValueList;
    private final Activity activity;
    private final ItemClickListener itemClickListener;
    private Map<Integer, Integer> mSpinnerSelectedItem;
    private AdapterCapturedImages adapterCapturedImages;
    private boolean isViewOnly;
    private String imgBaseUrl;

    public AdapterAttributesItems(List<AttributeResult> attributesList, Activity activity, ItemClickListener itemClickListener, boolean isViewOnly, String imgBaseUrl) {
        this.activity = activity;
        this.imgBaseUrl = imgBaseUrl;
        this.attributesList = attributesList;
        this.itemClickListener = itemClickListener;
        this.isViewOnly = isViewOnly;
        attributesValueList = new ArrayList<>();
        mSpinnerSelectedItem = new HashMap<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.inflater_spinner_item) {
            return new SpinnerViewHolder(view);
        } else if (viewType == R.layout.inflater_number_item) {
            return new NumberrViewHolder(view);
        } else if (viewType == R.layout.inflater_text_item) {
            return new TextViewHolder(view);
        } else if (viewType == R.layout.inflater_calendar_item) {
            return new DateViewHolder(view);
        } else if (viewType == R.layout.inflater_image_item) {
            return new ImageViewHolder(view);
        } else if (viewType == R.layout.inflater_qr_item) {
            return new QRViewHolder(view);
        } else {
            return new DateViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case R.layout.inflater_calendar_item: {
               // attributesList.get(position).setFormat("DD-MMM-YYYY");
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                dateViewHolder.tvAttributeName.setText(attributesList.get(position).getName());
                dateViewHolder.calendarView.setVisibility(View.GONE);

                if(TextUtils.isEmpty(attributesList.get(position).getFormat())){
                    attributesList.get(position).setFormat(DateUtil.DATE_FORMAT2);
                }/*else if(attributesList.get(position).getFormat().contains("MMM")){
                    String format=attributesList.get(position).getFormat();
                    format=format.replace("MMM","MMMM");
                    format=format.replace("DD","dd");
                    format=format.replace("YYYY","yyyy");
                    attributesList.get(position).setFormat(format);
                }*/else{
                    String format=attributesList.get(position).getFormat();
                    format=format.replace("DD","dd");
                    format=format.replace("YYYY","yyyy");
                    attributesList.get(position).setFormat(format);
                }
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT2, Locale.ENGLISH);
                try{
                    sdf = new SimpleDateFormat(attributesList.get(position).getFormat(),Locale.ENGLISH);
                }catch (Exception e){
                    sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT2,Locale.ENGLISH);
                }

                if (!TextUtils.isEmpty(attributesList.get(position).getDate())) {
                    dateViewHolder.tvCalendar.setText(attributesList.get(position).getDate());
                    Date parsedDate = null;
                    try {
                        parsedDate = sdf.parse(attributesList.get(position).getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());
                    dateViewHolder.calendarView.setDate(timestamp.getTime());
                } else {
                    dateViewHolder.tvCalendar.setText("");
                }


                SimpleDateFormat finalSdf = sdf;
                dateViewHolder.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    String sDate = finalSdf.format(calendar.getTime());
                    dateViewHolder.tvCalendar.setText(sDate);
                    attributesList.get(position).setDate(sDate);
                    dateViewHolder.calendarView.setVisibility(View.GONE);
                    Log.d("Date-->", "sDate formatted: " + sDate);
                });
                /*if(TextUtils.isEmpty(attributesList.get(position).getDate())){
                    attributesList.get(position).setDate(sdf.format(Calendar.getInstance().getTime()));
                    Log.d("Date-->", "sDate formatted: " + sdf.format(Calendar.getInstance().getTime()));
                }*/
                dateViewHolder.tvCalendar.setOnClickListener(v -> {
                    if(isViewOnly){
                        return;
                    }
                    if (dateViewHolder.calendarView.getVisibility() == View.GONE) {
                        dateViewHolder.calendarView.setVisibility(View.VISIBLE);
                    } else {
                        dateViewHolder.calendarView.setVisibility(View.GONE);
                    }

                });

                break;
            }
            case R.layout.inflater_number_item: {
                NumberrViewHolder numberrViewHolder = (NumberrViewHolder) holder;
                numberrViewHolder.tvAttributeName.setText(attributesList.get(position).getName());

                if(isViewOnly){
                    numberrViewHolder.edtNumber.setEnabled(false);
                }else{
                    numberrViewHolder.edtNumber.setEnabled(true);
                }

                if (!TextUtils.isEmpty(attributesList.get(position).getNumValue())) {
                    numberrViewHolder.edtNumber.setText(attributesList.get(position).getNumValue());
                } else {
                    numberrViewHolder.edtNumber.setText("");
                }
                numberrViewHolder.edtNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null && !TextUtils.isEmpty(s.toString())) {
                            attributesList.get(position).setNumValue(s.toString());
                        } else {
                            attributesList.get(position).setNumValue("");
                        }
                    }
                });
                break;
            }
            case R.layout.inflater_text_item: {
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.tvAttributeName.setText(attributesList.get(position).getName());

                if(isViewOnly){
                    textViewHolder.edtText.setEnabled(false);
                }else{
                    textViewHolder.edtText.setEnabled(true);
                }

                if (!TextUtils.isEmpty(attributesList.get(position).getTextValue())) {
                    textViewHolder.edtText.setText(attributesList.get(position).getTextValue());
                } else {
                    textViewHolder.edtText.setText("");
                }
                textViewHolder.edtText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null && !TextUtils.isEmpty(s.toString())) {
                            attributesList.get(position).setTextValue(s.toString());
                        } else {
                            attributesList.get(position).setTextValue("");
                        }
                    }
                });
                break;
            }
            case R.layout.inflater_image_item: {
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.tvAttributeName.setText(attributesList.get(position).getName());
                String min = "";
                String max = "";
                String msg = "";
                if (!TextUtils.isEmpty(attributesList.get(position).getMin())) {
                    min = attributesList.get(position).getMin();
                }
                if (!TextUtils.isEmpty(attributesList.get(position).getMax())) {
                    max = attributesList.get(position).getMax();
                    if(attributesList.get(position).getValues() != null && attributesList.get(position).getValues().size()>0){
                        if(Integer.parseInt(max)!=attributesList.get(position).getValues().size()){
                            max=String.valueOf(attributesList.get(position).getValues().size());
                        }
                    }
                }
                if(Integer.parseInt(min) > Integer.parseInt(max)){
                    min=max;
                }
                if (!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max)) {
                    msg = activity.getResources().getString(R.string.capture_min) + " " + min + " & " + activity.getResources().getString(R.string.max) + " " + max + activity.getResources().getString(R.string.images);
                } else if (TextUtils.isEmpty(min) && !TextUtils.isEmpty(max)) {
                    msg = activity.getResources().getString(R.string.capture_max) + " " + max + activity.getResources().getString(R.string.images);
                } else if (!TextUtils.isEmpty(min) && TextUtils.isEmpty(max)) {
                    msg = activity.getResources().getString(R.string.capture_min) + " " + min + activity.getResources().getString(R.string.images);
                } else {
                    msg = activity.getResources().getString(R.string.capture_images);
                }
                imageViewHolder.tvCapture.setText(msg);
                imageViewHolder.ivClick.setOnClickListener(view -> {
                    if(isViewOnly){
                        return;
                    }
                    itemClickListener.onItemClickListener(imageViewHolder.ivClick, position);
                });
                if (attributesList.get(position).getImgUrl() != null && attributesList.get(position).getImgUrl().size() > 0) {
                    imageViewHolder.rvImages.setVisibility(View.VISIBLE);
                    adapterCapturedImages = new AdapterCapturedImages(attributesList.get(position).getImgUrl(), activity, this, position,isViewOnly,imgBaseUrl);
                    imageViewHolder.rvImages.setAdapter(adapterCapturedImages);
                    imageViewHolder.rvImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                } else {
                    imageViewHolder.rvImages.setVisibility(View.GONE);
                }
                break;
            }
            case R.layout.inflater_spinner_item: {
                SpinnerViewHolder spinnerViewHolder = (SpinnerViewHolder) holder;
                spinnerViewHolder.tvAttributeName.setText(attributesList.get(position).getName());

                if(isViewOnly){
                    spinnerViewHolder.spinnerAttribute.setEnabled(false);
                }else{
                    spinnerViewHolder.spinnerAttribute.setEnabled(true);
                }

                if (TextUtils.isEmpty(attributesList.get(position).getSelection()) || attributesList.get(position).getSelection().equals("Single Select")) {
                    if (attributesList.get(position).getValues() != null && attributesList.get(position).getValues().size() > 0) {
                        attributesValueList = new ArrayList<>();
                        attributesValueList.add(0, activity.getResources().getString(R.string.select));
                        for (ValueModel valueModel : attributesList.get(position).getValues()) {
                            attributesValueList.add(valueModel.getAttributeValue());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, attributesValueList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerViewHolder.spinnerAttribute.setAdapter(adapter);
                        if (attributesList.get(position).getSelectedValueList() != null && attributesList.get(position).getSelectedValueList().size() > 0) {
                            int selection = 0;
                            for (int i = 0; i < attributesValueList.size(); i++) {
                                if (attributesValueList.get(i).equals(attributesList.get(position).getSelectedValueList().get(0))) {
                                    selection = i;
                                    break;
                                }
                            }
                            spinnerViewHolder.spinnerAttribute.setSelection(selection);
                        }

                    }
                    spinnerViewHolder.spinnerAttribute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            List<String> selectedList = new ArrayList<>();
                            if (pos > 0) {
                                mSpinnerSelectedItem.put(position, pos);
                                selectedList.add(attributesList.get(position).getValues().get(pos - 1).getAttributeValue());

                            } else {
                                if (mSpinnerSelectedItem.containsKey(position)) {
                                    mSpinnerSelectedItem.remove(position);
                                }
                                selectedList = new ArrayList<>();
                            }
                            attributesList.get(position).setSelectedValueList(selectedList);

                            //Toast.makeText(activity, attributesList.get(position).getValues().get(pos).getAttributeValue(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    if (mSpinnerSelectedItem.containsKey(position)) {
                        spinnerViewHolder.spinnerAttribute.setSelection(mSpinnerSelectedItem.get(position));
                    }
                    spinnerViewHolder.multiSpinner.setVisibility(View.GONE);
                    spinnerViewHolder.spinnerAttribute.setVisibility(View.VISIBLE);
                } else {
                    spinnerViewHolder.multiSpinner.setVisibility(View.VISIBLE);
                    spinnerViewHolder.spinnerAttribute.setVisibility(View.GONE);
                    attributesValueList = new ArrayList<>();
                    for (ValueModel valueModel : attributesList.get(position).getValues()) {
                        attributesValueList.add(valueModel.getAttributeValue());
                    }
                    if (attributesList.get(position).getSelectedValueList() == null || attributesList.get(position).getSelectedValueList().size() == 0) {
                        spinnerViewHolder.multiSpinner.setItems(attributesValueList, activity.getResources().getString(R.string.select),
                                this::onItemsSelected, position, new ArrayList<String>());
                    } else {
                        spinnerViewHolder.multiSpinner.setItems(attributesValueList, activity.getResources().getString(R.string.select),
                                this::onItemsSelected, position, attributesList.get(position).getSelectedValueList());
                    }

                }


                break;
            }
            case R.layout.inflater_qr_item: {
                QRViewHolder viewHolder = (QRViewHolder) holder;
                viewHolder.tvAttributeName.setText(attributesList.get(position).getName());


                if (!TextUtils.isEmpty(attributesList.get(position).getQrValue())) {
                    viewHolder.tvQrValue.setText(attributesList.get(position).getQrValue());
                }
                viewHolder.ivCapture.setOnClickListener(view -> {
                    if(isViewOnly){
                        return;
                    }
                    itemClickListener.onItemClickListener(viewHolder.ivCapture, position);
                });
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (attributesList.get(position).getType()) {
            case AppConstants.ATTRIBUTE_TYPE.TEXT:
                return R.layout.inflater_text_item;
            case AppConstants.ATTRIBUTE_TYPE.NUMBER:
                return R.layout.inflater_number_item;
            case AppConstants.ATTRIBUTE_TYPE.PICKLIST:
                return R.layout.inflater_spinner_item;
            case AppConstants.ATTRIBUTE_TYPE.IMAGE:
                return R.layout.inflater_image_item;
            case AppConstants.ATTRIBUTE_TYPE.DATE:
                return R.layout.inflater_calendar_item;
            case AppConstants.ATTRIBUTE_TYPE.QR_CODE:
                return R.layout.inflater_qr_item;
            default:
                return R.layout.inflater_text_item;
        }

    }

    @Override
    public int getItemCount() {
        return attributesList.size();
    }


    @Override
    public void onImageRemoveListener(View view, int position, int attributePos) {
        if (view.getId() == R.id.iv_remove) {
            if(attributesList.get(attributePos).getImgUrl() != null &&
                    attributesList.get(attributePos).getImgUrl().size()>position){
                attributesList.get(attributePos).getImgUrl().remove(position);
                adapterCapturedImages.notifyDataSetChanged();
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onItemsSelected(boolean[] selected, int pos) {
        List<String> selectedList = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedList.add(attributesList.get(pos).getValues().get(i).getAttributeValue());
            }
        }
        attributesList.get(pos).setSelectedValueList(selectedList);
    }

    public static class SpinnerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.spinner_attribute)
        Spinner spinnerAttribute;
        @BindView(R.id.multi_spinner)
        MultiSpinner multiSpinner;
        @BindView(R.id.view_spinner)
        View viewSpinner;

        public SpinnerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class NumberrViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.edt_number)
        EditText edtNumber;

        public NumberrViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.edt_text)
        EditText edtText;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.calendar_view)
        CalendarView calendarView;
        @BindView(R.id.tv_calendar)
        TextView tvCalendar;

        public DateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.tv_capture)
        TextView tvCapture;
        @BindView(R.id.iv_click)
        ImageView ivClick;
        @BindView(R.id.rv_images)
        RecyclerView rvImages;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class QRViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attribute_name)
        TextView tvAttributeName;
        @BindView(R.id.tv_qr_value)
        TextView tvQrValue;
        @BindView(R.id.iv_capture)
        ImageView ivCapture;

        public QRViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}