package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.List;

import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.position.MajiangPosition;
import com.dml.majiang.position.MajiangPositionUtil;

public class WenzhouMajiangPanResult extends PanResult {

	private String zhuangPlayerId;

	private boolean hu;

	private boolean zimo;

	private String dianpaoPlayerId;

	private List<WenzhouMajiangPanPlayerResult> playerResultList;

	@Override
	public List<String> allPlayerIds() {
		List<String> allPlayerIds = new ArrayList<>();
		playerResultList.forEach((playerResult) -> allPlayerIds.add(playerResult.getPlayerId()));
		return allPlayerIds;
	}

	@Override
	public String findZhuangPlayerId() {
		return zhuangPlayerId;
	}

	@Override
	public boolean ifPlayerHu(String playerId) {
		for (WenzhouMajiangPanPlayerResult playerResult : playerResultList) {
			if (playerResult.getPlayerId().equals(playerId)) {
				return playerResult.isHu();
			}
		}
		return false;
	}

	@Override
	public MajiangPosition playerMenFeng(String playerId) {
		for (WenzhouMajiangPanPlayerResult playerResult : playerResultList) {
			if (playerResult.getPlayerId().equals(playerId)) {
				return playerResult.getMenFeng();
			}
		}
		return null;
	}

	@Override
	public String findXiajiaPlayerId(String playerId) {

		MajiangPosition playerMenFeng = playerMenFeng(playerId);
		MajiangPosition xiajiaMenFeng = MajiangPositionUtil.nextPositionAntiClockwise(playerMenFeng);
		String xiajiaPlayerId = findPlayerIdByMenFeng(xiajiaMenFeng);
		while (xiajiaPlayerId == null) {
			xiajiaMenFeng = MajiangPositionUtil.nextPositionAntiClockwise(xiajiaMenFeng);
			xiajiaPlayerId = findPlayerIdByMenFeng(xiajiaMenFeng);
		}
		return xiajiaPlayerId;

	}

	private String findPlayerIdByMenFeng(MajiangPosition menFeng) {
		for (WenzhouMajiangPanPlayerResult playerResult : playerResultList) {
			if (playerResult.getMenFeng().equals(menFeng)) {
				return playerResult.getPlayerId();
			}
		}
		return null;
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

	public List<WenzhouMajiangPanPlayerResult> getPlayerResultList() {
		return playerResultList;
	}

	public void setPlayerResultList(List<WenzhouMajiangPanPlayerResult> playerResultList) {
		this.playerResultList = playerResultList;
	}

}
