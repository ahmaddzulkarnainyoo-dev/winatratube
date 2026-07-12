/*
 * SPDX-FileCopyrightText: 2026 Winatratube contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import org.schabi.newpipe.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // Install SplashScreen API BEFORE super.onCreate()
        // Syntax Java static method — sudah terbukti berhasil di MainActivity.java
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        final ActivitySplashBinding binding =
                ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Animasi Glow: fade in 400ms ---
        final ObjectAnimator glowFadeIn =
                ObjectAnimator.ofFloat(binding.splashGlow, "alpha", 0f, 1f);
        glowFadeIn.setDuration(400L);

        // --- Animasi Glow: breathing (scale + alpha) berulang ---
        final ValueAnimator glowBreathing = ValueAnimator.ofFloat(0f, 1f, 0f);
        glowBreathing.setDuration(800L);
        glowBreathing.setRepeatMode(ValueAnimator.REVERSE);
        glowBreathing.setRepeatCount(ValueAnimator.INFINITE);
        glowBreathing.setInterpolator(new AccelerateDecelerateInterpolator());
        glowBreathing.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull final ValueAnimator animator) {
                final float v = (float) animator.getAnimatedValue();
                // scale: 1.0 -> 1.15 -> 1.0
                final float scale = 1f + (0.15f * v);
                binding.splashGlow.setScaleX(scale);
                binding.splashGlow.setScaleY(scale);
                // alpha: 0.6 -> 1.0 -> 0.6
                binding.splashGlow.setAlpha(0.6f + (0.4f * v));
            }
        });

        // --- Animasi Icon W: fade in + scale ---
        final ObjectAnimator iconFadeIn =
                ObjectAnimator.ofFloat(binding.splashIconW, "alpha", 0f, 1f);
        iconFadeIn.setDuration(400L);
        final ObjectAnimator iconScaleX =
                ObjectAnimator.ofFloat(binding.splashIconW, "scaleX", 0.8f, 1f);
        iconScaleX.setDuration(400L);
        final ObjectAnimator iconScaleY =
                ObjectAnimator.ofFloat(binding.splashIconW, "scaleY", 0.8f, 1f);
        iconScaleY.setDuration(400L);

        final AnimatorSet iconAnimSet = new AnimatorSet();
        iconAnimSet.playTogether(iconFadeIn, iconScaleX, iconScaleY);

        // --- Animasi Title: fade in dengan delay 500ms ---
        final ObjectAnimator titleFadeIn =
                ObjectAnimator.ofFloat(binding.splashTitle, "alpha", 0f, 1f);
        titleFadeIn.setDuration(400L);
        titleFadeIn.setStartDelay(500L);

        // --- Animasi Subtitle: fade in dengan delay 800ms ---
        final ObjectAnimator subtitleFadeIn =
                ObjectAnimator.ofFloat(binding.splashSubtitle, "alpha", 0f, 1f);
        subtitleFadeIn.setDuration(400L);
        subtitleFadeIn.setStartDelay(800L);

        // Jalankan semua animasi bersama
        final AnimatorSet splashAnimSet = new AnimatorSet();
        splashAnimSet.playTogether(glowFadeIn, iconAnimSet, titleFadeIn, subtitleFadeIn);
        splashAnimSet.start();

        // Mulai breathing glow setelah fade-in selesai
        glowFadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                glowBreathing.start();
            }
        });

        // --- Pindah ke MainActivity setelah 1500ms ---
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    overridePendingTransition(0, 0);
                }
                finish();
            }
        }, 1500L);
    }
}
