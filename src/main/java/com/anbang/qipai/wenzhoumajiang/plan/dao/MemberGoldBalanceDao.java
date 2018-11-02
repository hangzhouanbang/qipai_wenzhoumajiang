package com.anbang.qipai.wenzhoumajiang.plan.dao;

import com.anbang.qipai.wenzhoumajiang.plan.bean.MemberGoldBalance;

public interface MemberGoldBalanceDao {

	void save(MemberGoldBalance memberGoldBalance);

	MemberGoldBalance findByMemberId(String memberId);
}
