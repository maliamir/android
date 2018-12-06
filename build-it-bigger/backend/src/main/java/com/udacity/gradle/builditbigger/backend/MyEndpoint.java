package com.udacity.gradle.builditbigger.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import com.udacity.joke.JokeRepository;

/**
 * An endpoint class we are exposing.
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.builditbigger.gradle.udacity.com",
                ownerName = "backend.builditbigger.gradle.udacity.com",
                packagePath = ""
        )

)
public class MyEndpoint {

    private MyBean myBean;
    private JokeRepository jokeRepository;

    public MyEndpoint() {
        this.myBean = new MyBean();
        this.jokeRepository = new JokeRepository();
    }

    @ApiMethod(name = "getComputerJoke")
    public MyBean loadComputerJoke(){
        this.myBean.setData(this.jokeRepository.getComputerJoke());
        return this.myBean;
    }

    @ApiMethod(name = "getNormalJoke")
    public MyBean loadNormalJoke(){
        this.myBean.setData(this.jokeRepository.getNormalJoke());
        return this.myBean;
    }

}