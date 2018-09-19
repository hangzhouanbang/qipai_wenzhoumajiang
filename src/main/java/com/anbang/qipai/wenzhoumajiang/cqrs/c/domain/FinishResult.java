package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

public class FinishResult {
	private MajiangGameValueObject majiangGameValueObject;
	private WenzhouMajiangJuResult juResult;

	public MajiangGameValueObject getMajiangGameValueObject() {
		return majiangGameValueObject;
	}

	public void setMajiangGameValueObject(MajiangGameValueObject majiangGameValueObject) {
		this.majiangGameValueObject = majiangGameValueObject;
	}

	public WenzhouMajiangJuResult getJuResult() {
		return juResult;
	}

	public void setJuResult(WenzhouMajiangJuResult juResult) {
		this.juResult = juResult;
	}

}
