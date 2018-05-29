package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication implements AnalogListener
{

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
            colorArray[colorIndex++] = 0.9f;
            // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 0.1f;
            // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 0.1f;
            // Alpha value (no transparency set here)
            colorArray[colorIndex++] = 1.0f;
        }
        mesh.setBuffer(Type.Color, 4, colorArray);
        mesh.setStatic();
        geo.setMaterial(mat);
        geo.updateModelBound();
        rootNode.attachChild(geo);

        Geometry boxGeo;
        Node box = (Node) assetManager.loadModel("Models/box.j3o");
        boxGeo = (Geometry) (((Node) ((Node) box.getChild(0)).getChild(0)).getChild(0));
        boxGeo.setMaterial(mat);
        rootNode.attachChild(box);
        //this.changeColorOfVertices(boxGeo, null);
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

                Mesh meshHit = closest.getGeometry().getMesh();
                final int TRIANGLECOUNT = meshHit.getTriangleCount();

                Vector3f contactPoint = closest.getContactPoint();

                Triangle triangleHit = closest.getTriangle(null);
                final Vector3f REF_NORMAL = triangleHit.getNormal();

                ArrayList<Vector3f> verticesHit = new ArrayList<>();
                ArrayList<Integer> verticesHitIndex = new ArrayList<>();
                //Add initial vertices to start search from
                verticesHit.add(triangleHit.get1());
                verticesHit.add(triangleHit.get2());
                verticesHit.add(triangleHit.get3());

                /*
                 * This algorithm searches for every triangle that is adjacent
                 * to the hit triangle and has the same normal value. This means
                 * you get the biggest possible flat plane that contains the hit
                 * triangle.
                 */
                Boolean added;
                Vector3f v3_new;
                do // as long as new vertices were added
                {
                    added = false;
                    /*
                     * iterate over every continues vertice pair and find
                     * triangles in the list that have exactly these two
                     * vertices in common and have the same normal value.
                     */
                    for (int i = 0; i < verticesHit.size() - 1; i++)
                    {
                        v3_new = missingVertexNormalAdjacentTriangle(verticesHit.get(i), verticesHit.get(i + 1), TRIANGLECOUNT, meshHit, REF_NORMAL, verticesHit);
                        if (v3_new != null)
                        {
                            verticesHit.add(i + 1, v3_new);
                            added = true;
                        }

                    }
                    //Also compare the last and first vertex in the list
                    v3_new = missingVertexNormalAdjacentTriangle(verticesHit.get(0), verticesHit.get(verticesHit.size() - 1), TRIANGLECOUNT, meshHit, REF_NORMAL, verticesHit);
                    if (v3_new != null)
                    {
                        verticesHit.add(v3_new);
                        added = true;
                    }
                }
                while (added);
                changeColorOfVertices(closest.getGeometry(), verticesHit);
            }
        }
    }

    /**
     * Takes the two given vertices and searches for them in the given mesh. If
     * a triangle is found that contains both vertices and one new one and has
     * the same normal value the new vertice will be returned. If no vertice
     * matching the conditions can be found null is returned.
     *
     * @param v1
     * @param v2
     * @param triangleCount
     * @param meshHit
     * @param referenceNormal
     *
     * @return
     */
    private Vector3f missingVertexNormalAdjacentTriangle(Vector3f v1, Vector3f v2, int triangleCount, Mesh meshHit, Vector3f referenceNormal, ArrayList<Vector3f> verticesHit)
    {
        Triangle triangleMesh = new Triangle();
        for (int j = 0; j < triangleCount; j++)
        {
            Vector3f v3_new;
            // 1. get new triangle
            meshHit.getTriangle(j, triangleMesh);
            // 2. check if it is adjacent to the current vertices
            if ((v3_new = triangleMatchingTwo(triangleMesh, v1, v2)) != null)
            {
                // 3. check if its normal (direction) is equal
                if (triangleMesh.getNormal().distance(referenceNormal) <= 0.001f)
                {
                    if (!verticesHit.contains(v3_new))
                    {
                        return v3_new;
                    }

                }
            }
        }
        return null;
    }

    /**
     * Compares the given triangle with the two vertices. If both vertices are
     * part of the triangle it returns the third one. Else NULL is returned.
     *
     * @param triangle
     * @param v1
     * @param v2
     *
     * @return
     */
    public Vector3f triangleMatchingTwo(Triangle triangle, Vector3f v1, Vector3f v2)
    {
        // t3
        if (triangle.get1().equals(v1) && triangle.get2().equals(v2))
        {
            return triangle.get3();
        }
        // t3
        if (triangle.get1().equals(v2) && triangle.get2().equals(v1))
        {
            return triangle.get3();
        }
        // t2
        if (triangle.get1().equals(v1) && triangle.get3().equals(v2))
        {
            return triangle.get2();
        }
        // t2
        if (triangle.get1().equals(v2) && triangle.get3().equals(v1))
        {
            return triangle.get2();
        }
        // t1
        if (triangle.get2().equals(v1) && triangle.get3().equals(v2))
        {
            return triangle.get1();
        }
        // t1
        if (triangle.get2().equals(v2) && triangle.get3().equals(v1))
        {
            return triangle.get1();
        }
        return null;
    }

    private void changeColorOfVertices(Geometry geometry, ArrayList<Vector3f> changeVertices)
    {
        Mesh mesh = geometry.getMesh();
        mesh.clearBuffer(Type.Color);

        FloatBuffer colorArray = BufferUtils.createFloatBuffer(new float[4 * mesh.getVertexCount()]);
        FloatBuffer vertices = (FloatBuffer) mesh.getBuffer(Type.Position).getData();
        System.out.println(colorArray.limit() + "|" + mesh.getVertexCount() + "|" + changeVertices.size());
        float color = 0.8f;
        float offColor = 0.2f;
        int j;
        for (int i = 0; i < mesh.getVertexCount(); i++)
        {
            j = i * 4; // RGBA Value for every Vertex
            if (changeVertices == null || changeVertices.contains(new Vector3f(vertices.get(i * 3), vertices.get(i * 3 + 1), vertices.get(i * 3 + 2))))
            {
                colorArray.put(j, color);
                colorArray.put(j + 1, color);
                colorArray.put(j + 2, color);
                colorArray.put(j + 3, 1.0f);
            }
            else
            {
                colorArray.put(j, offColor);
                colorArray.put(j + 1, offColor);
                colorArray.put(j + 2, offColor);
                colorArray.put(j + 3, 1.0f);
            }
        }
        mesh.setBuffer(Type.Color, 4, colorArray);
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
