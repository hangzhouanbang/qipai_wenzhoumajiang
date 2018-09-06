package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dml.mpgame.game.GamePlayerOnlineState;

public class MajiangGameValueObject {

	private String gameId;
	private int panshu;
	private int renshu;
	private boolean jinjie;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;
	private MajiangGameState state;
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = new HashMap<>();
	private Map<String, MajiangGamePlayerState> playerStateMap = new HashMap<>();
	private Map<String, GamePlayerOnlineState> playerOnlineStateMap = new HashMap<>();
	private Map<String, Integer> playeTotalScoreMap = new HashMap<>();

	public MajiangGameValueObject() {
	}

	public MajiangGameValueObject(MajiangGame majiangGame) {
		gameId = majiangGame.getGameId();
		panshu = majiangGame.getPanshu();
		renshu = majiangGame.getRenshu();
		jinjie = majiangGame.isJinjie();
		teshushuangfan = majiangGame.isTeshushuangfan();
		caishenqian = majiangGame.isCaishenqian();
		shaozhongfa = majiangGame.isShaozhongfa();
		lazila = majiangGame.isLazila();
		state = majiangGame.getState();
		playerMaidiStateMap.putAll(majiangGame.getPlayerMaidiStateMap());
		playerStateMap.putAll(majiangGame.getPlayerStateMap());
		playerOnlineStateMap.putAll(majiangGame.getPlayerOnlineStateMap());
		playeTotalScoreMap.putAll(majiangGame.getPlayeTotalScoreMap());
	}

	public List<String> allPlayerIds() {
		return new ArrayList<>(playerStateMap.keySet());
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public MajiangGameState getState() {
		return state;
	}

	public void setState(MajiangGameState state) {
		this.state = state;
	}

	public Map<String, MajiangGamePlayerState> getPlayerStateMap() {
		return playerStateMap;
	}

	public void setPlayerStateMap(Map<String, MajiangGamePlayerState> playerStateMap) {
		this.playerStateMap = playerStateMap;
	}

	public Map<String, GamePlayerOnlineState> getPlayerOnlineStateMap() {
		return playerOnlineStateMap;
	}

	public void setPlayerOnlineStateMap(Map<String, GamePlayerOnlineState> playerOnlineStateMap) {
		this.playerOnlineStateMap = playerOnlineStateMap;
	}

	public Map<String, Integer> getPlayeTotalScoreMap() {
		return playeTotalScoreMap;
	}

	public void setPlayeTotalScoreMap(Map<String, Integer> playeTotalScoreMap) {
		this.playeTotalScoreMap = playeTotalScoreMap;
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
