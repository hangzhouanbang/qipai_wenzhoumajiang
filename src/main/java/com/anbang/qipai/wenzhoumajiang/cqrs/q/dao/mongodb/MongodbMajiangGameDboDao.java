package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.MajiangGameDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb.repository.MajiangGameDboRepository;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.dml.mpgame.game.GamePlayerOnlineState;

@Component
public class MongodbMajiangGameDboDao implements MajiangGameDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MajiangGameDboRepository repository;

	@Override
	public MajiangGameDbo findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void save(MajiangGameDbo majiangGameDbo) {
		repository.save(majiangGameDbo);
	}

	@Override
	public void updatePlayerOnlineState(String id, String playerId, GamePlayerOnlineState onlineState) {
		MajiangGameDbo majiangGameDbo = repository.findOne(id);
		majiangGameDbo.getPlayers().forEach((player) -> {
			if (player.getPlayerId().equals(playerId)) {
				player.setOnlineState(onlineState);
			}
		});
		repository.save(majiangGameDbo);
	}

}
