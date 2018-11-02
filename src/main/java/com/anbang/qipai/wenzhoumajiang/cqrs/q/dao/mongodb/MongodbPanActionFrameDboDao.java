package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.PanActionFrameDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.PanActionFrameDbo;

@Component
public class MongodbPanActionFrameDboDao implements PanActionFrameDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(PanActionFrameDbo dbo) {
		mongoTemplate.insert(dbo);
	}

	@Override
	public PanActionFrameDbo findByGameIdAndPanNoAndActionNo(String gameId, int panNo, int actionNo) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameId").is(gameId));
		query.addCriteria(Criteria.where("panNo").is(panNo));
		query.addCriteria(Criteria.where("actionNo").is(actionNo));
		return mongoTemplate.findOne(query, PanActionFrameDbo.class);
	}

}
