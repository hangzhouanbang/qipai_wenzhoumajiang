package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.player.GamePlayerState;

public class PlayerAfterMaidi implements GamePlayerState {

	public static final String name = "PlayerAfterMaidi";

	@Override
	public String name() {
		return name;
	}

}
