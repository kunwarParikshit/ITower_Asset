package com.isl.util;

/**
 * Created by dhakan on 7/29/2019.
 */
public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
