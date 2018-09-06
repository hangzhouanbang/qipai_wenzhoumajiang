package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.result.CurrentPanResultBuilder;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.MajiangPlayer;

public class WenzhouMajiangPanResultBuilder implements CurrentPanResultBuilder {
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap;
	private boolean jinjie;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;

	@Override
	public PanResult buildCurrentPanResult(Ju ju, long panFinishTime) {
		Pan currentPan = ju.getCurrentPan();
		WenzhouMajiangPanResult latestFinishedPanResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
		Map<String, Integer> playerTotalScoreMap = new HashMap<>();
		if (latestFinishedPanResult != null) {
			for (WenzhouMajiangPanPlayerResult panPlayerResult : latestFinishedPanResult.getPlayerResultList()) {
				playerTotalScoreMap.put(panPlayerResult.getPlayerId(), panPlayerResult.getTotalScore());
			}
		}

		MajiangPlayer huPlayer = currentPan.findHuPlayer();

		List<String> playerIdList = currentPan.sortedPlayerIdList();
		List<WenzhouMajiangPanPlayerResult> playerResultList = new ArrayList<>();

		return null;

	}

	public boolean isJinjie() {
		return jinjie;
	}

	public void setJinjie(boolean jinjie) {
		this.jinjie = jinjie;
	}

	public boolean isTeshushuangfan() {
		return teshushuangfan;
	}

	public void setTeshushuangfan(boolean teshushuangfan) {
		this.teshushuangfan = teshushuangfan;
	}

	public boolean isCaishenqian() {
		return caishenqian;
	}

	public void setCaishenqian(boolean caishenqian) {
		this.caishenqian = caishenqian;
	}

	public boolean isShaozhongfa() {
		return shaozhongfa;
	}

	public void setShaozhongfa(boolean shaozhongfa) {
		this.shaozhongfa = shaozhongfa;
	}

	public boolean isLazila() {
		return lazila;
	}

	public void setLazila(boolean lazila) {
		this.lazila = lazila;
	}

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
	}

}
