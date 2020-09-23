package com.offcn.project.enums;


public enum ProjectInvoiceEnume {

    NO_FP("0", "不开发票"),
    HAVE_FP("1", "开发票");

    private String code;
    private String type;

    ProjectInvoiceEnume(String code, String type) {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
