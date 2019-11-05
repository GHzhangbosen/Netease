package coder.zhang.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import coder.zhang.arouter.ARouter;
import coder.zhang.arouter.bean.RouterBean;
import coder.zhang.compiler.util.Constants;
import coder.zhang.compiler.util.EmptyUtils;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"coder.zhang.arouter.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Constants.MODULE_NAME, Constants.PACKAGE_NAME_FOR_APT})
public class ARouterProcessor extends AbstractProcessor {

    private Elements elementUtils; // 操作Element的工具类
    private Types typeUtils; // TypeElement的工具类
    private Messager messager; // 日志输出
    private Filer filer; // 文件生成器

    private String moduleName;
    private String packageNameForApt;

    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();
    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForApt = options.get(Constants.PACKAGE_NAME_FOR_APT);
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName: " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageNameForApt: " + packageNameForApt);
        }

        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForApt)) {
            throw new RuntimeException("注解处理器接收到的参数moduleName或者packageNameForApt为空，请配置后重试");
        }
    }

//    // 需要处理的注解类型
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
//    }
//
//    // 使用哪个版本的Java来处理注解
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return super.getSupportedSourceVersion();
//    }
//
//    // 接收从外面传来的参数
//    @Override
//    public Set<String> getSupportedOptions() {
//        return super.getSupportedOptions();
//    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (EmptyUtils.isEmpty(set)) return false;

        // 所有使用ARouter注解的节点
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        try {
            parseElements(elements);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "异常: " + e.getMessage());
        }
        return true;
    }

    private void parseElements(Set<? extends Element> elements) throws IOException {
        if (EmptyUtils.isEmpty(elements)) return;
        // 1、获取所有被ARouter注解的元素
        // 2、遍历，获取每个元素的注解属性值，并封装到实体类
        // 3、临时存储所有元素对应的实体类到集合中
        // 4、生成ARouter$$Path$$order类
        // 5、生成ARouter$$Group$$order类

        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY_WHOLE_PAHT);
        TypeElement callType = elementUtils.getTypeElement(Constants.CALL_WHOLE_PATH);
        TypeMirror activityMirror = activityType.asType();
        TypeMirror callMirror = callType.asType();

        for (Element element : elements) {
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为: " + elementMirror.toString());

            ARouter aRouter = element.getAnnotation(ARouter.class);
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else if (typeUtils.isSubtype(elementMirror, callMirror)) {
                bean.setType(RouterBean.Type.CALL);
            } else {
                throw new RuntimeException("@ARouter只能作用在ACTIVITY之上");
            }

            valueOfPathMap(bean);
        }

        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.LOADGROUP_WHOLE_PATH);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.LOADPATH_WHOLE_PATH);
        createPathFile(pathLoadType);
        createGroupFile(groupLoadType, pathLoadType);
    }

    private void valueOfPathMap(RouterBean bean) {
        if (checkARouterPath(bean)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean: " + bean.toString());
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            if (routerBeans == null) {
                routerBeans = new ArrayList<>();
                tempPathMap.put(bean.getGroup(), routerBeans);
            }
            if (!routerBeans.contains(bean)) routerBeans.add(bean);
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解未按规范配置，如/app/MainActivity");
        }
    }

    private boolean checkARouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解未按规范配置，如/app/MainActivity");
            return false;
        }

        // 错误配置，如"/MainActivity"
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解未按规范配置，如/app/MainActivity");
            return false;
        }

        // 错误配置，如"/app/a/MainActivity"
        String substring = path.substring(1, path.lastIndexOf("/"));
        if (substring.contains("/")) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解未按规范配置，如/app/MainActivity");
            return false;
        }

        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解中group的属性值必须和moduleName相同");
            return false;
        } else {
            bean.setGroup(moduleName);
        }
        return true;
    }

//    public class ARouter$$Path$$order implements ARouterLoadPath {
//
//        @Override
//        public Map<String, RouterBean> loadPath() {
//            Map<String, RouterBean> pathMap = new HashMap<>();
//            pathMap.put("/order/Order_MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY, Order_MainActivity.class, "order", "/order/MainActivity"));
//            return pathMap;
//        }
//    }
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) return;
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_NAME_PATH)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn);

        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class),
                Constants.VARIABLE_NAME_PATH,
                ClassName.get(HashMap.class));

        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {

            for (RouterBean routerBean : entry.getValue()) {
                methodBuilder.addStatement("$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        Constants.VARIABLE_NAME_PATH,
                        routerBean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        routerBean.getType(),
                        ClassName.get((TypeElement) routerBean.getElement()),
                        routerBean.getGroup(),
                        routerBean.getPath());
            }

            methodBuilder.addStatement("return $N", Constants.VARIABLE_NAME_PATH);

            String finalClassName = Constants.FILE_NAME_PREFIX_PATH + moduleName;
//            String finalClassName = Constants.FILE_NAME_PREFIX_PATH + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件为: " + packageNameForApt + "." + finalClassName);

//            public class ARouter$$Path$$order implements ARouterLoadPath
            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(pathLoadType))
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile.builder(packageNameForApt, typeSpec)
                    .build()
                    .writeTo(filer);

            tempGroupMap.put(moduleName, finalClassName);
        }
    }

    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) return;

//        @Override
//        public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
//            Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
//            groupMap.put("order", ARouter$$Path$$order.class);
//            return groupMap;
//        }
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))));

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_NAME_Group)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn);

        methodBuilder.addStatement("$T<$T, $T<? extends $T>> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Class.class),
                ClassName.get(pathLoadType),
                Constants.VARIABLE_NAME_GROUP,
                ClassName.get(HashMap.class));

        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    Constants.VARIABLE_NAME_GROUP,
                    moduleName,
                    ClassName.get(packageNameForApt, entry.getValue()));
        }

        methodBuilder.addStatement("return $N", Constants.VARIABLE_NAME_GROUP);

        String finalClassName = Constants.FILE_NAME_PREFIX_GROUP + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(groupLoadType))
                .addMethod(methodBuilder.build())
                .build();
        JavaFile.builder(packageNameForApt, typeSpec)
                .build()
                .writeTo(filer);
    }
}
