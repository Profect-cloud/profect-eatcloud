package profect.eatcloud.common;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import profect.eatcloud.Domain.Admin.exception.AdminException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
	public ResponseEntity<String> handleBadRequest(Exception e) {
		return ResponseEntity
			.badRequest()
			.body(e.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(e.getMessage());
	}

	@ExceptionHandler(AdminException.class)
	public ResponseEntity<ApiResponse<Void>> handleAdminException(AdminException ex) {
		// ex.getErrorCode().getCode() 는 비즈니스 코드; HTTP 상태 코드는 ApiResponseStatus.NOT_FOUND 같은 걸로 매핑
		ApiResponseStatus status = switch (ex.getErrorCode()) {
			case ADMIN_NOT_FOUND -> ApiResponseStatus.NOT_FOUND;
			case EMAIL_ALREADY_EXISTS -> ApiResponseStatus.BAD_REQUEST;
			default -> ApiResponseStatus.INTERNAL_ERROR;
		};
		ApiResponse<Void> body = ApiResponse.of(status, null);
		return ResponseEntity
			.status(status.getHttpStatus())
			.body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleInternalError(Exception e) {
		return ResponseEntity
			.internalServerError()
			.body(e.getMessage());
	}
}
