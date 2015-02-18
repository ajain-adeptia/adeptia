package com.test;

import java.util.ResourceBundle;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.exception.ErrorCode;
import com.exception.SystemException;
import com.exception.impl.SourceErrorCode;
import com.exception.impl.TargetErrorCode;

public class Test {

	public static void main(String[] args) throws SystemException {
		// TODO Auto-generated method stub
		try {
			throw new SystemException(
					SourceErrorCode.ACCOUNT_CONFIGURATION_INCORRECT);
		} catch (SystemException e) {
			if (e.getErrorCode() instanceof SourceErrorCode) {
				System.out.println("Source Error");
			} else if (e.getErrorCode() instanceof TargetErrorCode) {
				System.out.println("Target Error");
			}
			if (e.getErrorCode() == SourceErrorCode.ACCOUNT_CONFIGURATION_INCORRECT) {
				System.out.println("Account Information is incorrect");
			}
		}

		System.out
				.println(getUserText(SourceErrorCode.ACCOUNT_CONFIGURATION_INCORRECT));

		try {
			validate("user", "aman");
		} catch (SystemException e) {
			System.out.println(e.getMessage());
		}

		try {
			// throw new RuntimeException("Runtime Exception!!", new Exception(
			// "Exception!!", new Throwable("Throwable !!")));

			throw new RuntimeException("Runtime Exception!!", new Exception(
					"Exception!!", new SystemException("Source Throwable !!",
							null,
							SourceErrorCode.ACCOUNT_CONFIGURATION_INCORRECT)));
		} catch (Exception e) {
			Throwable root = ExceptionUtils.getRootCause(e);
			root.printStackTrace();
		}
	}

	private static final String USER = "admin";

	public static void validate(String field, String value)
			throws SystemException {
		if (value == null || !value.trim().equalsIgnoreCase(USER)) {
			throw new SystemException(
					SourceErrorCode.ACCOUNT_CONFIGURATION_INCORRECT)
					.set("field", field).set("value", value)
					.set("required-user", USER);
		}
	}

	public static String getUserText(ErrorCode errorCode) {
		if (errorCode == null) {
			return null;
		}
		String key = errorCode.getClass().getSimpleName() + "__" + errorCode;
		ResourceBundle bundle = ResourceBundle.getBundle("exceptions");
		return bundle.getString(key);
	}

}
