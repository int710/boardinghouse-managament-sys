package com.ptpmud.quanlynhatro.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.nio.file.Path;

public class aesKey {

    // Key phải đúng 16 ký tự cho AES-128 hoặc 32 ký tự cho AES-256
    // Chuỗi dưới đây đúng 32 ký tự (AES-256)
    private static final String AES_KEY = "PTPMUD_KMA_BuiThanhQuan_CT070242"; 

    public static void main(String[] args) {
        try {
            // Kiểm tra file nguồn trước khi đọc
            Path inputPath = Paths.get("config/firebase-adminsdk.json");
            if (!Files.exists(inputPath)) {
                System.err.println("Lỗi: Không tìm thấy file tại " + inputPath.toAbsolutePath());
                return;
            }

            byte[] jsonBytes = Files.readAllBytes(inputPath);
            
            // Khởi tạo thuật toán
            SecretKeySpec secretKey = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(jsonBytes);
            
            // Đảm bảo thư mục resources tồn tại
            File resourceDir = new File("src/main/resources");
            if (!resourceDir.exists()) resourceDir.mkdirs();

            Files.write(Paths.get("src/main/resources/config.dat"), encryptedBytes);
            
            System.out.println(" Thành công! Đã tạo file mã hóa tại: src/main/resources/config.dat");
            System.out.println(" Độ dài Key: " + AES_KEY.length() + " bytes");
            
        } catch (Exception e) {
            System.err.println(" Lỗi mã hóa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}