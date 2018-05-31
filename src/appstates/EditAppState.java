/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appstates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import examples.EditModeExample;
import java.util.ArrayList;
import utils.AbstractInputController;
import utils.MeshUtils;
import utils.VertexUtils;

/**
 *
 * @author Fabian Rauscher
 */
public class EditAppState extends BaseAppState
{
    private EditModeExample app;
    private InputController input;

    private Vector3f contactPoint = null;
    private ArrayList<Vector3f> selectedVertices = null;

    @Override
    protected void initialize(Application app)
    {
        this.app = (EditModeExample) app;
        this.input = new InputController();
        input.setUpInput();
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
            if (name.equals("MOUSE_MOVE"))
            {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
                app.getRootNode().collideWith(ray, results);

                if (results.size() > 0)
                {
                    CollisionResult closest = results.getClosestCollision();
                    contactPoint = closest.getContactPoint();
                    selectedVertices = MeshUtils.calcFlatArea(closest);
                    VertexUtils.changeColorOfVertices(closest.getGeometry(), selectedVertices);
                }
            }
        }

        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            if (name.equals("SWITCH_MODE") && !isPressed)
            {
                getStateManager().detach(getState(EditAppState.class));
                getStateManager().attach(new SelectAppState());
            }

            if (name.equals("MOUSE_LEFT_CLICK") && selectedVertices != null && contactPoint != null)
            {
                getStateManager().detach(getState(EditAppState.class));
                getStateManager().attach(new EditStartAppState(contactPoint, selectedVertices));
            }
        }

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

        public void cleanUpInput()
        {
            app.getInputManager().removeListener(this);
        }

    }
}
