package iuh.fit.se.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "global_configurations", // Đổi tên cho tổng quát
    uniqueConstraints = @UniqueConstraint(columnNames = {"configKey"})
)
@Data // Thay thế cho @FieldDefaults, tự tạo getter/setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfiguration {

    public static final String KEY_LAST_RESET_POINT_TIME = "LAST_RESET_POINT_TIME";
//    public static final String KEY_LAST_REPORT_TIME = "LAST_REPORT_TIME"

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String configKey; // Tên của cấu hình (ví dụ: "LAST_RESET_POINT_TIME")

    // Sử dụng kiểu String để lưu trữ linh hoạt nhiều loại giá trị
    private String configValue;

    // Có thể thêm trường mô tả
    private String description;

    // Phương thức tiện ích để lấy giá trị dưới dạng LocalDateTime
    public LocalDateTime getConfigValueAsDateTime() {
        return configValue != null ? LocalDateTime.parse(configValue) : null;
    }

    // Phương thức tiện ích để set giá trị từ LocalDateTime
    public void setConfigValueFromDateTime(LocalDateTime dateTime) {
        this.configValue = dateTime != null ? dateTime.toString() : null;
    }
}