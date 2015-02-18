package com.exception.impl;

import com.exception.ErrorCode;

public enum TargetErrorCode implements ErrorCode {

	ACCOUNT_CONFIGURATION_INCORRECT(201), NETWORK_ISSUE(202);

	private final int number;

	private TargetErrorCode(int number) {
		this.number = number;
	}

	@Override
	public int getNumber() {
		return number;
	}

}
