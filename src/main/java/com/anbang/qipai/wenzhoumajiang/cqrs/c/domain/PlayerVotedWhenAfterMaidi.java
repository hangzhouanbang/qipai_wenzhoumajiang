package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.player.GamePlayerState;

public class PlayerVotedWhenAfterMaidi implements GamePlayerState {

	public static final String name = "PlayerVotedWhenAfterMaidi";

	@Override
	public String name() {
		return name;
	}

}
