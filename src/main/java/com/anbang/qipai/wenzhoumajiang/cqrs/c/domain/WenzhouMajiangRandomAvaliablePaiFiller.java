package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.avaliablepai.AvaliablePaiFiller;

public class WenzhouMajiangRandomAvaliablePaiFiller implements AvaliablePaiFiller {

	private long seed;
	private boolean shaozhongfa;
	private boolean queyise;// 缺一色

	public WenzhouMajiangRandomAvaliablePaiFiller() {
	}

	public WenzhouMajiangRandomAvaliablePaiFiller(long seed, boolean shaozhongfa, boolean queyise) {
		this.seed = seed;
		this.shaozhongfa = shaozhongfa;
		this.queyise = queyise;
	}

	@Override
	public void fillAvaliablePai(Ju ju) throws Exception {
		Set<MajiangPai> notPlaySet = new HashSet<>();
		notPlaySet.add(MajiangPai.chun);
		notPlaySet.add(MajiangPai.xia);
		notPlaySet.add(MajiangPai.qiu);
		notPlaySet.add(MajiangPai.dong);
		notPlaySet.add(MajiangPai.mei);
		notPlaySet.add(MajiangPai.lan);
		notPlaySet.add(MajiangPai.zhu);
		notPlaySet.add(MajiangPai.ju);
		if (shaozhongfa) {
			notPlaySet.add(MajiangPai.hongzhong);
			notPlaySet.add(MajiangPai.facai);
		}
		if (queyise) {
			int start = new Random(seed).nextInt(3);
			for (int i = start * 9; i < start * 9 + 9; i++) {
				notPlaySet.add(MajiangPai.valueOf(i));
			}
		}
		MajiangPai[] allMajiangPaiArray = MajiangPai.values();
		List<MajiangPai> playPaiTypeList = new ArrayList<>();
		for (int i = 0; i < allMajiangPaiArray.length; i++) {
			MajiangPai pai = allMajiangPaiArray[i];
			if (!notPlaySet.contains(pai)) {
				playPaiTypeList.add(pai);
			}
		}

		List<MajiangPai> allPaiList = new ArrayList<>();
		playPaiTypeList.forEach((paiType) -> {
			for (int i = 0; i < 4; i++) {
				allPaiList.add(paiType);
			}
		});

		Collections.shuffle(allPaiList, new Random(seed + ju.countFinishedPan()));
		ju.getCurrentPan().setAvaliablePaiList(allPaiList);
		ju.getCurrentPan().setPaiTypeList(playPaiTypeList);
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public boolean isShaozhongfa() {
		return shaozhongfa;
	}

	public void setShaozhongfa(boolean shaozhongfa) {
		this.shaozhongfa = shaozhongfa;
	}

	public boolean isQueyise() {
		return queyise;
	}

	public void setQueyise(boolean queyise) {
		this.queyise = queyise;
	}

}
