package coder.zhang.skin_package.bean;

import android.content.res.TypedArray;
import android.util.SparseIntArray;

public class AttrBean {

    private static final int DEFAULT_INDEX = -1;

    private SparseIntArray resourcesMap;

    public AttrBean() {
        resourcesMap = new SparseIntArray();
    }

    public void saveViewResources(TypedArray typedArray, int[] styleable) {
        if (typedArray == null) return;
        resourcesMap.clear();
        for (int i = 0, size = typedArray.length(); i < size; i++) {
            resourcesMap.put(styleable[i], typedArray.getResourceId(i, DEFAULT_INDEX));
        }
    }

    public int getViewResource(int styleable) {
        return resourcesMap.get(styleable);
    }
}
