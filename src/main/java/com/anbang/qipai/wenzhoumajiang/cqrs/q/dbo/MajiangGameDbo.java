package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerMaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.plan.bean.PlayerInfo;
import com.dml.mpgame.game.GamePlayerOnlineState;

public class MajiangGameDbo {
	private String id;// 就是gameid
	private int lianzhuangCount = 1;
	private int panshu;
	private int renshu;
	private boolean jinjie;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;
	private MajiangGameState state;
	private List<MajiangGamePlayerDbo> players;

	public MajiangGameDbo() {
	}

	public MajiangGameDbo(MajiangGameValueObject majiangGame, Map<String, PlayerInfo> playerInfoMap) {
		id = majiangGame.getGameId();
		lianzhuangCount = majiangGame.getLianzhuangCount();
		panshu = majiangGame.getPanshu();
		renshu = majiangGame.getRenshu();
		jinjie = majiangGame.isJinjie();
		teshushuangfan = majiangGame.isTeshushuangfan();
		caishenqian = majiangGame.isCaishenqian();
		shaozhongfa = majiangGame.isShaozhongfa();
		lazila = majiangGame.isLazila();
		state = majiangGame.getState();

		players = new ArrayList<>();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = majiangGame.getPlayerMaidiStateMap();
		Map<String, MajiangGamePlayerState> playerStateMap = majiangGame.getPlayerStateMap();
		Map<String, GamePlayerOnlineState> playerOnlineStateMap = majiangGame.getPlayerOnlineStateMap();
		Map<String, Integer> playeTotalScoreMap = majiangGame.getPlayeTotalScoreMap();
		majiangGame.allPlayerIds().forEach((playerId) -> {
			PlayerInfo playerInfo = playerInfoMap.get(playerId);
			MajiangGamePlayerDbo playerDbo = new MajiangGamePlayerDbo();
			playerDbo.setHeadimgurl(playerInfo.getHeadimgurl());
			playerDbo.setNickname(playerInfo.getNickname());
			playerDbo.setOnlineState(playerOnlineStateMap.get(playerId));
			playerDbo.setPlayerId(playerId);
			playerDbo.setMaidiState(playerMaidiStateMap.get(playerId));
			playerDbo.setState(playerStateMap.get(playerId));
			if (playeTotalScoreMap.get(playerId) != null) {
				playerDbo.setTotalScore(playeTotalScoreMap.get(playerId));
			}
			players.add(playerDbo);
		});

	}

	public MajiangGamePlayerDbo findPlayer(String playerId) {
		for (MajiangGamePlayerDbo player : players) {
			if (player.getPlayerId().equals(playerId)) {
				return player;
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<MajiangGamePlayerDbo> getPlayers() {
		return players;
	}

	public void setPlayers(List<MajiangGamePlayerDbo> players) {
		this.players = players;
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

	public int getLianzhuangCount() {
		return lianzhuangCount;
	}

	public void setLianzhuangCount(int lianzhuangCount) {
		this.lianzhuangCount = lianzhuangCount;
	}

}
