package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.HashMap;
import java.util.Map;

import com.dml.mpgame.game.GameValueObject;

public class MajiangGameManager {

	private Map<String, MajiangGame> gameIdMajiangGameMap = new HashMap<>();

	public MajiangGameValueObject newMajiangGame(GameValueObject gameValueObject, int panshu, int renshu,
			boolean jinjie1, boolean jinjie2, boolean teshushuangfan, boolean caishenqian, boolean shaozhongfa,
			boolean lazila, boolean gangsuanfen) {
		String gameId = gameValueObject.getId();
		MajiangGame majiangGame = new MajiangGame();
		majiangGame.setPanshu(panshu);
		majiangGame.setRenshu(renshu);
		majiangGame.setJinjie1(jinjie1);
		majiangGame.setJinjie2(jinjie2);
		majiangGame.setTeshushuangfan(teshushuangfan);
		majiangGame.setCaishenqian(caishenqian);
		majiangGame.setShaozhongfa(shaozhongfa);
		majiangGame.setLazila(lazila);
		majiangGame.setGangsuanfen(gangsuanfen);
		majiangGame.setGameId(gameId);
		majiangGame.updateByGame(gameValueObject);
		gameIdMajiangGameMap.put(gameId, majiangGame);
		return new MajiangGameValueObject(majiangGame);
	}

	public MajiangGameValueObject updateMajiangGameByGame(GameValueObject game) {
		MajiangGame majiangGame = gameIdMajiangGameMap.get(game.getId());
		return majiangGame.updateByGame(game);
	}

	public WenzhouMajiangJuResult finishMajiangGame(String gameId) {
		MajiangGame game = gameIdMajiangGameMap.remove(gameId);
		if (game.getJu() != null) {
			return (WenzhouMajiangJuResult) game.finishJu();
		} else {
			return null;
		}
	}

	public Map<String, MajiangGamePlayerMaidiState> createJuAndReadyFirstPan(GameValueObject game, long currentTime)
			throws Exception {
		MajiangGame majiangGame = gameIdMajiangGameMap.get(game.getId());
		return majiangGame.createJuAndReadyFirstPan(game, currentTime);
	}

	public MaidiResult maidi(String playerId, boolean state, String gameId) throws Exception {
		MajiangGame majiangGame = gameIdMajiangGameMap.get(gameId);
		MaidiResult maidiResult = majiangGame.maidi(playerId, state);
		return maidiResult;
	}

	public MajiangActionResult majiangAction(String playerId, String gameId, int actionId, long actionTime)
			throws Exception {
		MajiangGame game = gameIdMajiangGameMap.get(gameId);
		MajiangActionResult majiangActionResult = game.action(playerId, actionId, actionTime);
		if (majiangActionResult.getJuResult() != null) {// 都结束了
			gameIdMajiangGameMap.remove(gameId);
		}
		return majiangActionResult;
	}

	public ReadyToNextPanResult readyToNextPan(String playerId, String gameId) throws Exception {
		MajiangGame game = gameIdMajiangGameMap.get(gameId);
		ReadyToNextPanResult readyToNextPanResult = game.readyToNextPan(playerId);
		return readyToNextPanResult;
	}
}
