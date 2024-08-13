package com.arthurpv15.apimanagement.enums;

public enum OutgoingStatus {
    PAID(1),
    PENDING(2),
    SCHEDULED(3),
    LATE(4);

    private int code;

    OutgoingStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OutgoingStatus valueOfCode(int code){
        for (OutgoingStatus value : OutgoingStatus.values()){
            if(value.getCode() == code){
                return value;
            }
        }
        throw new IllegalArgumentException("Code invalid for status of the Outgoing");
    }
}