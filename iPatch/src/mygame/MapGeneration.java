/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import java.util.Random;
import fastnoise.FastNoise;


/**
 *
 * @author Joseph Leigh
 */
public class MapGeneration 
        
     
{
    static BulletAppState bulletAppState;
    static Node rootNode;
    static AssetManager assetManager;
    static int xAxis = 200;
    static int yAxis = 64;
    static int zAxis = 200;
    static int[][] heightMap = new int[xAxis][zAxis];

    
    public static void loadArrayIntoWorld(TerrainType[][][] worldArray) 
    {
        for (int x = 0; x < worldArray.length; x += 1)
        {
            for (int y = 0; y < worldArray[x].length; y += 1)
            {
                for (int z = 0; z < worldArray[x][y].length; z += 1)
                {
                    if (worldArray[x][y][z] != null)
                    {
                        loadBox(x, y, z, worldArray[x][y][z]); 
                    }
                }
            }
        }
    }
    
    
    private static void loadBox(int xCo, int yCo, int zCo, TerrainType skin)
    {
        //System.out.println("in here");
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry box_geom = new Geometry("box", box);
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        if (skin == TerrainType.SAND)
        {
            mat.setColor("Color", ColorRGBA.Yellow);
        }
        else
        {
            mat.setColor("Color", ColorRGBA.Green);
        }
        box_geom.setMaterial(mat);
        box_geom.setLocalTranslation(xCo+0.5f, yCo+0.5f, zCo+0.5f);
        rootNode.attachChild(box_geom);
        RigidBodyControl box_phys = new RigidBodyControl(0f);
        box_geom.addControl(box_phys);
        bulletAppState.getPhysicsSpace().add(box_phys);
    }
    
    public static TerrainType[][][] generateArray()
    {
        generateHeightMap();
        TerrainType[][][] mapArray = new TerrainType[MapGeneration.xAxis][MapGeneration.yAxis][MapGeneration.zAxis];
        for (int x = 0; x < MapGeneration.heightMap.length; x += 1)
        {
            for (int y = 0; y< MapGeneration.heightMap[x].length; y+= 1)
            {
                if (MapGeneration.heightMap[x][y] > 0)
                {
                    if (MapGeneration.heightMap[x][y] <= 5)
                    {
                        mapArray[x][MapGeneration.heightMap[x][y]][y] = TerrainType.SAND;
                    }
                    else
                    {
                        mapArray[x][MapGeneration.heightMap[x][y]][y] = TerrainType.GRASS;
                    }
                }
            }
        }
        return mapArray;
    }
   
    private static void generateHeightMap()
    {
        Random rand = new Random();
        FastNoise.seed = rand.nextInt(100000000) + 1;
        FastNoise.init();
        for (int x = 0; x < MapGeneration.heightMap.length; x += 1)
        {
            for (int y = 0; y< MapGeneration.heightMap[x].length; y += 1)
            {
                //heightMap[x][y] = (int)(64.0 * FastNoise.noise(0.1, 1.0));
                if (((int)(32.0 * FastNoise.noise(x / 64.0f, y/64.0f)) > 0))
                {
                    MapGeneration.heightMap[x][y] = (int)(32.0 * FastNoise.noise(x / 64.0f, y/64.0f));
                }
            }    
        }
    }
    
    private static void findAccesable()
    {
        
    }
}
