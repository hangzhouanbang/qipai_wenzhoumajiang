package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.PanResultDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb.repository.PanResultDboRepository;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.PanResultDbo;

@Component
public class MongodbPanResultDboDao implements PanResultDboDao {

	@Autowired
	private PanResultDboRepository repository;

	@Override
	public void save(PanResultDbo panResultDbo) {
		repository.save(panResultDbo);
	}

	@Override
	public PanResultDbo findByGameIdAndPanNo(String gameId, int panNo) {
		return repository.findOneByGameIdAndPanNo(gameId, panNo);
	}

}
