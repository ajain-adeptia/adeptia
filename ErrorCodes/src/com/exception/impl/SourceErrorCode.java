package com.exception.impl;

import com.exception.ErrorCode;

public enum SourceErrorCode implements ErrorCode {

	ACCOUNT_CONFIGURATION_INCORRECT(101), NETWORK_ISSUE(102), FILE_SIZE_EXCEEDED(
			103);

	private final int number;

	private SourceErrorCode(int number) {
		this.number = number;
	}

	@Override
	public int getNumber() {
		return number;
	}

}
