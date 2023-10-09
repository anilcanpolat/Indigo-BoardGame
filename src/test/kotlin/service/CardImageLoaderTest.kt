package service

import entity.CardSuit
import entity.CardValue
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.test.*

/**
 * Test cases for the [CardImageLoader]
 */
class CardImageLoaderTest {
    /**
     * The [CardImageLoader] that is tested with this test class
     */
    private val imageLoader: CardImageLoader = CardImageLoader()

    /**
     * A queen of hearts test image that is used to be compared to images
     * loaded with [imageLoader]. Please note that it could be initialized
     * directly, but to showcase the use of the [BeforeTest] annotation, it
     * is initialized in [loadCompareImage]
     */
    private var queenOfHearts : BufferedImage? = null

    /**
     * Executed before all test methods in this class. Initializes the
     * [queenOfHearts] property.
     */
    @BeforeTest
    fun loadCompareImage() {
        queenOfHearts =
            ImageIO.read(CardImageLoaderTest::class.java.getResource("/queen_of_hearts.png"))
    }

    /**
     * Loads the image for every possible suit/value combination as well as
     * front and back side and checks whether the resulting [BufferedImage]
     * has the correct dimensions of 130x200 px.
     */
    @Test
    fun testLoadAll() {
        val allImages = mutableListOf<BufferedImage>()
        CardSuit.values().forEach { suit ->
            CardValue.values().forEach { value ->
                allImages += imageLoader.frontImageFor(suit, value)
            }
        }
        allImages += imageLoader.backImage
        allImages += imageLoader.blankImage

        allImages.forEach {
            assertEquals(130, it.width)
            assertEquals(200, it.height)
        }
    }

    /**
     * Loads the queen of hearts from the [imageLoader] and tests equality to [queenOfHearts]
     */
    @Test
    fun testCardEquality() {
        val testImage = imageLoader.frontImageFor(CardSuit.HEARTS, CardValue.QUEEN)
        assertTrue (testImage sameAs queenOfHearts)
    }

    /**
     * Loads the ace of spades from the [imageLoader] and tests inequality [queenOfHearts]
     */
    @Test
    fun testCardUnequality() {
        val testImage = imageLoader.frontImageFor(CardSuit.SPADES, CardValue.ACE)
        assertFalse(testImage sameAs queenOfHearts)
    }

}


/**
 * Tests equality of two [BufferedImage]s by first checking if they have the same dimensions
 * and then comparing every pixels' RGB value.
 */
private infix fun BufferedImage.sameAs(other: Any?): Boolean {

    // if the other is not even a BufferedImage, we are done already
    if (other !is BufferedImage) {
        return false
    }

    // check dimensions
    if (this.width != other.width || this.height != other.height) {
        return false
    }

    // compare every pixel
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (this.getRGB(x, y) != other.getRGB(x, y))
                return false
        }
    }

    // if we reach this point, dimensions and pixels match
    return true

}
