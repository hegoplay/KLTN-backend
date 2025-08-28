package iuh.fit.se.entity.enumerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Vai trò và quyền của người tổ chức sự kiện")
public enum OrganizerRole {
	MODIFY,
	CHECK_IN,
	REGISTER,
	BAN,
	CODE,
}
