package com.yangbingdong.acme.biz.api.i18n;

import java.util.ListResourceBundle;

/**
 * @author yangbingdong1994@gmail.com
 */
public class HardcodeResourceBundle extends ListResourceBundle {

    private static final Object[][] contents = {
            {"test.name", "brandon"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
