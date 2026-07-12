/*
 * SPDX-FileCopyrightText: 2026 Winatratube contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import org.schabi.newpipe.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install SplashScreen API BEFORE super.onCreate()
        SplashScreen.installSplashScreen(this)

        super.onCreate(savedInstanceState)

        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Animasi Glow: fade in 400ms ---
        val glowFadeIn = ObjectAnimator.ofFloat(binding.splashGlow, "alpha", 0f, 1f)
        glowFadeIn.duration = 400L

        // --- Animasi Glow: breathing (scale + alpha) berulang ---
        val glowBreathing = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 800L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val v = animator.animatedValue as Float
                // scale: 1.0 -> 1.15 -> 1.0
                val scale = 1f + (0.15f * v)
                binding.splashGlow.scaleX = scale
                binding.splashGlow.scaleY = scale
                // alpha: 0.6 -> 1.0 -> 0.6
                binding.splashGlow.alpha = 0.6f + (0.4f * v)
            }
        }

        // --- Animasi Icon W: fade in + scale ---
        val iconFadeIn = ObjectAnimator.ofFloat(binding.splashIconW, "alpha", 0f, 1f)
        iconFadeIn.duration = 400L
        val iconScaleX = ObjectAnimator.ofFloat(binding.splashIconW, "scaleX", 0.8f, 1f)
        iconScaleX.duration = 400L
        val iconScaleY = ObjectAnimator.ofFloat(binding.splashIconW, "scaleY", 0.8f, 1f)
        iconScaleY.duration = 400L

        val iconAnimSet = AnimatorSet().apply {
            playTogether(iconFadeIn, iconScaleX, iconScaleY)
        }

        // --- Animasi Title: fade in dengan delay 500ms ---
        val titleFadeIn = ObjectAnimator.ofFloat(binding.splashTitle, "alpha", 0f, 1f).apply {
            duration = 400L
            startDelay = 500L
        }

        // --- Animasi Subtitle: fade in dengan delay 800ms ---
        val subtitleFadeIn = ObjectAnimator.ofFloat(binding.splashSubtitle, "alpha", 0f, 1f).apply {
            duration = 400L
            startDelay = 800L
        }

        // Jalankan semua animasi bersama
        val splashAnimSet = AnimatorSet().apply {
            playTogether(glowFadeIn, iconAnimSet, titleFadeIn, subtitleFadeIn)
            start()
        }

        // Mulai breathing glow setelah fade-in selesai
        glowFadeIn.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                glowBreathing.start()
            }
        })

        // --- Pindah ke MainActivity setelah 1500ms ---
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overridePendingTransition(0, 0)
            }
            finish()
        }, 1500L)
    }
}