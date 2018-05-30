package examples;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;

/**
 * Creates a simple custom mesh with vertex coloring.
 *
 *
 * @author Fabian Rauscher
 */
public class CustomMesh extends SimpleApplication
{

    public static void main(String[] args)
    {
        CustomMesh app = new CustomMesh();
        AppSettings appSetting = new AppSettings(true);
        appSetting.setFrameRate(60);
        app.showSettings = false;
        app.setSettings(appSetting);
        app.start();

    }

    @Override
    public void simpleInitApp()
    {
        Mesh mesh = new Mesh();

        Vector3f[] vertices = new Vector3f[12];

        //Center
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(3, 0, 0);
        vertices[2] = new Vector3f(0, 3, 0);
        vertices[3] = new Vector3f(3, 3, 0);

        //Left
        vertices[4] = new Vector3f(-3, 0, 1);
        vertices[5] = new Vector3f(-3, 3, 1);

        //Top
        vertices[6] = new Vector3f(0, 6, 1);
        vertices[7] = new Vector3f(3, 6, 1);

        //Right
        vertices[8] = new Vector3f(6, 3, -1);
        vertices[9] = new Vector3f(6, 0, -1);

        //Bottom
        vertices[10] = new Vector3f(0, -3, 1);
        vertices[11] = new Vector3f(3, -3, 1);

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

        /*
         * Every triplet defines one triangle. It is important to define the
         * vertices counter clockwise otherwise the triangle faces in the wrong
         * direction. The numbers represent the index of the vertices array.
         */
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

        Geometry geo = new Geometry("CustomMesh", mesh);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        //mat.setColor("Color", ColorRGBA.Blue);

        int colorIndex = 0;
        float[] colorArray = new float[mesh.getVertexCount() * 4];
        for (int i = 0; i < 4; i++)
        {
            // Red value (is increased by .2 on each next vertex here)
            colorArray[colorIndex++] = 0.1f + (.2f * i);
            // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 0.9f - (0.2f * i);
            // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 0.5f;
            // Alpha value (no transparency set here)
            colorArray[colorIndex++] = 1.0f;
        }
        mesh.setBuffer(Type.Color, 4, colorArray);
        mesh.setStatic();
        geo.setMaterial(mat);
        geo.updateModelBound();
        rootNode.attachChild(geo);
    }

    @Override
    public void simpleUpdate(float tpf)
    {
        //add update code
    }

    @Override
    public void simpleRender(RenderManager rm)
    {
        //add render code
    }
}
