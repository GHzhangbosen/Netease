package coder.zhang.arouter.bean;

import javax.lang.model.element.Element;

public class RouterBean {

    public enum Type {
        ACTIVITY,
        // 跨模块的业务接口
        CALL
    }

    private Type type;
    private Element element;
    private String group;
    private String path;
    private Class clazz;

    private RouterBean(Builder builder) {
        this.element = builder.element;
        this.group = builder.group;
        this.path = builder.path;
    }

    public RouterBean(Type type, String group, String path, Class clazz) {
        this.type = type;
        this.group = group;
        this.path = path;
        this.clazz = clazz;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public Class getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public static RouterBean create(Type type, Class clazz, String group, String path) {
        return new RouterBean(type, group, path, clazz);
    }

    public static final class Builder {

        private Element element;
        private String group;
        private String path;

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path不能为空，如/app/MainActivity");
            }
            return new RouterBean(this);
        }
    }
}
