# Advertising
* A plugin for Minecraft Bukkit servers
* Allows your players to make advertising
* When a player advertises, the player earn ad-point and admin earn cash from adfly
* With ad-point, player can buy something which is hard to buy with in game money.

# Inistallation
Take the php files from backend folder. Put these files in your apache server. You will see the following lines in the ayar.php file
```
$Key = "mentalr0b123asdf";
$IV = "fdsa321b0rlatnem";
```
Set up two passwords of 16 letters long and change these lines with these passwords. Do not forget to write these passwords in the config.yml file in the plugin folder.
```
key: 'mentalr0b123asdf'
key2: 'fdsa321b0rlatnem'
```
In config.yml you will see following lines:
```
#This line is about where credits is storing
credits: '127.0.0.1/credits.php'
#This line is about when plugin get the credits, this credits need to remove from storing place
remover: '127.0.0.1/remove.php'
#This line is the php script
script: '127.0.0.1/script.php'
```
Change these lines according to your apache server.

In config.yml you will see following lines:
```
USERID: #YOUR USERID
PUBLICAPIKEY: '#YOUR PUBLICAPIKEY'
SECRETAPIKEY: '#YOUR SECRETAPIKEY'
```
Change this lines with your adfly api keys. You can learn your apikeys from adfly
In ads.yml
```
Ads:
  anadolumc:
    Cost: 1.0
    Link: http://google.com
```
When player types a command like /adv generate anadolumc, plugin looks ads.yml file. If there is an anadolumc named ad then it generates an adfly link to your apache server. When someone click the adfly link and goes to your apache server, apache server writes credit to player's account and redirects to http://google.com. So player earns 1 credit. Admin earns cash

In creditShop.yml you can change or add item packets which buyable with credits.
```
Shops:
  tool1:
    CreditCost: 100.0
    Items:
      '322':
        Data: 1
        Amount: 8
        Name: '&4Advertising Gift'
      '276':
        Amount: 1
        Name: '&4Advertising Gift'
        Enchantments:
        - DAMAGE_ALL:2
    Commands:
    - say %player% just bought the tool1 !
```
Commands section works on console
# In-Game Usage
* You can use the plugin with IG-Commands
* Type '/adv' for player commands
* Type '/adv admin' for admin commands
