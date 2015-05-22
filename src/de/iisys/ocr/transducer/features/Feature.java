package de.iisys.ocr.transducer.features;

/**
 * Feature
 * de.iisys.ocr.transducer.features
 * Created by reza on 04.08.14.
 */
public abstract class Feature implements IFeature {
    private String mName = null;

    @Override
    public String getName() {
        if (mName != null)
            return mName;

        return getClass().getName();
    }

    public void setName(String name) {
        this.mName = name;
    }
}
