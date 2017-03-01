package com.example.contentproviderdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by frank.bi on 2/27/2017.
 */

class Utils {
    static final int NO_ANIMATION = 0;
    static final int ANIMATION_TYPE_LEFT_RIGHT = 1;
    static final int ANIMATION_TYPE_UP_DOWN = 2;
    static final int ANIMATION_SCALE = 3;

    static void startActivityUseAnimation(Context context, Intent intent, int requestCode, int animationType) {
        if (context != null && context instanceof Activity && intent != null) {
            if (requestCode == -1) {
                context.startActivity(intent);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }

            if (animationType == ANIMATION_TYPE_LEFT_RIGHT) {
                ((Activity) context).overridePendingTransition(R.anim.from_right_in, R.anim.to_left_out);
            } else if (animationType == ANIMATION_TYPE_UP_DOWN) {
                ((Activity) context).overridePendingTransition(R.anim.from_down_in, R.anim.to_up_out);
            } else if (animationType == ANIMATION_SCALE) {
                ((Activity) context).overridePendingTransition(R.anim.scale_in, 0);
            } else {
                //NO_ANIMATION
            }
        }
    }

    static void finishActivityUseAnimation(Context context, int animationType) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).finish();

            if (animationType == ANIMATION_TYPE_LEFT_RIGHT) {
                ((Activity) context).overridePendingTransition(R.anim.from_left_in, R.anim.to_right_out);
            } else if (animationType == ANIMATION_TYPE_UP_DOWN) {
                ((Activity) context).overridePendingTransition(R.anim.from_up_in, R.anim.to_down_out);
            } else if (animationType == ANIMATION_SCALE) {
                ((Activity) context).overridePendingTransition(R.anim.scale_in, 0);
            } else {
                //NO_ANIMATION
            }
        }
    }
}
