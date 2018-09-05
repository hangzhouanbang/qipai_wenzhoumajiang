package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.FinishResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameManager;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangJuResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.mpgame.game.Game;
import com.dml.mpgame.game.GameState;
import com.dml.mpgame.game.GameValueObject;
import com.dml.mpgame.game.finish.vote.MostPlayersWinVoteCalculator;
import com.dml.mpgame.game.finish.vote.VoteAfterStartedGameFinishStrategy;
import com.dml.mpgame.game.finish.vote.VoteAfterStartedGameFinishStrategyValueObject;
import com.dml.mpgame.game.finish.vote.VoteOption;
import com.dml.mpgame.game.join.FixedNumberOfPlayersGameJoinStrategy;
import com.dml.mpgame.game.leave.HostGameLeaveStrategy;
import com.dml.mpgame.game.ready.FixedNumberOfPlayersGameReadyStrategy;
import com.dml.mpgame.server.GameServer;

@Component
public class GameCmdServiceImpl extends CmdServiceBase implements GameCmdService {

	@Override
	public MajiangGameValueObject newMajiangGame(String gameId, String playerId, Integer difen, Integer taishu,
			Integer panshu, Integer renshu, Boolean dapao) {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.playerCreateGame(gameId,
				new FixedNumberOfPlayersGameJoinStrategy(renshu), new FixedNumberOfPlayersGameReadyStrategy(renshu),
				new HostGameLeaveStrategy(playerId),
				new VoteAfterStartedGameFinishStrategy(playerId, new MostPlayersWinVoteCalculator()), playerId);
		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.newMajiangGame(gameValueObject, panshu,
				renshu);
		return majiangGameValueObject;
	}

	@Override
	public MajiangGameValueObject leaveGame(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.leave(playerId);

		if (gameValueObject != null) {
			MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
			MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);

			return majiangGameValueObject;
		} else {
			return null;
		}
	}

	@Override
	public ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception {
		ReadyForGameResult result = new ReadyForGameResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.ready(playerId);

		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);
		result.setMajiangGame(majiangGameValueObject);

		if (gameValueObject.getState().equals(GameState.playing)) {
			PanActionFrame firstActionFrame = majiangGameManager.createJuAndStartFirstPan(gameValueObject, currentTime);
			result.setFirstActionFrame(firstActionFrame);
		}
		return result;
	}

	@Override
	public MajiangGameValueObject joinGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.join(playerId, gameId);

		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);

		return majiangGameValueObject;
	}

	@Override
	public MajiangGameValueObject backToGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.back(playerId, gameId);

		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);

		return majiangGameValueObject;
	}

	@Override
	public void bindPlayer(String playerId, String gameId) {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		gameServer.bindPlayer(playerId, gameId);
	}

	@Override
	public FinishResult finish(String playerId) throws Exception {
		FinishResult result = new FinishResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.finishGame(playerId);
		result.setVoteFinishStrategy(
				(VoteAfterStartedGameFinishStrategyValueObject) gameValueObject.getFinishStrategy());

		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);
		result.setMajiangGameValueObject(majiangGameValueObject);

		if (gameValueObject.getState().equals(GameState.finished)) {
			WenzhouMajiangJuResult juResult = majiangGameManager.finishMajiangGame(gameValueObject.getId());
			result.setJuResult(juResult);
		}
		return result;
	}

	@Override
	public FinishResult voteToFinish(String playerId, Boolean yes) throws Exception {
		FinishResult finishResult = new FinishResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject;
		Game game = gameServer.findGamePlayerPlaying(playerId);
		VoteAfterStartedGameFinishStrategy voteAfterStartedGameFinishStrategy = (VoteAfterStartedGameFinishStrategy) game
				.getFinishStrategy();
		if (yes) {
			gameValueObject = voteAfterStartedGameFinishStrategy.vote(playerId, VoteOption.yes, game);
		} else {
			gameValueObject = voteAfterStartedGameFinishStrategy.vote(playerId, VoteOption.no, game);
		}
		finishResult.setVoteFinishStrategy(
				(VoteAfterStartedGameFinishStrategyValueObject) gameValueObject.getFinishStrategy());

		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		MajiangGameValueObject majiangGameValueObject = majiangGameManager.updateMajiangGameByGame(gameValueObject);
		finishResult.setMajiangGameValueObject(majiangGameValueObject);

		if (gameValueObject.getState().equals(GameState.finished)) {
			WenzhouMajiangJuResult juResult = majiangGameManager.finishMajiangGame(game.getId());
			finishResult.setJuResult(juResult);
		}
		return finishResult;
	}

	@Override
	public GameValueObject finishGameImmediately(String gameId) throws Exception {
		MajiangGameManager majiangGameManager = singletonEntityRepository.getEntity(MajiangGameManager.class);
		majiangGameManager.finishMajiangGame(gameId);
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		GameValueObject gameValueObject = gameServer.finishGameImmediately(gameId);
		return gameValueObject;
	}

}
