package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.GameState;

public class VotingWhenMaidi implements GameState {

	public static final String name = "VotingWhenMaidi";

	@Override
	public String name() {
		return name;
	}

}
