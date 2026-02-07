package com.library.manG.System.com.library.system.DTO;

public class RESPONSE {

    private String status;
    private Object responseObject;

    public RESPONSE() {
    }

    public RESPONSE(String status, Object responseObject) {
        this.status = status;
        this.responseObject = responseObject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }

    @Override
    public String toString() {
        return "RESPONSE{" +
                "status='" + status + '\'' +
                ", responseObject=" + responseObject +
                '}';
    }
}
