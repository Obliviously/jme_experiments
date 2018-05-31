/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Ray;
import examples.EditModeExample;
import utils.AbstractInputController;

/**
 *
 * @author Fabian Rauscher
 */
public class SelectAppState extends BaseAppState
{
    private EditModeExample app;
    private AppStateManager stateManager;
    private InputController input;

    @Override
    protected void initialize(Application app)
    {
        this.app = (EditModeExample) app;
        this.stateManager = app.getStateManager();
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
        public void setUpInput()
        {
            //MOUSE        
            app.getInputManager().addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_X, true));
            app.getInputManager().addMapping("MOUSE_MOVE", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
            app.getInputManager().addListener(this, "MOUSE_MOVE");

            //KEYBOURD
            app.getInputManager().addMapping("SWITCH_MODE", new KeyTrigger(KeyInput.KEY_TAB));
            app.getInputManager().addListener(this, "SWITCH_MODE");
        }

        @Override
        public void cleanUpInput()
        {
            app.getInputManager().removeListener(this);
        }

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
                    //TODO
                    System.out.println("SELECT");
                }
            }
        }

        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            if (name.equals("SWITCH_MODE") && !isPressed)
            {
                stateManager.detach(getState(SelectAppState.class));
                stateManager.attach(new EditAppState());
            }
        }

    }
}
