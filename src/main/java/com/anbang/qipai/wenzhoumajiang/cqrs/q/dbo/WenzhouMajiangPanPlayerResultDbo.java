package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanPlayerResult;
import com.dml.majiang.player.valueobj.MajiangPlayerValueObject;

public class WenzhouMajiangPanPlayerResultDbo {
	private String playerId;
	private WenzhouMajiangPanPlayerResult playerResult;
	private MajiangPlayerValueObject player;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public WenzhouMajiangPanPlayerResult getPlayerResult() {
		return playerResult;
	}

	public void setPlayerResult(WenzhouMajiangPanPlayerResult playerResult) {
		this.playerResult = playerResult;
	}

	public MajiangPlayerValueObject getPlayer() {
		return player;
	}

	public void setPlayer(MajiangPlayerValueObject player) {
		this.player = player;
	}

}
