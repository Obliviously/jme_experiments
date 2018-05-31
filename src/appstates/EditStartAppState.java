/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appstates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import examples.EditModeExample;
import java.util.ArrayList;
import utils.AbstractInputController;
import utils.MeshUtils;
import utils.VertexUtils;

/**
 *
 * @author Fabian Rauscher
 */
public class EditStartAppState extends BaseAppState
{
    private EditModeExample app;
    private InputController input;
    private Node rootNode;
    private AssetManager assetManager;

    private Vector3f lastContactPoint;
    private ArrayList<Vector3f> currSelectedVertices;
    private ArrayList<Geometry> marks = new ArrayList<>();

    private final String MARKNAME = "mark";
    private int markCounter = 0;

    public EditStartAppState(Vector3f lastContactPoint, ArrayList<Vector3f> currSelectedVertices)
    {
        this.lastContactPoint = lastContactPoint;
        this.currSelectedVertices = currSelectedVertices;
    }

    @Override
    protected void initialize(Application app)
    {
        this.app = (EditModeExample) app;
        this.input = new InputController();
        input.setUpInput();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();

        addMark(lastContactPoint);
    }

    /**
     * A red ball that marks the last confirmed spot.
     */
    protected void addMark(Vector3f point)
    {
        Sphere sphere = new Sphere(15, 15, 0.1f);
        Geometry mark = new Geometry(MARKNAME + markCounter++, sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        mark.setLocalTranslation(point);
        marks.add(mark);
        rootNode.attachChild(mark);
    }

    @Override
    protected void cleanup(Application app)
    {
        input.cleanUpInput();
    }

    @Override
    protected void onEnable()
    {
    }

    @Override
    protected void onDisable()
    {
    }

    private class InputController extends AbstractInputController implements AnalogListener, ActionListener
    {
        @Override
        public void onAnalog(String name, float value, float tpf)
        {
        }

        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            if (name.equals("SWITCH_MODE") && !isPressed)
            {
                getStateManager().detach(getState(EditStartAppState.class));
                getStateManager().attach(new SelectAppState());
            }

            if (name.equals("MOUSE_LEFT_CLICK") && !isPressed)
            {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
                app.getRootNode().collideWith(ray, results);

                if (results.size() > 0)
                {
                    CollisionResult closest = results.getClosestCollision();
                    Vector3f newContactPoint = closest.getContactPoint();
                    ArrayList<Vector3f> newSelectedVertices = MeshUtils.calcFlatArea(closest);
                    if (newSelectedVertices.equals(currSelectedVertices))
                    {
                        addMark(newContactPoint);
                    }
                    else
                    {
                        //calculate intersection of the two vertice lists.
                        newSelectedVertices.retainAll(currSelectedVertices);
                        System.out.println(newSelectedVertices.size());
                        switch (newSelectedVertices.size())
                        {
                            case 0:
                                System.out.println("cant do that");
                                break;
                            case 1:
                                addMark(newSelectedVertices.get(0));
                                break;
                            case 2:
                                addMark(newSelectedVertices.get(0));
                                addMark(newSelectedVertices.get(1));
                                break;
                            default:
                                System.out.println("this shouldnt happen");
                                break;
                        }

                    }

                }
            }
        }

        @Override
        public void setUpInput()
        {
            //MOUSE        
            app.getInputManager().addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_X, false));
            app.getInputManager().addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
            app.getInputManager().addMapping("MOUSE_LEFT_CLICK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            app.getInputManager().addListener(this, "MOUSE_LEFT_CLICK");
            app.getInputManager().addListener(this, "MOUSE_MOVE");

            //KEYBOARD
            app.getInputManager().addMapping("SWITCH_MODE", new KeyTrigger(KeyInput.KEY_TAB));
            app.getInputManager().addListener(this, "SWITCH_MODE");
        }

        @Override
        public void cleanUpInput()
        {
            app.getInputManager().removeListener(this);
        }

    }

}
