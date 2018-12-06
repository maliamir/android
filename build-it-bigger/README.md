# Gradle for Android and Java Final Project

In this project, you will create an app with multiple flavors that uses
multiple libraries and Google Cloud Endpoints. The finished app will consist
of four modules. A Java library that provides jokes, a Google Cloud Endpoints
(GCE) project that serves those jokes, an Android Library containing an
activity for displaying jokes, and an Android app that fetches jokes from the
GCE module and passes them to the Android Library for display.

## Why this Project

As Android projects grow in complexity, it becomes necessary to customize the
behavior of the Gradle build tool, allowing automation of repetitive tasks.
Particularly, factoring functionality into libraries and creating product
flavors allow for much bigger projects with minimal added complexity.

## What Will I Learn?

You will learn the role of Gradle in building Android Apps and how to use
Gradle to manage apps of increasing complexity. You'll learn to:

* Add free and paid flavors to an app, and set up your build to share code between them
* Factor reusable functionality into a Java library
* Factor reusable Android functionality into an Android library
* Configure a multi project build to compile your libraries and app
* Use the Gradle App Engine plugin to deploy a backend
* Configure an integration test suite that runs against the local App Engine development server



## Screenshots - Free version (Added share joke option)

![screen shot 2018-04-23 at 09 38 20](https://user-images.githubusercontent.com/33655422/39115888-eb418f88-46da-11e8-8017-db1186864c45.png)
![screen shot 2018-04-23 at 09 38 35](https://user-images.githubusercontent.com/33655422/39115889-eb5c050c-46da-11e8-96fb-69f8a40b0e2d.png)
![screen shot 2018-04-23 at 09 41 18](https://user-images.githubusercontent.com/33655422/39115891-eb77302a-46da-11e8-9728-2d0f55c30c5d.png)

## Screenshots - Paid Version (NO ADS)

![screen shot 2018-04-23 at 10 09 38](https://user-images.githubusercontent.com/33655422/39117458-cc2b5e22-46de-11e8-8631-4dd8350c1293.png)
![screen shot 2018-04-23 at 10 11 59](https://user-images.githubusercontent.com/33655422/39117460-cc910fce-46de-11e8-9ef5-4eedeb3966ab.png)


# Rubric

### Required Components

* Project contains a Java library for supplying jokes
* Project contains an Android library with an activity that displays jokes passed to it as intent extras.
* Project contains a Google Cloud Endpoints module that supplies jokes from the Java library. Project loads jokes from GCE module via an async task.
* Project contains connected tests to verify that the async task is indeed loading jokes.
* Project contains paid/free flavors. The paid flavor has no ads, and no unnecessary dependencies.

### Required Behavior

* App retrieves jokes from Google Cloud Endpoints module and displays them via an Activity from the Android Library.

### Libraries Used 

* [ButterKnife](http://jakewharton.github.io/butterknife/) 


