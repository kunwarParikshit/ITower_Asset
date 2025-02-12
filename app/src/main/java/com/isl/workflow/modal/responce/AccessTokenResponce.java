package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccessTokenResponce {
    @SerializedName("refresh_token_expires_in")
    @Expose
    private String refreshTokenExpiresIn;
    @SerializedName("api_product_list")
    @Expose
    private String apiProductList;
    @SerializedName("api_product_list_json")
    @Expose
    private List<String> apiProductListJson;
    @SerializedName("organization_name")
    @Expose
    private String organizationName;
    @SerializedName("developer.email")
    @Expose
    private String developerEmail;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("issued_at")
    @Expose
    private String issuedAt;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("application_name")
    @Expose
    private String applicationName;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("expires_in")
    @Expose
    private String expiresIn;
    @SerializedName("refresh_count")
    @Expose
    private String refreshCount;
    @SerializedName("status")
    @Expose
    private String status;

    public String getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(String refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String getApiProductList() {
        return apiProductList;
    }

    public void setApiProductList(String apiProductList) {
        this.apiProductList = apiProductList;
    }

    public List<String> getApiProductListJson() {
        return apiProductListJson;
    }

    public void setApiProductListJson(List<String> apiProductListJson) {
        this.apiProductListJson = apiProductListJson;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshCount() {
        return refreshCount;
    }

    public void setRefreshCount(String refreshCount) {
        this.refreshCount = refreshCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
