package com.anbang.qipai.wenzhoumajiang.msg.msjobj;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.WenzhouMajiangPanPlayerResultDbo;

public class WenzhouMajiangPanPlayerResultMO {
	private String playerId;// 玩家id
	private String nickname;// 玩家昵称
	private int score;// 一盘总分
	private boolean hu;

	public WenzhouMajiangPanPlayerResultMO(MajiangGamePlayerDbo gamePlayerDbo,
			WenzhouMajiangPanPlayerResultDbo panPlayerResult) {
		playerId = gamePlayerDbo.getPlayerId();
		nickname = gamePlayerDbo.getNickname();
		score = panPlayerResult.getPlayerResult().getScore();
		hu = panPlayerResult.getPlayer().getHu() != null;
	}

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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isHu() {
		return hu;
	}

	public void setHu(boolean hu) {
		this.hu = hu;
	}

}
