import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MovieRecommendationApp {
    private JFrame frame;
    private JTextField userIdField;
    private JTextArea recommendationsArea;
    private JTextField movieNameField;
    private JComboBox<String> ratingRangeBox;
    private BipartiteGraph graph;
    private Map<Integer, String> movieNames;

    public MovieRecommendationApp() {
        graph = new BipartiteGraph();
        movieNames = new HashMap<>();
        String store=System.getProperty("user.dir");
        initializeDataFromFile(store+"\\OOPSproject\\MovieRecommendation\\javaproject.txt");
        initializeMovieNames(store+"\\OOPSproject\\MovieRecommendation\\movies.csv");
        initializeGUI();
    }

    private class BipartiteGraph {
        private Map<Integer, Map<Integer, Integer>> userToMovie;
        private Map<Integer, Map<Integer, Integer>> movieToUser;

        public BipartiteGraph() {
            userToMovie = new HashMap<>();
            movieToUser = new HashMap<>();
        }

        public void addRating(int userId, int movieId, int rating) {
            userToMovie.computeIfAbsent(userId, k -> new HashMap<>())
                      .put(movieId, rating);
            movieToUser.computeIfAbsent(movieId, k -> new HashMap<>())
                      .put(userId, rating);
        }

        public Map<Integer, Integer> getUserRatings(int userId) {
            return userToMovie.getOrDefault(userId, new HashMap<>());
        }

        public Map<Integer, Integer> getMovieRatings(int movieId) {
            return movieToUser.getOrDefault(movieId, new HashMap<>());
        }

        public Set<Integer> findSimilarUsers(int userId) {
            Set<Integer> similarUsers = new HashSet<>();
            Map<Integer, Integer> userMovies = getUserRatings(userId);

            for (Map.Entry<Integer, Map<Integer, Integer>> entry : userToMovie.entrySet()) {
                int otherUserId = entry.getKey();
                if (otherUserId == userId) continue;

                long commonMovies = entry.getValue().keySet()
                    .stream()
                    .filter(userMovies::containsKey)
                    .count();

                if (commonMovies >= 3) {
                    similarUsers.add(otherUserId);
                }
            }
            return similarUsers;
        }
    }

    private void initializeDataFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("userId")) continue;
                String[] parts = line.split("\t");
                int userId = Integer.parseInt(parts[0]);
                int movieId = Integer.parseInt(parts[1]);
                int rating = (int) Math.round(Double.parseDouble(parts[2]));
                graph.addRating(userId, movieId, rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void initializeMovieNames(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("movieId")) continue;
                String[] parts = line.split(",");
                int movieId = Integer.parseInt(parts[0]);
                String movieName = parts[1].trim();
                movieNames.put(movieId, movieName);
            }
        } catch (IOException e) {
            System.err.println("Error reading movie names file: " + e.getMessage());
        }
    }

    private List<String> recommendMovies(int userId) {
        Set<Integer> similarUsers = graph.findSimilarUsers(userId);
        Map<Integer, Integer> userRatedMovies = graph.getUserRatings(userId);
        Map<Integer, Integer> movieScores = new HashMap<>();
        Map<Integer, Integer> ratingCounts = new HashMap<>();

        for (int similarUser : similarUsers) {
            Map<Integer, Integer> similarUserRatings = graph.getUserRatings(similarUser);
            for (Map.Entry<Integer, Integer> entry : similarUserRatings.entrySet()) {
                int movieId = entry.getKey();
                int rating = entry.getValue();
                if (!userRatedMovies.containsKey(movieId) && rating >= 4) {
                    movieScores.put(movieId, movieScores.getOrDefault(movieId, 0) + rating);
                    ratingCounts.put(movieId, ratingCounts.getOrDefault(movieId, 0) + 1);
                }
            }
        }

        return movieScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(entry -> {
                    int movieId = entry.getKey();
                    String movieName = movieNames.getOrDefault(movieId, "Unknown Movie");
                    double avgRating = (double) entry.getValue() / ratingCounts.get(movieId);
                    return movieName + " - " + String.format("%.1f", avgRating);
                })
                .collect(Collectors.toList());
    }

    private List<String> findUsersWhoRatedMovie(String movieName, int ratingRange) {
        Optional<Integer> movieIdOpt = movieNames.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(movieName))
                .map(Map.Entry::getKey)
                .findFirst();

        if (movieIdOpt.isEmpty()) {
            recommendationsArea.setText("Movie not found.");
            return Collections.emptyList();
        }

        int movieId = movieIdOpt.get();
        List<String> users = new ArrayList<>();
        Map<Integer, Integer> movieRatings = graph.getMovieRatings(movieId);

        for (Map.Entry<Integer, Integer> entry : movieRatings.entrySet()) {
            int lowerBound = ratingRange - 1;
            int upperBound = ratingRange;
            
            if (entry.getValue() > lowerBound && entry.getValue() <= upperBound) {
                users.add("User " + entry.getKey() + " rated " + movieName + " with " + entry.getValue());
            }
        }

        return users;
    }

    private void initializeGUI() {
        frame = new JFrame("Movie Recommendation App");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        panel.setBorder(blackBorder);

        placeComponents(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("User ID:");
        userIdField = new JTextField(20);
        
        JLabel movieLabel = new JLabel("Movie Name:");
        movieNameField = new JTextField(20);
        
        JLabel ratingLabel = new JLabel("Rating Range:");
        String[] ratings = {"1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars"};
        ratingRangeBox = new JComboBox<>(ratings);

        JButton recommendButton = new JButton("Recommend Movies");
        JButton findUsersButton = new JButton("Find Similar Users");

        addHoverEffect(recommendButton);
        addHoverEffect(findUsersButton);

        inputPanel.add(userLabel);
        inputPanel.add(userIdField);
        inputPanel.add(movieLabel);
        inputPanel.add(movieNameField);
        inputPanel.add(ratingLabel);
        inputPanel.add(ratingRangeBox);
        inputPanel.add(recommendButton);
        inputPanel.add(findUsersButton);
        
        panel.add(inputPanel);

        recommendationsArea = new JTextArea(10, 30);
        recommendationsArea.setLineWrap(true);
        recommendationsArea.setWrapStyleWord(true);
        recommendationsArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(recommendationsArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane);

        recommendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    List<String> recommendedMovies = recommendMovies(userId);
                    String output = "Recommended Movies (Movie Name - Rating):\n" + String.join("\n", recommendedMovies);
                    recommendationsArea.setText(output);
                    writeToFile("output.txt", output);
                } catch (NumberFormatException ex) {
                    String errorMessage = "Please enter a valid User ID.";
                    recommendationsArea.setText(errorMessage);
                    writeToFile("C:\\\\Users\\\\Momin\\\\eclipse-workspace\\\\begineer course\\\\src\\\\output", errorMessage);
                }
            }
        });

        findUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String movieName = movieNameField.getText().trim();
                int selectedRating = ratingRangeBox.getSelectedIndex() + 1;
                
                List<String> users = findUsersWhoRatedMovie(movieName, selectedRating);
                String output;
                if (users.isEmpty()) {
                    output = "No users found who rated the movie " + selectedRating + " stars.";
                } else {
                    output = "Users who rated " + movieName + " " + selectedRating + " stars:\n" + 
                            String.join("\n", users);
                }
                recommendationsArea.setText(output);
                writeToFile("C:\\Users\\Momin\\eclipse-workspace\\begineer course\\src\\output", output);
            }
        });
    }

    private void addHoverEffect(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.LIGHT_GRAY);
            }
        });
    }

    private void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(content);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieRecommendationApp::new);
    }
}
