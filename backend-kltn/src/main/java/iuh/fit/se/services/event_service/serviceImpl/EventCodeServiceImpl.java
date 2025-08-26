package iuh.fit.se.services.event_service.serviceImpl;

import java.time.Duration;
import java.util.Random;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import iuh.fit.se.services.event_service.service.EventCodeService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EventCodeServiceImpl implements EventCodeService {

	private final RedissonClient redissonClient;
	private static final String EVENT_CODE_KEY_PREFIX = "event:code:";

	@Override
	public String generateOrUpdateEventCode(String eventId) {
		// 1. Tạo mã code (ví dụ: 6 chữ số)
		String eventCode = generateNumericCode(6);

		// 2. Lưu mã code vào Redis, gắn với eventId
		String codeKey = getCodeKey(eventId);
		RBucket<String> codeBucket = redissonClient.getBucket(codeKey);
		codeBucket.set(eventCode, Duration.ofMinutes(10)); // Code có hiệu lực
															// trong 24h

		log.info("Generated code for event {}: {}", eventId, eventCode);
		return eventCode;
	}

	@Override
	public String getCurrentEventCode(String eventId) {
		String codeKey = getCodeKey(eventId);
		RBucket<String> codeBucket = redissonClient.getBucket(codeKey);
		String currentCode = codeBucket.get();

		if (currentCode == null) {
			throw new RuntimeException(
				"Event chưa được tạo mã code. Vui lòng tạo mã trước.");
		}
		return currentCode;
	}

	@Override
	public boolean verifyEventCode(String eventId, String enteredCode) {
		String currentCode = getCurrentEventCode(eventId);

		// So sánh mã code (không phân biệt hoa thường nếu cần)
		boolean isValid = currentCode.equals(enteredCode.trim());

		if (isValid) {
			log.info("Code verified successfully for event {}", eventId);
		} else {
			log
				.warn(
					"Failed code attempt for event {}. Entered: {}, Expected: {}",
					eventId, enteredCode, currentCode);
		}
		return isValid;
	}

	@Override
	public void disableEventCode(String eventId) {
		String codeKey = getCodeKey(eventId);
        redissonClient.getBucket(codeKey).delete();
        log.info("Disabled code for event {}", eventId);
	}

	@Override
	public boolean hasEventCode(String eventId) {
		String codeKey = getCodeKey(eventId);
        return redissonClient.getBucket(codeKey).isExists();
	}

	// Hàm tiện ích tạo mã số
	private String generateNumericCode(int length) {
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < length; i++) {
			code.append(random.nextInt(10)); // Random số từ 0-9
		}
		return code.toString();
	}

	// Hàm tiện ích tạo mã alphanumeric (chữ và số) - Tùy chọn
	@SuppressWarnings("unused")
	private String generateAlphanumericCode(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < length; i++) {
			code.append(characters.charAt(random.nextInt(characters.length())));
		}
		return code.toString();
	}

	private String getCodeKey(String eventId) {
		return EVENT_CODE_KEY_PREFIX + eventId;
	}

}
