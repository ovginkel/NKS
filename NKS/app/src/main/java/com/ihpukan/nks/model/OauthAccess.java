package com.ihpukan.nks.model;

public class OauthAccess extends AbstractErrorModel {

    public String access_token;
    public String scope;

    @Override
    public String toString() {
        return "access_token: " + access_token
                + " ok: " + ok
                + " error: " + error
                + " warning: " + warning;
    }
}
