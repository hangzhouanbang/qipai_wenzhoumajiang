package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.GameState;

public class MaidiState implements GameState {

	public static final String name = "MaidiState";

	@Override
	public String name() {
		return name;
	}

}
