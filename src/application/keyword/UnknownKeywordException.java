package application.keyword;

public class UnknownKeywordException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownKeywordException() {}

	public UnknownKeywordException(String message) {
		super(message);
	}

	public UnknownKeywordException(Throwable cause) {
		super(cause);
	}

	public UnknownKeywordException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownKeywordException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
