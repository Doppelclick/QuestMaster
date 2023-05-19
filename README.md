# QuestMaster
A mod for custom Hypixel Skyblock quest guides

## Features
+ Quest categories
+ Quest consisting of elements
+ Each element has a trigger, which activates the element and a waypoint render in the world as well as a name rendered onscreen
+ Onscreen quest gui

## Command
+ /questmaster, /qm
  + help | This message
  + toggle | Toggle the mod
  + next [first quest / *quest name*] | Skip to the next element of a quest
  + main | Main gui and quests
  + config | General config gui
  + info | Edit the info display position and config
  + reload | Reload config and quests from file

## Quests
+ Trigger types:
  + Manually enable
  + Chat message (Uses Java regex patterns, meaning you must use e.g. "\\[" for "[")
  + Position clicked
  + Item collected
  + Player position