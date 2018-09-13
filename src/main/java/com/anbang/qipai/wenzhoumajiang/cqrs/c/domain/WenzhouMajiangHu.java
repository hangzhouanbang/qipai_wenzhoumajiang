package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.nio.ByteBuffer;

import com.dml.majiang.player.Hu;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;

public class WenzhouMajiangHu extends Hu {

	private WenzhouMajiangPanPlayerHufan hufan;

	private boolean huxingHu;// 三财神推倒就不是胡形的胡

	public WenzhouMajiangHu() {
	}

	public WenzhouMajiangHu(ShoupaiPaiXing shoupaiPaiXing, WenzhouMajiangPanPlayerHufan hufan) {
		super(shoupaiPaiXing);
		this.hufan = hufan;
		this.huxingHu = true;
	}

	public WenzhouMajiangHu(WenzhouMajiangPanPlayerHufan hufan) {
		this.hufan = hufan;
		this.huxingHu = false;
	}

	public WenzhouMajiangPanPlayerHufan getHufan() {
		return hufan;
	}

	public void setHufan(WenzhouMajiangPanPlayerHufan hufan) {
		this.hufan = hufan;
	}

	public boolean isHuxingHu() {
		return huxingHu;
	}

	public void setHuxingHu(boolean huxingHu) {
		this.huxingHu = huxingHu;
	}

	@Override
	public void toByteBuffer(ByteBuffer bb) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillByByteBuffer(ByteBuffer bb) throws Throwable {
		// TODO Auto-generated method stub

	}

}
