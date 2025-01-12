package at.laborg.briss.utils.rectcapture;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.itextpdf.text.Rectangle;

import at.laborg.briss.utils.RectangleInfo;

@TestInstance(Lifecycle.PER_CLASS)
class CaptureRectangleTest {

	CaptureRectangle testCaptureRectangle;

	private CaptureRectangle buildTestCaptureRectangle() {
		CaptureRectangle populatedCaptureRectangle = new CaptureRectangle();

		populatedCaptureRectangle.storePageRectangle(1, new Rectangle(2.6F, 5.2F, 100F, 500F));
		populatedCaptureRectangle.storePageRectangle(1, new Rectangle(5.14F, 4.2F, 400F, 300F));
		populatedCaptureRectangle.storePageRectangle(3, new Rectangle(2.6F, 4.2F, 400F, 500F));
		populatedCaptureRectangle.storePageRectangle(4, new Rectangle(600F, 650F, 250F, 350F));
		populatedCaptureRectangle.storePageRectangle(4, new Rectangle(300F, 350F, 750F, 200F));
		populatedCaptureRectangle.storePageRectangle(6, new Rectangle(300F, 350F, 750F, 350F));
		populatedCaptureRectangle.storePageRectangle(7, new Rectangle(2000F, 2500F, 2502F, 1000F));
		populatedCaptureRectangle.storePageRectangle(8, new Rectangle(3000F, 3500F, 3502F, 2000F));

		Boolean computedRects = populatedCaptureRectangle.computeBiggestRects();
		Assertions.assertTrue(computedRects);

		return populatedCaptureRectangle;
	}

	@Test
	void testBiggestRectsFound() {
		Map<Integer, List<Float>> expected = Map.of(1, List.of(2.6F, 4.2F, 400F, 500F), 3,
				List.of(2.6F, 4.2F, 400F, 500F), 4, List.of(300F, 350F, 750F, 350F), 6, List.of(300F, 350F, 750F, 350F),
				7, List.of(2000F, 2500F, 2502F, 1000F), 8, List.of(3000F, 3500F, 3502F, 2000F));
		Map<Integer, List<Float>> result = testCaptureRectangle.getBiggestPageRects();

		Assertions.assertEquals(expected, result);
	}

	@Test
	void testUniqueBiggestRectsFound() {
		Map<List<Float>, List<Integer>> expected = Map.of(List.of(2.6F, 4.2F, 400F, 500F), List.of(1, 3),
				List.of(300F, 350F, 750F, 350F), List.of(4, 6), List.of(2000F, 2500F, 2502F, 1000F), List.of(7),
				List.of(3000F, 3500F, 3502F, 2000F), List.of(8));

		Map<List<Float>, List<Integer>> result = testCaptureRectangle.getUniqueBiggestRects();

		Assertions.assertEquals(expected, result);
	}

	@Test
	void testBiggestRectsNotComputedTwice() {
		Boolean computedTwice = testCaptureRectangle.computeBiggestRects();
		Assertions.assertFalse(computedTwice);
	}

	@Test
	void testGettersAndRectSimplifier() {
		Map<List<Float>, List<Integer>> uniqueBiggestRects = testCaptureRectangle.getUniqueBiggestRects();
		Map<List<Float>, Set<PageEvenOddEnum>> uniqueBiggestRectsEvenness = testCaptureRectangle
				.getUniqueBiggestRectsEvennness();

		CropEvenOddSimplifier cropEvenOddSimplifier = new CropEvenOddSimplifier(uniqueBiggestRects,
				uniqueBiggestRectsEvenness);

		List<Float> resultEvensRect = cropEvenOddSimplifier.getRectangleForEvens();
		List<Float> resultOddsRect = cropEvenOddSimplifier.getRectangleForOdds();
		List<Integer> resultExcludes = cropEvenOddSimplifier.getExcludes();

		List<Float> expectedEvensRect = List.of(300F, 350F, 750F, 350F);
		List<Float> expectedOddsRect = List.of(2.6F, 4.2F, 400F, 500F);
		List<Integer> expectedExcludes = List.of(7, 8);

		Assertions.assertEquals(expectedEvensRect, resultEvensRect);
		Assertions.assertEquals(expectedOddsRect, resultOddsRect);
		Assertions.assertEquals(expectedExcludes, resultExcludes);

	}

	@BeforeEach
	void setup() {
		testCaptureRectangle = buildTestCaptureRectangle();
	}
}