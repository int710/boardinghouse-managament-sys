package com.ptpmud.quanlynhatro.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * Service quản lý kết nối Firebase Realtime Database
 *
 * @author PTPMUD
 */
public class FirebaseService {

    private static FirebaseService instance;
    private DatabaseReference databaseRef;
    private boolean initialized = false;

    private FirebaseService() {
        // Private constructor để singleton
    }

    /**
     * Lấy instance duy nhất của FirebaseService
     */
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    /**
     * Khởi tạo Firebase với file service account JSON
     *
     * @param serviceAccountPath Đường dẫn đến file JSON
     * @param databaseUrl URL của Firebase Realtime Database
     * @throws IOException Nếu file không tồn tại hoặc lỗi đọc file
     */
    public void initialize(String databaseUrl) throws IOException {
        if (initialized) {
            return;
        }
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/configFirebase.dat");
            if (is == null) {
                throw new java.io.FileNotFoundException("Không tìm thấy file config trong resources.");
            }

            byte[] encryptedData = is.readAllBytes();
            is.close();

            // 2. Cấu hình giải mã AES với Key của bạn (Phải khớp 100% với file mã hóa)
            String aesKey = "PTPMUD_KMA_BuiThanhQuan_CT070242";
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(aesKey.getBytes("UTF-8"), "AES");
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);

            // 3. Giải mã dữ liệu trong RAM
            byte[] decryptedJson = cipher.doFinal(encryptedData);

            // 4. Chuyển dữ liệu đã giải mã sang InputStream để khởi tạo Firebase
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(decryptedJson);

            FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(bais))
                        .setDatabaseUrl(databaseUrl)
                        .build();

            FirebaseApp.initializeApp(options);
            databaseRef = FirebaseDatabase.getInstance().getReference();
            initialized = true;

            System.out.println("Firebase đã được khởi tạo bảo mật từ Resource (config.dat)");

        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Firebase: " + e.getMessage());
            throw new IOException("Không thể khởi tạo Firebase bảo mật", e);
        }
    }

    /**
     * Lấy reference đến một node cụ thể trong database
     *
     * @param path Đường dẫn đến node (ví dụ: "chat_messages")
     * @return DatabaseReference
     */
    public DatabaseReference getReference(String path) {
        if (!initialized) {
            throw new IllegalStateException("Firebase chưa được khởi tạo. Hãy gọi initialize() trước.");
        }
        return databaseRef.child(path);
    }

    /**
     * Gửi một tin nhắn lên Firebase
     *
     * @param user Tên người gửi
     * @param content Nội dung tin nhắn
     */
    public void sendMessage(String user, String content) {
        if (!initialized) {
            System.err.println("Firebase chưa được khởi tạo!");
            return;
        }

        DatabaseReference messagesRef = databaseRef.child("chat_messages");

        // Tạo đối tượng tin nhắn
        ChatMessage message = new ChatMessage(user, content, System.currentTimeMillis());

        // Push tin nhắn lên Firebase (tự động tạo key unique)
        messagesRef.push().setValueAsync(message.toMap());
    }

    /**
     * Lắng nghe tin nhắn mới từ Firebase
     *
     * @param listener Callback khi có tin nhắn mới
     */
    public void listenForMessages(MessageListener listener) {
        if (!initialized) {
            System.err.println("Firebase chưa được khởi tạo!");
            return;
        }

        DatabaseReference messagesRef = databaseRef.child("chat_messages");

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Khi có tin nhắn mới được thêm vào
                try {
                    String user = snapshot.child("user").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    if (user != null && content != null && timestamp != null) {
                        ChatMessage message = new ChatMessage(user, content, timestamp);

                        // Cập nhật UI trong EDT thread
                        SwingUtilities.invokeLater(() -> {
                            listener.onMessageReceived(message);
                        });
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi xử lý tin nhắn: " + e.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Lỗi Firebase listener: " + error.getMessage());
            }
        });
    }

    /**
     * Lưu tin nhắn ghim (chỉ admin)
     */
    public void setPinnedMessage(String content) {
        if (!initialized) {
            System.err.println("Firebase chưa được khởi tạo!");
            return;
        }

        DatabaseReference pinnedRef = databaseRef.child("pinned_message");

        java.util.HashMap<String, Object> pinnedData = new java.util.HashMap<>();
        pinnedData.put("content", content);
        pinnedData.put("timestamp", System.currentTimeMillis());

        pinnedRef.setValueAsync(pinnedData);
    }

    /**
     * Xóa tin nhắn ghim
     */
    public void clearPinnedMessage() {
        if (!initialized) {
            return;
        }
        databaseRef.child("pinned_message").removeValueAsync();
    }

    /**
     * Lắng nghe thay đổi tin nhắn ghim
     */
    public void listenForPinnedMessage(PinnedMessageListener listener) {
        if (!initialized) {
            System.err.println("Firebase chưa được khởi tạo!");
            return;
        }

        DatabaseReference pinnedRef = databaseRef.child("pinned_message");

        pinnedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String content = snapshot.child("content").getValue(String.class);
                    if (content != null) {
                        SwingUtilities.invokeLater(() -> listener.onPinnedMessageChanged(content));
                    }
                } else {
                    SwingUtilities.invokeLater(() -> listener.onPinnedMessageChanged(null));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Lỗi pinned message listener: " + error.getMessage());
            }
        });
    }

    /**
     * Kiểm tra xem Firebase đã được khởi tạo chưa
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Interface callback cho tin nhắn mới
     */
    public interface MessageListener {

        void onMessageReceived(ChatMessage message);
    }

    /**
     * Interface callback cho tin nhắn ghim
     */
    public interface PinnedMessageListener {

        void onPinnedMessageChanged(String content);
    }

    /**
     * Class đại diện cho một tin nhắn
     */
    public static class ChatMessage {

        private String user;
        private String content;
        private long timestamp;

        public ChatMessage(String user, String content, long timestamp) {
            this.user = user;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getUser() {
            return user;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public java.util.Map<String, Object> toMap() {
            java.util.HashMap<String, Object> map = new java.util.HashMap<>();
            map.put("user", user);
            map.put("content", content);
            map.put("timestamp", timestamp);
            return map;
        }
    }
}
