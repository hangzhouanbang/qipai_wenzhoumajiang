package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.GameFinishVoteDbo;
import com.dml.mpgame.game.finish.vote.GameFinishVoteValueObject;

public interface GameFinishVoteDboDao {

	void save(GameFinishVoteDbo gameFinishVoteDbo);

	void update(String gameId, GameFinishVoteValueObject gameFinishVoteValueObject);

	GameFinishVoteDbo findByGameId(String gameId);

	void removeGameFinishVoteDboByGameId(String gameId);
}
