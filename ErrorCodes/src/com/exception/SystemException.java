package com.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class SystemException extends Exception {
	private static final long serialVersionUID = 1L;

	public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
		if (exception instanceof SystemException) {
			SystemException se = (SystemException) exception;
			if (errorCode != null && errorCode != se.getErrorCode()) {
				return new SystemException(exception.getMessage(), exception,
						errorCode);
			}
			return se;
		} else {
			return new SystemException(exception.getMessage(), exception,
					errorCode);
		}
	}

	public static SystemException wrap(Throwable exception) {
		return wrap(exception, null);
	}

	private ErrorCode errorCode;
	private final Map<String, Object> properties = new TreeMap<String, Object>();

	public SystemException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public SystemException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public SystemException(Throwable cause, ErrorCode errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public SystemException(String message, Throwable cause, ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public SystemException setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		return (T) properties.get(name);
	}

	public SystemException set(String name, Object value) {
		properties.put(name, value);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			printStackTrace(new PrintWriter(s));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			s.println(this);
			s.println("\t-------------------------------");
			if (errorCode != null) {
				s.println("\t" + errorCode + ":"
						+ errorCode.getClass().getName());
			}
			for (String key : properties.keySet()) {
				s.println("\t" + key + "=[" + properties.get(key) + "]");
			}
			s.println("\t-------------------------------");
			StackTraceElement[] trace = getStackTrace();
			for (int i = 0; i < trace.length; i++)
				s.println("\tat " + trace[i]);

			Throwable ourCause = getCause();
			if (ourCause != null) {
				ourCause.printStackTrace(s);
			}
			s.flush();
		}
	}

	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		String superMessage = super.getMessage();
		if (superMessage != null) {
			sb.append(superMessage);
			sb.append("\n");
		}
		for (String key : properties.keySet()) {
			sb.append("\t" + key + "=[" + properties.get(key) + "]");
		}
		return sb.toString();
	}
}
