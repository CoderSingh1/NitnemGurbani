package com.satnamsinghmaggo.nitnemgurbani;

import android.content.Context;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;

import java.util.concurrent.atomic.AtomicReference;

public class GlobalLoader {
    private static final AtomicReference<LottieComposition> preloadedComposition = new AtomicReference<>();

    // Call this once (like in Application or first Activity) to preload the animation
    public static void preloadAnimation(Context context, String assetName) {
        LottieCompositionFactory.fromAsset(context, assetName)
                .addListener(composition -> preloadedComposition.set(composition))
                .addFailureListener(e -> e.printStackTrace());
    }

    // Use this when setting to a LottieAnimationView
    public static LottieComposition getPreloadedAnimation() {
        return preloadedComposition.get();
    }

    public static boolean isAnimationLoaded() {
        return preloadedComposition.get() != null;
    }
}
