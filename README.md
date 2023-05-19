# QuestMaster
<p>
  <a href="https://github.com/Doppelclick/QuestMaster/releases/latest" target="_blank">
    <img alt="version" src="https://img.shields.io/github/release/Doppelclick/QuestMaster?color=blue&style=for-the-badge" />
  </a>
  <a href="https://github.com/doppelclick/diana" target="_blank">
    <img alt="version" src="https://img.shields.io/static/v1?label=on&message=Github&color=black&style=for-the-badge"/>
  </a>
  <a href="https://discord.com/channels/@me" target="_blank">
    <img alt="discord" src="https://img.shields.io/badge/Discord-Doppelclick%235993-blue?style=for-the-badge&logo=appveyor" />
  </a>
</p>
A mod for custom Hypixel Skyblock quest guides

<p>
  <a>
    <img alt="quest gui" src="https://i.imgur.com/46MkiJW.png" />
  </a>
  <a>
    <img alt="quest waypoint" src="https://i.imgur.com/XZza2jp.png" />
  </a>
</p>

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