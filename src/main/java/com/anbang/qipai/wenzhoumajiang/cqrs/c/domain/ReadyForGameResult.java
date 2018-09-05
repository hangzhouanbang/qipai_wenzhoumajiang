package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.pan.frame.PanActionFrame;

public class ReadyForGameResult {
	private MajiangGameValueObject majiangGame;
	private PanActionFrame firstActionFrame;

	public MajiangGameValueObject getMajiangGame() {
		return majiangGame;
	}

	public void setMajiangGame(MajiangGameValueObject majiangGame) {
		this.majiangGame = majiangGame;
	}

	public PanActionFrame getFirstActionFrame() {
		return firstActionFrame;
	}

	public void setFirstActionFrame(PanActionFrame firstActionFrame) {
		this.firstActionFrame = firstActionFrame;
	}

}
