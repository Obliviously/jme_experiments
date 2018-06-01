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
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import examples.EditModeExample;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Logger logger = Logger.getLogger(EditStartAppState.class.getName());

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
                    if (newSelectedVertices.containsAll(currSelectedVertices))
                    {
                        addMark(newContactPoint);
                        addLineBetween(marks.get(marks.size() - 2), marks.get(marks.size() - 1));

                    }
                    else
                    {
                        //Make a real copy because 'retainAll' mutates the list.
                        ArrayList<Vector3f> tempVertices = new ArrayList<>(newSelectedVertices);
                        //calculate intersection of the two vertice lists.
                        newSelectedVertices.retainAll(currSelectedVertices);

                        switch (newSelectedVertices.size())
                        {
                            case 0:
                                logger.log(Level.INFO, "Selection is invalid!");
                                break;
                            case 1:
                                System.out.println("1");
                                currSelectedVertices = tempVertices;
                                VertexUtils.changeColorOfVertices(closest.getGeometry(), currSelectedVertices);

                                addMark(newSelectedVertices.get(0));
                                addMark(newContactPoint);

                                addLineBetween(marks.get(marks.size() - 3), marks.get(marks.size() - 2));
                                addLineBetween(marks.get(marks.size() - 2), marks.get(marks.size() - 1));
                                break;
                            case 2:
                                System.out.println("2");
                                currSelectedVertices = tempVertices;
                                VertexUtils.changeColorOfVertices(closest.getGeometry(), currSelectedVertices);

                                lastContactPoint = marks.get(marks.size() - 1).getLocalTranslation();
                                Vector3f closestContactPoint = calcClosestPointOnLine(newSelectedVertices.get(0), newSelectedVertices.get(1), lastContactPoint);
                                addMark(closestContactPoint);
                                addMark(newContactPoint);

                                addLineBetween(marks.get(marks.size() - 3), marks.get(marks.size() - 2));
                                addLineBetween(marks.get(marks.size() - 2), marks.get(marks.size() - 1));

                                break;
                            default:
                                logger.log(Level.WARNING, "This should not happen!");
                                break;
                        }

                    }

                }
            }
        }

        private void addLineBetween(Geometry one, Geometry two)
        {
            Vector3f min = new Vector3f(one.getLocalTranslation());

            Vector3f max = new Vector3f(two.getLocalTranslation());

            min.minLocal(two.getLocalTranslation());

            max.maxLocal(one.getLocalTranslation());

            Mesh box = new Box(min, max);
            Geometry boxGeom = new Geometry("Box", box);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Red);
            boxGeom.setMaterial(mat);

            //boxGeom.setLocalTranslation(one.getLocalTranslation());
            rootNode.attachChild(boxGeom);
        }

        private Vector3f calcClosestPointOnLine(Vector3f lineStart, Vector3f lineEnd, Vector3f point)
        {
            Vector3f closestPoint = lineStart;
            Vector3f line = lineStart.subtract(lineEnd);
            float linePosition = 0.1f;

            if (lineStart.distance(lineEnd) < lineStart.add(line.mult(linePosition)).distance(lineEnd))
            {
                lineStart = lineEnd;
            }

            double lastDistance = Double.MAX_VALUE;
            double currDistance = lineStart.distance(point);

            for (int i = 1; currDistance < lastDistance; i++)
            {
                lastDistance = currDistance;
                closestPoint = lineStart.add(line.mult(linePosition * i));
                currDistance = point.distance(closestPoint);
            }
            return closestPoint.subtract(line.mult(linePosition));
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
