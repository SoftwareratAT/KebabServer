# Kebab 1.19.3
## _An open-source Minecraft backend server_

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

## Build Kebab

1. Clone repository
2. Run "mvn clean package" in order to build the project
