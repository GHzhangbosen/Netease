package coder.zhang.skin_library.bean;

import android.content.res.TypedArray;
import android.util.SparseIntArray;

public class AttrsBean {

    private static final int DEFAULT_INDEX = -1;
    private SparseIntArray resourcesMap;

    public AttrsBean() {
        resourcesMap = new SparseIntArray();
    }

    public void saveViewResources(TypedArray typedArray, int[] styleable) {
        if (typedArray == null || styleable == null) return;
        resourcesMap.clear();
        for (int i = 0, size = typedArray.length(); i < size; i++) {
            int key = styleable[i];
            int resourceId = typedArray.getResourceId(i, DEFAULT_INDEX);
            resourcesMap.put(key, resourceId);
        }
    }

    public int getViewResource(int styleable) {
        return resourcesMap.get(styleable);
    }
}
