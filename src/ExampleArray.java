import java.util.Arrays;

public class ExampleArray extends Thread {

    private static final int SIZE = 10_000_000;
    private static final int HALF = SIZE / 2;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start...");
        method();
        method1();
        System.out.println("Finish!");
    }

    // Вычисляем в одном потоке
    public static void method() {

        float[] array = new float[SIZE];        //Исходный массив
        float resultSingle = 0;                 //Для контроля правильности результата

        Arrays.fill(array, 1f);             //Заполняем единицами

        long time = System.currentTimeMillis();    //Засекаем время расчета

        for (int i = 0; i < array.length; i++) {
            array[i] = (float)(array[i] * Math.sin(0.2f + i / 5f) * Math.cos(0.2f + i / 5f) * Math.cos(0.4f + i / 2f));
        }
        //Получаем время выполения расчета
        System.out.println("Обработка в одном потоке " + (System.currentTimeMillis() - time) + " мсек");

        //Посчитаем сумму элементов для контроля правильности
        for (float v : array){
            resultSingle += v;
        }
        System.out.println("resultSingle = " + resultSingle);   //Сумма элементов для контроля
    }

    // Вычисляем в двух потоках
    public static void method1() throws InterruptedException {
        float[] array = new float[SIZE];            //Исходный массив
        float[] arrayHalf1 = new float[HALF];       //Создаем временные массивы с размерностью в пол-исходного
        float[] arrayHalf2 = new float[HALF];
        float resultThread = 0;                     //Для контроля правильности результата

        Arrays.fill(array, 1f);                 //Заполняем исходный массив

        long time = System.currentTimeMillis();        //Засекаем время деления на под-массивы

        System.arraycopy(array, 0, arrayHalf1, 0, HALF); // Копируем исходный массив
        System.arraycopy(array, HALF, arrayHalf2, 0, HALF);     // в два вспомогательных
        // Получаем время
        System.out.println("Разбивка массивов " + (System.currentTimeMillis() - time) + " мсек");

        time = System.currentTimeMillis(); // Засекаем время выполнения в многопоточном варианте

        //Создали потоки
        Thread thread1 =  new Thread(() -> count1(arrayHalf1));
        Thread thread2 =  new Thread(() -> count2(arrayHalf2));

        //Запустили потоки
        thread1.start();
        thread2.start();

        //Ожидаем выполнение потоков
        thread1.join();
        thread2.join();

        //Получили время выполнения расчета двук массивов в многопоточности
        System.out.println("Обработка в двух потоках " + (System.currentTimeMillis() - time) + " мсек");

        time = System.currentTimeMillis();  //Засекаем время склейки

        //Склеиваем массивы в исходный
        System.arraycopy(arrayHalf1, 0, array, 0, HALF);
        System.arraycopy(arrayHalf2, 0, array, HALF, HALF);

        //Получили время склейки
        System.out.println("Склейка " + (System.currentTimeMillis() - time) + " мсек");

        //Контроль правильности
        for (float v : array) {
            resultThread += v;
        }
        System.out.println("resultThread = " + resultThread);
    }

    //Метод расчета для первого потока, первая половина массива
    private static void count1(float[] arr1) {
        long time = System.currentTimeMillis();  //Засекаем время
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = (float)(arr1[i] * Math.sin(0.2f + i / 5f) * Math.cos(0.2f + i / 5f) * Math.cos(0.4f + i / 2f));
        }
        //Получили время
        System.out.println("Первый массив " + (System.currentTimeMillis() - time) + " мсек");
    }

    //Метод расчета для второго потока, вторая половина массива.
    //Здесь для правильности вычислений откорректировали формулу,
    // добавив к значению i приращение массива HALF
    private static void count2(float[] arr2) {
        long time = System.currentTimeMillis();  //Засекаем время
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = (float)(arr2[i] * Math.sin(0.2f + (i + HALF) / 5f) * Math.cos(0.2f + (i + HALF) / 5f) * Math.cos(0.4f + (i + HALF) / 2f));
        }
        //Получили время
        System.out.println("Второй массив " + (System.currentTimeMillis() - time) + " мсек");
    }
}