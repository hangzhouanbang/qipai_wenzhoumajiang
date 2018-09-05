package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.pan.frame.PanActionFrame;

public class MajiangActionResult {

	private MajiangGameValueObject majiangGame;
	private PanActionFrame panActionFrame;
	private WenzhouMajiangPanResult panResult;
	private WenzhouMajiangJuResult juResult;

	public MajiangGameValueObject getMajiangGame() {
		return majiangGame;
	}

	public void setMajiangGame(MajiangGameValueObject majiangGame) {
		this.majiangGame = majiangGame;
	}

	public PanActionFrame getPanActionFrame() {
		return panActionFrame;
	}

	public void setPanActionFrame(PanActionFrame panActionFrame) {
		this.panActionFrame = panActionFrame;
	}

	public WenzhouMajiangPanResult getPanResult() {
		return panResult;
	}

	public void setPanResult(WenzhouMajiangPanResult panResult) {
		this.panResult = panResult;
	}

	public WenzhouMajiangJuResult getJuResult() {
		return juResult;
	}

	public void setJuResult(WenzhouMajiangJuResult juResult) {
		this.juResult = juResult;
	}

}
