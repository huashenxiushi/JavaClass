package top.remake.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个用来存储控制器的全局类。
 * 它被用于在其他地方获取控制器中的组件。
 *
 * @author ZeroTwo_CHEN
 */
public final class ControllerMap {
    // 一个用于存储控制器的映射，其中控制器类名作为键，控制器对象作为值。
    private static final Map<String, Object> CONTROLLER_MAP = new HashMap<>();

    // 私有构造函数，防止实例化此工具类。
    private ControllerMap() {
    }

    /**
     * 将控制器添加到映射中。
     *
     * @param controller 要添加的控制器对象。
     */
    public static void addController(Object controller) {
        CONTROLLER_MAP.put(controller.getClass().getName(), controller);
    }

    /**
     * 通过其类从映射中检索控制器。
     *
     * @param controllerName 要检索的控制器的类。
     * @return 控制器对象，如果未找到给定类的控制器，则返回null。
     */
    public static Object getController(Class<?> controllerName) {
        return CONTROLLER_MAP.get(controllerName.getName());
    }
}