package com.ihpukan.nks.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vginkeo on 2017/09/12.
 */

public class JavaScriptTokenIntercept {

        public String token;

        public JavaScriptTokenIntercept()
        {
            token = "";
        }

        @SuppressWarnings("unused")

        @android.webkit.JavascriptInterface
        public void processContent(String aContent)
        {
            String myPattern = "xoxs\\-[^\"]*";
            Pattern p = Pattern.compile(myPattern);
            Matcher m = p.matcher(aContent);
            while(m.find())
            {
                token = m.group(0);
            }
        }
}
