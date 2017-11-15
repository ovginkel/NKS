package com.ihpukan.nks.model;

import java.util.List;

public class IMWrapper extends AbstractErrorModel {

    public List<IM> ims;

    @Override
    public String toString() {
        return ims != null ? "ims: " + ims.size() : "not found ims";
    }
}
