package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.FinishResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGame;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangJuResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.dml.mpgame.game.Game;
import com.dml.mpgame.game.GameValueObject;
import com.dml.mpgame.game.WaitingStart;
import com.dml.mpgame.game.extend.fpmpv.back.FpmpvBackStrategy;
import com.dml.mpgame.game.extend.fpmpv.leave.FpmpvLeaveStrategy;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.extend.vote.MostPlayersWinVoteCalculator;
import com.dml.mpgame.game.extend.vote.VoteOption;
import com.dml.mpgame.game.join.FixedNumberOfPlayersGameJoinStrategy;
import com.dml.mpgame.game.ready.FixedNumberOfPlayersGameReadyStrategy;
import com.dml.mpgame.server.GameServer;

@Component
public class GameCmdServiceImpl extends CmdServiceBase implements GameCmdService {

	@Override
	public MajiangGameValueObject newMajiangGame(String gameId, String playerId, Integer panshu, Integer renshu,
			Boolean jinjie1, Boolean jinjie2, Boolean teshushuangfan, Boolean caishenqian, Boolean shaozhongfa,
			Boolean lazila, Boolean gangsuanfen) {

		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);

		MajiangGame newGame = new MajiangGame();

		newGame.setPanshu(panshu);
		newGame.setRenshu(renshu);
		newGame.setJinjie1(jinjie1);
		newGame.setJinjie2(jinjie2);
		newGame.setTeshushuangfan(teshushuangfan);
		newGame.setCaishenqian(caishenqian);
		newGame.setShaozhongfa(shaozhongfa);
		newGame.setLazila(lazila);
		newGame.setGangsuanfen(gangsuanfen);

		newGame.setJoinStrategy(new FixedNumberOfPlayersGameJoinStrategy(renshu));
		newGame.setReadyStrategy(new FixedNumberOfPlayersGameReadyStrategy(renshu));
		newGame.setLeaveStrategy(new FpmpvLeaveStrategy(playerId));
		newGame.setBackStrategy(new FpmpvBackStrategy());
		newGame.create(gameId, playerId);
		gameServer.playerCreateGame(newGame, playerId);

		return new MajiangGameValueObject(newGame);

	}

	@Override
	public MajiangGameValueObject leaveGame(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		Game game = gameServer.findGamePlayerPlaying(playerId);
		gameServer.leave(playerId);
		return new MajiangGameValueObject((MajiangGame) game);
	}

	@Override
	public ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception {

		ReadyForGameResult result = new ReadyForGameResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		gameServer.ready(playerId);

		MajiangGame majiangGame = (MajiangGame) gameServer.findGamePlayerPlaying(playerId);
		MajiangGameValueObject majiangGameValueObject = new MajiangGameValueObject(majiangGame);
		result.setMajiangGame(majiangGameValueObject);
		return result;

	}

	@Override
	public MajiangGameValueObject joinGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		gameServer.join(playerId, gameId);
		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		return new MajiangGameValueObject(majiangGame);
	}

	@Override
	public MajiangGameValueObject backToGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		gameServer.back(playerId, gameId);

		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		MajiangGameValueObject majiangGameValueObject = new MajiangGameValueObject(majiangGame);

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
		MajiangGame majiangGame = (MajiangGame) gameServer.findGamePlayerPlaying(playerId);
		// 在准备阶段不会发起投票
		if (majiangGame.getState().name().equals(WaitingStart.name)) {
			// 是主机的话直接解散，不是的话自己走人
			if (majiangGame.getCreatePlayerId().equals(playerId)) {
				majiangGame.cancel();
			} else {
				majiangGame.quit(playerId);
			}
		} else {
			majiangGame.launchVoteToFinish(playerId, new MostPlayersWinVoteCalculator());
			majiangGame.voteToFinish(playerId, VoteOption.yes);
		}

		MajiangGameValueObject majiangGameValueObject = new MajiangGameValueObject(majiangGame);

		result.setMajiangGameValueObject(majiangGameValueObject);

		if (majiangGameValueObject.getState().name().equals(FinishedByVote.name)) {
			WenzhouMajiangJuResult juResult = (WenzhouMajiangJuResult) majiangGame.finishJu();
			result.setJuResult(juResult);
		}
		return result;
	}

	@Override
	public FinishResult voteToFinish(String playerId, Boolean yes) throws Exception {
		FinishResult finishResult = new FinishResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		MajiangGame majiangGame = (MajiangGame) gameServer.findGamePlayerPlaying(playerId);
		if (yes) {
			majiangGame.voteToFinish(playerId, VoteOption.yes);
		} else {
			majiangGame.voteToFinish(playerId, VoteOption.no);
		}

		MajiangGameValueObject majiangGameValueObject = new MajiangGameValueObject(majiangGame);
		finishResult.setMajiangGameValueObject(majiangGameValueObject);

		if (majiangGameValueObject.getState().name().equals(FinishedByVote.name)) {
			WenzhouMajiangJuResult juResult = (WenzhouMajiangJuResult) majiangGame.finishJu();
			finishResult.setJuResult(juResult);
		}
		return finishResult;
	}

	@Override
	public GameValueObject finishGameImmediately(String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		majiangGame.finishJu();
		gameServer.finishGameImmediately(gameId);
		return new MajiangGameValueObject(majiangGame);
	}

}
