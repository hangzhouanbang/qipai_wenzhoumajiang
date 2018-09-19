package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.player.GamePlayerState;

public class PlayerVotedWhenMaidi implements GamePlayerState {

	public static final String name = "PlayerVotedWhenMaidi";

	@Override
	public String name() {
		return name;
	}

}
