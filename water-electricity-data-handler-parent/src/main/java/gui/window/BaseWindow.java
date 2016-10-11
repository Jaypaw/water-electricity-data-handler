package gui.window;

import controller.BaseWindowController;


public abstract class BaseWindow<ControllerType extends BaseWindowController> {
    protected ControllerType controller;

    public void bindController(ControllerType controller)  {
        this.controller = controller;
    }

}
