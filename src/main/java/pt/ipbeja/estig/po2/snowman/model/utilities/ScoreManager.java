package pt.ipbeja.estig.po2.snowman.model.utilities;

import pt.ipbeja.estig.po2.snowman.model.Score;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Martim Dias - 24290
 * Classe utilitária para gerir os scores de cada nível.
 *
 * Responsável por carregar, guardar e atualizar os melhores scores,
 * armazenando apenas os 3 melhores por nível.
 */

public class ScoreManager {
    private static final String FILE_DIR = "src/main/resources/scores/";
    private List<Score> highScores = new ArrayList<>();

    /**
     * Carrega os scores do ficheiro correspondente ao nível.
     *
     * @param levelName nome do nível
     */
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

    /**
     * Guarda os scores no ficheiro correspondente ao nível.
     *
     * @param levelName nome do nível
     */
    public void saveScores(String levelName) {
        File file = new File(FILE_DIR + "scores_" + levelName + ".txt");
        file.getParentFile().mkdirs(); // Cria o dir se não existir

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Score score : highScores) {
                writer.println(score.getPlayerName() + ";" + score.getLevelName() + ";" + score.getMoves());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adiciona um novo score e atualiza o ficheiro com os 3 melhores.
     *
     * @param newScore novo score a adicionar
     */
    public void addScore(Score newScore) {
        String levelName = newScore.getLevelName();

        loadScores(levelName); // garantir que os atuais scores estão carregados

        highScores.add(newScore);

        // Mantém apenas os 3 melhores para esse nível
        highScores = highScores.stream()
                .filter(s -> s.getLevelName().equals(levelName))
                .sorted()
                .limit(3)
                .collect(Collectors.toList());

        saveScores(levelName);
    }

    /**
     * Obtém os 3 melhores scores para o nível especificado.
     *
     * @param levelName nome do nível
     * @return lista com os 3 melhores scores
     */
    public List<Score> getTopScores(String levelName) {
        loadScores(levelName); // Garantir que está carregado
        return highScores.stream()
                .filter(s -> s.getLevelName().equals(levelName))
                .sorted()
                .limit(3)
                .collect(Collectors.toList());
    }
}