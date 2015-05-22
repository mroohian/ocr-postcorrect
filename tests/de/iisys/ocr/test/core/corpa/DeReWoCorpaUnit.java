package de.iisys.ocr.test.core.corpa;

import de.iisys.ocr.corpa.DeReWoCorpa;

import java.io.File;

/**
 * DeReWoCorpaUnit
 * de.iisys.ocr.test.core.corpa
 * Created by reza on 04.09.14.
 */
public class DeReWoCorpaUnit {
    private DeReWoCorpa mCorpa;

    public DeReWoCorpaUnit(String corpusFile) {
        assert new File(corpusFile).exists();

        mCorpa = new DeReWoCorpa(corpusFile);
        mCorpa.load(true);
    }

    public DeReWoCorpa getCorpa() {
        return mCorpa;
    }
}
