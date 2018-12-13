package net.irext.collector.model;

/**
 * Filename:       IRCode.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IR Code
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
public class IRCode {

    private int id;
    private String text;
    private String key;
    private String code;

    public IRCode(int id, String text, String key, String code) {
        this.id = id;
        this.text = text;
        this.key = key;
        this.code = code;
    }

    public IRCode() {

    }

    public void setText(String text) {
        this.text = text;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return this.text;
    }

    public String getKey() {
        return this.key;
    }

    public String getCode() {
        return this.code;
    }
}
