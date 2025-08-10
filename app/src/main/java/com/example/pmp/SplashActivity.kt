package com.example.pmp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.pmp.databinding.ActivitySplashBinding
import com.example.pmp.ui.account.LoginUI

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //等待布局测量完成后再开始动画，以获取正确的视图尺寸
        binding.root.post {
            startAnimations()
        }
    }

    private fun startAnimations() {
        val mLeftDuration = 800L        // 左侧 "M" 动画时长
        val mDownDuration = 800L        // 右侧 "M" 动画时长
        val textRevealDuration = 900L   // 文字揭露动画时长
        val overlapDuration = 700L      // 两个 "M" 动画的重叠时间
        val postAnimationDelay = 500L   // 动画全部结束后到跳转的延迟

        // --- 动画1: 左侧 "M" 从左到右揭露 ---
        binding.ivMLeft.visibility = View.VISIBLE
        val mLeftReveal = ObjectAnimator.ofFloat(
            binding.maskView, "translationX", 0f, binding.ivMLeft.width.toFloat()
        ).apply {
            duration = mLeftDuration
            interpolator = AccelerateDecelerateInterpolator()
            //动画结束后移除遮罩，防止遮挡文字
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.maskView.visibility = View.GONE
                }
            })
        }

        // --- 动画2: 右侧 "M" 从上到下揭露 ---
        val mDownReveal = ValueAnimator.ofInt(0, binding.ivMDown.height).apply {
            duration = mDownDuration
            interpolator = AccelerateDecelerateInterpolator()
            // 通过改变 clipBounds 实现裁剪动画
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                binding.ivMDown.clipBounds = Rect(0, 0, binding.ivMDown.width, animatedValue)
            }
            // 动画开始时才显示 View
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    binding.ivMDown.visibility = View.VISIBLE
                }
            })
            // 设置延迟启动，实现与上一个动画的重叠效果
            startDelay = mLeftDuration - overlapDuration
        }

        // --- 动画3: 文字淡入 ---
        val textReveal = ValueAnimator.ofInt(0, binding.tvLogoText.width).apply {
            duration = textRevealDuration
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                // 裁剪区域的右边界从 0 变化到 TextView 的宽度
                val clipRect = Rect(0, 0, animatedValue, binding.tvLogoText.height)
                binding.tvLogoText.clipBounds = clipRect
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    // 动画开始前，必须将alpha设置为1，否则裁剪不可见
                    binding.tvLogoText.alpha = 1f
                }
            })

            //设置延迟启动，确保在 Logo 动画基本完成后再出现
            startDelay = (mLeftDuration - overlapDuration) + mDownDuration
        }

        // --- 组合并播放所有动画 ---
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(mLeftReveal, mDownReveal, textReveal)

        // --- 动画结束后延迟跳转 ---
        animatorSet.doOnEnd {
            binding.root.postDelayed({
                startActivity(Intent(this@SplashActivity, LoginUI::class.java))
                finish()
            }, postAnimationDelay)
        }

        animatorSet.start()
    }

}