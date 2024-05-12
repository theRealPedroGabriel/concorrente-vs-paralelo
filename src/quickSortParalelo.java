import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class quickSortParalelo {

    public static void main(String[] args) {
        int[] sizes = {100, 1000, 2000}; // Tamanhos dos conjuntos de dados
        String[] dataTypes = {"ordenado", "inversamente ordenado", "aleatório"}; // Natureza dos conjuntos de dados

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int size : sizes) {
            for (String dataType : dataTypes) {
                long totalTime = 0;

                for (int i = 0; i < 5; i++) {
                    int[] array = generateArray(size, dataType);
                    int numThreads = 8;//Runtime.getRuntime().availableProcessors(); //quantidade de threads maxima do pc
                    long startTime = System.nanoTime();
                    quickSort(numThreads,array);
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000; // Tempo de execução em milissegundos
                    totalTime += duration;
                }

                long averageTime = totalTime / 5; // Tempo médio de execução

                dataset.addValue(averageTime, dataType, String.valueOf(size));
            }
        }
        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Criando a janela
        ApplicationFrame frame = new ApplicationFrame("Quick Sort Performance");
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void quickSort(int numThreads,int[] array) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new QuickSortTask(array, 0, array.length - 1));
        pool.shutdown();
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;

        public QuickSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left < right) {
                int partitionIndex = partition(array, left, right);
                QuickSortTask leftTask = new QuickSortTask(array, left, partitionIndex - 1);
                QuickSortTask rightTask = new QuickSortTask(array, partitionIndex + 1, right);
                invokeAll(leftTask, rightTask);
            }
        }

        private int partition(int[] array, int left, int right) {
            int pivot = array[right];
            int i = left - 1;
            for (int j = left; j < right; j++) {
                if (array[j] < pivot) {
                    i++;
                    swap(array, i, j);
                }
            }
            swap(array, i + 1, right);
            return i + 1;
        }

        private void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static int[] generateArray(int size, String dataType) {
        int[] array = new int[size];

        // Gera o conjunto de dados de acordo com a natureza especificada
        switch (dataType) {
            case "ordenado":
                for (int i = 0; i < size; i++) {
                    array[i] = i;
                }
                break;
            case "inversamente ordenado":
                for (int i = 0; i < size; i++) {
                    array[i] = size - i;
                }
                break;
            case "aleatório":
                for (int i = 0; i < size; i++) {
                    array[i] = (int) (Math.random() * size);
                }
                break;
        }

        return array;
    }

    private static JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "grafico",
                "Tamanhos dos conjuntos de dados",
                "Tempo em MS",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);



        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED); // Cor para a série "x"
        renderer.setSeriesPaint(1, Color.BLUE); // Cor para a série "y"
        renderer.setSeriesPaint(2, Color.GREEN); // Cor para a série "z"

        chart.setBackgroundPaint(Color.WHITE);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(
                new TextTitle("Tempo de execusao do algoritimo quicksort paralelo",
                        new Font("Serif", java.awt.Font.BOLD, 18))
        );
        return chart;    }
}
