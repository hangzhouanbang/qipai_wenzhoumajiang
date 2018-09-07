package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangJuPlayerResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerDbo;

public class WenzhouMajiangJuPlayerResultVO {

	private String playerId;
	private String nickname;
	private String headimgurl;
	private int huCount;
	private int caishenCount;
	private int shuangfanCount;
	private int maxScore;
	private int totalScore;

	public WenzhouMajiangJuPlayerResultVO(WenzhouMajiangJuPlayerResult juPlayerResult,
			MajiangGamePlayerDbo majiangGamePlayerDbo) {
		playerId = majiangGamePlayerDbo.getPlayerId();
		nickname = majiangGamePlayerDbo.getNickname();
		headimgurl = majiangGamePlayerDbo.getHeadimgurl();
		huCount = juPlayerResult.getHuCount();
		caishenCount = juPlayerResult.getCaishenCount();
		shuangfanCount = juPlayerResult.getShuangfanCount();
		maxScore = juPlayerResult.getMaxScore();
		totalScore = juPlayerResult.getTotalScore();
	}

	public WenzhouMajiangJuPlayerResultVO(MajiangGamePlayerDbo majiangGamePlayerDbo) {
		playerId = majiangGamePlayerDbo.getPlayerId();
		nickname = majiangGamePlayerDbo.getNickname();
		headimgurl = majiangGamePlayerDbo.getHeadimgurl();
		huCount = 0;
		caishenCount = 0;
		shuangfanCount = 0;
		maxScore = 0;
		totalScore = 0;
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

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public int getHuCount() {
		return huCount;
	}

	public void setHuCount(int huCount) {
		this.huCount = huCount;
	}

	public int getCaishenCount() {
		return caishenCount;
	}

	public void setCaishenCount(int caishenCount) {
		this.caishenCount = caishenCount;
	}

	public int getShuangfanCount() {
		return shuangfanCount;
	}

	public void setShuangfanCount(int shuangfanCount) {
		this.shuangfanCount = shuangfanCount;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
}
