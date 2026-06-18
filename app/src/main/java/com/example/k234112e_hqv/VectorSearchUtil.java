package com.example.k234112e_hqv;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VectorSearchUtil {
    public static double lastEuclideanDistance = 0;
    public static double lastCosineSimilarity = 0;

    public static String normalizeText(String input) {
        if (input == null) {
            return "";
        }
        String text = removeVietnameseAccents(input).toLowerCase().trim();
        text = text.replace("semester", "hoc ki");
        text = text.replace("term", "hoc ki");
        text = text.replaceAll("[^a-z0-9\\s]", " ");
        text = text.replace("hoc ky", "hoc ki");
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    public static String removeVietnameseAccents(String input) {
        String text = Normalizer.normalize(input, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return text.replace("đ", "d").replace("Đ", "D");
    }

    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String normalized = normalizeText(text);
        if (normalized.isEmpty()) {
            return tokens;
        }
        for (String token : normalized.split(" ")) {
            tokens.add(token);
        }
        return tokens;
    }

    public static List<String> buildVocabulary(List<String> texts) {
        Set<String> vocabulary = new LinkedHashSet<>();
        for (String text : texts) {
            vocabulary.addAll(tokenize(text));
        }
        return new ArrayList<>(vocabulary);
    }

    public static double[] textToVector(String text, List<String> vocabulary) {
        List<String> tokens = tokenize(text);
        double[] vector = new double[vocabulary.size()];
        for (String token : tokens) {
            int index = vocabulary.indexOf(token);
            if (index >= 0) {
                vector[index]++;
            }
        }
        return vector;
    }

    public static double calculateEuclideanDistance(double[] vector1, double[] vector2) {
        double sum = 0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public static double calculateCosineSimilarity(double[] vector1, double[] vector2) {
        double dot = 0;
        double length1 = 0;
        double length2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dot += vector1[i] * vector2[i];
            length1 += vector1[i] * vector1[i];
            length2 += vector2[i] * vector2[i];
        }
        if (length1 == 0 || length2 == 0) {
            return 0;
        }
        return dot / (Math.sqrt(length1) * Math.sqrt(length2));
    }

    public static TrainingProgram findNearestProgram(String query, List<TrainingProgram> programs) {
        TrainingProgram bestProgram = null;
        double bestSimilarity = -1;

        // Build a shared vocabulary across all programs + query for fair comparison
        List<String> allTexts = new ArrayList<>();
        allTexts.add(query);
        for (TrainingProgram program : programs) {
            allTexts.add(getProgramText(program));
        }
        List<String> vocabulary = buildVocabulary(allTexts);
        double[] queryVector = textToVector(query, vocabulary);

        for (TrainingProgram program : programs) {
            String programText = getProgramText(program);
            double[] programVector = textToVector(programText, vocabulary);
            double similarity = calculateCosineSimilarity(queryVector, programVector);
            double distance = calculateEuclideanDistance(queryVector, programVector);

            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestProgram = program;
                lastEuclideanDistance = distance;
                lastCosineSimilarity = similarity;
            }
        }
        return bestProgram;
    }

    public static SemesterProgram findNearestSemester(String query, TrainingProgram selectedProgram) {
        int semesterIndex = findSemesterByKeyword(query);
        if (semesterIndex >= 0 && semesterIndex < selectedProgram.getSemesters().size()) {
            SemesterProgram semester = selectedProgram.getSemesters().get(semesterIndex);
            updateScores(query, getSemesterText(semester));
            return semester;
        }

        SemesterProgram bestSemester = null;
        double bestDistance = Double.MAX_VALUE;
        for (SemesterProgram semester : selectedProgram.getSemesters()) {
            String semesterText = getSemesterText(semester);
            List<String> texts = new ArrayList<>();
            texts.add(query);
            texts.add(semesterText);
            List<String> vocabulary = buildVocabulary(texts);
            double[] queryVector = textToVector(query, vocabulary);
            double[] semesterVector = textToVector(semesterText, vocabulary);
            double distance = calculateEuclideanDistance(queryVector, semesterVector);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestSemester = semester;
                lastEuclideanDistance = distance;
                lastCosineSimilarity = calculateCosineSimilarity(queryVector, semesterVector);
            }
        }
        return bestSemester;
    }

    private static int findSemesterByKeyword(String query) {
        String text = normalizeText(query);
        // Use explicit "hoc ki N" or standalone digit tokens to avoid false positives
        if (text.matches(".*\\bhoc ki 1\\b.*") || text.matches(".*\\bki 1\\b.*")
                || text.matches(".*\\b1\\b.*") || text.contains(" mot ") || text.startsWith("mot ")
                || text.contains(" dau ") || text.startsWith("dau ")) {
            return 0;
        }
        if (text.matches(".*\\bhoc ki 2\\b.*") || text.matches(".*\\bki 2\\b.*")
                || text.matches(".*\\b2\\b.*") || text.contains(" hai ") || text.startsWith("hai ")) {
            return 1;
        }
        if (text.matches(".*\\bhoc ki 3\\b.*") || text.matches(".*\\bki 3\\b.*")
                || text.matches(".*\\b3\\b.*") || text.contains(" ba ") || text.startsWith("ba ")) {
            return 2;
        }
        return -1;
    }

    private static void updateScores(String query, String text) {
        List<String> texts = new ArrayList<>();
        texts.add(query);
        texts.add(text);
        List<String> vocabulary = buildVocabulary(texts);
        double[] queryVector = textToVector(query, vocabulary);
        double[] textVector = textToVector(text, vocabulary);
        lastEuclideanDistance = calculateEuclideanDistance(queryVector, textVector);
        lastCosineSimilarity = calculateCosineSimilarity(queryVector, textVector);
    }

    private static String getProgramText(TrainingProgram program) {
        StringBuilder builder = new StringBuilder();
        builder.append(program.getProgramName()).append(" ");
        // URL is excluded because normalizeText strips all non-alphanumeric chars,
        // making the URL contribute nothing useful to the vector.
        for (String keyword : program.getKeywords()) {
            builder.append(keyword).append(" ");
        }
        return builder.toString();
    }

    private static String getSemesterText(SemesterProgram semester) {
        StringBuilder builder = new StringBuilder();
        builder.append(semester.getSemesterName()).append(" ");
        builder.append(semester.getDescriptionText()).append(" ");
        for (Subject subject : semester.getSubjects()) {
            builder.append(subject.getName()).append(" ");
        }
        return builder.toString();
    }
}
