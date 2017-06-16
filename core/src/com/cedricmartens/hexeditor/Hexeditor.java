package com.cedricmartens.hexeditor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cedricmartens.hexeditor.map.Map;
import com.cedricmartens.hexeditor.tile.BuildingType;
import com.cedricmartens.hexeditor.tile.TileData;
import com.cedricmartens.hexmap.coordinate.Point;
import com.cedricmartens.hexmap.hexagon.*;
import com.cedricmartens.hexmap.map.HexMap;
import com.cedricmartens.hexmap.map.freeshape.HexFreeShapeBuilder;
import com.cedricmartens.hexmap.map.grid.HexGridBuilder;
import com.cedricmartens.hexeditor.map.Objective;
import com.cedricmartens.hexeditor.tile.TileType;
import flexjson.JSONSerializer;

import java.util.ArrayList;
import java.util.List;

public class Hexeditor extends ApplicationAdapter {

	SpriteBatch batch;
	PolygonSpriteBatch pSpriteBatch;
	private HexMap<TileData> grid;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private BitmapFont font;

	@Override
	public void create () {
		font = new BitmapFont();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		pSpriteBatch = new PolygonSpriteBatch();
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
		if(Gdx.input.isKeyJustPressed(Input.Keys.S))
		{
			saveMap();
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.W))
		{
			Vector3 loc = camera.unproject(new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), 0));
			Hexagon<TileData> hex = grid.getAt(new Point(loc.x, loc.y));

			if(hex != null)
			{
				if(hex.getHexData() == null)
				{
					hex.setHexData(new TileData(hex));
					hex.getHexData().setBuildingType(BuildingType.NONE);
					hex.getHexData().setTileType(TileType.GRASS);
				}else{
					TileType tileType = hex.getHexData().getTileType();
					if(tileType == null)
					{
						hex.getHexData().setTileType(TileType.GRASS);
					}else if(tileType.ordinal() != TileType.values().length - 1){

						hex.getHexData().setTileType(TileType.values()[tileType.ordinal() + 1]);
					}else{
						hex.setHexData(null);
					}
				}
			}
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.E))
		{
			Vector3 loc = camera.unproject(new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), 0));
			Hexagon<TileData> hex = grid.getAt(new Point(loc.x, loc.y));

			if(hex != null)
			{
				if(hex.getHexData() != null)
				{
					BuildingType buildingType = hex.getHexData().getBuildingType();
					if(buildingType.ordinal() != BuildingType.values().length - 1){
						hex.getHexData().setBuildingType(BuildingType.values()[buildingType.ordinal() + 1]);
					}else{
						hex.getHexData().setBuildingType(BuildingType.NONE);
					}
				}
			}
		}


		Gdx.gl.glClearColor(0.25f, 0.5f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);

		for(int i = 0; i < grid.getHexs().length; i++)
		{
			Hexagon<TileData> data = grid.getHexs()[i];

			HexGeometry geo = data.getHexGeometry();

			Point p0 = (Point) geo.getPoints().toArray()[0];
			Point pLast = (Point) geo.getPoints().toArray()[geo.getPoints().size() - 1];
			shapeRenderer.begin();
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
			shapeRenderer.end();

			if(data.getHexData() != null)
			{

				if(data.getHexData().getTileType() != null)
				{
					pSpriteBatch.setProjectionMatrix(camera.combined);
					pSpriteBatch.begin();

					int color = 0;
					switch (data.getHexData().getTileType()) {
						case GRASS:
							color = 0x11FF38FF;
							break;
						case WATER:
							color = 0x4286F4FF;
							break;
						case SAND:
							color = 0xe8d17fFF;
							break;
						case FOREST:
							color = 0x284919FF;
							break;
					}

					data.getHexData().getSprite(color).draw(pSpriteBatch);

					pSpriteBatch.end();
				}

				batch.begin();
				font.draw(batch, data.getHexData().getBuildingType().name(), (int)geo.getMiddlePoint().x - 15, (int)geo.getMiddlePoint().y + 15);
				batch.end();
			}
		}
	}

	private void saveMap()
	{
		HexFreeShapeBuilder<TileData> freeShapeBuilder = new HexFreeShapeBuilder<TileData>();
		freeShapeBuilder.setStyle(grid.getStyle());


		List<BuildingType> buildingTypes = new ArrayList<BuildingType>();
		List<TileType> tileTypes = new ArrayList<TileType>();

		for(int i = 0; i < grid.getHexs().length; i++)
		{
			Hexagon<TileData> h = grid.getHexs()[i];
			if(h.getHexData() != null) {
				freeShapeBuilder.getHexagons().add(
						h.getHexGeometry().getMiddlePoint()
				);
				buildingTypes.add(h.getHexData().getBuildingType());
				tileTypes.add(h.getHexData().getTileType());
			}
		}

		Map map = new Map();
		map.setBuilder(freeShapeBuilder);
		map.setCalculateScore(true);
		map.setObjectives(new Objective[]{
				new Objective(new int[]{0, 0, 0, 0, 0, 0, 0, 0}, 25),
				new Objective(new int[]{0, 0, 0, 0, 0, 0, 0, 0}, 30),
				new Objective(new int[]{0, 0, 0, 0, 0, 0, 0, 0}, 35)
		});

		BuildingType[] buildingTypesArr = new BuildingType[buildingTypes.size()];

		for(int i = 0; i < buildingTypes.size(); i++)
			buildingTypesArr[i] = buildingTypes.get(i);

		TileType[] tileTypesArr = new TileType[tileTypes.size()];

		for(int i = 0; i < tileTypes.size(); i++)
			tileTypesArr[i] =  tileTypes.get(i);

		map.setBuildingTypes(buildingTypesArr);
		map.setTileTypes(tileTypesArr);

		JSONSerializer serializer = new JSONSerializer();
		String serialized = serializer.deepSerialize(map);

		String res = serialized.replace("hexeditor", "hexpert");

		Gdx.files.local(
				Integer.toString(map.hashCode()) + ".hexmap"
		).writeString(res, false);
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
