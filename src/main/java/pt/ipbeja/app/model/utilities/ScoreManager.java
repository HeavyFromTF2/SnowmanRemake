package pt.ipbeja.app.model.utilities;

import pt.ipbeja.app.model.Score;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class ScoreManager {
    private static final String FILE_DIR = "src/main/resources/scores/";
    private List<Score> highScores = new ArrayList<>();

    public void loadScores(String levelName) {
        highScores.clear();
        File file = new File(FILE_DIR + "scores_" + levelName + ".txt");

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    highScores.add(new Score(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScores(String levelName) {
        File file = new File(FILE_DIR + "scores_" + levelName + ".txt");
        file.getParentFile().mkdirs(); // cria a pasta se não existir

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Score score : highScores) {
                writer.println(score.getPlayerName() + ";" + score.getLevelName() + ";" + score.getMoves());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addScore(Score newScore) {
        String levelName = newScore.getLevelName();

        // Carrega antes de adicionar, para garantir que temos os atuais
        loadScores(levelName);

        highScores.add(newScore);

        // Mantém apenas os 3 melhores para esse nível
        highScores = highScores.stream()
                .filter(s -> s.getLevelName().equals(levelName))
                .sorted()
                .limit(3)
                .collect(Collectors.toList());

        saveScores(levelName);
    }

    public List<Score> getTopScores(String levelName) {
        loadScores(levelName); // Garantir que está carregado
        return highScores.stream()
                .filter(s -> s.getLevelName().equals(levelName))
                .sorted()
                .limit(3)
                .collect(Collectors.toList());
    }
}