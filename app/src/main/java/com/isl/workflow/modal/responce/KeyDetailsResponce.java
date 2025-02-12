package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyDetailsResponce {
    @SerializedName("getKeyDetailsResponse")
    @Expose
    private GetKeyDetailsResponse getKeyDetailsResponse;
 /*   @SerializedName("handOutKeyResponse")
    @Expose
    private String handOutKeyResponse;
    @SerializedName("updateKeyCylinderAuthorisationsResponse")
    @Expose
    private String updateKeyCylinderAuthorisationsResponse;*/

    public GetKeyDetailsResponse getGetKeyDetailsResponse() {
        return getKeyDetailsResponse;
    }

    public void setGetKeyDetailsResponse(GetKeyDetailsResponse getKeyDetailsResponse) {
        this.getKeyDetailsResponse = getKeyDetailsResponse;
    }
}
