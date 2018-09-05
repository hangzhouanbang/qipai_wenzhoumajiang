package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.plan.bean.PlayerInfo;
import com.dml.mpgame.game.GamePlayerOnlineState;

public class MajiangGameDbo {
	private String id;// 就是gameid
	private int difen;
	private int taishu;
	private int panshu;
	private int renshu;
	private boolean dapao;
	private MajiangGameState state;
	private List<MajiangGamePlayerDbo> players;

	public MajiangGameDbo() {
	}

	public MajiangGameDbo(MajiangGameValueObject majiangGame, Map<String, PlayerInfo> playerInfoMap) {
		id = majiangGame.getGameId();
		difen = majiangGame.getDifen();
		taishu = majiangGame.getTaishu();
		panshu = majiangGame.getPanshu();
		renshu = majiangGame.getRenshu();
		dapao = majiangGame.isDapao();
		state = majiangGame.getState();

		players = new ArrayList<>();
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

	public int getDifen() {
		return difen;
	}

	public void setDifen(int difen) {
		this.difen = difen;
	}

	public int getTaishu() {
		return taishu;
	}

	public void setTaishu(int taishu) {
		this.taishu = taishu;
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

	public boolean isDapao() {
		return dapao;
	}

	public void setDapao(boolean dapao) {
		this.dapao = dapao;
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

}
