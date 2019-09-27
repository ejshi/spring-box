package com.sjz.mvc.core;

import com.sjz.mvc.annotations.Controller;
import com.sjz.mvc.annotations.RequestMapping;
import com.sjz.mvc.annotations.Resource;
import com.sjz.mvc.annotations.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author shijun.
 * @date 2019/9/26 14:18
 * @description 核心控制类
 */
public class DispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private static Set<Class<?>> classess = new HashSet<>();

    private static Map<String, Object> iocMap = new HashMap<>();

    /**
     * url与method的对应
     */
    private static Map<String, Method> handlerMapping = new HashMap<>();

    /**
     * url与类对象的对应
     */
    private static Map<String, Object> controllerMap = new HashMap<>();
    /**
     * url与类对象的对应
     */
    private static Map<String, Object> serviceMap = new HashMap<>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("=============== 数据开始初始化 ================");
        doLoadConfig(servletConfig.getInitParameter("contextConfigLocation"));
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
        initHandlerMapping();
        try {
            doIoc();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("========================= 依赖注入失败 ===================");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String uri = req.getRequestURI();
        if("/".equals(uri)){
            resp.getWriter().print("hello springmvc");
            return;
        }
        System.out.println("http请求URL=================================" + uri);
        Method method = handlerMapping.get(uri);
        Object instance = controllerMap.get(uri);

        Parameter[] parameters = method.getParameters();

        Object[] paramValues = new Object[parameters.length];

        //Method参数和HttpServletRequest请求参数对应
        int i = 0;
        for (Parameter parameter : parameters) {
            String name = parameter.getName();
            String value = req.getParameter(name);
            System.out.println(name + "=======请求参数=======" + value);
            paramValues[i++] = value;
        }

        //执行方法调用
        Object invoke = method.invoke(instance,paramValues);
        //执行数据输出
        resp.getWriter().print(invoke);
    }

    /**
     * 加载配置文件
     *
     * @param configPath
     */
    private void doLoadConfig(String configPath) {
        try {
            System.out.println("开始加载配置文件 ==================== " + configPath);
            //获取classpath下的文件
            String shotPath = configPath.split(":")[1];
            String fullPath = Thread.currentThread().getContextClassLoader().getResource(shotPath).getPath();
            System.out.println("====================== 配置文件路径：" + fullPath);
            InputStream inputStream = new FileInputStream(new File(fullPath));
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("文件路径错误，configPath = " + configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描包
     *
     * @param packageName
     */
    protected void doScanner(String packageName) {
        try {
            System.out.println("开始扫描包 ==================== " + packageName);
            classess = Scanner.getClasses(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 实例化
     */
    private void doInstance() {
        System.out.println("开始实例化 ==================== ");
        if (classess.isEmpty()) {
            return;
        }
        try {
            for (Class<?> aClass : classess) {
                if (aClass.isAnnotationPresent(Controller.class)) {
                    Controller controller = aClass.getAnnotation(Controller.class);
                    String key = controller.value();
                    if (key != null && key.length() > 0) {
                        iocMap.put(key, aClass.newInstance());
                    } else {
                        iocMap.put(toLowwerFristCase(aClass.getSimpleName()),aClass.newInstance());
                    }
                    continue;
                }

                if (aClass.isAnnotationPresent(Service.class)) {
                    Service service = aClass.getAnnotation(Service.class);
                    String key = service.value();
                    if (key != null && key.length() > 0) {
                        iocMap.put(key, aClass.newInstance());
                    } else {
                        iocMap.put(toLowwerFristCase(aClass.getSimpleName()),aClass.newInstance());
                    }
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * URI与方法对应
     */
    private void initHandlerMapping() {
        System.out.println("开始处理对应关系 ==================== ");
        if(iocMap.isEmpty()){
            return;
        }

        for (Map.Entry<String, Object> classEntry : iocMap.entrySet()) {
            Class<?> aClass = classEntry.getValue().getClass();
            if(!aClass.isAnnotationPresent(Controller.class)){
                continue;
            }

            RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);

            String baseUrl = (requestMapping == null || requestMapping.value() == null) ? "" : requestMapping.value();

            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                if(methodAnnotation == null){
                    continue;
                }
                String methodUrl = methodAnnotation.value();
                methodUrl = (baseUrl + "/" + methodUrl).replaceAll("/+","/");
                System.out.println(methodUrl +" =========== url - method =========== "+method.getName());
                //添加URL与method的映射
                handlerMapping.put(methodUrl, method);

                //添加url与具体对象的对应
                controllerMap.put(methodUrl, classEntry.getValue());
            }
        }
    }

    /**
     * 依赖注入
     */
    private void doIoc() throws Exception {
        System.out.println("开始依赖注入 ==================== ");
        if(iocMap.isEmpty()){
            return;
        }

        for (Map.Entry<String, Object> classEntry : iocMap.entrySet()) {
            Class<?> aClass = classEntry.getValue().getClass();
            if(!aClass.isAnnotationPresent(Controller.class)){
                continue;
            }

            Field[] fields = aClass.getDeclaredFields();

            if(fields.length == 0){
                continue;
            }

            for (Field field : fields) {
                if(!field.isAnnotationPresent(Resource.class)){
                    continue;
                }
                Resource resource = field.getAnnotation(Resource.class);
                String fieldName = isBlank(resource.value()) ? field.getName() : resource.value();
                Object service = iocMap.get(fieldName);
                if(service == null){
                    throw new IllegalArgumentException(fieldName + "没有对应的注解类");
                }
                //属性注入
                field.setAccessible(true);
                field.set(classEntry.getValue(), service);
            }
        }
    }

    /**
     * 首字母小写
     * @param data
     * @return
     */
    private static String toLowwerFristCase(String data) {
        char[] chars = data.toCharArray();
        char c = chars[0];
        if(c >= 'A' && c <= 'Z'){
            chars[0] = (char) (c + ('a'-'A'));
        }
        return new String(chars);
    }


    private static boolean isBlank(String data){
        return data == null || data.length() == 0;
    }
}
