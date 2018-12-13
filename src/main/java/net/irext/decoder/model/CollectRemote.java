package net.irext.decoder.model;

/**
 * Filename:       CollectRemote.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CollectRemote Model
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
public class CollectRemote {

    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String cityCode;
    private String cityName;
    private Integer operatorId;
    private String operatorName;
    private String remoteMap;
    private String protocol;
    private String remote;

    public CollectRemote(Integer id, Integer categoryId, String categoryName, Integer brandId, String brandName,
                         String cityCode, String cityName, Integer operatorId, String operatorName,
                         String remoteMap, String protocol, String remote) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.remoteMap = remoteMap;
        this.protocol = protocol;
        this.remote = remote;
    }

    public CollectRemote() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemoteMap() {
        return remoteMap;
    }

    public void setRemoteMap(String remoteMap) {
        this.remoteMap = remoteMap;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }
}
