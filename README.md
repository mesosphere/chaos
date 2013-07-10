# Chaos

Framework for writing lightweight scala services.

To package and install Chaos into your local Maven repo:

    mvn package install:install

Example app in src/main/scala/mesosphere/chaos/example. To run the example:

    mvn package install:install
    java -cp target/chaos-<VERSION>.jar mesosphere.chaos.example.Main

How do you release a new version of chaos?

    mvn release:prepare
    mvn release:perform

