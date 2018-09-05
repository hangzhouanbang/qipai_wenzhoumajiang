package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.fapai.FaPaiStrategy;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.position.MajiangPosition;
import com.dml.majiang.position.MajiangPositionUtil;

public class WenzhouMajiangFaPaiStrategy implements FaPaiStrategy {

	private int faPaiCountsForOnePlayer;

	public WenzhouMajiangFaPaiStrategy() {
	}

	public WenzhouMajiangFaPaiStrategy(int faPaiCountsForOnePlayer) {
		this.faPaiCountsForOnePlayer = faPaiCountsForOnePlayer;
	}

	@Override
	public void faPai(Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		List<MajiangPai> avaliablePaiList = currentPan.getAvaliablePaiList();
		MajiangPosition zhuangPlayerMenFeng = currentPan.findMenFengForZhuang();
		for (int i = 0; i < faPaiCountsForOnePlayer; i++) {
			MajiangPosition playerMenFeng = zhuangPlayerMenFeng;
			for (int j = 0; j < 4; j++) {
				MajiangPlayer player = currentPan.findPlayerByMenFeng(playerMenFeng);
				if (player != null) {
					faPai(avaliablePaiList, player);
				}
				playerMenFeng = MajiangPositionUtil.nextPositionAntiClockwise(playerMenFeng);
			}
		}
	}

	private void faPai(List<MajiangPai> avaliablePaiList, MajiangPlayer player) {
		MajiangPai pai = avaliablePaiList.remove(0);
		player.addShoupai(pai);
	}

}
