package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerMaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;

public class MajiangGamePlayerMaidiDbo {
	private String id;
	private String gameId;
	private int panNo;
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap;

	public MajiangGamePlayerMaidiDbo() {

	}

	public MajiangGamePlayerMaidiDbo(MajiangGameValueObject majiangGame) {
		this.gameId = majiangGame.getId();
		this.panNo = majiangGame.getCurrentPanNo();
		this.playerMaidiStateMap = majiangGame.getPlayerMaidiStateMap();
	}

	public List<String> allPlayerIds() {
		return new ArrayList<>(playerMaidiStateMap.keySet());
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

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
	}

}
