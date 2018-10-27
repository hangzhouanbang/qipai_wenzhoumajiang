package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGame;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Game;
import com.dml.mpgame.game.GameValueObject;
import com.dml.mpgame.game.WaitingStart;
import com.dml.mpgame.game.extend.fpmpv.back.FpmpvBackStrategy;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.extend.vote.MostPlayersWinVoteCalculator;
import com.dml.mpgame.game.extend.vote.OnlineVotePlayersFilter;
import com.dml.mpgame.game.extend.vote.VoteOption;
import com.dml.mpgame.game.extend.vote.leave.VoteWaiverLeaveStrategy;
import com.dml.mpgame.game.join.FixedNumberOfPlayersGameJoinStrategy;
import com.dml.mpgame.game.leave.HostGameLeaveStrategy;
import com.dml.mpgame.game.leave.OfflineGameLeaveStrategy;
import com.dml.mpgame.game.player.PlayerFinished;
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
		newGame.setFixedPlayerCount(renshu);
		newGame.setJinjie1(jinjie1);
		newGame.setJinjie2(jinjie2);
		newGame.setTeshushuangfan(teshushuangfan);
		newGame.setCaishenqian(caishenqian);
		newGame.setShaozhongfa(shaozhongfa);
		newGame.setLazila(lazila);
		newGame.setGangsuanfen(gangsuanfen);

		newGame.setVotePlayersFilter(new OnlineVotePlayersFilter());

		newGame.setJoinStrategy(new FixedNumberOfPlayersGameJoinStrategy(renshu));
		newGame.setReadyStrategy(new FixedNumberOfPlayersGameReadyStrategy(renshu));

		newGame.setLeaveByOfflineStrategyAfterStart(new VoteWaiverLeaveStrategy());
		newGame.setLeaveByOfflineStrategyBeforeStart(new OfflineGameLeaveStrategy());

		newGame.setLeaveByHangupStrategyAfterStart(new VoteWaiverLeaveStrategy());
		newGame.setLeaveByHangupStrategyBeforeStart(new OfflineGameLeaveStrategy());

		newGame.setLeaveByPlayerStrategyAfterStart(new VoteWaiverLeaveStrategy());
		newGame.setLeaveByPlayerStrategyBeforeStart(new HostGameLeaveStrategy(playerId));

		newGame.setBackStrategy(new FpmpvBackStrategy());
		newGame.create(gameId, playerId);
		gameServer.playerCreateGame(newGame, playerId);

		return new MajiangGameValueObject(newGame);

	}

	@Override
	public MajiangGameValueObject leaveGame(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		Game game = gameServer.findGamePlayerPlaying(playerId);
		MajiangGameValueObject majiangGameValueObject = gameServer.leaveByPlayer(playerId);
		if (game.getState().name().equals(FinishedByVote.name)) {// 有可能离开的时候正在投票，由于离开自动投弃权最终导致游戏结束
			gameServer.finishGame(game.getId());
		}
		return majiangGameValueObject;
	}

	@Override
	public MajiangGameValueObject leaveGameByOffline(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		Game game = gameServer.findGamePlayerPlaying(playerId);
		MajiangGameValueObject majiangGameValueObject = gameServer.leaveByOffline(playerId);
		if (game.getState().name().equals(FinishedByVote.name)) {// 有可能离开的时候正在投票，由于离开自动投弃权最终导致游戏结束
			gameServer.finishGame(game.getId());
		}
		return majiangGameValueObject;
	}

	@Override
	public MajiangGameValueObject leaveGameByHangup(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		Game game = gameServer.findGamePlayerPlaying(playerId);
		MajiangGameValueObject majiangGameValueObject = gameServer.leaveByHangup(playerId);
		if (game.getState().name().equals(FinishedByVote.name)) {// 有可能离开的时候正在投票，由于离开自动投弃权最终导致游戏结束
			gameServer.finishGame(game.getId());
		}
		return majiangGameValueObject;
	}

	@Override
	public ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception {

		ReadyForGameResult result = new ReadyForGameResult();
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		MajiangGameValueObject majiangGameValueObject = gameServer.ready(playerId, currentTime);
		result.setMajiangGame(majiangGameValueObject);
		return result;

	}

	@Override
	public MajiangGameValueObject joinGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		return gameServer.join(playerId, gameId);
	}

	@Override
	public MajiangGameValueObject backToGame(String playerId, String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		return gameServer.back(playerId, gameId);
	}

	@Override
	public void bindPlayer(String playerId, String gameId) {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		gameServer.bindPlayer(playerId, gameId);
	}

	@Override
	public MajiangGameValueObject finish(String playerId) throws Exception {
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

		if (majiangGame.getState().name().equals(FinishedByVote.name)) {
			gameServer.finishGame(majiangGame.getId());
		}
		return new MajiangGameValueObject(majiangGame);
	}

	@Override
	public MajiangGameValueObject voteToFinish(String playerId, Boolean yes) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		MajiangGame majiangGame = (MajiangGame) gameServer.findGamePlayerPlaying(playerId);
		if (yes) {
			majiangGame.voteToFinish(playerId, VoteOption.yes);
		} else {
			majiangGame.voteToFinish(playerId, VoteOption.no);
		}

		if (majiangGame.getState().name().equals(FinishedByVote.name)) {
			gameServer.finishGame(majiangGame.getId());
		}
		return new MajiangGameValueObject(majiangGame);
	}

	@Override
	public GameValueObject finishGameImmediately(String gameId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		majiangGame.finish();
		majiangGame.setState(new Finished());
		majiangGame.updateAllPlayersState(new PlayerFinished());
		gameServer.finishGame(gameId);
		return new MajiangGameValueObject(majiangGame);
	}

}
