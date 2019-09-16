package retrofit2;

import org.apache.commons.lang3.StringUtils;

public class HttpException extends RuntimeException {
    private final int code;
    private final String message;
    private final transient C3224Response<?> response;

    private static String getMessage(C3224Response<?> response2) {
        C3226Utils.checkNotNull(response2, "response == null");
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ");
        sb.append(response2.code());
        sb.append(StringUtils.SPACE);
        sb.append(response2.message());
        return sb.toString();
    }

    public HttpException(C3224Response<?> response2) {
        super(getMessage(response2));
        this.code = response2.code();
        this.message = response2.message();
        this.response = response2;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public C3224Response<?> response() {
        return this.response;
    }
}
