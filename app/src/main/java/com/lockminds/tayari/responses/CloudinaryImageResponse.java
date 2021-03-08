package com.lockminds.tayari.responses;

import java.util.Map;

public class CloudinaryImageResponse {
    String version_id,height,resource_type,etag,signature,url,secure_url,format,asset_id,public_id,original_file,placeholder,width,created_at,bytes,type;
    String tags;

    public CloudinaryImageResponse() {
    }

    public CloudinaryImageResponse(String version_id, String height, String resource_type, String etag, String signature, String url, String secure_url, String format, String asset_id, String public_id, String original_file, String placeholder, String width, String created_at, String bytes, String type, String tags) {
        this.version_id = version_id;
        this.height = height;
        this.resource_type = resource_type;
        this.etag = etag;
        this.signature = signature;
        this.url = url;
        this.secure_url = secure_url;
        this.format = format;
        this.asset_id = asset_id;
        this.public_id = public_id;
        this.original_file = original_file;
        this.placeholder = placeholder;
        this.width = width;
        this.created_at = created_at;
        this.bytes = bytes;
        this.type = type;
        this.tags = tags;
    }

    public CloudinaryImageResponse(Map result) {
        this.version_id = result.get("version_id").toString();
        this.height = result.get("height").toString();;
        this.resource_type = result.get("resource_type").toString();
        this.etag = result.get("etag").toString();
        this.signature = result.get("signature").toString();
        this.url = result.get("url").toString();
        this.secure_url = result.get("secure_url").toString();
        this.format = result.get("format").toString();
        this.asset_id = result.get("asset_id").toString();
        this.public_id = result.get("public_id").toString();
        this.placeholder = result.get("placeholder").toString();
        this.width = result.get("width").toString();
        this.bytes = result.get("bytes").toString();
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecure_url() {
        return secure_url;
    }

    public void setSecure_url(String secure_url) {
        this.secure_url = secure_url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public String getOriginal_file() {
        return original_file;
    }

    public void setOriginal_file(String original_file) {
        this.original_file = original_file;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
