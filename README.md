# Sellstick
#### SellStick is a Minecraft plugin that allows customisable selling of chest, shulker and barrel contents

Sellstick is a fork of the popular Spigot plugin created by [shmkane](https://github.com/shmkane/SellStick/)

## Features

- Sell contents of chests, barrels and shulkers
- Essentials, ShopGUI+ and Indepedent Sell Interface
- Sell Multiplier
- Combine sellsticks

If you have any issues or features, please open up an issue or have a go at it yourself in your own PR!

###### Supported Platforms: Spigot, Paper and other servers implementing Bukkit API

---
## Commands
```
/sellstick reload - Loads new config changes onto server
/sellstick give <player> <amount> <uses/infinite> - Give player sellsticks
/sellstick merge - Combine two or more sellsticks to form one with combined uses
/sellstick toggle - Toggle sellstick sell messages from chat to action bar for less clutter
```

#### Examples
```
/sellstick give <PlayerName> 1 20 - Gives 1 sellstick with 20 uses
/sellstick give <PlayerName> 2 10 - Gives 2 sellsticks each with 10 uses
/sellstick give <PlayerName> 1 infinite - Gives an infinite sellstick
/sellstick give <PlayerName> 2 i - Gives 2 infinite sellsticks
```
---
## Permissions
```
sellstick.give - Allows the player with this permission to give another player sell sticks!
sellstick.use - Allows the player with this permission to use a sell stick!
sellstick.multiplier.x - Allows players to sell by a factor (e.g. sellstick.multiplier.1.1)
```
