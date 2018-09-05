package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangJuPlayerResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerDbo;

public class WenzhouMajiangJuPlayerResultVO {

	private String playerId;
	private String nickname;
	private String headimgurl;
	private int huCount;
	private int caishenCount;
	private int dapaoCount;
	private int maxHushu;
	private int totalScore;

	public WenzhouMajiangJuPlayerResultVO(WenzhouMajiangJuPlayerResult juPlayerResult,
			MajiangGamePlayerDbo majiangGamePlayerDbo) {
		playerId = majiangGamePlayerDbo.getPlayerId();
		nickname = majiangGamePlayerDbo.getNickname();
		headimgurl = majiangGamePlayerDbo.getHeadimgurl();
		huCount = juPlayerResult.getHuCount();
		caishenCount = juPlayerResult.getCaishenCount();
		dapaoCount = juPlayerResult.getDapaoCount();
		maxHushu = juPlayerResult.getMaxHushu();
		totalScore = juPlayerResult.getTotalScore();
	}

	public WenzhouMajiangJuPlayerResultVO(MajiangGamePlayerDbo majiangGamePlayerDbo) {
		playerId = majiangGamePlayerDbo.getPlayerId();
		nickname = majiangGamePlayerDbo.getNickname();
		headimgurl = majiangGamePlayerDbo.getHeadimgurl();
		huCount = 0;
		caishenCount = 0;
		dapaoCount = 0;
		maxHushu = 0;
		totalScore = 0;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public int getHuCount() {
		return huCount;
	}

	public int getCaishenCount() {
		return caishenCount;
	}

	public int getDapaoCount() {
		return dapaoCount;
	}

	public int getMaxHushu() {
		return maxHushu;
	}

	public int getTotalScore() {
		return totalScore;
	}

}
