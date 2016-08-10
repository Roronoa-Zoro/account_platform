package com.lp.account.client.enums;

public enum OperationType {

	Plus(1),
	Minus(2);
	private int type;

	private OperationType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
}
