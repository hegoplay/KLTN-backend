package iuh.fit.se.services.post_service.dto;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record PostCreateRequestDto(
    @NotBlank(message = "Tiêu đề không được để trống") String title,
    @NotBlank(message = "Nội dung không được để trống") String content,
    String featureImageName,
    @NotNull(message = "Trạng thái là bắt buộc") FunctionStatus status
) {}
