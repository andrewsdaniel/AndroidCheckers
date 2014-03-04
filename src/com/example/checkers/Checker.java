package com.example.checkers;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Checker extends TiledSprite {
	
	private boolean isQueened, isBlack, touchEnabled;
	private int row, col;

	public Checker(int row, int col, boolean isBlack, boolean touchEnabled, float pX, float pY, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexObjectManager) {
		super(pX, pY, pTextureRegion, pVertexObjectManager);
		this.col = col;
		this.row = row;
		this.isBlack = isBlack;
		this.touchEnabled = touchEnabled;
	}

	public boolean isQueened() {
		return isQueened;
	}

	public void setQueened(boolean isQueened) {
		this.isQueened = isQueened;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public void setBlack(boolean isBlack) {
		this.isBlack = isBlack;
	}

	public boolean isTouchEnabled() {
		return touchEnabled;
	}

	public void setTouchEnabled(boolean touchEnabled) {
		this.touchEnabled = touchEnabled;
	}
	
}
