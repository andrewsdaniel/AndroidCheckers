package com.example.checkers;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.util.Log;


public class CheckerBoardSquare extends Sprite {
	
	Checker checker;
	
	public CheckerBoardSquare(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexObjectManager) {
		super(pX, pY, pTextureRegion, pVertexObjectManager);
		
	}
	
	public Checker getChecker()
	{
		if(this.checker != null)
			return this.checker;
		return null;
	}
	
	public void setChecker(Checker checker)
	{
		this.checker = checker;
	}
	
	public void removeChecker()
	{
		Log.i("info", "Removing checker at row " + checker.getRow() + " col " + checker.getCol());
		System.out.println("Removing checker at row " + checker.getRow() + " col " + checker.getCol());
		Debug.d("Removing checker at row " + checker.getRow() + " col " + checker.getCol());
		this.checker = null;
	}
	
}
