package com.vxl.baomat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class VXLMaHoaMatKhau {
    private static final int DO_MANH_BCRYPT = 10;
    private static final int SO_BYTE_TOI_DA = 72;
    private static final Pattern MA_BCRYPT = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    private static final BCryptPasswordEncoder BO_MA_HOA = new BCryptPasswordEncoder(DO_MANH_BCRYPT);

    private VXLMaHoaMatKhau() {
    }

    public static String maHoa(String matKhau) {
        if (matKhau == null) {
            throw new IllegalArgumentException("Mật khẩu không được null.");
        }
        if (!coDoDaiHopLe(matKhau)) {
            throw new IllegalArgumentException("Mật khẩu BCrypt không được vượt quá 72 byte.");
        }
        return BO_MA_HOA.encode(matKhau);
    }

    public static boolean khop(String matKhau, String matKhauDaLuu) {
        if (matKhau == null || matKhauDaLuu == null || !coDoDaiHopLe(matKhau)) {
            return false;
        }
        if (laBCrypt(matKhauDaLuu)) {
            return BO_MA_HOA.matches(matKhau, matKhauDaLuu);
        }
        return MessageDigest.isEqual(
                matKhau.getBytes(StandardCharsets.UTF_8),
                matKhauDaLuu.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean coDoDaiHopLe(String matKhau) {
        return matKhau != null && matKhau.getBytes(StandardCharsets.UTF_8).length <= SO_BYTE_TOI_DA;
    }
    public static boolean canNangCap(String matKhauDaLuu) {
        return !laBCrypt(matKhauDaLuu) || BO_MA_HOA.upgradeEncoding(matKhauDaLuu);
    }

    public static boolean laBCrypt(String matKhauDaLuu) {
        return matKhauDaLuu != null && MA_BCRYPT.matcher(matKhauDaLuu).matches();
    }
}