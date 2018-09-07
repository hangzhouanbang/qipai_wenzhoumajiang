package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

public class WenzhouMajiangJuPlayerResult {

	private String playerId;
	private int huCount;
	private int caishenCount;
	private int shuangfanCount;
	private int maxScore;
	private int totalScore;

	public void increaseHuCount() {
		huCount++;
	}

	public void increaseCaishenCount(int amount) {
		caishenCount += amount;
	}

	public void tryAndUpdateMaxScore(int score) {
		if (score > maxScore) {
			maxScore = score;
		}
	}

	public void increaseShuangfanCount() {
		shuangfanCount++;
	}

	public void increaseTotalScore(int amount) {
		totalScore += amount;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
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
