package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;

import com.dml.majiang.pan.result.PanResult;

public class WenzhouMajiangPanResult extends PanResult {

	private boolean hu;

	private boolean zimo;

	private String dianpaoPlayerId;

	private List<WenzhouMajiangPanPlayerResult> playerResultList;

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

	public List<WenzhouMajiangPanPlayerResult> getPlayerResultList() {
		return playerResultList;
	}

	public void setPlayerResultList(List<WenzhouMajiangPanPlayerResult> playerResultList) {
		this.playerResultList = playerResultList;
	}

}
