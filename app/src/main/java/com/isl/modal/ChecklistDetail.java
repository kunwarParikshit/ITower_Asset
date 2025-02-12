package com.isl.modal;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ChecklistDetail {

    private int id;
    private int prvId;
    private int preMaxImg;
    private int preMinImg;
    private int postMaxImg;
    private int postMinImg;
    private int preImgCounter;
    private int postImgCounter;
    private int lenBefore;
    private int lenAfter;
    private String isMadatory;
    // private boolean isSkipMadatory;
    private boolean isVisible;
    private String isRwMadatory;
    private boolean isRemarkMadatory;
    private char fieldType;
    private char fieldCat;
    private char rFlag;
    private char dataType;

    private String fieldName;

    private String visitorStatus;
    private String visitorRemarks;

    private String reviwerStatus;
    private String reviwerRemarks;

    private String dropDownValue;
    private String itemKey;
    private String selectedVal;
    private String grpName;
    private String oldBaseDataStatus;

    private Map<String, List<Integer>> childConditions;
    private List<Integer> visibleChildFields;
    private List<String> remarkMada;
    private TextView textView;
    private TextView tvDivider;
    private EditText editText;
    private Spinner spinner;
    private TextView tvRemarksLink;
    private TextView imageUrlLink;
    private EditText etViRemarks;
    private EditText etRiRemarks;
    private EditText etReviewRemarks;
    private EditText editText1;
    private EditText etValue;

    private Button prePhoto;
    private Button postPhoto;
    private RecyclerView pre_grid;
    private RecyclerView post_grid;
    private TextView capturePrePhoto;
    private TextView capturePostPhoto;


    //used in generate CSV file
    private List<String> preImgName;
    private List<String> postImgName;

    private List<String> preImageTagList;
    private List<String> postImageTagList;
    private List<String> preImageMediaList;
    private List<String> postImageMediaList;

    private List<MediaInfo> preMediaInfoList;
    private List<MediaInfo> postMediaInfoList;


    private String autoSumChecklistId;

    public String getAutoSumChecklistId() {
        return autoSumChecklistId;
    }

    public void setAutoSumChecklistId(String autoSumChecklistId) {
        this.autoSumChecklistId = autoSumChecklistId;
    }

    public String getReviwerStatus() {
        return reviwerStatus;
    }

    public void setReviwerStatus(String reviwerStatus) {
        this.reviwerStatus = reviwerStatus;
    }

    public String getReviwerRemarks() {
        return reviwerRemarks;
    }

    public void setReviwerRemarks(String reviwerRemarks) {
        this.reviwerRemarks = reviwerRemarks;
    }

    public String getVisitorRemarks() {
        return visitorRemarks;
    }

    public void setVisitorRemarks(String visitorRemarks) {
        this.visitorRemarks = visitorRemarks;
    }

    public String getVisitorStatus() {
        return visitorStatus;
    }

    public void setVisitorStatus(String visitorStatus) {
        this.visitorStatus = visitorStatus;
    }

    public List<String> getPreImgName() {
        if(this.preImgName == null){
            preImgName = new ArrayList<String>();
        }
        return preImgName;
    }

    public void setPreImgName(List<String> preImgName) {
        this.preImgName = preImgName;
    }

    public List<String> getPostImgName() {
        if(this.postImgName == null){
            postImgName = new ArrayList<String>();
        }
        return postImgName;
    }

    public void setPostImgName(List<String> postImgName) {
        this.postImgName = postImgName;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean enable) {
        isVisible = enable;
    }

    public String isMadatory() {
        return isMadatory;
    }
   /* public boolean isSkipMadatory() {
        return isSkipMadatory;
    }*/

    public boolean isRemarkMadatory() {
        return isRemarkMadatory;
    }

    public String isRwMadatory() {
        return isRwMadatory;
    }

    public void setMadatory(String madatory) {
        isMadatory = madatory;
    }

   /* public void setSkipMadatory(char madatory) {
        isSkipMadatory = madatory == 'S'?true:false;
    }*/

    public void setRemarkMadatory(char remarkMadatory) {
        isRemarkMadatory = remarkMadatory=='Y'?true:false;
    }

    public void setRwMadatory(String rwMadatory) {
        isRwMadatory = rwMadatory;
    }

    public TextView getImageUrlLink() {
        return imageUrlLink;
    }

    public void setImageUrlLink(TextView imageUrlLink) {
        this.imageUrlLink = imageUrlLink;
    }

    public TextView getTvDivider() {
        return tvDivider;
    }

    public void setTvDivider(TextView tvDivider) {
        this.tvDivider = tvDivider;
    }

    public int getPreImgCounter() {
        return preImgCounter;
    }

    public void setPreImgCounter(int preImgCounter) {
        this.preImgCounter = preImgCounter;
    }

    public void increasePreImgCounter(int counter) {
        this.preImgCounter = this.preImgCounter+counter;
    }

    public void increasePostImgCounter(int counter) {
        this.postImgCounter = this.postImgCounter+counter;
    }

    public int getPostImgCounter() {
        return postImgCounter;
    }

    public void setPostImgCounter(int postImgCounter) {
        this.postImgCounter = postImgCounter;
    }


    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public List<String> getPreMediaType() {
        return preImageMediaList;
    }

    public void setPreMediaType(String preImageMedia) {
        ArrayList list = new ArrayList<String>();
        String[] media = null;
        if(preImageMedia!=null){
            media = preImageMedia.split( "\\," );
            for(int a = 0; a<media.length;a++){
                int index = media[a].lastIndexOf("_");
                list.add(media[a].substring(index+1,media[a].length()).trim());
            }
        }
        this.preImageMediaList = list;
    }

    public List<String> getPrePhotoTag() {
        return preImageTagList;
    }

    public void setPrePhotoTag(String prePhotoTag) {
        ArrayList list = new ArrayList<String>();
        String[] tagMsg = null;
        if(prePhotoTag!=null){
            tagMsg = prePhotoTag.split( "\\," );
            for(int a = 0; a<tagMsg.length;a++){
                int index = tagMsg[a].lastIndexOf("_");
                list.add(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());
            }
        }
        this.preImageTagList = list;
    }


    public void setPreMediaInfoList(String prePhotoTag,String preImageMedia) {
        ArrayList list = new ArrayList<MediaInfo>();
        String[] tagMsg = null;
        String[] media = null;

        if(preImageMedia!=null){
            media = preImageMedia.split( "\\," );
        }

        if(prePhotoTag!=null){
            tagMsg = prePhotoTag.split( "\\," );

            for(int a = 0; a<tagMsg.length;a++){
                MediaInfo imgInfo = new MediaInfo();

                int index = tagMsg[a].lastIndexOf("_");
                imgInfo.setTag(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());

                if(media!=null && media.length >= tagMsg.length){
                    //imgInfo.setMediaType(media[a].substring(index+1,media[a].length()).trim());
                    imgInfo.setMediaType(media[a].trim());
                }else{
                    imgInfo.setMediaType("jpg");
                }
                imgInfo.setFlag(0);
                list.add(imgInfo);
                //list.add(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());
            }
        }
        this.preMediaInfoList = list;
    }


    public List<MediaInfo> getPreMediaInfoList() {
        return preMediaInfoList;
    }


    public void setPostMediaInfoList(String postPhotoTag,String postImageMedia) {
        ArrayList list = new ArrayList<MediaInfo>();
        String[] tagMsg = null;
        String[] media = null;

        if(postImageMedia!=null){
            media = postImageMedia.split( "\\," );
        }

        if(postPhotoTag!=null){
            tagMsg = postPhotoTag.split( "\\," );

            for(int a = 0; a<tagMsg.length;a++){
                MediaInfo imgInfo = new MediaInfo();

                int index = tagMsg[a].lastIndexOf("_");
                imgInfo.setTag(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());

                if(media!=null && media.length >= tagMsg.length){
                    //imgInfo.setMediaType(media[a].substring(index+1,media[a].length()).trim());
                    imgInfo.setMediaType(media[a].trim());
                }else{
                    imgInfo.setMediaType("jpg");
                }
                imgInfo.setFlag(0);
                list.add(imgInfo);
                //list.add(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());
            }
        }
        this.postMediaInfoList = list;
    }


    public List<MediaInfo> getPostMediaInfoList() {
        return postMediaInfoList;
    }



    public List<String> getPostMediaType() {
        return postImageMediaList;
    }

    public void setPostMediaType(String postImageMedia) {
        ArrayList list = new ArrayList<String>();
        String[] media = null;
        if(postImageMedia!=null){
            media = postImageMedia.split( "\\," );
            for(int a = 0; a<media.length;a++){
                int index = media[a].lastIndexOf("_");
                list.add(media[a].substring(index+1,media[a].length()).trim());
            }
        }
        this.postImageMediaList = list;
    }

    public List<String> getPostPhotoTag() {
        return postImageTagList;
    }

    public void setPostPhotoTag(String postPhotoTag) {
        ArrayList list = new ArrayList<String>();
        String[] tagMsg = null;
        if(postPhotoTag!=null){
            tagMsg = postPhotoTag.split( "\\," );
            for(int a = 0; a<tagMsg.length;a++){
                int index = tagMsg[a].lastIndexOf("_");
                list.add(tagMsg[a].substring(index+1,tagMsg[a].length()).trim());
            }
        }
        this.postImageTagList = list;
    }

    public TextView getCapturePrePhoto() {
        return capturePrePhoto;
    }

    public void setCapturePrePhoto(TextView capturePrePhoto) {
        this.capturePrePhoto = capturePrePhoto;
    }

    public TextView getCapturePostPhoto() {
        return capturePostPhoto;
    }

    public void setCapturePostPhoto(TextView capturePostPhoto) {
        this.capturePostPhoto = capturePostPhoto;
    }

    public void setPrePhotoConfig(String prePhotoConfig) {
        if(prePhotoConfig != null && prePhotoConfig.contains("~")){
            String[] arr = prePhotoConfig.split( "~" );
            this.preMinImg = Integer.parseInt(arr[0]);
            this.preMaxImg = Integer.parseInt(arr[1]);
        } else{
            this.preMinImg = -1;
            this.preMaxImg = -1;
        }
    }

    public void setPostPhotoConfig(String postPhotoConfig) {

        if(postPhotoConfig!=null && postPhotoConfig.contains("~")){
            String[] arr = postPhotoConfig.split( "~" );
            this.postMinImg = Integer.parseInt(arr[0]);
            this.postMaxImg = Integer.parseInt(arr[1]);
        } else{
            this.postMinImg = -1;
            this.postMaxImg = -1;
        }
    }

    public RecyclerView getPre_grid() {
        return pre_grid;
    }

    public void setPre_grid(RecyclerView pre_grid) {
        this.pre_grid = pre_grid;
    }

    public RecyclerView getPost_grid() {
        return post_grid;
    }

    public void setPost_grid(RecyclerView post_grid) {
        this.post_grid = post_grid;
    }

    public Button getPrePhoto() {
        return prePhoto;
    }

    public void setPrePhoto(Button prePhoto) {
        this.prePhoto = prePhoto;
    }

    public Button getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(Button postPhoto) {
        this.postPhoto = postPhoto;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getOldBaseDataStatus() {
        return oldBaseDataStatus;
    }

    public void setOldBaseDataStatus(String oldBaseDataStatus) {
        this.oldBaseDataStatus = oldBaseDataStatus;
    }

    public List<Integer> getVisibleChildFields() {
        if (visibleChildFields==null) {
            visibleChildFields = new ArrayList<Integer>();
        }
        return visibleChildFields;
    }

    public void setVisibleChildFields(List<Integer> visibleChildFields) {
        this.visibleChildFields = visibleChildFields;
    }

    public EditText getEtRiRemarks() {
        return etRiRemarks;
    }
    public void setEtRiRemarks(EditText etRiRemarks) {
        this.etRiRemarks = etRiRemarks;
    }

    public char getFieldType() {
        return fieldType;
    }
    public void setFieldType(char fieldType) {
        this.fieldType = fieldType;
    }

    public char getFieldCat() {
        return fieldCat;
    }
    public void setFieldCat(char fieldCat) {
        this.fieldCat = fieldCat;
    }

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getSelectedVal() {
        return selectedVal;
    }
    public void setSelectedVal(String selectedVal) {
        this.selectedVal = selectedVal;
    }

    public String getDropDownValue() {
        return dropDownValue;
    }

    public void setDropDownValue(String dropDownValue) {
        this.dropDownValue = dropDownValue;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getPrvId() {
        return prvId;
    }
    public void setPrvId(int id) {
        this.prvId = id;
    }

    public TextView getTextView() {
        return textView;
    }
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public EditText getEditText() {
        return editText;
    }
    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public EditText getEditText1() {
        return editText1;
    }
    public void setEditText1(EditText editText1) {
        this.editText1 = editText1;
    }

    public Spinner getSpinner() {
        return spinner;
    }
    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public Map<String, List<Integer>> getChildConditions() {
        return childConditions;
    }
    public void setChildIteams(Map<String, List<Integer>> childConditions) {
        this.childConditions = childConditions;
    }

    public TextView getTvRemarksLink() {
        return tvRemarksLink;
    }
    public void setTvRemarksLink(TextView tvRemarksLink) {
        this.tvRemarksLink = tvRemarksLink;
    }

    public char getrFlag() {
        return rFlag;
    }
    public void setrFlag(char rFlag) {
        this.rFlag = rFlag;
    }

    public EditText getEtViRemarks() {
        return etViRemarks;
    }
    public void setEtViRemarks(EditText etViRemarks) {
        this.etViRemarks = etViRemarks;
    }

    public EditText getEtReviewRemarks() {
        return etReviewRemarks;
    }
    public void setEtReviewRemarks(EditText etReviewRemarks) {
        this.etReviewRemarks = etReviewRemarks;
    }

    public EditText getEtValue() {
        return etValue;
    }
    public void setEtValue(EditText etValue) {
        this.etValue = etValue;
    }

    public List<String> getRemarkMada() {
        if(this.remarkMada == null){
            remarkMada = new ArrayList<String>();
        }
        return remarkMada;
    }

    public void setRemarkMada(String remarkMada) {
        System.out.println("remarkMada==="+remarkMada);
        if(remarkMada==null || "N".equals(remarkMada)){
            this.isRemarkMadatory = false;
        } else if("Y".equals(remarkMada)){
            this.isRemarkMadatory = true;
        } else{

            this.isRemarkMadatory = false;
            String [] arr = remarkMada.split(",");

            for (int i=0;i<arr.length;i++){
                String [] tarr = arr[i].split("~");
                if("Y".equals(tarr[1])){
                    getRemarkMada().add(tarr[0]);
                    this.isRemarkMadatory = true;
                }
            }
        }
    }

    public void setLength(String length) {

        if(length.indexOf(",") > -1)
        {
            String[] arr = length.split(",");
            lenAfter = Integer.parseInt( arr[1] );
            lenBefore = Integer.parseInt(arr[0]) - lenAfter;
        }
        else
        {
            lenBefore = Integer.parseInt(length);
        }
    }

    public int getLenAfter() {
        return lenAfter;
    }
    public void setLenAfter(int lenAfter) {
        this.lenAfter = lenAfter;
    }

    public int getLenBefore() {
        return lenBefore;
    }
    public void setLenBefore(int lenBefore) {
        this.lenBefore = lenBefore;
    }

    public char getDataType() {
        return dataType;
    }
    public void setDataType(char datType) {
        this.dataType = datType;
    }

    public int getPreMaxImg() {
        return preMaxImg;
    }
    public void setPreMaxImg(int preMaxImg) {
        this.preMaxImg = preMaxImg;
    }

    public int getPreMinImg() {
        return preMinImg;
    }
    public void setPreMinImg(int preMinImg) {
        this.preMaxImg = preMinImg;
    }

    public int getPostMaxImg() {
        return postMaxImg;
    }
    public void setPostMaxImg(int postMaxImg) {
        this.postMaxImg = postMaxImg;
    }

    public int getPostMinImg() {
        return postMinImg;
    }
    public void setPostMinImg(int postMinImg) {
        this.postMinImg = postMinImg;
    }
}
