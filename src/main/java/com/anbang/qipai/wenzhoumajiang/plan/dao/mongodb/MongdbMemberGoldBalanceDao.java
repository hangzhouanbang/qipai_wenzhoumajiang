package com.anbang.qipai.wenzhoumajiang.plan.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.plan.bean.MemberGoldBalance;
import com.anbang.qipai.wenzhoumajiang.plan.dao.MemberGoldBalanceDao;
import com.anbang.qipai.wenzhoumajiang.plan.dao.mongodb.repository.MemberGoldBalanceRepository;

@Component
public class MongdbMemberGoldBalanceDao implements MemberGoldBalanceDao {

	@Autowired
	private MemberGoldBalanceRepository memberGoldBalanceRepository;

	@Override
	public void save(MemberGoldBalance memberGoldBalance) {
		memberGoldBalanceRepository.save(memberGoldBalance);
	}

	@Override
	public MemberGoldBalance findByMemberId(String memberId) {
		return memberGoldBalanceRepository.findOneByMemberId(memberId);
	}

}
