# BlueBridge
BlueBridge is a modular bridge plugin for Bukkit (or Spigot/Paper) servers with the aim to bring multiple minecraft region plugins to BlueMap (Currently, WorldGuard and GriefPrevention are supported but there's always more on its way and the issues are open for ideas and suggestions).

# How to use
Install BlueBridgeCore and the addons you need just like any other plugin, configure them to fit your needs and you are ready to go!  
For more detailed information, check out the [wiki](https://github.com/Mark-225/BlueBridge/wiki)!

# Download
Take a look at the [releases](https://github.com/Mark-225/BlueBridge/releases) section for direct downloads of the plugin jar files or build the plugin yourself by cloning this repo and executing "gradlew build" from the main directory.

# Uninstall
If you don't want to use the plugin anymore and already removed the jar files from your plugins folder, you might have noticed that the region markers and markersets stay visible.
You have two options to deal with this issue:
1. (Easiest solution) If you don't have any other markers anyways, you can just delete the markers.json from bluemap/web/data and let bluemap generate a new one on the next restart.
2. If you have other markers you want to keep, you can manually remove BlueBridge's markersets from your markers.json. To do this, delete the complete JSON Object with the markerset id you want to remove from the "markerSets" JSON array (don't forget the curly braces and if necessary the comma that seperated one JSON object from the next in the array). Since JSON can become very convoluted and hard to read if there's a lot of data, you can set the render-default setting of all BlueBridge addons to false and start the server with those settings once before removing the plugins. That way, all markers will be removed and only the empty markersets remain in the markers.json, which makes it a lot easier to find the beginning and end of a markerset.

Example for solution 2:
If this was your markers.json, remove everything that's strikethrough ('...' means some attributes are left out for readability).
<pre><code>
{
  "markerSets": [
    <strike>{
      "id": "!BlueBridge_RegionSet#BlueBridgeWG",
      "label": "WorldGuard Regions",
      "toggleable": true,
      "defaultHide": false,
      "marker": [
      {
          "id": "!BlueBridge_RegionMarker#BlueBridgeWG_98f08d55-e409-4b9e-b619-f74c55e4de93/world:test",
          "type": "shape",
          "map": "world",
          "position": {
            "x": 34.5,
            "y": 63.0,
            "z": 44.0
          },
          ...
      },
      ...
    },</strike>(<-- Including this comma)
    {
      "id": "Markerset_you_want_to_keep",
      "label": "Important Markers",
      "toggleable": true,
      ...
    }
  ]
}
</code></pre>

Afterwards it should look like this:
<pre><code>
{
  "markerSets": [
    {
      "id": "Markerset_you_want_to_keep",
      "label": "Important Markers",
      "toggleable": true,
      ...
    }
  ]
}
</code></pre>
