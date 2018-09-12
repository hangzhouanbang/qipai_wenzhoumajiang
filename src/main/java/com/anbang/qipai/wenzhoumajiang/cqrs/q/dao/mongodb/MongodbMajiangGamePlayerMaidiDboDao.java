package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerMaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.MajiangGamePlayerMaidiDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerMaidiDbo;

@Component
public class MongodbMajiangGamePlayerMaidiDboDao implements MajiangGamePlayerMaidiDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addMajiangGamePlayerMaidiDbo(MajiangGamePlayerMaidiDbo dbo) {
		mongoTemplate.insert(dbo);
	}

	@Override
	public MajiangGamePlayerMaidiDbo findByGameIdAndPanNo(String gameId, int panNo) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameId").is(gameId));
		query.addCriteria(Criteria.where("panNo").is(panNo));
		return mongoTemplate.findOne(query, MajiangGamePlayerMaidiDbo.class);
	}

	@Override
	public void updateMajiangGamePlayerMaidiDbo(String gameId, int panNo,
			Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameId").is(gameId));
		query.addCriteria(Criteria.where("panNo").is(panNo));
		Update update = new Update();
		update.set("playerMaidiStateMap", playerMaidiStateMap);
		mongoTemplate.updateFirst(query, update, MajiangGamePlayerMaidiDbo.class);
	}

}
