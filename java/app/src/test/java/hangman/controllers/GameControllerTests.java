package hangman.controllers;

import hangman.mocks.MockIdentifierGenerator;
import hangman.models.Game;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GameControllerTests {

    @Test
    public void createGameReturnsValidUUID() {
        // arrange
        var newId = UUID.randomUUID();
        var mockIdentifierGenerator = new MockIdentifierGenerator(newId);
        var gameController = new GameController(mockIdentifierGenerator);

        // act
        var result = gameController.createGame();

        // assert
        assertEquals("New game identifier is not valid.", newId, result);
    }

    @Test
    public void validGetGame(){
        HashMap<UUID, Game>games = new HashMap();
        var newId = UUID.randomUUID();
        var mockIdentifierGenerator = new MockIdentifierGenerator(newId);
        var gameController = new GameController(mockIdentifierGenerator);

        gameController.createGame();

        assertEquals(newId, games.containsKey(newId));

    }

    @Test
    public void invalidGetGame(){

    }

    @Test
    public void deleteGame(){

    }

    @Test
    public void makeGuess(){

    }
}
