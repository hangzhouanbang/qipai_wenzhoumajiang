package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.JuResultDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.mongodb.repository.JuResultDboRepository;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.JuResultDbo;

@Component
public class MongodbJuResultDboDao implements JuResultDboDao {

	@Autowired
	private JuResultDboRepository repository;

	@Override
	public void save(JuResultDbo juResultDbo) {
		repository.save(juResultDbo);
	}

	@Override
	public JuResultDbo findByGameId(String gameId) {
		return repository.findOneByGameId(gameId);
	}

}
