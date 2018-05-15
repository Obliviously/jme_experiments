package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.TreeMap;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication implements AnalogListener
{
    private BulletAppState bulletAppState;

    public static void main(String[] args)
    {
        Main app = new Main();
        AppSettings appSetting = new AppSettings(true);
        appSetting.setFrameRate(60);
        app.showSettings = false;
        app.setSettings(appSetting);
        app.start();
    }

    @Override
    public void simpleInitApp()
    {
        setUpKeys();
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        Mesh mesh = new Mesh();

        Vector3f[] vertices = new Vector3f[12];

        //Center
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(3, 0, 0);
        vertices[2] = new Vector3f(0, 3, 0);
        vertices[3] = new Vector3f(3, 3, 0);

        //Left
        vertices[4] = new Vector3f(-3, 0, 0);
        vertices[5] = new Vector3f(-3, 3, 0);

        //Top
        vertices[6] = new Vector3f(0, 6, 0);
        vertices[7] = new Vector3f(3, 6, 0);

        //Right
        vertices[8] = new Vector3f(6, 3, 0);
        vertices[9] = new Vector3f(6, 0, 0);

        //Bottom
        vertices[10] = new Vector3f(0, -3, 0);
        vertices[11] = new Vector3f(3, -3, 0);

        Vector2f[] texCoord = new Vector2f[12];
        //Center
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(3, 0);
        texCoord[2] = new Vector2f(0, 3);
        texCoord[3] = new Vector2f(3, 3);

        //Left
        texCoord[4] = new Vector2f(-3, 0);
        texCoord[5] = new Vector2f(-3, 3);

        //Top
        texCoord[6] = new Vector2f(0, 6);
        texCoord[7] = new Vector2f(3, 6);

        //Right
        texCoord[8] = new Vector2f(6, 3);
        texCoord[9] = new Vector2f(6, 0);

        //Bottom
        texCoord[10] = new Vector2f(0, -3);
        texCoord[11] = new Vector2f(3, -3);

        int[] indexes =
        {
            2, 0, 1,
            1, 3, 2,
            5, 4, 0,
            2, 5, 0,
            6, 2, 3,
            7, 6, 3,
            8, 3, 1,
            9, 8, 1,
            10, 11, 0,
            11, 1, 0
        };

        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));

        //Quad quad = new Quad(1,1); //This replaces all of the above
        Geometry geo = new Geometry("CustomMesh", mesh);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        //mat.setColor("Color", ColorRGBA.Blue);

        int colorIndex = 0;
        float[] colorArray = new float[mesh.getVertexCount() * 4];
        System.out.println(mesh.getVertexCount());
        for (int i = 0; i < 4; i++)
        {
            // Red value (is increased by .2 on each next vertex here)
            colorArray[colorIndex++] = 0.1f + (.2f * i);
            // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 0.9f + (0.2f * i);
            // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 0.9f;
            // Alpha value (no transparency set here)
            colorArray[colorIndex++] = 1.0f;
        }
        mesh.setBuffer(Type.Color, 4, colorArray);
        mesh.setStatic();
        geo.setMaterial(mat);
        geo.updateModelBound();
        bulletAppState.getPhysicsSpace().add(geo);
        rootNode.attachChild(geo);
    }

    private void setUpKeys()
    {

        inputManager.addMapping("MOUSE_MOVE", new MouseAxisTrigger(mouseInput.AXIS_X, true));
        inputManager.addMapping("MOUSE_MOVE", new MouseAxisTrigger(mouseInput.AXIS_Y, false));

        inputManager.addListener(this, "MOUSE_MOVE");

    }

    @Override
    public void onAnalog(String name, float value, float tpf)
    {
        if (name.equals("MOUSE_MOVE"))
        {
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            rootNode.collideWith(ray, results);
            if (results.size() > 0)
            {
                CollisionResult closest = results.getClosestCollision();
                Geometry geometry = closest.getGeometry();
                Vector3f contactPoint = closest.getContactPoint();
                FloatBuffer vertices = (FloatBuffer) geometry.getMesh().getBuffer(Type.Position).getData();
                Vector3f vertice;
                TreeMap verticeDistanceMap = new TreeMap();
                for (int i = 0, n = 0; i < vertices.limit() - 3; i += 3, n++)
                {
                    vertice = new Vector3f(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2));
                    verticeDistanceMap.put(contactPoint.distance(vertice), i);
                }
                int[] verticeIndex =
                {
                    (int) verticeDistanceMap.pollLastEntry().getValue(),
                    (int) verticeDistanceMap.pollLastEntry().getValue(),
                    (int) verticeDistanceMap.pollLastEntry().getValue(),
                    (int) verticeDistanceMap.pollLastEntry().getValue()
                };
                changeColorOfPolygon(geometry, verticeIndex);
            }
        }
    }

    private void changeColorOfPolygon(Geometry geometry, int[] verticesPos)
    {
        if (verticesPos.length != 4)
        {
            System.err.println("Need exactly 4 vertices to change color");
        }
        FloatBuffer colorArray = (FloatBuffer) geometry.getMesh().getBuffer(Type.Color).getData();

        int position;
        for (int i = 0; i < verticesPos.length; i++)
        {
            System.out.println(verticesPos[i]);
            position = verticesPos[i];
            colorArray.put(position, 0.5f);
            colorArray.put(position + 1, 0.5f);
            colorArray.put(position + 2, 0.5f);
            colorArray.put(position + 3, 1.0f);
        }
        geometry.getMesh().setBuffer(Type.Color, 4, colorArray);
        geometry.updateModelBound();

    }

    @Override
    public void simpleUpdate(float tpf)
    {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm)
    {
        //TODO: add render code
    }
}
