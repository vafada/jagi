/**
 * DescriptorTokenizer.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.util.Enumeration;

public class DescriptorTokenizer implements Enumeration {
    protected String text;
    protected String returnValue;
    protected boolean more = true;
    protected int start = 0;

    public DescriptorTokenizer(String text) {
        this.text = text;

        if ((text.length() == 0) || (text.startsWith("()"))) {
            if (text.startsWith("()")) {
                returnValue = text.substring(2);
            }

            more = false;
        }
    }

    public boolean hasMoreElements() {
        return more;
    }

    public Object nextElement() {
        if (more) {
            int start = this.start, end;
            String text = this.text;
            String result = null;

            if (text.charAt(start) == '(') {
                start++;
            }

            end = start;

            while (text.charAt(end) == '[') {
                end++;
            }

            if (text.charAt(end) != ')') {
                if (text.charAt(end) == 'L') {
                    while (text.charAt(end) != ';') {
                        end++;
                    }
                }

                end++;
                result = text.substring(start, end);
                start = this.start = end;
            }

            if (text.charAt(start) == ')') {
                returnValue = text.substring(start + 1);
                more = false;
            }

            return result;
        }

        return null;
    }

    public String getReturnValue() {
        return returnValue;
    }
}
