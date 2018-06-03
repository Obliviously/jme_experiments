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
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import examples.EditModeExample;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Boulder;
import utils.AbstractInputController;
import utils.MeshUtils;
import utils.VertexUtils;

/**
 *
 * @author Fabian Rauscher
 */
public class CreateBoulderAppState extends BaseAppState
{
    private static final Logger LOGGER = Logger.getLogger(CreateBoulderAppState.class.getName());

    private EditModeExample app;
    private InputController input;
    private Node rootNode;
    private AssetManager assetManager;

    private Boulder boulder;
    private ArrayList<Vector3f> currSelectedVertices;
    //private ArrayList<Vector3f> boulderTopo = new ArrayList<>();

    private final String MARKNAME = "mark";

    public CreateBoulderAppState(Boulder boulder, ArrayList<Vector3f> currSelectedVertices)
    {
        this.boulder = boulder;
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
        addMark(boulder.getLastMark());
    }

    /**
     * A sphere that marks a spot.
     *
     * @param position The position of the sphere
     */
    protected void addMark(Vector3f position)
    {
        Sphere sphere = new Sphere(15, 15, 0.05f);
        Geometry mark = new Geometry(MARKNAME, sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", boulder.getColor());
        mark.setMaterial(mark_mat);
        mark.setLocalTranslation(position);
        boulder.addMark(position);
        rootNode.attachChild(mark);
    }

    private void addLineBetween(Vector3f start, Vector3f end)
    {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", boulder.getColor());
        Line line = new Line(start, end);
        Geometry lineGeo = new Geometry("line", line);
        lineGeo.setMaterial(mat);
        rootNode.attachChild(lineGeo);
    }

    private void extendBoulderTo(CollisionResult closest)
    {
        Vector3f contactPoint = closest.getContactPoint();
        ArrayList<Vector3f> newSelectedVertices = MeshUtils.calcFlatArea(closest);
        //New point is on the same flat polygon as the last one.
        if (newSelectedVertices.containsAll(currSelectedVertices))
        {
            addMark(contactPoint);
            addLineBetween(boulder.getNthLastMark(2), boulder.getNthLastMark(1));
        }
        else
        {
            /**
             * New point is not on the same flat polygon as the last one. Now we
             * have to distinguish between three cases.
             * 1. New and old have no vertices in common -> Cant connect the
             * points.
             * 2. New and old have one vertex in common -> The points are
             * connected over this vertex.
             * 3. New and old have 2 vertices in common -> Calculate the closest
             * point on the line (that is created by these vertices) to the old
             * point and use it as the connecting point.
             */
            ArrayList<Vector3f> tempSelectedVertices = new ArrayList<>(newSelectedVertices);
            newSelectedVertices.retainAll(currSelectedVertices);
            switch (newSelectedVertices.size())
            {
            case 0:
                LOGGER.log(Level.INFO, "Selection is invalid! Can only connect adjacent polygons.");
                break;
            case 1:
                addMark(newSelectedVertices.get(0));
                addMark(contactPoint);

                addLineBetween(boulder.getNthLastMark(3), boulder.getNthLastMark(2));
                addLineBetween(boulder.getNthLastMark(2), boulder.getNthLastMark(1));

                currSelectedVertices = tempSelectedVertices;
                break;
            case 2:
                Vector3f lastContactPoint = boulder.getLastMark();
                Vector3f closestContactPoint = VertexUtils.calcClosestPointOnLine(newSelectedVertices.get(0), newSelectedVertices.get(1), lastContactPoint);
                addMark(closestContactPoint);
                addMark(contactPoint);

                addLineBetween(boulder.getNthLastMark(3), boulder.getNthLastMark(2));
                addLineBetween(boulder.getNthLastMark(2), boulder.getNthLastMark(1));

                currSelectedVertices = tempSelectedVertices;
                break;
            default:
                LOGGER.log(Level.WARNING, "This should not happens!");
                break;
            }

        }
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

    private class InputController extends AbstractInputController implements ActionListener
    {
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            if (name.equals("SWITCH_MODE") && !isPressed)
            {
                getStateManager().detach(getState(CreateBoulderAppState.class));
                getStateManager().attach(new SelectAppState());
            }

            if (name.equals("MOUSE_LEFT_CLICK") && !isPressed)
            {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
                app.getRootNode().collideWith(ray, results);

                if (results.size() > 0)
                {
                    extendBoulderTo(results.getClosestCollision());
                }
            }
        }

        @Override
        public void setUpInput()
        {
            app.getInputManager().addListener(this, "MOUSE_LEFT_CLICK");
            app.getInputManager().addListener(this, "MOUSE_MOVE");
            app.getInputManager().addListener(this, "SWITCH_MODE");
        }

        @Override
        public void cleanUpInput()
        {
            app.getInputManager().removeListener(this);
        }

    }

}
