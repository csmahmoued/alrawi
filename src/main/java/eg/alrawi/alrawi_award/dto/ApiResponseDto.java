package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ApiResponseDto<T> {
    private boolean status;
    private String message;
    private T responseBody;
    private List<String> errors;
    private LocalDateTime timestamp;


    public ApiResponseDto(boolean success, String message, T responseBody, List<String> errors) {
        this.status = success;
        this.message = message;
        this.responseBody = responseBody;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, message, data, null);
    }

    public static <T> ApiResponseDto<T> error(List<String> errors) {
        return new ApiResponseDto<>(false, "FAIL", null, errors);
    }

    public static <T> ApiResponseDto<T> businessException(List<String> errors) {
        return new ApiResponseDto<>(false, "FAIL", null, errors);
    }


}
