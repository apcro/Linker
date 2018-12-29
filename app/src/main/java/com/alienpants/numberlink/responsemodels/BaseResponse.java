package com.alienpants.numberlink.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cro on 23/08/2017.
 */

public class BaseResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    public BaseResponse() {}

    public BaseResponse(Integer status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}
