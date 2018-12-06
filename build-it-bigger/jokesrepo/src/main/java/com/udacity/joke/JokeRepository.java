package com.udacity.joke;

import java.util.Random;

public final class JokeRepository {

    private static String[] computerJokes;
    private static String[] normalJokes;

    private Random random;

    static {

        computerJokes = new String[6];
        computerJokes[0] = "Q. How does a computer get drunk?\n\nA. It takes screenshots.";
        computerJokes[1] = "Q: Why was the computer cold?" + "\n\nA: It left it's Windows open!";
        computerJokes[2] = "Q: Why did the computer keep sneezing?\n\nA: It had a virus! ";
        computerJokes[3] = "Q. How does a computer get drunk?\n\nA. It takes screenshots.";
        computerJokes[4] = "What did the dentist say to the computer?\n\nThis won't hurt a byte.";
        computerJokes[5] = "What part of a computer does a spider use?\n\nThe webcam.";

        normalJokes = new String[6];
        normalJokes[0] = "Q: Anton, do you think I’m a bad mother?" + "\n\nA: My name is Paul.";
        normalJokes[1] = "Q: What is dangerous?\n\nA: Sneezing while having diarrhea!";
        normalJokes[2] = "Google request: How to disable autocorrect in wife?";
        normalJokes[3] = "A naked women robbed a bank. Nobody could remember her face.";
        normalJokes[4] = "Q: Can a kangaroo jump higher than a house?" + "\n\nA: Of course, a house doesn’t jump at all";
        normalJokes[5] = "My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away.";

    }

    public JokeRepository() {
        this.random = new Random();
    }

    public String getComputerJoke() {
        return computerJokes[this.random.nextInt(computerJokes.length)];
    }

    public String getNormalJoke(){
        return normalJokes[this.random.nextInt(normalJokes.length)];
    }

}