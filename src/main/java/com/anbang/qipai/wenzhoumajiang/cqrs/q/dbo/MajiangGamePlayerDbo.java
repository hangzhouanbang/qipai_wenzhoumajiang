package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerState;
import com.dml.mpgame.game.GamePlayerOnlineState;

public class MajiangGamePlayerDbo {
	private String playerId;
	private String nickname;
	private String headimgurl;
	private MajiangGamePlayerState state;
	private GamePlayerOnlineState onlineState;
	private int totalScore;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public MajiangGamePlayerState getState() {
		return state;
	}

	public void setState(MajiangGamePlayerState state) {
		this.state = state;
	}

	public GamePlayerOnlineState getOnlineState() {
		return onlineState;
	}

	public void setOnlineState(GamePlayerOnlineState onlineState) {
		this.onlineState = onlineState;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

}
