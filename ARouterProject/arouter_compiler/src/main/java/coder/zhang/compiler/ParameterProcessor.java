package coder.zhang.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import coder.zhang.arouter.Parameter;
import coder.zhang.compiler.util.Constants;
import coder.zhang.compiler.util.EmptyUtils;

@AutoService(Processor.class)
@SupportedAnnotationTypes(Constants.PARAMETER_ANNOTATION_TYPES)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;

    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (EmptyUtils.isEmpty(set)) return false;

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
        if (EmptyUtils.isEmpty(elements)) return false;

        valueOfParametermap(elements);

        try {
            createParameterFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void valueOfParametermap(Set<? extends Element> elements) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<Element> fields = tempParameterMap.get(typeElement);
            if (fields == null) {
                fields = new ArrayList<>();
                tempParameterMap.put(typeElement, fields);
            }
            if (!fields.contains(element)) fields.add(element);
        }
    }

//    @Override
//    public void loadParameter(Object target) {
//        MainActivity t = (MainActivity) target;
//
//        t.name = t.getIntent().getStringExtra("name");
//        t.age = t.getIntent().getIntExtra("age", t.age);
//    }
    private void createParameterFile() throws IOException {
        if (EmptyUtils.isEmpty(tempParameterMap)) return;

        for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {

            TypeElement typeElement = entry.getKey();
            ClassName className = ClassName.get(typeElement);
            messager.printMessage(Diagnostic.Kind.NOTE, className.simpleName() + "注解类的个数: " + tempParameterMap.size() + ", 该类注解属性的个数: " + entry.getValue().size());

            ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.OBJECT, Constants.LOADPARAMETER_PARAMETER_NAME)
                    .build();
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_NAME_SUBFIX_LOADPARAMETER)
                    .addAnnotation(ClassName.get(Override.class))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(parameterSpec);

            methodBuilder.addStatement("$T t = ($T) $N",
                    ClassName.get(typeElement),
                    ClassName.get(typeElement),
                    Constants.LOADPARAMETER_PARAMETER_NAME);

            for (Element element : entry.getValue()) {
                TypeMirror typeMirror = element.asType();
                int ordinal = typeMirror.getKind().ordinal();
                Parameter parameter = element.getAnnotation(Parameter.class);
                String valueName = element.getSimpleName().toString();
                String parameterKey = EmptyUtils.isEmpty(parameter.name()) ? element.getSimpleName().toString() : parameter.name();
                messager.printMessage(Diagnostic.Kind.NOTE, "parameter.name(): " + parameter.name() + ", valueName: " + valueName);
                if (ordinal == TypeKind.INT.ordinal()) {
                    methodBuilder.addStatement("$N.$N = $N.getIntent().getIntExtra($S, $N.$N)",
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            valueName,
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            parameterKey,
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            valueName);
                } else if (ordinal == TypeKind.BOOLEAN.ordinal()) {
                    methodBuilder.addStatement("$N.$N = $N.getIntent().getBooleanExtra($S, $N.$N)",
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            valueName,
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            parameterKey,
                            Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                            valueName);
                } else {
                    if (typeMirror.toString().equalsIgnoreCase(Constants.STRING_WHOLE_PATH)) {
                        methodBuilder.addStatement("$N.$N = $N.getIntent().getStringExtra($S)",
                                Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                                valueName,
                                Constants.VARIABLE_NAME_SUBFIX_LOADPARAMETER,
                                parameterKey);
                    }
                }
            }

            TypeElement parameterTypeElement = elementUtils.getTypeElement(Constants.LOADPARAMETER_WHOLE_PATH);
            String finalClassName = className.simpleName() + Constants.FILE_NAME_SUBFIX_LOADPARAMETER;
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成获取参数类文件: " + className.packageName() + "." + finalClassName);
            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(parameterTypeElement))
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile.builder(className.packageName(), typeSpec)
                    .build()
                    .writeTo(filer);
        }
    }
}
