//Написать метод, которому в качестве аргумента передается
// не пустой одномерный целочисленный массив.
// Метод должен вернуть новый массив,
// который получен путем вытаскивания из исходного массива элементов,
// идущих после последней четверки.
// Входной массив должен содержать хотя бы одну четверку,
// иначе в методе необходимо выбросить RuntimeException.
// Написать набор тестов для этого метода (по 3-4 варианта входных данных).
// Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(
                arrayAfter4(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7})));
    }
    //Задание №1
    public static int[] arrayAfter4(int[] array){
        int index4 = -1;
        int n = array.length;
        for (int i=n-1; i>=0; i--){
            if (array[i]==4) {
                index4 = ++i;
                i = -1;
            }
        }
        System.out.println(Arrays.toString(array));
        if (index4==-1) throw new RuntimeException("В массиве нет цифры 4");
        int[] newArray = new int[n-=index4];
        System.arraycopy(array,index4,newArray,0,n);
        return newArray;
    }
    //Задание №2
    public static boolean isOnlyOneOrFour(int[] array){
        boolean isOne = false;
        boolean isFour = false;

        for (int a:array) {
            switch (a){
                case 1: if(!isOne) isOne=true; break;
                case 4: if(!isFour) isFour=true; break;
                default: return false;
            }
        }
        return isOne && isFour;
    }
}