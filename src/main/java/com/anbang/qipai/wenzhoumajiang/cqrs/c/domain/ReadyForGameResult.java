package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

public class ReadyForGameResult {
	private MajiangGameValueObject majiangGame;

	public MajiangGameValueObject getMajiangGame() {
		return majiangGame;
	}

	public void setMajiangGame(MajiangGameValueObject majiangGame) {
		this.majiangGame = majiangGame;
	}
}
