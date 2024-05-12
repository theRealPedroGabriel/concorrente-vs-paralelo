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

public class mergeSortSequencial {

    public static void main(String[] args) {
        int[] sizes = {10000, 100000, 1000000}; // Tamanhos dos conjuntos de dados
        String[] dataTypes = {"ordenado", "inversamente ordenado", "aleatório"}; // Natureza dos conjuntos de dados

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int size : sizes) {
            for (String dataType : dataTypes) {
                long totalTime = 0;

                for (int i = 0; i < 5; i++) {
                    int[] array = generateArray(size, dataType);
                    long startTime = System.nanoTime();
                    mergeSort(array, 0, array.length - 1);
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
        ApplicationFrame frame = new ApplicationFrame("Merge Sort Performance");
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private static void merge(int[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++)
            L[i] = array[left + i];
        for (int j = 0; j < n2; j++)
            R[j] = array[mid + 1 + j];

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
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
                new TextTitle("Tempo de execusao do algoritimo mergesort sequencial",
                        new Font("Serif", java.awt.Font.BOLD, 18))
        );
        return chart;
    }
}
