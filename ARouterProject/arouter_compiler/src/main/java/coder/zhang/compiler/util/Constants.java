package coder.zhang.compiler.util;

public class Constants {

    public static final String AROUTER_ANNOTATION_TYPES = "coder.zhang.arouter.ARouter";

    // 子模块的名称
    public static final String MODULE_NAME = "moduleName";
    // 存放Apt生成文件的目录
    public static final String PACKAGE_NAME_FOR_APT = "packageNameForApt";

    public static final String STRING_WHOLE_PATH = "java.lang.String";
    public static final String ACTIVITY_WHOLE_PAHT = "android.app.Activity";
    public static final String LOADGROUP_WHOLE_PATH = "coder.zhang.arouter_api.ARouterLoadGroup";
    public static final String LOADPATH_WHOLE_PATH = "coder.zhang.arouter_api.ARouterLoadPath";

    public static final String METHOD_NAME_PATH = "loadPath";
    public static final String METHOD_NAME_Group = "loadGroup";
    public static final String VARIABLE_NAME_PATH = "pathMap";
    public static final String VARIABLE_NAME_GROUP = "groupMap";

    public static final String FILE_NAME_PREFIX_PATH = "ARouter$$Path$$";
    public static final String FILE_NAME_PREFIX_GROUP = "ARouter$$Group$$";

    //////////模块回调//////////
    public static final String CALL_WHOLE_PATH = "";


    //////Parameter/////////////
    public static final String PARAMETER_ANNOTATION_TYPES = "coder.zhang.arouter.Parameter";
    public static final String LOADPARAMETER_WHOLE_PATH = "coder.zhang.arouter_api.LoadParameter";
    public static final String LOADPARAMETER_PARAMETER_NAME = "target";
    public static final String FILE_NAME_SUBFIX_LOADPARAMETER = "$$Parameter";
    public static final String METHOD_NAME_SUBFIX_LOADPARAMETER = "loadParameter";
    public static final String VARIABLE_NAME_SUBFIX_LOADPARAMETER = "t";
}
