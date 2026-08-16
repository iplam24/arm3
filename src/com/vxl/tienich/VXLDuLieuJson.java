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
        return getByte(khoa, (byte)0);
    }

    public byte getByte(String khoa, byte macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        try {
            return Byte.parseByte(o.toString());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public short getShort(String khoa) {
        return getShort(khoa, (short)0);
    }

    public short getShort(String khoa, short macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        try {
            return Short.parseShort(o.toString());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public int getInt(String khoa) {
        return getInt(khoa, 0);
    }

    public int getInt(String khoa, int macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public long getLong(String khoa) {
        return getLong(khoa, 0L);
    }

    public long getLong(String khoa, long macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        try {
            return Long.parseLong(o.toString());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public String getString(String khoa) {
        return getString(khoa, "");
    }

    public String getString(String khoa, String macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        return o.toString();
    }

    public boolean getBoolean(String khoa) {
        return getBoolean(khoa, false);
    }

    public boolean getBoolean(String khoa, boolean macDinh) {
        if (this.doiTuong == null) return macDinh;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return macDinh;
        try {
            return Boolean.parseBoolean(o.toString());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public JSONArray getJSONArray(String khoa) {
        if (this.doiTuong == null) return null;
        Object o = this.doiTuong.get(khoa);
        if (o == null) return null;
        try {
            if (o instanceof JSONArray) return (JSONArray)o;
            return (JSONArray) JSON.parse(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean containsKey(String khoa) {
        return this.doiTuong != null && this.doiTuong.containsKey(khoa);
    }

    public boolean containsValue(Object giaTri) {
        return this.doiTuong != null && this.doiTuong.containsValue(giaTri);
    }

    public boolean isEmpty() {
        return this.doiTuong == null || this.doiTuong.isEmpty();
    }
}
