package com.s.sdk.login.model;

public class UserGameInfo {
    String areaId;
    String roleId;
    String roleName;
    String roleLevel;


    public UserGameInfo(String areaId, String roleId, String roleName, String roleLevel) {
        this.areaId = areaId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleLevel = roleLevel;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleLevel() {
        return roleLevel;
    }
}
