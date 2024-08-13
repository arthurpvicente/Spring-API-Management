package com.arthurpv15.apimanagement.enums;

public enum IncomeStatus {
    RECEIVED(1),
    PENDING(2),
    SCHEDULED(3),
    LATE(4);

    private int code;

    IncomeStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IncomeStatus valueOfCode(int code){
        for(IncomeStatus value : values()){
            if(value.getCode() == code){
                return value;
            }
        }
        throw new IllegalArgumentException("Code invalid for status of the Income");
    }

}
