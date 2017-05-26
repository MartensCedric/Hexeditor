package com.martenscedric.hexeditor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cedricmartens.hexmap.coordinate.Point;
import com.cedricmartens.hexmap.hexagon.*;
import com.cedricmartens.hexmap.map.HexMap;
import com.cedricmartens.hexmap.map.grid.HexGridBuilder;
import com.martenscedric.hexeditor.tile.TileData;

public class Hexeditor extends ApplicationAdapter {

	SpriteBatch batch;
	private HexMap<TileData> grid;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		batch = new SpriteBatch();
		HexGridBuilder<TileData> hexGridBuilder = new HexGridBuilder<TileData>()
				.setHeight(9)
				.setWidth(9)
				.setShape(HexagonShape.HEXAGON)
				.setStyle(new HexStyle(80, HexagonOrientation.FLAT_TOP));

		grid = hexGridBuilder.build();

		camera.translate(-400, 0);
		camera.zoom = 1.4f;
		camera.update();
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(0.25f, 0.5f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		for(int i = 0; i < grid.getHexs().length; i++)
		{
			Hexagon<TileData> data = grid.getHexs()[i];

			HexGeometry geo = data.getHexGeometry();

			Point p0 = (Point) geo.getPoints().toArray()[0];
			Point pLast = (Point) geo.getPoints().toArray()[geo.getPoints().size() - 1];
			shapeRenderer.line((float)p0.x, (float)p0.y,
					(float)pLast.x, (float)pLast.y,
					Color.BLACK, Color.BLACK);
			for(int j = 1; j < geo.getPoints().size(); j++)
			{
				Point current = (Point)geo.getPoints().toArray()[j];
				Point precedent = (Point)geo.getPoints().toArray()[j - 1];
				shapeRenderer.line((float)current.x, (float)current.y,
						(float)precedent.x, (float)precedent.y,
						Color.BLACK, Color.BLACK);
			}
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
