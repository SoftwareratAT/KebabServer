# Kebab 1.19.3
## _An open-source Minecraft backend server (Still in developement)_

Kebab is an lightweight Minecraft backend server solution providing many features for developers.
Note: **Kebab is not a standalone service!** You need some kind of proxy like Velocity that runs in front of Kebab!

_**Requirements:**_
- Velocity (Bungeecord may work, but you won't receive support if you're using it)
- Java 18 or higher
## Features

- Plugins, based on a huge API Kebab offers
- Multithreading
- Regular worlds are saved in Schematic files (Ideal for Cloud templates and prebuild maps)

Kebab is very lightweight and doesn't have Minecrafts' default features like built in commands, 
mob AIs, user saving and more. To get all these features you need to built your own plugins and
run them on your Kebab server. If you stop a Kebab server, any data on the server will get lost.
Saving the userdata, world and more has to be handled by a plugin.

## API

### The API is currently only available thru the server jar file

Maven:
```sh
<dependency>
     <groupId>com.uroria</groupId>
     <artifactId>Kebab</artifactId>
     <version>VERSION</version>
     <scope>system</scope>
     <systemPath>PATH/TO/THE/JAR</systemPath>
</dependency>
```
Make sure to replace "VERSION" and "PATH/TO/THE/JAR" with the right information!

## Build Kebab

1. Install Git if not already installed and clone the Kebab repo into a folder
```sh
git clone https://github.com/Uroria/KebabServer.git .
```
2. Install Maven if not already installed and build the project
```sh
mvn clean package
```
3. If the project was build successfully, you will find the final .jar in ./target
