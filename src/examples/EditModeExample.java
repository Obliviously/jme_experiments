package examples;

import appstates.SelectAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import utils.VertexUtils;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author Fabian Rauscher
 */
public class EditModeExample extends SimpleApplication
{
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
        //innitialize input mappings
        initInputMappings();

        //load world
        initWorld();

        //initial state
        this.getStateManager().attach(new SelectAppState());
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

    private void initWorld()
    {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        Geometry boxGeo;
        Node box = (Node) assetManager.loadModel("Models/box.blend");
        boxGeo = (Geometry) (((Node) ((Node) box.getChild(0)).getChild(0)).getChild(0));
        boxGeo.setMaterial(mat);
        rootNode.attachChild(box);
        VertexUtils.changeColorOfVertices(boxGeo, null);
    }

    private void initInputMappings()
    {
        inputManager.addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MOUSE_LEFT_CLICK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("SWITCH_MODE", new KeyTrigger(KeyInput.KEY_TAB));
    }
}
