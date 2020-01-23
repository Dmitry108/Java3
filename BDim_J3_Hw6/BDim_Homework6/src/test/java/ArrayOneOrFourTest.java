import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ArrayOneOrFourTest {
    private boolean expected;
    private int[] gotten;

    public ArrayOneOrFourTest(boolean expected, int[] gotten){
        this.expected=expected;
        this.gotten=gotten;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> test(){
        return Arrays.asList(new Object[][]{
                {true, new int[]{1, 1, 1, 4, 4, 1, 4, 4}},
                {false, new int[]{1, 1, 1, 1, 1, 1}},
                {false, new int[]{4, 4, 4, 4}},
                {false, new int[]{1, 4, 4, 1, 1, 4, 3}}});
    }
    @Test
    public void oneOrFourTest(){
        Assert.assertEquals(expected,Main.isOnlyOneOrFour(gotten));
    }
}
