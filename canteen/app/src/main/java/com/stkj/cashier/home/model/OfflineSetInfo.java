package com.stkj.cashier.home.model;

public class OfflineSetInfo {
    private String F_Id;
    private String limitMoney;
    private int limitCount;
    private String machine_Number;
    private String F_CreatorTime;
    private String F_CompanyId;

    public OfflineSetInfo() {
    }

    public String getF_Id() {
        return F_Id;
    }

    public void setF_Id(String f_Id) {
        F_Id = f_Id;
    }

    public String getLimitMoney() {
        return limitMoney;
    }

    public void setLimitMoney(String limitMoney) {
        this.limitMoney = limitMoney;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public String getMachine_Number() {
        return machine_Number;
    }

    public void setMachine_Number(String machine_Number) {
        this.machine_Number = machine_Number;
    }

    public String getF_CreatorTime() {
        return F_CreatorTime;
    }

    public void setF_CreatorTime(String f_CreatorTime) {
        F_CreatorTime = f_CreatorTime;
    }

    public String getF_CompanyId() {
        return F_CompanyId;
    }

    public void setF_CompanyId(String f_CompanyId) {
        F_CompanyId = f_CompanyId;
    }

    @Override
    public String toString() {
        return "OfflineSetInfo{" +
                "F_Id='" + F_Id + '\'' +
                ", limitMoney='" + limitMoney + '\'' +
                ", limitCount=" + limitCount +
                ", machine_Number='" + machine_Number + '\'' +
                ", F_CreatorTime='" + F_CreatorTime + '\'' +
                ", F_CompanyId='" + F_CompanyId + '\'' +
                '}';
    }
}