package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanPlayerResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanResult;

public class PanResultDbo {
	private String id;
	private String gameId;
	private int panNo;
	private String zhuangPlayerId;
	private boolean hu;
	private boolean zimo;
	private String dianpaoPlayerId;
	private List<WenzhouMajiangPanPlayerResultDbo> playerResultList;
	private long finishTime;

	public PanResultDbo() {
	}

	public PanResultDbo(String gameId, WenzhouMajiangPanResult wenzhouMajiangPanResult) {
		this.gameId = gameId;
		panNo = wenzhouMajiangPanResult.getPan().getNo();
		zhuangPlayerId = wenzhouMajiangPanResult.findZhuangPlayerId();
		hu = wenzhouMajiangPanResult.isHu();
		zimo = wenzhouMajiangPanResult.isZimo();
		dianpaoPlayerId = wenzhouMajiangPanResult.getDianpaoPlayerId();
		playerResultList = new ArrayList<>();
		for (WenzhouMajiangPanPlayerResult playerResult : wenzhouMajiangPanResult.getPlayerResultList()) {
			WenzhouMajiangPanPlayerResultDbo dbo = new WenzhouMajiangPanPlayerResultDbo();
			dbo.setPlayerId(playerResult.getPlayerId());
			dbo.setPlayerResult(playerResult);
			dbo.setPlayer(wenzhouMajiangPanResult.findPlayer(playerResult.getPlayerId()));
			playerResultList.add(dbo);
		}

		finishTime = wenzhouMajiangPanResult.getPanFinishTime();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public String getZhuangPlayerId() {
		return zhuangPlayerId;
	}

	public void setZhuangPlayerId(String zhuangPlayerId) {
		this.zhuangPlayerId = zhuangPlayerId;
	}

	public boolean isHu() {
		return hu;
	}

	public void setHu(boolean hu) {
		this.hu = hu;
	}

	public boolean isZimo() {
		return zimo;
	}

	public void setZimo(boolean zimo) {
		this.zimo = zimo;
	}

	public String getDianpaoPlayerId() {
		return dianpaoPlayerId;
	}

	public void setDianpaoPlayerId(String dianpaoPlayerId) {
		this.dianpaoPlayerId = dianpaoPlayerId;
	}

	public List<WenzhouMajiangPanPlayerResultDbo> getPlayerResultList() {
		return playerResultList;
	}

	public void setPlayerResultList(List<WenzhouMajiangPanPlayerResultDbo> playerResultList) {
		this.playerResultList = playerResultList;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

}
