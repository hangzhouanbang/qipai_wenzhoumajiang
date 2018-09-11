package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.chi.MajiangPlayerChiActionUpdater;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;

public class WenzhouMajiangChiActionUpdater implements MajiangPlayerChiActionUpdater {

	@Override
	public void updateActions(MajiangChiAction chiAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.clearAllPlayersActionCandidates();

		MajiangPlayer player = currentPan.findPlayerById(chiAction.getActionPlayerId());
		List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
		JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
				.findListener(JuezhangStatisticsListener.class);
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
		guipaiTypeSet.toArray(guipaiTypes);
		MajiangPai guipaiType = guipaiTypes[0];
		boolean juefeng = false;
		boolean genfeng = false;
		// 循环了三次、代码需要改进
		for (MajiangPai pai : fangruShoupaiList) {
			if (!MajiangPai.baiban.equals(pai)) {
				if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
					if (juezhangStatisticsListener.ifJuezhang(pai)) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
						juefeng = true;
					}
				}
			} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
				if (juezhangStatisticsListener.ifJuezhang(pai)) {
					player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
					juefeng = true;
				}
			} else {

			}
		}
		for (MajiangPai pai : fangruShoupaiList) {
			if (!MajiangPai.baiban.equals(pai)) {
				if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
					if (!juefeng && juezhangStatisticsListener.ifMingPai(pai)
							&& player.getShoupaiCalculator().count(pai) == 1) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
						genfeng = true;
					}
				}
			} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
				if (!juefeng && juezhangStatisticsListener.ifMingPai(pai)
						&& player.getShoupaiCalculator().count(pai) == 1) {
					player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
					genfeng = true;
				}
			} else {

			}
		}
		for (MajiangPai pai : fangruShoupaiList) {
			if (!MajiangPai.baiban.equals(pai)) {
				if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
					if (!juefeng && !genfeng && player.getShoupaiCalculator().count(pai) == 1) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
					}
				}
			} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
				if (!juefeng && !genfeng && player.getShoupaiCalculator().count(pai) == 1) {
					player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
				}
			} else {

			}
		}
		if (player.getActionCandidates().isEmpty()) {
			player.generateDaActions();
		}
	}

}
