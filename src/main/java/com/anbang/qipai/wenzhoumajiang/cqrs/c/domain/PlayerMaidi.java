package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.player.GamePlayerState;

public class PlayerMaidi implements GamePlayerState {

	public static final String name = "PlayerMaidi";

	@Override
	public String name() {
		return name;
	}

}
