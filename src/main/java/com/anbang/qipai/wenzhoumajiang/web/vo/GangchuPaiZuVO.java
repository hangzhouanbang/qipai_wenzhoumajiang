package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.GangType;
import com.dml.majiang.player.chupaizu.GangchuPaiZu;

public class GangchuPaiZuVO {

	private MajiangPai paiType;
	private GangType gangType;

	public GangchuPaiZuVO(GangchuPaiZu gangchuPaiZu) {
		paiType = gangchuPaiZu.getGangzi().getPaiType();
		gangType = gangchuPaiZu.getGangType();
	}

	public MajiangPai getPaiType() {
		return paiType;
	}

	public GangType getGangType() {
		return gangType;
	}

}
