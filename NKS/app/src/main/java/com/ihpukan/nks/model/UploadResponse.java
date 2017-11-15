package com.ihpukan.nks.model;

public class UploadResponse extends AbstractErrorModel {

    public static String FU_RESPONSE = "FU_RESPONSE";
    public String file;

    @Override
    public String toString() {
        return String.valueOf(ok);
    }
}