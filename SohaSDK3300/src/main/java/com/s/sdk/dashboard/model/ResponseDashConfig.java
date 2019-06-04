package com.s.sdk.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.s.sdk.base.BaseResponse;

import java.util.List;

public class ResponseDashConfig extends BaseResponse {
    @SerializedName("data")
    List<DashBoardItem> listData;

    public List<DashBoardItem> getListData() {
        return listData;
    }

}
