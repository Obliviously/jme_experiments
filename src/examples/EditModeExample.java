package examples;

import appstates.EditAppState;
import appstates.SelectAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
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
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author Fabian Rauscher
 */
public class EditModeExample extends SimpleApplication
{
    private BaseAppState editAppState = new EditAppState();
    private BaseAppState selectAppState = new SelectAppState();

    public static void main(String[] args)
    {
        EditModeExample app = new EditModeExample();
        AppSettings appSetting = new AppSettings(true);
        appSetting.setFrameRate(60);
        app.showSettings = false;
        app.setSettings(appSetting);
        app.start();

    }

    @Override
    public void simpleInitApp()
    {
        //load world
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        Geometry boxGeo;
        Node box = (Node) assetManager.loadModel("Models/box.blend");
        boxGeo = (Geometry) (((Node) ((Node) box.getChild(0)).getChild(0)).getChild(0));
        boxGeo.scale(1);
        boxGeo.setMaterial(mat);
        rootNode.attachChild(box);
        VertexUtils.changeColorOfVertices(boxGeo, null);

        //initial state
        this.getStateManager().attach(selectAppState);

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
