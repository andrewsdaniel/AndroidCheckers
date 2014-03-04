package com.example.checkers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.widget.Toast;

public class CheckersActivity extends SimpleBaseGameActivity {
	
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	
	private final Scene scene = new Scene();
	
	private ArrayList<ArrayList<CheckerBoardSquare> > txSquares;
	
	private int iNumRed = 0, iNumBlack = 0;
	
	private ITexture main_font_texture;
	private ITextureRegion txBackground;
	private TextureRegion txBlackSquare;
	private TextureRegion txWhiteSquare;
	private TiledTextureRegion txCheckerRedTile;
	private TiledTextureRegion txCheckerBlackTile;
	
	private Text redCountText, blackCountText;
	private Font main_font;
	
	private boolean isBlacksTurn = true;
	

	/*@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera));
		return engine;
	}*/
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		
		txSquares = new ArrayList<ArrayList<CheckerBoardSquare>>();
		
		try {
			
			// Set up fonts
						main_font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, BitmapTextureFormat.RGBA_8888,
								TextureOptions.BILINEAR_PREMULTIPLYALPHA, Typeface.DEFAULT, 40, true, Color.WHITE_ABGR_PACKED_INT);
						main_font.load();
						main_font_texture = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
						
			// create textures from png images
			ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getResources().openRawResource(R.raw.checkers_bg);
				}
			});
			ITexture darkSquareTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getResources().openRawResource(R.raw.checkers_dark_sq);
				}
			});
			ITexture liteSquareTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getResources().openRawResource(R.raw.checkers_lite_sq);
				}
			});
			
			// load textures into memory
			backgroundTexture.load();
			darkSquareTexture.load();
			liteSquareTexture.load();
			
			// set up regions
			this.txBackground = TextureRegionFactory.extractFromTexture(backgroundTexture);
			this.txBlackSquare = TextureRegionFactory.extractFromTexture(darkSquareTexture);
			this.txWhiteSquare = TextureRegionFactory.extractFromTexture(liteSquareTexture);
					
			// create tiled texture
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			BuildableBitmapTextureAtlas mBitmapTextureAtlasRed = new BuildableBitmapTextureAtlas(this.getTextureManager(), 128, 64, TextureOptions.NEAREST);
			txCheckerRedTile = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlasRed, this, "checkers_red_tile.png", 2, 1);
			try {
					mBitmapTextureAtlasRed.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
					mBitmapTextureAtlasRed.load();				
			} catch (TextureAtlasBuilderException e){
				Debug.e(e);
			}
			
			// create tiled texture
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			BuildableBitmapTextureAtlas mBitmapTextureAtlasBlack = new BuildableBitmapTextureAtlas(this.getTextureManager(), 128, 64, TextureOptions.NEAREST);
			txCheckerBlackTile = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlasBlack, this, "checkers_black_tile.png", 2, 1);
			try {
					mBitmapTextureAtlasBlack.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
					mBitmapTextureAtlasBlack.load();				
			} catch (TextureAtlasBuilderException e){
				Debug.e(e);
			}
			
		} catch (IOException e) {
			Debug.e(e);
		}
		
	}

	@Override
	protected Scene onCreateScene() {
		
		// draw background
		Sprite backgroundSprite = new Sprite(0, 0, this.txBackground, getVertexBufferObjectManager());
		scene.attachChild(backgroundSprite);
		
		// lay tiles for squares, alternating colors
		boolean isBlack = true;
		for(int i = 0; i < 8; i++) {
			// build row to be added to main array
			ArrayList<CheckerBoardSquare> row = new ArrayList<CheckerBoardSquare>();
			for(int j = 0; j < 8; j++) {
				if(isBlack) { 																				// create black square
					CheckerBoardSquare blackSquare = new CheckerBoardSquare(40 + (j * 50), 40 + (i * 50),
							this.txBlackSquare, getVertexBufferObjectManager());
					row.add(blackSquare);																	// add square to row
					scene.attachChild(blackSquare);
					isBlack = false;																		// toggle color
				} else { 																					// create white square
					CheckerBoardSquare whiteSquare = new CheckerBoardSquare(40 + (j * 50), 40 + (i * 50),
							this.txWhiteSquare, getVertexBufferObjectManager());
					row.add(whiteSquare);																	// add square to row
					scene.attachChild(whiteSquare);
					isBlack = true;																			// toggle color
				}
				
			}
			isBlack = !isBlack;	// toggle color to offset next row from previous
			txSquares.add(row);	// add row to board
		}

		// populate starting positions for checkers
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if( ( i < 3 ) && ( ( i % 2 == 0 && j % 2 != 0) || ( i%2 != 0 && j % 2 == 0 ) ) ) { 			// populate red checker if position is a red starting position
					iNumRed++;
					Checker checker = new Checker(i, j, false, true, 40 + (j * 50), 40 + (i * 50),
							this.txCheckerRedTile, getVertexBufferObjectManager()){
						@Override
						public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
								float pTouchAreaLocalY) { 													// add touch response to checker
							if(this.isTouchEnabled() && !isBlacksTurn) {
								this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2,
									pSceneTouchEvent.getY() - this.getHeight() / 2); 						// follow touch while held
								if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
									checkForValidCollisionWithSquare(this);									// check for valid move when touch is lifted
								}
							}
							return true;
						}
					};
					txSquares.get(i).get(j).setChecker(checker);
					scene.attachChild(checker);
					scene.registerTouchArea(checker);
				} else if( ( i > 4 ) && ( ( i % 2 == 0 && j % 2 != 0) || ( i%2 != 0 && j % 2 == 0 ) ) ) { 	// populate black checker if position is a black starting position
					iNumBlack++;
					Checker checker = new Checker(i, j, true, true, 40 + (j * 50), 40 + (i * 50),
							this.txCheckerBlackTile, getVertexBufferObjectManager()) {
						@Override
						public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
								float pTouchAreaLocalY) {													// add touch response to checker
							if(this.isTouchEnabled() && isBlacksTurn) {
								this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2,
									pSceneTouchEvent.getY() - this.getHeight() / 2); 						// follow touch while held 
								if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
									checkForValidCollisionWithSquare(this); 								// check for valid move when touch is lifted
								}
							}
							return true;
						}
					};
					txSquares.get(i).get(j).setChecker(checker);
					scene.attachChild(checker);
					scene.registerTouchArea(checker);
				}
			}
		}
		
		blackCountText = new Text(0, 0, main_font, "Black: " + iNumBlack, this.getVertexBufferObjectManager());
		blackCountText.setPosition(CAMERA_WIDTH - (blackCountText.getWidth() + 40), 10);
		scene.attachChild(blackCountText);
		
		redCountText = new Text(0, 0, main_font, "Red:   " + iNumRed, this.getVertexBufferObjectManager());
		redCountText.setPosition(CAMERA_WIDTH - (redCountText.getWidth() + 40), blackCountText.getHeight() + 20);
		scene.attachChild(redCountText);
		
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		return scene;
	}
	
	private void checkForValidCollisionWithSquare(Checker checker) {
		
		CheckerBoardSquare targetSquare = null;
		boolean wasMoved = false;
		
		// check for valid black move (or queened red)
		if(checker.isBlack() || checker.isQueened()) {
			if( checker.getCol() > 0 && checker.getRow() > 0 && 													// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1 ) ) &&	// check for collision with valid square
					txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1 ).getChecker() == null ) { 		// check that targeted square is empty
				targetSquare = txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1 );
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();
				checker.setRow(checker.getRow() - 1);
				checker.setCol(checker.getCol() - 1);
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);
				wasMoved = true;
				moveCheckerToSquare(checker, targetSquare);
			}else if( checker.getCol() < 7 && checker.getRow() > 0 && 												// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() - 1 ).get(checker.getCol() + 1 ) ) && 	// check for collision with valid square
					txSquares.get( checker.getRow() - 1 ).get( checker.getCol() + 1 ).getChecker() == null) { 		// check that targeted square is empty
				targetSquare = txSquares.get( checker.getRow() - 1 ).get( checker.getCol() + 1 );
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();
				checker.setRow(checker.getRow() - 1);
				checker.setCol(checker.getCol() + 1);
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);
				wasMoved = true;
				moveCheckerToSquare(checker, targetSquare);
			}
		}
		
		// check for valid red move (or queened black)
		if((!checker.isBlack() || checker.isQueened()) && !wasMoved) {
			if( checker.getCol() < 7 && checker.getRow() < 7 && 													// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() + 1 ).get( checker.getCol() + 1 ) ) &&	// check for collision with valid square
					txSquares.get( checker.getRow() + 1 ).get( checker.getCol() + 1 ).getChecker() == null ) { 		// check that targeted square is empty
				targetSquare = txSquares.get( checker.getRow() + 1 ).get( checker.getCol() + 1 );
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();
				checker.setRow(checker.getRow() + 1);
				checker.setCol(checker.getCol() + 1);
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);
				wasMoved = true;
				moveCheckerToSquare(checker, targetSquare);
			}else if( checker.getCol() > 0 && checker.getRow() < 7 && 												// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() + 1 ).get(checker.getCol() - 1 ) ) && 	// check for collision with valid square
					txSquares.get( checker.getRow() + 1 ).get( checker.getCol() - 1 ).getChecker() == null ) { 		// check that targeted square is empty
				targetSquare = txSquares.get( checker.getRow() + 1 ).get( checker.getCol() - 1 );
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();
				checker.setRow(checker.getRow() + 1);
				checker.setCol(checker.getCol() - 1);
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);
				wasMoved = true;
				moveCheckerToSquare(checker, targetSquare);
			}
		}
		
		// check for valid black jump (or queened red)
		if(!wasMoved && (checker.isBlack() || checker.isQueened())) {
			if( checker.getCol() > 1 && checker.getRow() > 1 && 																// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() - 2 ).get( checker.getCol() - 2 ) ) &&				// check for collision with valid square
					txSquares.get( checker.getRow() - 2 ).get( checker.getCol() - 2 ).getChecker() == null    &&				// check that targeted square is empty
					txSquares.get( checker.getRow() - 1).get( checker.getCol() - 1).getChecker() != null      &&				// check that there is a piece to jump
					txSquares.get( checker.getRow() - 1).get( checker.getCol() - 1).getChecker().isBlack() != checker.isBlack()	// check that piece being jumped is the opposite color
					) { 		
				targetSquare = txSquares.get( checker.getRow() - 2 ).get( checker.getCol() - 2 );								// save reference to target square
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();										// remove piece from current square
				checker.setRow(checker.getRow() - 2);																			// update piece's new row
				checker.setCol(checker.getCol() - 2);																			// update piece's new column
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);										// add piece to new square
				wasMoved = true;																								// flag piece as moved successfully
				moveCheckerToSquare(checker, targetSquare);																		// move piece's graphic to new square
				scene.unregisterTouchArea(txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).getChecker());		// remove touch area for jumped piece
				txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).getChecker().detachSelf();						// remove jumped piece's graphic from scene
				if(txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).getChecker().isBlack())						// update count for removed piece
					iNumBlack--;
				else
					iNumRed--;
				txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).removeChecker();								// remove jumped piece from square
			}else if( checker.getCol() < 6 && checker.getRow() > 1 && 															// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() - 2 ).get(checker.getCol() + 2 ) )     &&				// check for collision with valid square
					txSquares.get( checker.getRow() - 2 ).get( checker.getCol() + 2 ).getChecker() == null       &&				// check that targeted square is empty
							txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).getChecker() != null &&				// check that there is a piece to jump
					txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).getChecker().isBlack() != checker.isBlack() // check that piece being jumped is the opposite color
					) {
				targetSquare = txSquares.get( checker.getRow() - 2 ).get( checker.getCol() + 2 );								// save reference to target square
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();										// remove piece from current square
				checker.setRow(checker.getRow() - 2);																			// update piece's new row
				checker.setCol(checker.getCol() + 2);																			// update piece's new column
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);										// add piece to new square
				wasMoved = true;																								// flag piece as moved successfully
				moveCheckerToSquare(checker, targetSquare);																		// move piece's graphic to new square
				scene.unregisterTouchArea(txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).getChecker());		// remove touch area for jumped piece
				txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).getChecker().detachSelf();						// remove jumped piece's graphic from scene
				if(txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).getChecker().isBlack())						// update count for removed piece
					iNumBlack--;
				else
					iNumRed--;
				txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).removeChecker();								// remove jumped piece from square
			}
		}
		
		// check for valid red jump (or queened black)
		if(!wasMoved && (!checker.isBlack() || checker.isQueened())) {
			if( checker.getCol() < 6  && checker.getRow() < 6 && 																// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() + 2 ).get( checker.getCol() + 2 ) ) &&				// check for collision with valid square
					txSquares.get( checker.getRow() + 2 ).get( checker.getCol() + 2 ).getChecker() == null    &&				// check that targeted square is empty
					txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).getChecker() != null      &&				// check that there is a piece to jump
					txSquares.get( checker.getRow() + 1).get( checker.getCol() + 1).getChecker().isBlack() != checker.isBlack()	// check that piece being jumped is the opposite color
					) { 		
				targetSquare = txSquares.get( checker.getRow() + 2 ).get( checker.getCol() + 2 );								// save reference to target square
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();										// remove piece from current square
				checker.setRow(checker.getRow() + 2);																			// update piece's new row
				checker.setCol(checker.getCol() + 2);																			// update piece's new column
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);										// add piece to new square
				wasMoved = true;																								// flag piece as moved successfully
				moveCheckerToSquare(checker, targetSquare);																		// move piece's graphic to new square
				scene.unregisterTouchArea(txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1).getChecker());		// remove touch area for jumped piece
				txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1).getChecker().detachSelf();						// remove jumped piece's graphic from scene
				if(txSquares.get( checker.getRow() - 1 ).get( checker.getCol() - 1).getChecker().isBlack()) {					// update count for removed piece
					iNumBlack--;
					blackCountText.setText("Black: " + iNumBlack);
				}
				else {
					iNumRed--;
					redCountText.setText("Red:   " + iNumRed);
				}
				txSquares.get( checker.getRow() - 1).get( checker.getCol() - 1).removeChecker();								// remove jumped piece from square
			}else if( checker.getCol() > 1 && checker.getRow() < 6 && 															// check that we are not going to go out of bounds on the array
					checker.collidesWith( txSquares.get( checker.getRow() + 2 ).get(checker.getCol() - 2 ) ) && 				// check for collision with valid square
					txSquares.get( checker.getRow() + 2 ).get( checker.getCol() - 2 ).getChecker() == null   &&					// check that targeted square is empty
					txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).getChecker() != null     &&					// check that there is a piece to jump
					txSquares.get( checker.getRow() + 1).get( checker.getCol() - 1).getChecker().isBlack() != checker.isBlack() // check that piece being jumped is the opposite color
					) {
				targetSquare = txSquares.get( checker.getRow() + 2 ).get( checker.getCol() - 2 );								// save reference to target square
				txSquares.get( checker.getRow() ).get( checker.getCol() ).removeChecker();										// remove piece from current square
				checker.setRow(checker.getRow() + 2);																			// update piece's new row
				checker.setCol(checker.getCol() - 2);																			// update piece's new column
				txSquares.get(checker.getRow()).get(checker.getCol()).setChecker(checker);										// add piece to new square
				wasMoved = true;																								// flag piece as moved successfully
				scene.unregisterTouchArea(txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).getChecker());		// remove touch area for jumped piece
				txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).getChecker().detachSelf();						// remove jumped piece's graphic from scene
				if(txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).getChecker().isBlack()) {					// update count for removed piece
					iNumBlack--;
					blackCountText.setText("Black: " + iNumBlack);
				}
				else {
					iNumRed--;
					redCountText.setText("Red:   " + iNumRed);
				}
				txSquares.get( checker.getRow() - 1).get( checker.getCol() + 1).removeChecker();								// remove jumped piece from square
			}
		}
		
		if (!wasMoved) { // if valid move was not found, move back to originating square
			targetSquare = txSquares.get( checker.getRow() ).get( checker.getCol() );
			moveCheckerToSquare(checker, targetSquare);
		} else {
			isBlacksTurn = !isBlacksTurn;
		}
		
		if(iNumRed == 0) {
			gameToast("Congratulations, black has won!");
			scene.clearTouchAreas();
		} else if (iNumBlack == 0) {
			gameToast("Congratulations, red has won!");
			scene.clearTouchAreas();
		}
	}
	
	private void moveCheckerToSquare(Checker checker, Sprite targetSquare) {
		checker.setPosition(targetSquare.getX() + targetSquare.getWidth()/2 -
				checker.getWidth()/2, targetSquare.getY() + targetSquare.getHeight() -
				checker.getHeight());
		if((checker.isBlack() && checker.getRow() == 0) ||
				(!checker.isBlack() && checker.getRow() == 7)) {
			checker.setQueened(true);
			checker.setCurrentTileIndex(1);
		}
		
	}
	
	public void gameToast(final String msg) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

}
