package com.vxl.tienich;

// Vũ Xuân Lâm đẹp trai VCL
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public final class VXLDuLieuJson {
    private final JSONObject doiTuong;

    public VXLDuLieuJson(JSONObject json) {
        this.doiTuong = json;
    }

    public byte getByte(String khoa) {
        return Byte.parseByte(this.doiTuong.get(khoa).toString());
    }

    public short getShort(String khoa) {
        return Short.parseShort(this.doiTuong.get(khoa).toString());
    }

    public int getInt(String khoa) {
        return Integer.parseInt(this.doiTuong.get(khoa).toString());
    }

    public long getLong(String khoa) {
        return Long.parseLong(this.doiTuong.get(khoa).toString());
    }

    public String getString(String khoa) {
        return this.doiTuong.get(khoa).toString();
    }

    public boolean getBoolean(String khoa) {
        return Boolean.parseBoolean(this.doiTuong.get(khoa).toString());
    }

    public JSONArray getJSONArray(String khoa) {
        return (JSONArray) JSON.parse(this.doiTuong.get(khoa).toString());
    }

    public boolean containsKey(String khoa) {
        return this.doiTuong.containsKey(khoa);
    }

    public boolean containsValue(String khoa) {
        return this.doiTuong.containsValue(khoa);
    }

    public boolean isEmpty() {
        return this.doiTuong.isEmpty();
    }

}

