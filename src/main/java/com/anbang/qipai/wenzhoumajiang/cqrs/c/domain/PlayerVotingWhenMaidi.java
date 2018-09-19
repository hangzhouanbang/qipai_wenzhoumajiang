package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.mpgame.game.player.GamePlayerState;

public class PlayerVotingWhenMaidi implements GamePlayerState {

	public static final String name = "PlayerVotingWhenMaidi";

	@Override
	public String name() {
		return name;
	}

}
