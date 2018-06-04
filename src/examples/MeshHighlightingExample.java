package examples;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import utils.MeshUtils;
import utils.VertexUtils;

/**
 * This is the MeshHighlightingExample Class of your Game. You should only do
 * initialization here. Move your Logic into AppStates or Controls
 *
 * @author Fabian Rauscher
 */
public class MeshHighlightingExample extends SimpleApplication implements AnalogListener
{
    public static void main(String[] args)
    {
        MeshHighlightingExample app = new MeshHighlightingExample();
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

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);

        Geometry boxGeo;
        Node box = (Node) assetManager.loadModel("Models/box.blend");
        boxGeo = (Geometry) (((Node) ((Node) box.getChild(0)).getChild(0)).getChild(0));
        boxGeo.scale(1);
        boxGeo.setMaterial(mat);
        rootNode.attachChild(box);
        VertexUtils.changeColorOfVertices(boxGeo, null);
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
                MeshUtils.calcFlatArea(results.getClosestCollision());
            }
        }
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
