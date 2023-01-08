package shareit.helper;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import shareit.controllers.ControllerBase;

public class NavigationHelper {
    
    @Autowired
    private ApplicationContext applicationContext;

    private Deque<Class<? extends ControllerBase>> navigationStack;

    private Class<? extends ControllerBase> currentController;

    public NavigationHelper() {
        navigationStack = new LinkedList<>();
    }

    public void setFirstEntry(Class<? extends ControllerBase> controller) throws IOException {
        iniController(controller);
    }

    public void navigateTo(Class<? extends ControllerBase> controller) throws IOException {

        navigationStack.add(currentController);

        iniController(controller);

    }

    public void navigateBack() throws IOException {

        if (navigationStack.isEmpty()) return;

        var controller = navigationStack.pollLast();

        iniController(controller);

    }

    private void iniController(Class<? extends ControllerBase> controller) throws IOException {
        this.currentController = controller;
        ((ControllerBase)applicationContext.getBean(controller)).display();
    }

}
