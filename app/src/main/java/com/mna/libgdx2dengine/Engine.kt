package com.mna.libgdx2dengine

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.ExtendViewport

class Engine() : ApplicationAdapter() {

    // MARK: - Properties -

    private val MIN_FRAME_LENGTH = 1f / 120f // 60fps
    private var timeSinceLastRender = 0f

    // game

    private lateinit var batch : SpriteBatch
    private lateinit var texture : Texture
    private lateinit var polygon : Polygon
    private lateinit var circle : Circle
    private lateinit var rectangle : Rectangle
    private lateinit var font : BitmapFont

    // camera

    var camera : OrthographicCamera? = null
    var viewport : ExtendViewport? = null

    // shape renderer

    private lateinit var shapeRenderer : ShapeRenderer

    // polygon batch

    private lateinit var polyBatch : PolygonSpriteBatch

    // MARK: - Lifecycle -

    override fun create() {

        // camera

        camera = OrthographicCamera()
        viewport = ExtendViewport(1080f, 1920f, camera)

        // shape

        shapeRenderer = ShapeRenderer()

        // polyBatch

        polyBatch = PolygonSpriteBatch()

        // game variables

        batch = SpriteBatch()
        texture = Texture("spaceship.png")
        circle = Circle(0f,0f, 200f)
        rectangle = Rectangle(5f, 5f, 400f, 200f)
        polygon = Polygon(floatArrayOf(250f, 250f, 540f, 1670f, 830f, 250f, 250f, 250f,))

        // font generator

        val generator = FreeTypeFontGenerator(Gdx.files.internal("zorque.otf"))

        // fontParameter

        val fontParameter : FreeTypeFontGenerator.FreeTypeFontParameter =
            FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameter.size = 160 // font size

        // font

        font = generator.generateFont(fontParameter)

        generator.dispose()

    }

    // MARK: - Render Loop -

    override fun render() {

        timeSinceLastRender += Gdx.graphics.deltaTime

        // only render once every 1/60th of a second (60fps)

        if (timeSinceLastRender >= MIN_FRAME_LENGTH) {

            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

            // camera

            camera?.update() //update our camera every frame

            /* Polygon Renderer */

            polyBatch.begin()

            val pix = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pix.setColor(-0x21524101) // DE is red, AD is green and BE is blue.

            pix.fill()

            val textureSolid = Texture(pix)

            val polyRegTop = PolygonRegion(
                TextureRegion(textureSolid),
                polygon!!.transformedVertices,
                shortArrayOf(
                    0, 1, 2,  // Two triangles using vertex indices.
                    0, 2, 3 // Take care of the counter-clockwise direction.
                )
            )

            val polyTop = PolygonSprite(polyRegTop)

            polyTop.draw(polyBatch)

            polyBatch.end()

            /* Sprite Batch Renderer */

            // draw

            batch.begin()

            // draw texture

            batch.draw(texture,
                (camera!!.viewportWidth / 2f) - 100f,
                (camera!!.viewportHeight / 2f) - 200f,
                200f,
                200f)

            // draw font

            font.draw(
                batch,
                "Hello world",
                (camera!!.viewportWidth / 2f) - 525f,
                (camera!!.viewportHeight / 2f) - 425f
            )

            // touches

            if (Gdx.input.justTouched()) {

                // touch

                Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
                Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

            }

            batch.end()

            /* Shape Renderer */

            // draw circle

            shapeRenderer.color = Color.ORANGE
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

            shapeRenderer.circle(
                (camera!!.viewportWidth / 2f) + circle!!.x,
                (camera!!.viewportHeight / 2f) + 200f + circle!!.y,
                circle!!.radius)

            shapeRenderer.end()

            // draw rectangle

            shapeRenderer.color = Color.BLUE
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            shapeRenderer.rect(
                (camera!!.viewportWidth / 2f) + rectangle!!.x,
                (camera!!.viewportHeight / 2f) + 200f + rectangle!!.y,
                rectangle!!.width,
                rectangle!!.height)

            shapeRenderer.end()

            /* Collision Detection */

            // detect collision between shapes

            collisionDetection()

            /* Render Loop */

            // fps loop

            timeSinceLastRender = 0f

        }

    }

    // MARK: - Shape Collision Detection -

    private fun collisionDetection(){

        if (Intersector.overlaps(circle, rectangle)) {

            // collision happened

        }

    }

    // MARK: - Camera -

    override fun resize(width: Int, height: Int) {

        // aspect fill

        viewport!!.update(width, height, true)

        batch.projectionMatrix = camera!!.combined

        polyBatch.projectionMatrix = camera!!.combined

        shapeRenderer.projectionMatrix = camera!!.combined;

    }

    // MARK: - Dispose -

    override fun dispose() {

        super.dispose()

        camera = null
        viewport = null

        batch.dispose()
        shapeRenderer.dispose()
        polyBatch.dispose()

        texture.dispose()
        font.dispose()

    }

    // MARK: - Pause -

    override fun pause() {

        //

    }

    // MARK: - Resume -

    override fun resume() {

        //

    }

}
