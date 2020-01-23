import org.junit.Assert;
import org.junit.Test;

public class ArrayAfter4Test {
    @Test
    public void arrayAfter4Test1() {
        Assert.assertArrayEquals(new int[]{5, 6}, Main.arrayAfter4(new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    public void arrayAfter4Test2() {
        Assert.assertArrayEquals(new int[]{1, 1, 1, 1}, Main.arrayAfter4(new int[]{7, 4, 5, 4, 1, 1, 1, 1}));
    }

    @Test
    public void arrayAfter4Test3() {
        Assert.assertArrayEquals(new int[]{}, Main.arrayAfter4(new int[]{0, 4}));
    }

    @Test(expected = RuntimeException.class)
    public void arrayAfter4TestException() {
        Main.arrayAfter4(new int[]{1, 1, 1, 1, 1});
    }
}