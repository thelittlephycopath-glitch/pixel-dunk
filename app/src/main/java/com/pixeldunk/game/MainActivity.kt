package com.pixeldunk.game

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import kotlin.math.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 22, 35)
        window.navigationBarColor = Color.rgb(18, 22, 35)
        setContentView(DunkView())
    }

    private inner class DunkView : View(this@MainActivity) {
        private val p = Paint().apply { isAntiAlias = false; textSize = 12f; typeface = android.graphics.Typeface.MONOSPACE }
        private var sx = 1f; private var sy = 1f
        private var ballX = 58f; private var ballY = 438f
        private var vx = 0f; private var vy = 0f
        private var dragging = false; private var launched = false
        private var aimX = 0f; private var aimY = 0f
        private var score = 0; private var misses = 0; private var level = 1
        private var rimX = 250f; private val rimY = 250f
        private var scored = false; private var flash = 0f
        private var secret = false; private var secretTimer = 0f; private var secretPhase = 0; private var secretSeen = false
        private var lastTime = System.nanoTime()
        private var frame = 0

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            sx = width / 320f; sy = height / 568f
            canvas.save(); canvas.scale(sx, sy)
            val now = System.nanoTime()
            val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0f, .033f)
            lastTime = now
            update(dt)
            drawScene(canvas)
            canvas.restore()
            postInvalidateOnAnimation()
        }

        private fun update(dt: Float) {
            frame++
            if (secret) {
                secretTimer -= dt
                if (secretTimer <= 0f) {
                    when (secretPhase) {
                        1 -> { secretPhase = 2; secretTimer = .22f }
                        2 -> { secretPhase = 3; secretTimer = .52f }
                        else -> { secret = false; secretSeen = true; secretPhase = 0; resetBall() }
                    }
                }
                return
            }
            if (!launched) return
            val oldY = ballY
            vx *= .999f; vy += 520f * dt
            ballX += vx * dt; ballY += vy * dt
            if (ballX < 12f || ballX > 308f) { vx *= -.72f; ballX = ballX.coerceIn(12f,308f) }
            if (ballY > 500f) {
                misses++
                // One-time hidden scare: no menu hint, and only after sustained play.
                if (!secretSeen && level >= 3 && misses >= 4 && ballX > 220f) {
                    secret = true; secretPhase = 1; secretTimer = .58f
                } else resetBall()
            }
            if (!scored && oldY < rimY && ballY >= rimY && ballX in (rimX - 13f)..(rimX + 22f) && vy > 0f) {
                score++; scored = true; level = 1 + score / 3
                rimX = if (level % 2 == 0) 224f else 250f
                resetBall()
            }
            if (abs(ballX - rimX) < 8f && abs(ballY-rimY) < 8f && vy > 0f) { vy *= -.45f; vx *= -.7f }
        }

        private fun resetBall() {
            ballX = 58f; ballY = 438f; vx = 0f; vy = 0f
            launched = false; scored = false; dragging = false
        }

        private fun rect(c: Canvas, x: Float, y: Float, w: Float, h: Float, color: Int) {
            p.color = color; c.drawRect(x, y, x+w, y+h, p)
        }
        private fun text(c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int) {
            p.textSize = size; p.color = color; c.drawText(s, x, y, p)
        }

        private fun drawNarrator(c: Canvas) {
            // Adult narrator portrait: fashionable, warm, expressive pixel-art styling.
            val bob = if (frame % 48 < 24) 0f else 1f
            val hair = Color.rgb(74, 39, 57)
            val skin = Color.rgb(246, 190, 158)
            val outfit = Color.rgb(190, 91, 137)
            // Portrait frame and soft accent.
            rect(c, 244f, 82f, 66f, 91f, Color.rgb(49, 35, 58))
            rect(c, 247f, 85f, 60f, 85f, Color.rgb(238, 184, 190))
            // Hair silhouette and side locks.
            rect(c, 254f, 91f+bob, 47f, 48f, hair)
            rect(c, 250f, 105f+bob, 9f, 43f, hair)
            rect(c, 296f, 108f+bob, 8f, 43f, hair)
            // Face, fringe, eyes and animated mouth.
            rect(c, 260f, 101f+bob, 34f, 39f, skin)
            rect(c, 258f, 96f+bob, 38f, 12f, hair)
            rect(c, 263f, 116f+bob, 5f, 3f, Color.rgb(54, 37, 49))
            rect(c, 284f, 116f+bob, 5f, 3f, Color.rgb(54, 37, 49))
            rect(c, 272f, 128f+bob, 9f, if (frame % 36 < 5) 2f else 4f, Color.rgb(166, 72, 100))
            rect(c, 258f, 126f+bob, 5f, 3f, Color.rgb(235, 132, 143))
            rect(c, 290f, 126f+bob, 5f, 3f, Color.rgb(235, 132, 143))
            // Stylish collar and outfit.
            rect(c, 258f, 141f+bob, 38f, 6f, Color.rgb(255, 224, 207))
            rect(c, 253f, 147f+bob, 48f, 22f, outfit)
            rect(c, 272f, 147f+bob, 8f, 15f, Color.rgb(255, 224, 207))
            rect(c, 250f, 164f+bob, 54f, 6f, Color.rgb(125, 57, 99))
            // Dialogue card.
            rect(c, 12f, 448f, 296f, 92f, Color.rgb(48, 37, 57))
            rect(c, 15f, 451f, 290f, 86f, Color.rgb(245, 221, 214))
            rect(c, 15f, 451f, 290f, 5f, Color.rgb(213, 128, 158))
            val line = when {
                secret -> "Oh... something is here."
                score > 0 && score % 3 == 0 -> "That's my champion! So lovely!"
                score > 0 -> "Look at you go, sweetheart!"
                misses >= 3 -> "Aww, breathe. Try once more!"
                misses > 0 -> "Ooh, close one, honey!"
                dragging -> "Easy does it... you've got this!"
                else -> "Ready, sweetheart? Show me!"
            }
            text(c, "MOMMY", 25f, 473f, 10f, Color.rgb(151, 67, 111))
            text(c, line, 25f, 496f, 10f, Color.rgb(54, 42, 57))
            text(c, "♡", 279f, 473f, 12f, Color.rgb(213, 102, 143))
        }

        private fun drawScene(c: Canvas) {
            rect(c,0f,0f,320f,568f,Color.rgb(18,22,35))
            // Pixel-art gym backdrop.
            rect(c,0f,90f,320f,310f,Color.rgb(31,43,61))
            for (i in 0..7) rect(c, i*44f, 112f, 2f, 275f, Color.rgb(40,55,73))
            rect(c,0f,390f,320f,178f,Color.rgb(94,67,53))
            for (i in 0..9) rect(c,i*34f,390f,1f,178f,Color.rgb(117,85,64))
            rect(c,0f,390f,320f,4f,Color.rgb(224,174,105))
            text(c,"PIXEL DUNK",16f,34f,19f,Color.WHITE)
            text(c,"SCORE  $score",16f,62f,12f,Color.rgb(255,218,120))
            text(c,"LEVEL  $level",245f,62f,10f,Color.LTGRAY)
            // Backboard, rim and net.
            rect(c,rimX+20f,190f,7f,75f,Color.rgb(206,218,224))
            rect(c,rimX-4f,190f,54f,5f,Color.WHITE)
            rect(c,rimX-2f,rimY,30f,4f,Color.rgb(255,139,64))
            for (i in 0..4) rect(c,rimX+i*6f,254f+(i%2)*3f,1f,25f,Color.rgb(220,225,226))
            // Small level-dependent obstacle.
            if (level >= 2) rect(c,146f,320f,if(level%2==0) 38f else 24f,7f,Color.rgb(224,174,105))
            if (dragging) {
                val dx = (ballX-aimX); val dy = (ballY-aimY)
                for (i in 1..8) {
                    val t = i/8f
                    val px = ballX + dx*t*1.6f
                    val py = ballY + dy*t*1.6f + 260f*t*t*.35f
                    rect(c,px,py,3f,3f,Color.rgb(255,220,130))
                }
                text(c,"PULL BACK  /  RELEASE",74f,520f,9f,Color.WHITE)
            } else if (!launched) text(c,"DRAG BACK TO AIM",92f,520f,10f,Color.LTGRAY)
            drawNarrator(c)
            // Ball with crisp pixel highlights.
            rect(c,ballX-9f,ballY-9f,18f,18f,Color.rgb(221,112,43))
            rect(c,ballX-6f,ballY-9f,3f,18f,Color.rgb(90,48,35))
            rect(c,ballX-9f,ballY-2f,18f,3f,Color.rgb(90,48,35))
            rect(c,ballX-5f,ballY-6f,3f,3f,Color.rgb(255,190,105))
            if (secret) {
                if (secretPhase == 1) {
                    // Almost-missed warning: the gym sinks into darkness, with two tiny distant pixels.
                    rect(c,0f,0f,320f,568f,Color.argb(218,3,5,10))
                    rect(c,151f,176f,3f,3f,Color.rgb(92,105,112))
                    rect(c,166f,176f,3f,3f,Color.rgb(92,105,112))
                } else if (secretPhase == 2) {
                    // Sudden close-up, deliberately abstract and non-graphic.
                    rect(c,0f,0f,320f,568f,Color.BLACK)
                    rect(c,28f,92f,264f,350f,Color.rgb(10,10,16))
                    rect(c,48f,126f,224f,250f,Color.rgb(205,207,198))
                    rect(c,74f,192f,58f,66f,Color.BLACK)
                    rect(c,188f,192f,58f,66f,Color.BLACK)
                    rect(c,91f,210f,20f,22f,Color.rgb(235,235,220))
                    rect(c,209f,210f,20f,22f,Color.rgb(235,235,220))
                    rect(c,116f,302f,88f,26f,Color.rgb(38,12,20))
                    rect(c,128f,302f,12f,10f,Color.WHITE)
                    rect(c,152f,302f,12f,10f,Color.WHITE)
                } else {
                    // Brief black beat before control returns to the court.
                    rect(c,0f,0f,320f,568f,Color.BLACK)
                    rect(c,157f,276f,6f,6f,Color.rgb(110,22,32))
                }
            }
        }

        override fun onTouchEvent(e: MotionEvent): Boolean {
            val x = e.x / (width / 320f); val y = e.y / (height / 568f)
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!launched && !secret && hypot(x-ballX,y-ballY) < 42f) {
                        dragging = true; aimX = x; aimY = y; return true
                    }
                }
                MotionEvent.ACTION_MOVE -> if (dragging) { aimX=x; aimY=y; invalidate(); return true }
                MotionEvent.ACTION_UP -> if (dragging) {
                    val dx = (ballX-x).coerceIn(-130f,130f)
                    val dy = (ballY-y).coerceIn(-180f,180f)
                    vx = dx*4.2f; vy = dy*4.2f
                    launched = true; dragging = false; invalidate(); return true
                }
            }
            return true
        }
    }
}
