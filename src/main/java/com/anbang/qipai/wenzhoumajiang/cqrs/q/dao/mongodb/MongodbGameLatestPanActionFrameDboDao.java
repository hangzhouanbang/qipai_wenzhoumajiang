package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.GameLatestPanActionFrameDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb.repository.GameLatestPanActionFrameDboRepository;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.GameLatestPanActionFrameDbo;

@Component
public class MongodbGameLatestPanActionFrameDboDao implements GameLatestPanActionFrameDboDao {

	@Autowired
	private GameLatestPanActionFrameDboRepository repository;

	@Override
	public GameLatestPanActionFrameDbo findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void save(String id, byte[] data) {
		GameLatestPanActionFrameDbo dbo = new GameLatestPanActionFrameDbo();
		dbo.setId(id);
		dbo.setData(data);
		repository.save(dbo);
	}

}
