package com.openwes.web;

/**
 *
 * @author xuanloc0511@gmail.com
 */
public class PathBuilder {

    private final StringBuilder mBuilder = new StringBuilder();

    public PathBuilder append(String path) {
        mBuilder.append(path);
        return this;
    }

    /**
     * Remove duplicated splash
     *
     * @return
     */
    public PathBuilder normalise() {
        String s = mBuilder.toString();
        mBuilder.setLength(0);
        boolean ignoreSplash = false;
        for (char c : s.toCharArray()) {
            if (c == '/') {
                if (ignoreSplash) {
                    continue;
                }
                ignoreSplash = true;
            } else {
                ignoreSplash = false;
            }
            mBuilder.append(c);
        }

        return this;
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }
}
