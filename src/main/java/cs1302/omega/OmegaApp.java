package cs1302.omega;

import cs1302.game.DemoGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.paint.Color;

/**
 * This program is a recreation of the classic arcade PONG. Pong is tennis-like two dimensional
 * arcade game where two players exchange a ball back and foward, similar to tennis, until one
 * person loses by not hitting the ball back. As the ball exchanges between two players, the ball's
 * speed increases. A player will be notified their results by a score board being present while the
 * game is in session.
 */
public class OmegaApp extends Application {

    // constants for dimensions of the player bars
    private static final int PLAYER_BARX = 20;
    private static final int PLAYER_BARY = 75;

    //constants for the window dimensions
    private static final int width = 1000;
    private static final int height = 600;

    //constant for the ball dimension
    private static final double BALL = 20;

    //variables
    int ballYSpeed = 1;
    int ballXSpeed = 1;
    double xBallSpot = width / 2;
    double yBallSpot = height / 2;
    DemoGame game;
    Canvas canvas;
    double yPlayer1 = height / 2;
    double yPlayer2 = height / 2;
    Label notice;
    ImageView banner;
    int player1Score = 0;
    int player2Score = 0;
    Image bannerImage;
    boolean gameOn;
    int xPlayer1 = 0;
    double xPlayer2 = width - PLAYER_BARX;
    VBox root;
    Timeline screenPlay;

    /**
     * Constructs an {@code OmegaApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public OmegaApp() {}

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        // setup scene
        Image bannerImage = new Image("file:resources/readme-banner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(true);
        banner.setFitWidth(640);
        Label notice = new Label ("Modify the starter code to suit your needs.");
        Label instructions = new Label ("Move left/right with arrow key; click rectangle" +
        "to teleport.");
        DemoGame game = new DemoGame(640,240);
        VBox root = new VBox(banner, notice, instructions, game);
        Scene scene = new Scene(root);

        // setup stage
        stage.setTitle("PONG!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

        //background size
		Canvas canvas = new Canvas(width, height);
		GraphicsContext screen = canvas.getGraphicsContext2D();

        Timeline screenPlay = new Timeline(new KeyFrame(Duration.millis(1), e ->
        gamePlay(screen)));
		screenPlay.setCycleCount(Timeline.INDEFINITE);

		//mouse control (move and click)
        canvas.setOnMouseMoved(e ->  yPlayer1  = e.getY());
		canvas.setOnMouseClicked(e ->  gameOn = true);
		stage.setScene(new Scene(new StackPane(canvas)));
		stage.show();
		screenPlay.play();

    } // start


    /**
     * Method to consider when the player loses. When the player loses, the other player's
     * score increase by an incrament of 1.
     */

    private void playerLoss() {
        if (xBallSpot < xPlayer1 - PLAYER_BARX) {
            player2Score++; //increasing score by 1
            gameOn = false;
        } // if
    } // p1Loss


    /**
     * method to consider when the player wins. When the player wins, the their score will
     * increase by an incrament of 1.
     */

    private void computerLoss() {
        if (xBallSpot > xPlayer2 + PLAYER_BARX) {
            player1Score++; // increasing score by 1
            gameOn = false;
        }
    } //computerLoss

    /**
     * This method checks if the ball for pong is consistently in bounds of the game board
     * dimensions.
     */

    private void ballInBounds() {
        if  (yBallSpot < 0 || yBallSpot > height) {
            ballYSpeed *= -1;
        }
    } // ballinBounds

    /**
     * As the game continues, we want the ball to increase in velocity and it's speed.
     */

    private void ballSpeed() {
        gameOn = true;
        if (((xBallSpot + BALL > xPlayer2) && yBallSpot >= yPlayer2 &&
        yBallSpot <= yPlayer2 + PLAYER_BARY) || ((xBallSpot < xPlayer1 + PLAYER_BARX)
        && yBallSpot >= yPlayer1 && yBallSpot <= yPlayer1 + PLAYER_BARY)) {
            ballYSpeed += 1 * Math.signum(ballYSpeed);
            ballXSpeed += 1 * Math.signum(ballXSpeed);
            ballYSpeed *= -1;
            ballXSpeed *= -1;
        }
    } // ballSpeed

    /**
     * This method sets the ball position and aligns the position of the ball with
     * its current speed.
     */

    private void ballTrack() {
        yBallSpot += ballYSpeed;
        xBallSpot += ballXSpeed;
    } // ballTrack

    /**
     * When each game ends, the game starts again with the original settings and location of
     * the paddles and the speed of the ball.
     */

    private void restart() {
        ballXSpeed = new Random().nextInt(2) == 0? 1: -1;
        ballYSpeed = new Random().nextInt(2) == 0? 1: -1;

        yBallSpot = height / 2;
        xBallSpot = width / 2;
    }

    /**
     * This is the main game loop of the program. It invokes other methods to
     * make up the entire game of pong. The score for reach player is shown
     * throughout the entire game. When one person loses, the game returns back to
     * it's original settings and the game starts again.
     *
     * @param screen the graphic visual display the user will be looking at to play
     * the game.
     */

    private void gamePlay (GraphicsContext screen) {
        screen.setFill(Color.BLACK);
        screen.fillRect(0,0,width,height);
        screen.setFill(Color.WHITE);
        screen.setFont(Font.font(30));

        if(gameOn) {
            ballTrack();
            if (xBallSpot < width - width  / 4) {
				yPlayer2 = yBallSpot - PLAYER_BARY / 2;
			}  else {
				yPlayer2 =  yBallSpot > yPlayer2 + PLAYER_BARY /
                    2 ?yPlayer2 += 1: yPlayer2 - 1;
			}
            screen.fillOval(xBallSpot, yBallSpot, BALL, BALL);
        } else {
            //if the game is currently not in play, a click button is available to start the game
            screen.setStroke(Color.WHITE);
            screen.setTextAlign(TextAlignment.CENTER);

            //set the start game text in the middle of the screen
			screen.strokeText("Start Game", width / 2, height / 2);
            restart(); // setting the game with the original game conditions
        }
        ballInBounds();
        playerLoss();
        computerLoss();
        ballSpeed();

        //displaying score
        screen.fillText(player1Score + "\t\t\t\t\t\t\t\t\t\t" + player2Score, width / 2, 100);
		screen.fillRect(xPlayer2, yPlayer2, PLAYER_BARX, PLAYER_BARY);
		screen.fillRect(xPlayer1, yPlayer1, PLAYER_BARX, PLAYER_BARY);

    } // gamePlay
} //OmegaApp
