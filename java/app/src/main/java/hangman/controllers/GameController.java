package hangman.controllers;

import com.google.gson.Gson;
import hangman.interfaces.IdentifierGeneration;
import hangman.models.Game;
import hangman.models.Guess;
import spark.Request;
import spark.Response;

import java.util.*;

public class  GameController {

    private static HashMap<UUID, Game> games = new HashMap();
    private static List<String> words = Arrays.asList("Banana", "Canine", "Unosquare", "Airport");

    private static List<String>correctGuesses = new ArrayList<>();

    private static final int MAXINCORRECTGUESSES = 6;

    private final IdentifierGeneration identifierGeneration;

    public GameController(IdentifierGeneration identifierGeneration) {
        this.identifierGeneration = identifierGeneration;
    }

    public UUID createGame() {
        var newGameId = identifierGeneration.retrieveIdentifier();
        var newGame = new Game(MAXINCORRECTGUESSES, retrieveWord());

        games.put(newGameId, newGame);

        return newGameId;
    }

    public Game getGame(Request request, Response response) {
        var gameArgument = request.params("game_id");
        var gameId = UUID.fromString(gameArgument);
        if (gameId == null || !games.containsKey(gameId)) {
            response.status(404);
            throw new IllegalArgumentException("Incorrect game ID");
        }

        return games.get(gameId);
    }

    public Game deleteGame(Request request, Response response){
        var game = getGame(request,response);
        if (game != null) {

            String ID = request.params("game_id");
            UUID gameID = UUID.fromString(ID);
            if (gameID == null || !games.containsKey(gameID)) {
                response.status(404);
                throw new IllegalArgumentException("Game ID not found");
            }
            Game deletedGame = games.remove(gameID);
            game.setStatus("Game Deleted!");
            return deletedGame;
        }
        return null;
    }

    public Game makeGuess(Request request, Response response) {
        var game = getGame(request, response);
        if (game != null) {
            var guess = new Gson().fromJson(request.body(), Guess.class);

            String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            if (guess == null || guess.getLetter() == null || guess.getLetter().length() != 1 || !validChars.contains(guess.getLetter())) {
                throw new IllegalArgumentException("Guess must be supplied with 1 letter");
            }

            List<String>incorrectGuesses = game.getIncorrectGuesses();

            String guessedLetter = guess.getLetter();

            if (incorrectGuesses.contains(guess.getLetter().toLowerCase()) || correctGuesses.contains(guess.getLetter().toLowerCase())) {
                throw new IllegalArgumentException("Letter already used as a guess, pick another letter");
            }

            if(game.getUnmaskedWord().toLowerCase().contains(guess.getLetter().toLowerCase())){
                String unmaskedWord = game.getUnmaskedWord();
                String word = game.getWord();
                StringBuilder updateWord = new StringBuilder();

                for (int i=0; i < word.length(); i++) {
                    char l = word.charAt(i);
                    char g = unmaskedWord.charAt(i);
                    if (Character.toString(l).equalsIgnoreCase("_")) {
                        if (Character.toString(unmaskedWord.charAt(i)).equalsIgnoreCase(guess.getLetter())) {
                            updateWord.append(g);
                            correctGuesses.add(guess.getLetter());
                        } else {
                            updateWord.append("_");
                        }
                    }else{
                        updateWord.append(l);
                    }
                }
                game.setWord(updateWord.toString());

            }else {
                incorrectGuesses.add(guessedLetter);
                game.setRemainingGuesses(game.getRemainingGuesses()-1);
            }

            if(game.getRemainingGuesses() <= 0){
                game.setStatus("Lost");
            }else if(game.getWord().equalsIgnoreCase(game.getUnmaskedWord()) && game.getRemainingGuesses() > 0){
                game.setStatus("Won");
            }

            if(game.getRemainingGuesses() < 0) {
                throw new IllegalArgumentException("Game over, no more guesses!");
            }

            return game;
        }
        return null;
    }



    private static String retrieveWord() {
        var rand = new Random();
        return words.get(rand.nextInt(words.size() - 1));
    }
}
